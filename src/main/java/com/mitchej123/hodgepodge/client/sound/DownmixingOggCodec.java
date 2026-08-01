package com.mitchej123.hodgepodge.client.sound;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.SoundConfig;

import paulscode.sound.ICodec;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOrbis;

/**
 * Wraps {@link CodecJOrbis} to do two things.
 * <p>
 * <b>Decodes without the quadratic copying.</b> CodecJOrbis reallocates and copies the whole accumulated array for
 * every 16 KB chunk, and does it while Paulscode holds its global lock - which is what freezes the client when a big
 * sound is first played. See {@link #readAllChunked()}. This applies to every non-streaming sound, mono included.
 * <p>
 * <b>Downmixes stereo to mono.</b> OpenAL only spatializes mono buffers, so a stereo sound is never panned - it plays
 * centred wherever it actually is - and costs twice the memory. (Distance fade is unaffected; Minecraft computes that
 * in Java and applies it as AL_GAIN.)
 * <p>
 * Only {@link #readAll()} is touched, which is the non-streaming path. Music and records are streamed, so they keep
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
        final SoundBuffer buffer = readAllChunked();
        // SpatializeSupport fixes the same problem without losing the stereo width, so defer to it when it is live.
        if (!SoundConfig.downmixStereoSounds || SpatializeSupport.active()
                || buffer == null
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

    /**
     * Replacement for {@link CodecJOrbis#readAll()}, which reallocates and copies the whole accumulated array for every
     * 16 KB chunk it decodes - ~900 MB of copying for a 5 MB sound, all of it while Paulscode holds its global
     * THREAD_SYNC lock, which is what stalls the client thread. {@link CodecJOrbis#read()} accumulates the same way but
     * stops at 128 KB, so looping it and joining once yields the same bytes for a fraction of the work.
     */
    private SoundBuffer readAllChunked() {
        final List<byte[]> chunks = new ArrayList<>();
        int total = 0;
        while (!delegate.endOfStream()) {
            final SoundBuffer chunk = delegate.read();
            if (chunk == null || chunk.audioData == null || chunk.audioData.length == 0) break;
            chunks.add(chunk.audioData);
            total += chunk.audioData.length;
        }
        if (chunks.isEmpty()) return null;

        final AudioFormat format = delegate.getAudioFormat();
        if (chunks.size() == 1) return new SoundBuffer(chunks.get(0), format);

        final byte[] all = new byte[total];
        int offset = 0;
        for (final byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, all, offset, chunk.length);
            offset += chunk.length;
        }
        return new SoundBuffer(all, format);
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
