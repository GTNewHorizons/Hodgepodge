package com.mitchej123.hodgepodge.client.sound;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.SoundConfig;
import com.mitchej123.hodgepodge.config.SoundConfig.Tristate;

import cpw.mods.fml.common.Loader;

/**
 * OpenAL Soft device settings Minecraft never exposes: HRTF (binaural positioning on headphones) and the output limiter
 * (protects the final mix from out-of-range peaks).
 * <p>
 * Requires lwjgl3ify; LWJGL2's bundled OpenAL predates both, so this no-ops on the Java 8 build. Done by reflection
 * because LWJGL2 and LWJGL3 share the {@code org.lwjgl.openal} package, so compiling against LWJGL3 would put two
 * different ALC10 classes on the classpath.
 */
public final class SoundDeviceTweaks {

    private SoundDeviceTweaks() {}

    private static final int ALC_TRUE = 1;
    private static final int ALC_FALSE = 0;
    private static final int ALC_FREQUENCY = 4103;
    /** From the SOFT extension classes; hardcoded since they are not on the compile classpath. */
    private static final int ALC_HRTF_SOFT = 6546;
    private static final int ALC_HRTF_STATUS_SOFT = 6547;
    private static final int ALC_OUTPUT_LIMITER_SOFT = 6554;
    private static final int ALC_OUTPUT_MODE_SOFT = 6572;
    private static final int ALC_STEREO_HRTF_SOFT = 6578;
    private static final String[] STATUS = { "disabled", "enabled", "denied", "required", "headphones detected",
            "unsupported output format" };

    /** Bumped when a new Library is constructed and consumed by the client thread. The only cross-thread state here. */
    private static final AtomicInteger reloads = new AtomicInteger();
    private static int seenReload = 0;

    private static Tristate appliedHrtf = Tristate.DEFAULT;
    private static Tristate appliedLimiter = Tristate.DEFAULT;
    private static long configuredDevice = 0L;
    private static boolean unavailable = false;

    /**
     * Called when a new Library is built. Minecraft normally does that on its Sound Library Loader thread, but a device
     * reload may use another thread. The settings live on the AL device, which the reload replaced.
     * <p>
     * It only bumps a counter, so that every field below stays written by the client thread alone. Sharing them across
     * threads would need more synchronization, while {@link #tick()} can sit inside {@code alcResetDeviceSOFT} for
     * milliseconds. This keeps the library-construction callback short and non-blocking.
     */
    static void invalidate() {
        reloads.incrementAndGet();
    }

    /** Picks up a pending invalidate. Client thread only, so the applied state has a single writer. */
    private static void consumeInvalidate() {
        final int current = reloads.get();
        if (current == seenReload) return;
        seenReload = current;
        appliedHrtf = Tristate.DEFAULT;
        appliedLimiter = Tristate.DEFAULT;
        configuredDevice = 0L;
        // A different device may support what the previous one did not, so the latch clears too.
        unavailable = false;
    }

    /** Polled from the client tick. Sole writer of the applied state, so no locking is needed here. */
    public static void tick() {
        consumeInvalidate();
        if (unavailable || (SoundConfig.hrtf == Tristate.DEFAULT && SoundConfig.outputLimiter == Tristate.DEFAULT)) {
            return;
        }
        if (!Loader.isModLoaded("lwjgl3ify")) {
            unavailable = true; // LWJGL2's OpenAL predates these extensions
            return;
        }
        try {
            final Class<?> al = Class.forName("org.lwjglx.openal.AL");
            if (!(Boolean) al.getMethod("isCreated").invoke(null)) return; // device not up yet, retry next poll
            final Object alcDevice = al.getMethod("getDevice").invoke(null);
            if (alcDevice == null) return;
            final long device = alcDevice.getClass().getField("device").getLong(alcDevice);

            // Backstop only: a recreated device can reuse the same native pointer, so this cannot be relied on to
            // spot a reload. LibraryHodgepodgeOpenAL's constructor calls invalidate(), which is the real signal.
            if (device != configuredDevice) {
                appliedHrtf = Tristate.DEFAULT;
                appliedLimiter = Tristate.DEFAULT;
                configuredDevice = device;
            }
            if (SoundConfig.hrtf != appliedHrtf || SoundConfig.outputLimiter != appliedLimiter) {
                applySettings(Class.forName("org.lwjgl.openal.ALC10"), device);
            }
        } catch (Throwable t) {
            unavailable = true;
            Common.log.warn("Could not configure the OpenAL device, leaving audio unchanged", t);
        }
    }

