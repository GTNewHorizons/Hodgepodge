package com.mitchej123.hodgepodge.client.sound;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ChannelFloorTest {

    @Test
    void paulscodeDefaultIsNotARequest() {
        final ChannelFloor floor = new ChannelFloor(28);
        assertEquals(16, floor.resolve(16, 28));
        assertEquals(64, floor.resolve(64, 16));
    }

    @Test
    void earlierRequestIsNeverLowered() {
        final ChannelFloor floor = new ChannelFloor(28);
        assertEquals(128, floor.resolve(64, 128));
        assertEquals(256, floor.resolve(256, 128));
        assertEquals(128, floor.resolve(32, 256));
    }

    @Test
    void lowerRequestLeavesOursInPlace() {
        final ChannelFloor floor = new ChannelFloor(28);
        assertEquals(64, floor.resolve(64, 40));
        assertEquals(40, floor.resolve(8, 64));
    }

    @Test
    void laterLowerWriteDoesNotLowerOurs() {
        final ChannelFloor floor = new ChannelFloor(28);
        assertEquals(64, floor.resolve(64, 28));
        assertEquals(64, floor.resolve(64, 32));
    }

    @Test
    void laterExternalWriteReplacesTheRequest() {
        final ChannelFloor floor = new ChannelFloor(28);
        assertEquals(64, floor.resolve(64, 28));
        assertEquals(512, floor.resolve(64, 512));
        assertEquals(64, floor.resolve(64, 28));
    }
}
