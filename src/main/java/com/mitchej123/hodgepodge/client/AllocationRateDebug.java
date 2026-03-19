package com.mitchej123.hodgepodge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class AllocationRateDebug {

    private long lastUpdateTime = System.nanoTime();
    private long lastFreeMemory = Runtime.getRuntime().freeMemory();
    private String cachedText = "";

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderGameOverlayTextEvent(RenderGameOverlayEvent.Text event) {
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            final long currentTime = System.nanoTime();
            if (currentTime - lastUpdateTime >= 1_000_000_000L) {
                final long currentFreeMemory = Runtime.getRuntime().freeMemory();
                final long memDiff = lastFreeMemory - currentFreeMemory;
                final double seconds = (currentTime - lastUpdateTime) / 1_000_000_000.0;
                final int allocationRate = MathHelper.floor_double(memDiff / seconds / (1024.0 * 1024.0));
                cachedText = String.format("Allocation rate: %s MB/s", allocationRate);
                lastFreeMemory = currentFreeMemory;
                lastUpdateTime = currentTime;
            }
            final int index = MathHelper.clamp_int(2, 0, event.right.size() - 1);
            event.right.add(index, cachedText);
        }
    }
}
