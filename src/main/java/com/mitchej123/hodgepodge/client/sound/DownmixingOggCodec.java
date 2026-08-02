package com.mitchej123.hodgepodge.client.sound;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
 * The downmix is the fallback, not the primary fix: {@link SpatializeSupport} solves the same problem without losing
 * the stereo width, and takes precedence wherever it works. In practice that leaves downmixing to the Java 8 build,
 * which has no such extension - hence {@link #chooseDownmixMode} caring about the level loss.
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
        if (format.getChannels() != 2 || format.getSampleSizeInBits() != 16 || excluded(source)) {
            return buffer;
        }

        final int before = buffer.audioData.length;
        final DownmixMode mode = chooseDownmixMode(buffer.audioData, format.isBigEndian());
        downmix(buffer, format, mode);
        downmixed = true;
        Common.log.debug(
                "Downmixed {} to mono ({}), {} KB -> {} KB",
                source,
                mode.name().toLowerCase(Locale.ROOT),
                before / 1024,
                buffer.audioData.length / 1024);
        return buffer;
    }

    /**
     * Chooses between averaging and taking either channel, without discarding ordinary distinct stereo content.
     * <p>
     * A nearly silent channel carries nothing useful, while strongly negative L/R correlation identifies actual phase
     * cancellation. Everything else stays averaged so unrelated right-channel content is not thrown away merely because
     * averaging has lower RMS.
     */
    static DownmixMode chooseDownmixMode(byte[] src, boolean bigEndian) {
        final int frames = src.length / 4;
        if (frames == 0) return DownmixMode.AVERAGE;
        // Every 4th frame is ample for this comparison, and quarters the cost of the analysis pass.
        final int stride = 4;
        double leftEnergy = 0, rightEnergy = 0, crossEnergy = 0;
        for (int frame = 0; frame < frames; frame += stride) {
            final int i = frame * 4;
            final int left = sample(src, i, bigEndian);
            final int right = sample(src, i + 2, bigEndian);
            leftEnergy += (double) left * left;
            rightEnergy += (double) right * right;
            crossEnergy += (double) left * right;
        }
        if (leftEnergy == 0 && rightEnergy == 0) return DownmixMode.AVERAGE;

        // A channel at least 30 dB below the other is effectively silent; do not halve the useful channel's level.
        final double silentRatio = 0.001;
        if (leftEnergy <= rightEnergy * silentRatio) return DownmixMode.RIGHT;
        if (rightEnergy <= leftEnergy * silentRatio) return DownmixMode.LEFT;

        final double correlation = crossEnergy / Math.sqrt(leftEnergy * rightEnergy);
        if (correlation < -0.5) {
            // Only discard a channel when averaging loses at least ~1 dB and the loss is demonstrably cancellation.
            final double midEnergy = (leftEnergy + rightEnergy + 2 * crossEnergy) / 4;
            final double louderEnergy = Math.max(leftEnergy, rightEnergy);
            if (louderEnergy > midEnergy * 1.259) {
                return leftEnergy >= rightEnergy ? DownmixMode.LEFT : DownmixMode.RIGHT;
            }
        }
        return DownmixMode.AVERAGE;
    }

    enum DownmixMode {
        AVERAGE,
        LEFT,
        RIGHT
    }

    /**
     * Keeps interface sounds stereo.
     * <p>
     * Downmixing exists to make world sounds locatable; a sound played at the listener gains nothing from it and just
     * loses its stereo image. Which sounds those are is only knowable per <i>play</i>, from the attenuation model,
     * while Paulscode caches one buffer per <i>file</i> - so a decode-time decision can never be more than a guess. A
     * path list is that guess, and a cheap one.
     * <p>
     * Being wrong is not costly in either direction: matching a world sound by mistake leaves it stereo, exactly as it
     * would be without this mod, and missing an interface sound leaves it mono. {@link SpatializeSupport} needs none of
     * this, since it decides per play from the real signal.
     */
    private static boolean excluded(String path) {
        if (path == null || path.isEmpty()) return false;
        final String lower = path.toLowerCase(Locale.ROOT);
        for (final String pattern : SoundConfig.downmixExclusions) {
            if (!pattern.isEmpty() && lower.contains(pattern.toLowerCase(Locale.ROOT))) return true;
        }
        return false;
    }

    private static int sample(byte[] src, int i, boolean bigEndian) {
        return bigEndian ? (src[i] << 8) | (src[i + 1] & 0xFF) : (src[i + 1] << 8) | (src[i] & 0xFF);
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
        final AudioFormat format = delegate.getAudioFormat();
        final int maxBytes = frameAlignedLimit(
                SoundSystemConfig.getMaxFileSize(),
                format == null ? 1 : format.getFrameSize());
        int total = 0;
        boolean truncated = false;
        while (!delegate.endOfStream() && total < maxBytes) {
            final SoundBuffer chunk = delegate.read();
            if (chunk == null || chunk.audioData == null || chunk.audioData.length == 0) break;
            final int keep = Math.min(chunk.audioData.length, maxBytes - total);
            chunks.add(keep == chunk.audioData.length ? chunk.audioData : Arrays.copyOf(chunk.audioData, keep));
            total += keep;
            if (keep < chunk.audioData.length) {
                truncated = true;
                break;
            }
        }
        truncated |= !delegate.endOfStream();
        if (truncated)
            Common.log.warn("Truncated {} at the configured decoded size limit of {} bytes", source, maxBytes);
        if (chunks.isEmpty()) return null;

        if (chunks.size() == 1) return new SoundBuffer(chunks.get(0), format);

        final byte[] all = new byte[total];
        int offset = 0;
        for (final byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, all, offset, chunk.length);
            offset += chunk.length;
        }
        return new SoundBuffer(all, format);
    }

    static int frameAlignedLimit(int maxBytes, int frameSize) {
        if (maxBytes <= 0) return 0;
        final int alignment = Math.max(frameSize, 1);
        return maxBytes - maxBytes % alignment;
    }

    private static AudioFormat toMono(AudioFormat format) {
        return new AudioFormat(format.getSampleRate(), format.getSampleSizeInBits(), 1, true, format.isBigEndian());
    }

    private static void downmix(SoundBuffer buffer, AudioFormat format, DownmixMode mode) {
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
            final int mono = mode == DownmixMode.LEFT ? left : mode == DownmixMode.RIGHT ? right : (left + right) >> 1;
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
