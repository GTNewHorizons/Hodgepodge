package com.mitchej123.hodgepodge.mixins.late.bibliocraft.leaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import jds.bibliocraft.rendering.TileEntityTableRenderer;
import jds.bibliocraft.tileentities.TileEntityTable;

@Mixin(TileEntityTableRenderer.class)
public class MixinTileEntityTableRenderer {

    @Shadow(remap = false)
    private TileEntityTable tableTile;

    @Inject(method = "renderTileEntityAt", at = @At("RETURN"))
    private void clearRef(CallbackInfo ci) {
        this.tableTile = null;
    }
}
