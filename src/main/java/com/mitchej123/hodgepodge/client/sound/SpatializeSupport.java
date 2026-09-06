package com.mitchej123.hodgepodge.client.sound;

import java.lang.reflect.Method;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.Compat;
import com.mitchej123.hodgepodge.config.SoundConfig;

/**
 * Positions world sounds and keeps non-positional stereo sounds out of OpenAL's HRTF virtualization.
 * <p>
 * Needs AL_SOFT_source_spatialize, which arrived in OpenAL Soft 1.19, long after the OpenAL LWJGL2 bundles. This is
 * therefore lwjgl3ify-only and stays off on the Java 8 build. Reflective for the same reason as
 * {@link SoundDeviceTweaks}: LWJGL2 and LWJGL3 share the {@code org.lwjgl.openal} package, so compiling against LWJGL3
 * would put two different AL10 classes on the classpath.
 */
public final class SpatializeSupport {

    private SpatializeSupport() {}

    private static final int AL_SOURCE_SPATIALIZE_SOFT = 4628;
    private static final int AL_DIRECT_CHANNELS_SOFT = 4147;
    private static final int AL_AUTO_SOFT = 2;
    private static final int AL_TRUE = 1;
    private static final int AL_FALSE = 0;

    private static Method alSourcei;
    private static boolean resolved = false;
    private static boolean spatializeSupported = false;
    private static boolean directChannelsSupported = false;

    /** Extension support belongs to the context, which a sound reload replaces. */
    static synchronized void invalidate() {
        resolved = false;
        spatializeSupported = false;
        directChannelsSupported = false;
        alSourcei = null;
    }

    /**
     * True when the codec should keep buffers stereo for positional playback.
     */
    public static boolean active() {
        if (!SoundConfig.spatializeStereoSounds) return false;
        resolve();
        return spatializeSupported;
    }

    /**
     * Writes both properties on every attach because channels are pooled. Non-positional sounds must use FALSE, not
     * AUTO (which still positions mono), plus direct routing to bypass HRTF's virtual speakers for stereo. Direct
     * routing does not bypass HRTF for mono buffers. World sounds always clear it, including when stereo spatialization
     * is disabled. UI/music routing is independent of that preference.
     */
    static void apply(int alSource, boolean positional) {
        resolve();
        try {
            if (spatializeSupported) {
                final int spatialize = !positional ? AL_FALSE
                        : SoundConfig.spatializeStereoSounds ? AL_TRUE : AL_AUTO_SOFT;
                alSourcei.invoke(null, alSource, AL_SOURCE_SPATIALIZE_SOFT, spatialize);
            }
            if (directChannelsSupported) {
                alSourcei.invoke(null, alSource, AL_DIRECT_CHANNELS_SOFT, positional ? AL_FALSE : AL_TRUE);
            }
        } catch (Throwable t) {
            spatializeSupported = false;
            directChannelsSupported = false;
            Common.log.warn("Could not configure source routing, disabling sound routing enhancements", t);
        }
    }

    private static synchronized void resolve() {
        if (resolved) return;
        resolved = true;
        if (!Compat.isLwjgl3ifyPresent()) return; // LWJGL2's OpenAL predates the extensions
        try {
            final Class<?> al10 = Class.forName("org.lwjgl.openal.AL10");
            final Method isPresent = al10.getMethod("alIsExtensionPresent", CharSequence.class);
            alSourcei = al10.getMethod("alSourcei", int.class, int.class, int.class);
            spatializeSupported = (Boolean) isPresent.invoke(null, "AL_SOFT_source_spatialize");
            directChannelsSupported = (Boolean) isPresent.invoke(null, "AL_SOFT_direct_channels");
            Common.log.info(
                    "OpenAL source routing: stereo spatialization {}, direct UI/music channels {}",
                    spatializeSupported,
                    directChannelsSupported);
        } catch (Throwable t) {
            spatializeSupported = false;
            directChannelsSupported = false;
            Common.log.warn("Could not set up source routing, leaving OpenAL defaults", t);
        }
    }
}
