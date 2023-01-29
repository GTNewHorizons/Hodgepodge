package com.mitchej123.hodgepodge.client;

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
        }
    }

    public int getTicks() {
        return ticks;
    }

    public int getDebugPieTextOffset() {
        return debugPieTextOffset;
    }
}
