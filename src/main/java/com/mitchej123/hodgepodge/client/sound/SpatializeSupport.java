package com.mitchej123.hodgepodge.client.sound;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.Compat;
import com.mitchej123.hodgepodge.config.SoundConfig;

/**
 * Positions world sounds and keeps non-positional sounds out of OpenAL's HRTF virtualization where supported.
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
    private static final int AL_PANNING_ENABLED_SOFT = 6636;
    private static final int AL_PAN_SOFT = 6637;
    private static final int AL_VERSION = 45058;
    private static final int AL_AUTO_SOFT = 2;
    private static final int AL_TRUE = 1;
    private static final int AL_FALSE = 0;

    private static Method alSourcei, alSourcef;
    private static boolean resolved = false;
    private static boolean spatializeSupported = false;
    private static boolean directChannelsSupported = false;
    private static boolean panningSupported = false;

    /** Extension support belongs to the context, which a sound reload replaces. */
    static synchronized void invalidate() {
        resolved = false;
        spatializeSupported = false;
        directChannelsSupported = false;
        panningSupported = false;
        alSourcei = null;
        alSourcef = null;
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
     * Writes routing properties on every attach because channels are pooled. Non-positional sounds must use FALSE, not
     * AUTO (which still positions mono), plus direct routing to bypass HRTF's virtual speakers. Experimental panning
     * lets mono use that direct stereo path without copying its PCM. Centered panning preserves stereo and plays mono
     * equally in both ears at the requested source gain. World sounds always clear panning and direct routing,
     * including when stereo spatialization is disabled. UI/music routing is independent of that preference.
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
            if (panningSupported) {
                alSourcei.invoke(null, alSource, AL_PANNING_ENABLED_SOFT, positional ? AL_FALSE : AL_TRUE);
                alSourcef.invoke(null, alSource, AL_PAN_SOFT, 0.0f);
            }
        } catch (Throwable t) {
            spatializeSupported = false;
            directChannelsSupported = false;
            panningSupported = false;
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
            alSourcef = al10.getMethod("alSourcef", int.class, int.class, float.class);
            spatializeSupported = (Boolean) isPresent.invoke(null, "AL_SOFT_source_spatialize");
            directChannelsSupported = (Boolean) isPresent.invoke(null, "AL_SOFT_direct_channels");
            if (directChannelsSupported && (Boolean) isPresent.invoke(null, "AL_SOFTX_source_panning")) {
                // Before 1.25, even writing the same flag to a paused source raises AL_INVALID_OPERATION.
                // Restrict this experimental path to versions that allow our normal playback/resume hook.
                final String version = (String) al10.getMethod("alGetString", int.class).invoke(null, AL_VERSION);
                final Matcher match = Pattern.compile(".*\\bALSOFT (\\d+)\\.(\\d+).*").matcher(version);
                panningSupported = match.matches() && (Integer.parseInt(match.group(1)) > 1
                        || Integer.parseInt(match.group(1)) == 1 && Integer.parseInt(match.group(2)) >= 25);
            }
            Common.log.info(
                    "OpenAL source routing: stereo spatialization {}, direct UI/music channels {}, experimental mono panning {}",
                    spatializeSupported,
                    directChannelsSupported,
                    panningSupported);
        } catch (Throwable t) {
            spatializeSupported = false;
            directChannelsSupported = false;
            panningSupported = false;
            Common.log.warn("Could not set up source routing, leaving OpenAL defaults", t);
        }
    }
}
