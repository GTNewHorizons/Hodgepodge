package com.mitchej123.hodgepodge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class AllocationRateHUD {

    private long lastUpdateTime = System.nanoTime();
    private long lastFreeMemory = Runtime.getRuntime().freeMemory();
    private String cachedText = "";

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderGameOverlayTextEvent(RenderGameOverlayEvent.Text event) {
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            final String text = this.updateText();
            final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
            final int strWidth = fr.getStringWidth(text);
            fr.drawStringWithShadow(text, event.resolution.getScaledWidth() - strWidth - 2, 2, 0xFFFFFFFF);
        }
    }

    public String updateText() {
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
        return cachedText;
    }
}
