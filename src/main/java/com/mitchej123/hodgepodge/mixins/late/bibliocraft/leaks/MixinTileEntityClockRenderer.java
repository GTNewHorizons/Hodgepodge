package com.mitchej123.hodgepodge.mixins.late.bibliocraft.leaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import jds.bibliocraft.rendering.TileEntityClockRenderer;
import jds.bibliocraft.tileentities.TileEntityClock;

@Mixin(TileEntityClockRenderer.class)
public class MixinTileEntityClockRenderer {

    @Shadow
    private TileEntityClock tile;

    @Inject(method = "renderTileEntityAt", at = @At("RETURN"))
    private void clearRef(CallbackInfo ci) {
        this.tile = null;
    }
}
