package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal_FixWordLeak {

    @Shadow
    public List tileEntities;

    @Shadow
    private WorldRenderer[] worldRenderers;

    @Shadow
    private WorldRenderer[] sortedWorldRenderers;

    @Inject(
            method = "setWorldAndLoadRenderers(Lnet/minecraft/client/multiplayer/WorldClient;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;theWorld:Lnet/minecraft/client/multiplayer/WorldClient;",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER))
    public void hodgepodge$clearWorldRenderersAndTileEntities(WorldClient worldClient, CallbackInfo ci) {
        if (worldClient == null) {
            tileEntities.clear();

            if (worldRenderers != null) {
                for (WorldRenderer worldRenderer : worldRenderers) {
                    worldRenderer.stopRendering();
                }
            }

            worldRenderers = null;
            sortedWorldRenderers = null;
        }
    }
}
