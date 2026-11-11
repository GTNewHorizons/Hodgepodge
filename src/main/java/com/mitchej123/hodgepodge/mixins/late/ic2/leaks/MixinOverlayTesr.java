package com.mitchej123.hodgepodge.mixins.late.ic2.leaks;

import net.minecraft.client.renderer.RenderBlocks;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ic2.core.block.OverlayTesr;

@Mixin(OverlayTesr.class)
public class MixinOverlayTesr {

    @Final
    @Shadow(remap = false)
    private RenderBlocks renderBlocks;

    @Inject(
            method = "renderTileEntityAt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()I"))
    private void clearRef(CallbackInfo ci) {
        this.renderBlocks.blockAccess = null;
    }
}
