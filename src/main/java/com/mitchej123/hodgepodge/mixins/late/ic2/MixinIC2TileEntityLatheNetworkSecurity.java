package com.mitchej123.hodgepodge.mixins.late.ic2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ic2.core.block.machine.tileentity.TileEntityLathe;

@Mixin(TileEntityLathe.class)
public class MixinIC2TileEntityLatheNetworkSecurity {

    @Inject(method = "process", at = @At("HEAD"), cancellable = true, remap = false)
    private void hodgepodge$validatePosition(int position, CallbackInfoReturnable<Boolean> callback) {
        if (position < 0 || position >= 5) {
            callback.setReturnValue(false);
        }
    }
}
