package com.mitchej123.hodgepodge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.util.LongHashMap;

import com.mitchej123.hodgepodge.util.SnapshotLongHashMap;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ClientTicker {

    public static final ClientTicker INSTANCE = new ClientTicker();

    private int ticks = 0;
    private int debugPieTextOffset = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ticks++;

            if (ticks % 5 == 0) {
                debugPieTextOffset++;
            }
        } else if (event.phase == TickEvent.Phase.END) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null) {
                final LongHashMap mapping = ((ChunkProviderClient) mc.theWorld.getChunkProvider()).chunkMapping;
                if (mapping instanceof SnapshotLongHashMap snap) {
                    snap.publishSnapshot();
                }
            }
        }
    }

    public int getTicks() {
        return ticks;
    }

    public int getDebugPieTextOffset() {
        return debugPieTextOffset;
    }
}
