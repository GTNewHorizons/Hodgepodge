package com.mitchej123.hodgepodge.client.sound;

import java.net.URL;

import javax.sound.sampled.AudioFormat;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.SoundConfig;

import paulscode.sound.ICodec;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOrbis;

/**
 * Downmixes stereo sounds to mono as they are decoded. OpenAL only spatializes mono buffers, so a stereo sound is never
 * panned - it plays centred wherever it actually is - and costs twice the memory. (Distance fade is unaffected;
 * Minecraft computes that in Java and applies it as AL_GAIN.)
 * <p>
 * Only {@link #readAll()} is downmixed, which is the non-streaming path. Music and records are streamed, so they keep
 * their stereo automatically.
 */
public class DownmixingOggCodec implements ICodec {

    private final CodecJOrbis delegate = new CodecJOrbis();
    private boolean downmixed = false;
    private String source = "";

    /**
     * Minecraft registers CodecJOrbis in the SoundManager constructor, before postInit and never again, so this wins.
     */
    public static void register() {
        try {
            SoundSystemConfig.setCodec("ogg", DownmixingOggCodec.class);
            Common.log.info("Installed stereo->mono downmixing ogg codec");
        } catch (SoundSystemException e) {
            Common.log.error("Could not install downmixing ogg codec, leaving sounds as-is", e);
        }
    }

    @Override
    public void reverseByteOrder(boolean b) {
        // CodecJOrbis ignores this - it always emits 16-bit signed little-endian, as it reports.
        delegate.reverseByteOrder(b);
    }

    @Override
    public boolean initialize(URL url) {
        downmixed = false;
        source = url == null ? "" : url.getFile();
        return delegate.initialize(url);
    }

    @Override
    public boolean initialized() {
        return delegate.initialized();
    }

    /** Streaming path: music and records, left alone. */
    @Override
    public SoundBuffer read() {
        return delegate.read();
    }

    /** Non-streaming path: positional sound effects. */
    @Override
    public SoundBuffer readAll() {
        final SoundBuffer buffer = delegate.readAll();
        if (!SoundConfig.downmixStereoSounds || buffer == null
                || buffer.audioData == null
                || buffer.audioFormat == null) {
            return buffer;
        }

        final AudioFormat format = buffer.audioFormat;
        if (format.getChannels() != 2 || format.getSampleSizeInBits() != 16) {
            return buffer;
        }

        final int before = buffer.audioData.length;
        downmix(buffer, format);
        downmixed = true;
        Common.log.debug("Downmixed {} to mono, {} KB -> {} KB", source, before / 1024, buffer.audioData.length / 1024);
        return buffer;
    }

    @Override
    public boolean endOfStream() {
        return delegate.endOfStream();
    }

    @Override
    public void cleanup() {
        delegate.cleanup();
    }

    @Override
    public AudioFormat getAudioFormat() {
        // Only the streaming path reads this; LibraryLWJGLOpenAL uses SoundBuffer.audioFormat instead.
        final AudioFormat format = delegate.getAudioFormat();
        return downmixed && format != null ? toMono(format) : format;
    }

    private static AudioFormat toMono(AudioFormat format) {
        return new AudioFormat(format.getSampleRate(), format.getSampleSizeInBits(), 1, true, format.isBigEndian());
    }

    private static void downmix(SoundBuffer buffer, AudioFormat format) {
        final byte[] src = buffer.audioData;
        final boolean bigEndian = format.isBigEndian();
        final int frames = src.length / 4; // 2 channels * 2 bytes
        final byte[] out = new byte[frames * 2];

        for (int frame = 0, in = 0, o = 0; frame < frames; frame++, in += 4, o += 2) {
            final int left, right;
            if (bigEndian) {
                left = (src[in] << 8) | (src[in + 1] & 0xFF);
                right = (src[in + 2] << 8) | (src[in + 3] & 0xFF);
            } else {
                left = (src[in + 1] << 8) | (src[in] & 0xFF);
                right = (src[in + 3] << 8) | (src[in + 2] & 0xFF);
            }
            final int mono = (left + right) >> 1;
            if (bigEndian) {
                out[o] = (byte) (mono >> 8);
                out[o + 1] = (byte) mono;
            } else {
                out[o] = (byte) mono;
                out[o + 1] = (byte) (mono >> 8);
            }
        }

        buffer.audioData = out;
        buffer.audioFormat = toMono(format);
    }
}
