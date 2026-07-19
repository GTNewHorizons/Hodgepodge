package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ic2.core.block.invslot.InvSlotScannable;

@Mixin(InvSlotScannable.ServerScannableCheck.class)
public class MixinIC2ScannableRpcSecurity {

    @Inject(method = "executeRpc", at = @At("HEAD"), cancellable = true, remap = false)
    private void hodgepodge$validateArguments(Object[] arguments, CallbackInfoReturnable<Boolean> callback) {
        if (arguments == null || arguments.length != 1 || !(arguments[0] instanceof ItemStack)) {
            callback.setReturnValue(false);
        }
    }
}
