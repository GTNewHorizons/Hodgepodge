package com.mitchej123.hodgepodge.mixins.late.thermaldynamics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cofh.thermaldynamics.duct.fluid.FluidGridSuper;
import cofh.thermaldynamics.duct.fluid.TileFluidDuctSuper;
import cofh.thermaldynamics.multiblock.MultiBlockGrid;

@Mixin(TileFluidDuctSuper.class)
public class MixinTileFluidDuctSuper {

    @Inject(method = "setGrid", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void hodgepodge$setGrid(MultiBlockGrid par1, CallbackInfo ci) {
        if (par1 != null && !(par1 instanceof FluidGridSuper)) {
            par1.destroy();
            ci.cancel();
        }
    }
}
