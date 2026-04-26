package com.mitchej123.hodgepodge.mixins.late.bibliocraft.leaks;

import jds.bibliocraft.rendering.TileEntityPrintPressRenderer;
import jds.bibliocraft.tileentities.TileEntityPrintPress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityPrintPressRenderer.class)
public class MixinTileEntityPrintPressRenderer {

    @Shadow
    private TileEntityPrintPress printTile;

    @Inject(method = "renderTileEntityAt", at = @At("RETURN"))
    private void clearRef(CallbackInfo ci) {
        this.printTile = null;
    }
}
