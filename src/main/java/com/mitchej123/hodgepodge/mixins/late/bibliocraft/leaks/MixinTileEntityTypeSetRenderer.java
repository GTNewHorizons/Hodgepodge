package com.mitchej123.hodgepodge.mixins.late.bibliocraft.leaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import jds.bibliocraft.rendering.TileEntityTypeSetRenderer;
import jds.bibliocraft.tileentities.TileEntityTypeMachine;

@Mixin(TileEntityTypeSetRenderer.class)
public class MixinTileEntityTypeSetRenderer {

    @Shadow
    private TileEntityTypeMachine typeTile;

    @Inject(method = "renderTileEntityAt", at = @At("RETURN"))
    private void clearRef(CallbackInfo ci) {
        this.typeTile = null;
    }
}
