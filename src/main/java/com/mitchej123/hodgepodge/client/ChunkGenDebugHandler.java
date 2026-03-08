package com.mitchej123.hodgepodge.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import com.mitchej123.hodgepodge.mixins.hooks.ChunkGenScheduler;
import com.mitchej123.hodgepodge.mixins.hooks.ChunkGenStats;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ChunkGenDebugHandler {

    public static final ChunkGenDebugHandler INSTANCE = new ChunkGenDebugHandler();

    private ChunkGenDebugHandler() {}

    @SubscribeEvent
    public void onRenderGameOverlayText(RenderGameOverlayEvent.Text event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (!mc.gameSettings.showDebugInfo) return;
        if (mc.thePlayer == null) return;

        final ChunkGenScheduler scheduler = ChunkGenScheduler.getForDimension(mc.thePlayer.dimension);
        if (scheduler == null) return;
        final ChunkGenStats s = scheduler.getStats();

        event.left.add("");
        event.left.add(
                String.format(
                        "[ChunkGen] used: %5.1f/%5.1fms  avg: ter %6.1fms pop %6.1fms",
                        s.getSnapBudgetUsedMs(),
                        s.getSnapBudgetAvailMs(),
                        s.getTerrainGenEmaMs(),
                        s.getPopulateEmaMs()));
        event.left.add(
                String.format(
                        "[ChunkGen] genQ: %4d  popQ: %4d  pipe: %4d  gen: %2d  pop: %2d",
                        s.getSnapGenQueue(),
                        s.getSnapPopQueue(),
                        s.getSnapPipelineBacklog(),
                        s.getSnapTerrainGenCount(),
                        s.getSnapPopCount()));
        event.left.add(
                String.format(
                        "[ChunkGen] blocked: %4d  stuck: %4d  fills: %4d  debt: %7.1fms",
                        s.getSnapBlockedLoads(),
                        s.getSnapStuckCount(),
                        s.getSnapNeighborFills(),
                        s.getDebtMs()));
    }
}
