package com.mitchej123.hodgepodge.mixins.late.bibliocraft.leaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import jds.bibliocraft.rendering.TileEntityLanternRenderer;
import jds.bibliocraft.tileentities.TileEntityLantern;

@Mixin(TileEntityLanternRenderer.class)
public class MixinTileEntityLanternRenderer {

    @Shadow(remap = false)
    private TileEntityLantern lanternTile;

    @Inject(method = "renderTileEntityAt", at = @At("RETURN"))
    private void clearRef(CallbackInfo ci) {
        this.lanternTile = null;
    }
}
