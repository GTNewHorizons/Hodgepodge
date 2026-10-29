package com.mitchej123.hodgepodge.client;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.management.ManagementFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class AllocationRateHUD {

    /** com.sun.management.ThreadMXBean#getTotalThreadAllocatedBytes (Java 14+), or null if unavailable */
    private static final MethodHandle TOTAL_ALLOCATED_BYTES = findTotalAllocatedBytes();

    private long lastUpdateTime = System.nanoTime();
    private long lastBytes = readAllocatedBytes();
    private String cachedText = "";

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderGameOverlayTextEvent(RenderGameOverlayEvent.Text event) {
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            final Minecraft mc = Minecraft.getMinecraft();
            final FontRenderer fr = mc.fontRenderer;
            final String text = this.updateText();
            final int strWidth = fr.getStringWidth(text);
            fr.drawStringWithShadow(text, event.resolution.getScaledWidth() - strWidth - 2, 2, 0xFFFFFFFF);
            fr.drawStringWithShadow(String.format("FPS: %d", mc.debugFPS), 2, 2, 0xFFFFFFFF);
        }
    }

    public String updateText() {
        final long currentTime = System.nanoTime();
        if (currentTime - lastUpdateTime >= 1_000_000_000L) {
            final long currentBytes = readAllocatedBytes();
            final long memDiff = currentBytes - lastBytes;
            final double seconds = (currentTime - lastUpdateTime) / 1_000_000_000.0;
            final int allocationRate = MathHelper.floor_double(memDiff / seconds / (1024.0 * 1024.0));
            cachedText = String.format("Allocation rate: %s MB/s", allocationRate);
            lastBytes = currentBytes;
            lastUpdateTime = currentTime;
        }
        return cachedText;
    }

    /**
     * Bytes allocated so far. Java 14+ has an exact counter; on older versions the negated free heap is used, which
     * only follows allocation between GCs (a GC frees memory and makes the difference negative).
     */
    private static long readAllocatedBytes() {
        if (TOTAL_ALLOCATED_BYTES != null) {
            try {
                return (long) TOTAL_ALLOCATED_BYTES.invokeExact();
            } catch (Throwable ignored) {}
        }
        return -Runtime.getRuntime().freeMemory();
    }

    private static MethodHandle findTotalAllocatedBytes() {
        try {
            final Object bean = ManagementFactory.getThreadMXBean();
            final Class<?> sunBean = Class.forName("com.sun.management.ThreadMXBean");
            if (!sunBean.isInstance(bean)) {
                return null;
            }
            final MethodHandle handle = MethodHandles.publicLookup()
                    .findVirtual(sunBean, "getTotalThreadAllocatedBytes", MethodType.methodType(long.class))
                    .bindTo(bean);
            // Returns -1 when allocation measurement is unsupported or disabled
            if ((long) handle.invokeExact() < 0) {
                return null;
            }
            return handle;
        } catch (Throwable t) {
            // Java 8-13 or a JVM without com.sun.management
            return null;
        }
    }
}
