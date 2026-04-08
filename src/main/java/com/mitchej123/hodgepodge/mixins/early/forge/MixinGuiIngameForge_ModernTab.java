package com.mitchej123.hodgepodge.mixins.early.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.client.tab.ModernTabRenderer;

/**
 * Replaces the vanilla player list rendering in GuiIngameForge with the modern tab renderer while preserving the
 * Pre/Post event lifecycle so other mods can still interact with the PLAYER_LIST overlay event.
 */
@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge_ModernTab extends GuiIngame {

    @Shadow(remap = false)
    private ScaledResolution res;

    @Shadow(remap = false)
    private RenderGameOverlayEvent eventParent;

    private MixinGuiIngameForge_ModernTab(Minecraft mc) {
        super(mc);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "renderPlayerList", at = @At("HEAD"), cancellable = true, remap = false)
    private void hodgepodge$modernTab(int width, int height, CallbackInfo ci) {
        ci.cancel();

        ScoreObjective scoreobjective = mc.theWorld.getScoreboard().func_96539_a(0);
        NetHandlerPlayClient handler = mc.thePlayer.sendQueue;

        if (mc.gameSettings.keyBindPlayerList.getIsKeyPressed()
                && (!mc.isIntegratedServerRunning() || handler.playerInfoList.size() > 1 || scoreobjective != null)) {
            if (MinecraftForge.EVENT_BUS
                    .post(new RenderGameOverlayEvent.Pre(eventParent, RenderGameOverlayEvent.ElementType.PLAYER_LIST)))
                return;
            mc.mcProfiler.startSection("playerList");
            ModernTabRenderer.INSTANCE.render(res);
            MinecraftForge.EVENT_BUS
                    .post(new RenderGameOverlayEvent.Post(eventParent, RenderGameOverlayEvent.ElementType.PLAYER_LIST));
        }
    }
}
