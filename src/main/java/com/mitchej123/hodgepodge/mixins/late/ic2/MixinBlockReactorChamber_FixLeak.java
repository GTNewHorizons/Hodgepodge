package com.mitchej123.hodgepodge.mixins.late.ic2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ic2.core.block.reactor.block.BlockReactorChamber;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;

@Mixin(BlockReactorChamber.class)
public class MixinBlockReactorChamber_FixLeak {

    @Shadow(remap = false)
    TileEntityNuclearReactorElectric reactor;

    @Inject(method = "randomDisplayTick", at = @At("RETURN"))
    private void clearRef(CallbackInfo ci) {
        this.reactor = null;
    }
}
