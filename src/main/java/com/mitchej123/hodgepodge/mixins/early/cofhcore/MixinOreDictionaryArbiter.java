package com.mitchej123.hodgepodge.mixins.early.cofhcore;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cofh.core.util.oredict.OreDictionaryArbiter;

@Mixin(OreDictionaryArbiter.class)
public class MixinOreDictionaryArbiter {

    @Inject(method = "registerOreDictionaryEntry", at = @At("HEAD"), cancellable = true, remap = false)
    private static void hodgepodge$fixNPE(ItemStack stack, String s, CallbackInfo ci) {
        if (stack == null) {
            ci.cancel();
        }
    }

}
