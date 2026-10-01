package com.mitchej123.hodgepodge.client.sound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
}
