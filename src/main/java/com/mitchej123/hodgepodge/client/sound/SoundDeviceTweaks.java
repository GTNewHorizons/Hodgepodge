package com.mitchej123.hodgepodge.client.sound;

import java.lang.reflect.Method;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.SoundConfig;
import com.mitchej123.hodgepodge.config.SoundConfig.Tristate;

import cpw.mods.fml.common.Loader;

/**
 * OpenAL Soft device settings Minecraft never exposes: HRTF (binaural positioning on headphones) and the output limiter
 * (stops the mix clipping when a lot of sources play at once).
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
    private static final int ALC_ANY_SOFT = 6573;
    private static final int ALC_STEREO_HRTF_SOFT = 6578;
    private static final String[] STATUS = { "disabled", "enabled", "denied", "required", "headphones detected",
            "unsupported output format" };

    private static Tristate appliedHrtf = Tristate.DEFAULT;
    private static Tristate appliedLimiter = Tristate.DEFAULT;
    private static long configuredDevice = 0L;
    private static boolean unavailable = false;

    /** Polled from the client tick. */
    public static void tick() {
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

            // Reloading the sound system destroys the device and opens a new one, taking our settings with it.
            // ArchaicFix does exactly that whenever the output device changes.
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

        final boolean ok = (Boolean) Class.forName("org.lwjgl.openal.SOFTHRTF")
                .getMethod("alcResetDeviceSOFT", long.class, int[].class).invoke(null, device, attribs(outputMode));
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
                appliedLimiter);
    }

    /**
     * OpenAL Soft re-reads its defaults on every reset and applies this list on top, so anything left out reverts. Both
     * settings therefore go in one call rather than two.
     * <p>
     * ALC_HRTF_SOFT alone cannot override a device configured for surround, and HRTF is only ever applied to stereo
     * output ({@code panning.cpp} reports unsupported-format for anything else) - so prefer ALC_SOFT_output_mode, which
     * forces stereo+HRTF outright.
     */
    private static int[] attribs(boolean outputMode) {
        final boolean hrtfOn = SoundConfig.hrtf == Tristate.ON;
        final boolean setHrtf = SoundConfig.hrtf != Tristate.DEFAULT;
        final boolean setLimiter = SoundConfig.outputLimiter != Tristate.DEFAULT;

        final int[] a = new int[(setHrtf ? 2 : 0) + (setLimiter ? 2 : 0) + 1];
        int i = 0;
        if (setHrtf) {
            if (outputMode) {
                a[i++] = ALC_OUTPUT_MODE_SOFT;
                a[i++] = hrtfOn ? ALC_STEREO_HRTF_SOFT : ALC_ANY_SOFT;
            } else {
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
