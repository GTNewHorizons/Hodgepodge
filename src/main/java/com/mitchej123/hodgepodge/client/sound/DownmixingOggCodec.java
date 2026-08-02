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
 * which has no such extension - hence {@link #planDownmix} caring about the level loss.
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
        final DownmixPlan plan = planDownmix(buffer.audioData, format.isBigEndian());
        downmix(buffer, format, plan);
        downmixed = true;
        Common.log.debug(
                "Downmixed {} to mono ({}{}), {} KB -> {} KB",
                source,
                plan.mode.name().toLowerCase(Locale.ROOT),
                plan.gain > 1.001f ? String.format(Locale.ROOT, " +%.1f dB", 20 * Math.log10(plan.gain)) : "",
                before / 1024,
                buffer.audioData.length / 1024);
        return buffer;
    }

    /**
     * Picks how to fold the two channels into one, and how much to scale the result.
     * <p>
     * Averaging cancels whatever is out of phase, which cost up to 4 dB on the GregTech sounds. Taking one channel
     * avoids that but discards anything only the other channel had, so it is reserved for the two cases where the
     * discarded channel demonstrably carries nothing: it is near-silent, or it is a near-perfect inversion of the one
     * kept.
     * <p>
     * Everything else keeps both channels and restores the lost level with gain instead. That is the common case - real
     * content is only ever partly out of phase, so no correlation threshold strict enough to mean "cancellation" is
     * ever reached. Measured over the pre-downmix GregTech and TecTech stereo files, correlation ran -0.30 to +1.00; a
     * cutoff of -0.5 corresponds to a 6 dB loss and never fires at all.
     */
    static DownmixPlan planDownmix(byte[] src, boolean bigEndian) {
        final int frames = src.length / 4;
        if (frames == 0) return new DownmixPlan(DownmixMode.AVERAGE, 1f);
        double leftEnergy = 0, rightEnergy = 0, crossEnergy = 0;
        int midPeak = 0;
        for (int frame = 0; frame < frames; frame++) {
            final int i = frame * 4;
            final int left = sample(src, i, bigEndian);
            final int right = sample(src, i + 2, bigEndian);
            final int mid = (left + right) >> 1;
            final int magnitude = mid < 0 ? -mid : mid;
            // The peak has to see every frame - it is a hard bound for the gain below, and a sampled maximum could
            // miss the one spike that matters and let the result clip.
            if (magnitude > midPeak) midPeak = magnitude;
            // The energies are statistical, so every 4th frame is ample and saves three quarters of the multiplies.
            if ((frame & 3) == 0) {
                leftEnergy += (double) left * left;
                rightEnergy += (double) right * right;
                crossEnergy += (double) left * right;
            }
        }
        if (leftEnergy == 0 && rightEnergy == 0) return new DownmixPlan(DownmixMode.AVERAGE, 1f);

        // A channel at least 30 dB below the other is effectively silent; do not halve the useful channel's level.
        final double silentRatio = 0.001;
        if (leftEnergy <= rightEnergy * silentRatio) return new DownmixPlan(DownmixMode.RIGHT, 1f);
        if (rightEnergy <= leftEnergy * silentRatio) return new DownmixPlan(DownmixMode.LEFT, 1f);

        final double correlation = crossEnergy / Math.sqrt(leftEnergy * rightEnergy);
        if (correlation < -0.9) {
            // Near-perfect inversion: the discarded channel is a negated copy, so nothing is lost by dropping it.
            return new DownmixPlan(leftEnergy >= rightEnergy ? DownmixMode.LEFT : DownmixMode.RIGHT, 1f);
        }

        final double midEnergy = (leftEnergy + rightEnergy + 2 * crossEnergy) / 4;
        if (midEnergy <= 0) return new DownmixPlan(DownmixMode.AVERAGE, 1f);
        // Aim for the louder channel's level - what the downmix would have been worth without cancellation.
        final double wanted = Math.sqrt(Math.max(leftEnergy, rightEnergy) / midEnergy);
        // Capped so it can never clip. Both terms are >= 1: mid can only be quieter than the louder channel, and
        // (left+right)/2 of two 16-bit samples cannot exceed 16-bit range.
        final float gain = (float) Math.min(wanted, midPeak > 0 ? 32767.0 / midPeak : wanted);
        return new DownmixPlan(DownmixMode.AVERAGE, gain);
    }

    /** How to fold the channels, and the gain to apply afterwards. */
    static final class DownmixPlan {

        final DownmixMode mode;
        final float gain;

        DownmixPlan(DownmixMode mode, float gain) {
            this.mode = mode;
            this.gain = gain;
        }
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

    private static void downmix(SoundBuffer buffer, AudioFormat format, DownmixPlan plan) {
        final byte[] src = buffer.audioData;
        final boolean bigEndian = format.isBigEndian();
        final DownmixMode mode = plan.mode;
        final float gain = plan.gain;
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
            int mono = mode == DownmixMode.LEFT ? left : mode == DownmixMode.RIGHT ? right : (left + right) >> 1;
            if (gain != 1f) {
                // The gain is peak-capped, so this clamp should never trigger; it is here so a rounding edge cannot
                // wrap a sample to the opposite sign.
                final int scaled = Math.round(mono * gain);
                mono = scaled > 32767 ? 32767 : scaled < -32768 ? -32768 : scaled;
            }
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
