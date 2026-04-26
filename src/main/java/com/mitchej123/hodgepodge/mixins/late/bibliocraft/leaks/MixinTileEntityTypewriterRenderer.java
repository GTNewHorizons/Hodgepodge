package com.mitchej123.hodgepodge.mixins.late.bibliocraft.leaks;


import jds.bibliocraft.rendering.TileEntityTypewriterRenderer;
import jds.bibliocraft.tileentities.TileEntityTypewriter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityTypewriterRenderer.class)
public class MixinTileEntityTypewriterRenderer {

    @Shadow
    private TileEntityTypewriter tile;

    @Inject(method = "renderTileEntityAt", at = @At("RETURN"))
    private void clearRef(CallbackInfo ci) {
        this.tile = null;
    }
}
