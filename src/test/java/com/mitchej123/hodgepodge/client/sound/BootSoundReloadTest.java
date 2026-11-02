package com.mitchej123.hodgepodge.client.sound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;

class BootSoundReloadTest {

    private List<Class<?>> savedLibraries;
    private Class<?> savedOggCodec;

    @BeforeEach
    void saveConfig() {
        BootSoundReload.reset();
        final LinkedList<?> libraries = SoundSystemConfig.getLibraries();
        savedLibraries = new ArrayList<>();
        if (libraries != null) for (Object library : libraries) savedLibraries.add((Class<?>) library);
        final Object ogg = SoundSystemConfig.getCodec("ogg");
        savedOggCodec = ogg == null ? null : ogg.getClass();
    }

    @AfterEach
    void restoreConfig() throws Exception {
        final LinkedList<?> libraries = SoundSystemConfig.getLibraries();
        if (libraries != null) {
            for (Object library : new ArrayList<>(libraries)) SoundSystemConfig.removeLibrary((Class<?>) library);
        }
        for (Class<?> library : savedLibraries) SoundSystemConfig.addLibrary(library);
        if (savedOggCodec != null) SoundSystemConfig.setCodec("ogg", savedOggCodec);
        BootSoundReload.reset();
    }

    @Test
    void snapshotTracksConfigChanges() throws Exception {
        final List<Object> before = BootSoundReload.snapshotConfig();
        assertEquals(before, BootSoundReload.snapshotConfig());

        SoundSystemConfig.addLibrary(LibraryHodgepodgeOpenAL.class);
        assertNotEquals(before, BootSoundReload.snapshotConfig());
        SoundSystemConfig.removeLibrary(LibraryHodgepodgeOpenAL.class);
        assertEquals(before, BootSoundReload.snapshotConfig());

        final int normal = SoundSystemConfig.getNumberNormalChannels();
        try {
            SoundSystemConfig.setNumberNormalChannels(normal + 1);
            assertNotEquals(before, BootSoundReload.snapshotConfig());
        } finally {
            SoundSystemConfig.setNumberNormalChannels(normal);
        }

        SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
        final List<Object> jorbis = BootSoundReload.snapshotConfig();
        SoundSystemConfig.setCodec("ogg", CodecWav.class);
        assertNotEquals(jorbis, BootSoundReload.snapshotConfig());
    }

    @Test
    void keepsOnlyDuringBootRefresh() {
        BootSoundReload.onEngineStarting();
        BootSoundReload.onEngineConstructed(() -> null);
        assertFalse(BootSoundReload.shouldKeepEngine(true));
        BootSoundReload.beginBootRefresh();
        assertTrue(BootSoundReload.shouldKeepEngine(true));
        assertFalse(BootSoundReload.shouldKeepEngine(false));
        BootSoundReload.endBootRefresh();
        assertFalse(BootSoundReload.shouldKeepEngine(true));
    }

    @Test
    void restartReasonFollowsEngineState() {
        final String[] engineReason = new String[1];
        assertEquals("engine not loaded", BootSoundReload.restartReason(false));
        assertEquals("engine not tracked", BootSoundReload.restartReason(true));

        BootSoundReload.onEngineStarting();
        BootSoundReload.onEngineConstructed(() -> engineReason[0]);
        assertNull(BootSoundReload.restartReason(true));

        engineReason[0] = "sources already created";
        assertEquals("sources already created", BootSoundReload.restartReason(true));
        engineReason[0] = null;

        BootSoundReload.onSoundCreated();
        assertEquals("sounds already created", BootSoundReload.restartReason(true));
        BootSoundReload.onEngineStarting();
        assertNull(BootSoundReload.restartReason(true));

        final int normal = SoundSystemConfig.getNumberNormalChannels();
        try {
            SoundSystemConfig.setNumberNormalChannels(normal + 1);
            assertEquals("sound config changed since start", BootSoundReload.restartReason(true));
        } finally {
            SoundSystemConfig.setNumberNormalChannels(normal);
        }
        assertNull(BootSoundReload.restartReason(true));

        BootSoundReload.endBootRefresh();
        BootSoundReload.onEngineStarting();
        BootSoundReload.onEngineConstructed(() -> null);
        assertEquals("engine not tracked", BootSoundReload.restartReason(true));
    }
}
