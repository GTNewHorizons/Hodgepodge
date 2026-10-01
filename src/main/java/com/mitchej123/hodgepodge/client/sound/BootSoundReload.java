package com.mitchej123.hodgepodge.client.sound;

import java.util.Arrays;
import java.util.List;

import com.mitchej123.hodgepodge.Common;

import paulscode.sound.Library;
import paulscode.sound.SoundSystemConfig;

/** Keeps the engine through the post-load resource reload when a restart would rebuild it identically. */
public final class BootSoundReload {

    public interface Engine {

        /** Null if a restart would rebuild an identical engine, else why not. */
        String hodgepodge$restartReason();
    }

    private static volatile boolean bootDone;
    private static volatile boolean inBootRefresh;
    private static volatile Engine engine;
    private static volatile boolean soundCreated;
    private static volatile List<Object> configAtStart;

    private BootSoundReload() {}

    public static void beginBootRefresh() {
        inBootRefresh = true;
    }

    public static void endBootRefresh() {
        inBootRefresh = false;
        bootDone = true;
        engine = null;
        configAtStart = null;
    }

    public static void onEngineStarting() {
        if (bootDone) return;
        configAtStart = snapshotConfig();
        soundCreated = false;
    }

    public static void onEngineConstructed(Engine constructed) {
        if (bootDone) return;
        engine = constructed;
    }

    public static void onSoundCreated() {
        if (bootDone) return;
        soundCreated = true;
    }

    public static boolean shouldKeepEngine(boolean loaded) {
        if (!inBootRefresh) return false;
        final String reason = restartReason(loaded);
        if (reason == null) {
            Common.log.info("Kept the sound engine across the post-load resource reload");
            return true;
        }
        Common.log.info("Restarting the sound engine for the post-load resource reload: {}", reason);
        return false;
    }

    private static String restartReason(boolean loaded) {
        final Engine current = engine;
        if (!loaded) return "engine not loaded";
        if (current == null) return "engine not tracked";
        if (soundCreated) return "sounds already created";
        if (!snapshotConfig().equals(configAtStart)) return "sound config changed since start";
        return current.hodgepodge$restartReason();
    }

    public static String libraryReason(Class<?> library) {
        if (library == null) return "sound library not initialized";
        if (library == Library.class) return "sound library fell back to silent mode";
        final List<?> configured = SoundSystemConfig.getLibraries();
        if (configured == null || !configured.contains(library)) return "sound library not in the configured list";
        return null;
    }

    static List<Object> snapshotConfig() {
        final List<?> libraries = SoundSystemConfig.getLibraries();
        return Arrays.asList(
                libraries == null ? "[]" : libraries.toString(),
                codecClass("ogg"),
                codecClass("wav"),
                SoundSystemConfig.getNumberNormalChannels(),
                SoundSystemConfig.getNumberStreamingChannels(),
                SoundSystemConfig.getDefaultAttenuation(),
                SoundSystemConfig.getDefaultRolloff(),
                SoundSystemConfig.getDopplerFactor(),
                SoundSystemConfig.getDopplerVelocity(),
                SoundSystemConfig.getDefaultFadeDistance(),
                SoundSystemConfig.getStreamingBufferSize(),
                SoundSystemConfig.getNumberStreamingBuffers(),
                SoundSystemConfig.getStreamQueueFormatsMatch(),
                SoundSystemConfig.getMaxFileSize(),
                SoundSystemConfig.getFileChunkSize(),
                SoundSystemConfig.getOverrideMIDISynthesizer());
    }

    private static Class<?> codecClass(String extension) {
        final Object codec = SoundSystemConfig.getCodec(extension);
        return codec == null ? null : codec.getClass();
    }
}
