package com.mitchej123.hodgepodge.client.sound;

import java.util.ArrayList;
import java.util.List;

import com.mitchej123.hodgepodge.Common;

import paulscode.sound.SoundSystemConfig;

/** Keeps the engine through the post-load resource reload when a restart would rebuild it identically. */
public final class BootSoundReload {

    public interface Engine {

        /** Null if a restart would rebuild an identical engine, else why not. */
        String hodgepodge$restartReason();
    }

    private static volatile boolean bootDone;
    private static boolean inBootRefresh;
    private static volatile Engine engine;
    private static volatile boolean soundCreated;
    private static volatile List<Object> configAtStart;

    private BootSoundReload() {}

    static void reset() {
        bootDone = false;
        inBootRefresh = false;
        engine = null;
        soundCreated = false;
        configAtStart = null;
    }

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

    static String restartReason(boolean loaded) {
        final Engine current = engine;
        if (!loaded) return "engine not loaded";
        if (current == null) return "engine not tracked";
        if (soundCreated) return "sounds already created";
        if (!snapshotConfig().equals(configAtStart)) return "sound config changed since start";
        return current.hodgepodge$restartReason();
    }

    // Libraries and codecs are what other mods swap; the rest is SoundSystemSettings.
    // Never master gain or logger: the engine writes those itself.
    static List<Object> snapshotConfig() {
        final List<Object> out = new ArrayList<>();
        final List<?> libraries = SoundSystemConfig.getLibraries();
        out.add(libraries == null ? "[]" : libraries.toString());
        out.add(codecClass("ogg"));
        out.add(codecClass("wav"));
        SoundSystemSettings.snapshot(out);
        return out;
    }

    private static Class<?> codecClass(String extension) {
        final Object codec = SoundSystemConfig.getCodec(extension);
        return codec == null ? null : codec.getClass();
    }
}
