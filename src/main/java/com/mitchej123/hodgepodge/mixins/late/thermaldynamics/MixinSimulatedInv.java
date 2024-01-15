package com.mitchej123.hodgepodge.mixins.late.thermaldynamics;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cofh.thermaldynamics.duct.item.SimulatedInv;

@Mixin(SimulatedInv.class)
public class MixinSimulatedInv {

    @Shadow(remap = false)
    ItemStack[] items;

    @Inject(method = "func_70301_a", at = @At("HEAD"), remap = false, cancellable = true)
    public void func_70301_a(int par1, CallbackInfoReturnable<ItemStack> cir) {
        if (this.items.length <= par1) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "func_70304_b", at = @At("HEAD"), remap = false, cancellable = true)
    public void func_70304_b(int par1, CallbackInfoReturnable<ItemStack> cir) {
        if (this.items.length <= par1) {
            cir.setReturnValue(null);
        }
    }
}