    private static void applySettings(Class<?> alc10, long device) throws Exception {
        final Method isPresent = alc10.getMethod("alcIsExtensionPresent", long.class, CharSequence.class);
        if (!(Boolean) isPresent.invoke(null, device, "ALC_SOFT_HRTF")) {
            unavailable = true;
            Common.log.info("This OpenAL device supports neither tweak, leaving audio unchanged");
            return;
        }

        final Method getInt = alc10.getMethod("alcGetIntegerv", long.class, int.class, int[].class);
        final int[] out = new int[1];
        getInt.invoke(null, device, ALC_FREQUENCY, out);
        final int rate = out[0];
        final boolean outputMode = (Boolean) isPresent.invoke(null, device, "ALC_SOFT_output_mode");
        final boolean limiterExt = (Boolean) isPresent.invoke(null, device, "ALC_SOFT_output_limiter");

        final boolean ok = (Boolean) Class.forName("org.lwjgl.openal.SOFTHRTF")
                .getMethod("alcResetDeviceSOFT", long.class, int[].class)
                .invoke(null, device, attribs(outputMode, limiterExt));
        if (!ok) {
            unavailable = true;
            Common.log.warn("alcResetDeviceSOFT failed, leaving audio unchanged");
            return;
        }
        appliedHrtf = SoundConfig.hrtf;
        appliedLimiter = SoundConfig.outputLimiter;

        getInt.invoke(null, device, ALC_HRTF_STATUS_SOFT, out);
        final int s = out[0];
        Common.log.info(
                "OpenAL device {} Hz (output-mode ext {}): HRTF {} -> {}, limiter {}",
                rate,
                outputMode ? "yes" : "no",
                appliedHrtf,
                s >= 0 && s < STATUS.length ? STATUS[s] : "unknown (" + s + ")",
                appliedLimiter == Tristate.DEFAULT || limiterExt ? appliedLimiter
                        : appliedLimiter + " (unsupported by this device)");
    }

    /**
     * OpenAL Soft re-reads its defaults on every reset and applies this list on top, so anything left out reverts. Both
     * settings therefore go in one call rather than two.
     * <p>
     * ALC_HRTF_SOFT alone cannot override a device configured for surround, and HRTF is only ever applied to stereo
     * output ({@code panning.cpp} reports unsupported-format for anything else). Prefer ALC_SOFT_output_mode, which
     * forces stereo+HRTF outright.
     */
    private static int[] attribs(boolean outputMode, boolean limiterExt) {
        final boolean hrtfOn = SoundConfig.hrtf == Tristate.ON;
        final boolean setHrtf = SoundConfig.hrtf != Tristate.DEFAULT;
        final boolean setLimiter = limiterExt && SoundConfig.outputLimiter != Tristate.DEFAULT;

        final int[] a = new int[(setHrtf ? 2 : 0) + (setLimiter ? 2 : 0) + 1];
        int i = 0;
        if (setHrtf) {
            if (hrtfOn && outputMode) {
                // Forces stereo output as well, which is the only way to get HRTF on a device set to surround.
                a[i++] = ALC_OUTPUT_MODE_SOFT;
                a[i++] = ALC_STEREO_HRTF_SOFT;
            } else {
                // OFF must go through ALC_HRTF_SOFT. ALC_ANY_SOFT means "no override" (alc.cpp:1437), so sending it
                // would just hand the decision back to the driver config instead of forcing HRTF off.
                a[i++] = ALC_HRTF_SOFT;
                a[i++] = hrtfOn ? ALC_TRUE : ALC_FALSE;
            }
        }
        if (setLimiter) {
            a[i++] = ALC_OUTPUT_LIMITER_SOFT;
            a[i++] = SoundConfig.outputLimiter == Tristate.ON ? ALC_TRUE : ALC_FALSE;
        }
        a[i] = 0;
        return a;
    }
}
