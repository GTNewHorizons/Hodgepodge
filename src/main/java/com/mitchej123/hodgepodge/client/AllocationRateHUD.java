package com.mitchej123.hodgepodge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class AllocationRateHUD {

    private final boolean isDebug;
    private long lastUpdateTime = System.nanoTime();
    private long lastFreeMemory = Runtime.getRuntime().freeMemory();
    private String cachedText = "";

    public AllocationRateHUD(boolean isDebug) {
        this.isDebug = isDebug;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderGameOverlayTextEvent(RenderGameOverlayEvent.Text event) {
        if (isDebug && Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            this.updateText();
            final int index = MathHelper.clamp_int(2, 0, event.right.size() - 1);
            event.right.add(index, cachedText);
        } else if (!isDebug && !Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            this.updateText();
            final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
            final int strWidth = fr.getStringWidth(cachedText);
            fr.drawStringWithShadow(cachedText, event.resolution.getScaledWidth() - strWidth - 2, 2, 0xFFFFFFFF);
        }
    }

    private void updateText() {
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
    }
}
