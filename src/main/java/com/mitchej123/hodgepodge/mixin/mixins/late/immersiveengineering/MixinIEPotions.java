package com.mitchej123.hodgepodge.mixin.mixins.late.immersiveengineering;

import net.minecraft.potion.Potion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import blusunrize.immersiveengineering.common.util.IEPotions;

@Mixin(value = IEPotions.class, remap = false)
public class MixinIEPotions {

    @Inject(method = "extendPotionArray", at = @At("HEAD"), cancellable = true)
    private static void hodgepodge$addConditionToExtendPotionArray(int extendBy, CallbackInfo ci) {
        if (Potion.potionTypes.length > 255) {
            ci.cancel();
        }
    }
}
