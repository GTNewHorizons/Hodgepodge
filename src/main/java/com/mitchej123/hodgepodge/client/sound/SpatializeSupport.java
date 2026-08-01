package com.mitchej123.hodgepodge.client.sound;

import java.lang.reflect.Method;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.SoundConfig;

import cpw.mods.fml.common.Loader;
import paulscode.sound.libraries.ChannelLWJGLOpenAL;

/**
 * Forces OpenAL to spatialize stereo sources, so they can be positioned without being downmixed to mono first.
 * <p>
 * Needs AL_SOFT_source_spatialize, which arrived in OpenAL Soft 1.19 - long after the OpenAL LWJGL2 bundles - so this
 * is lwjgl3ify-only and stays off on the Java 8 build. Reflective for the same reason as {@link SoundDeviceTweaks}:
 * LWJGL2 and LWJGL3 share the {@code org.lwjgl.openal} package, so compiling against LWJGL3 would put two different
 * AL10 classes on the classpath.
 */
public final class SpatializeSupport {

    private SpatializeSupport() {}

    private static final int AL_SOURCE_SPATIALIZE_SOFT = 4628;
    private static final int AL_AUTO_SOFT = 2;
    private static final int AL_TRUE = 1;

    private static Method alSourcei;
    private static boolean resolved = false;
    private static boolean supported = false;

    /**
     * True when stereo sounds will actually be positioned, and therefore should <i>not</i> be downmixed. Read by
     * {@link DownmixingOggCodec}, so the two never both act on the same sound.
     */
    public static boolean active() {
        return SoundConfig.spatializeStereoSounds && resolve();
    }

    /** Sets the flag on the channel's OpenAL source. Channels are pooled, so this runs on every attach. */
    static void apply(ChannelLWJGLOpenAL channel, boolean positional) {
        if (!SoundConfig.spatializeStereoSounds || channel.ALSource == null || !resolve()) return;
        try {
            // AL_AUTO restores stock behaviour for non-positional sounds, which is what UI clicks want.
            alSourcei.invoke(
                    null,
                    channel.ALSource.get(0),
                    AL_SOURCE_SPATIALIZE_SOFT,
                    positional ? AL_TRUE : AL_AUTO_SOFT);
        } catch (Throwable t) {
            supported = false; // stop trying; the codec falls back to downmixing on the next decode
            Common.log.warn("Could not set source spatialization, falling back to downmixing", t);
        }
    }

    private static synchronized boolean resolve() {
        if (resolved) return supported;
        resolved = true;
        if (!Loader.isModLoaded("lwjgl3ify")) return false; // LWJGL2's OpenAL predates the extension
        try {
            final Class<?> al10 = Class.forName("org.lwjgl.openal.AL10");
            final boolean present = (Boolean) al10.getMethod("alIsExtensionPresent", CharSequence.class)
                    .invoke(null, "AL_SOFT_source_spatialize");
            if (!present) {
                Common.log.info("AL_SOFT_source_spatialize unavailable, stereo sounds will be downmixed instead");
                return false;
            }
            alSourcei = al10.getMethod("alSourcei", int.class, int.class, int.class);
            supported = true;
            Common.log.info("Using AL_SOFT_source_spatialize; stereo sounds keep their width");
        } catch (Throwable t) {
            Common.log.warn("Could not set up source spatialization, stereo sounds will be downmixed instead", t);
        }
        return supported;
    }
}
