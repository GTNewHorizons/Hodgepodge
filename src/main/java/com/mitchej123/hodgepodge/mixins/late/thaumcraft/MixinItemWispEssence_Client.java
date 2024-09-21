package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import thaumcraft.common.items.ItemWispEssence;

@Mixin(ItemWispEssence.class)
public class MixinItemWispEssence_Client {

    @Inject(method = "getColorFromItemStack", at = @At("HEAD"), cancellable = true)
    private void hodgpodge$fixNullColor(ItemStack stack, int par2, CallbackInfoReturnable<Integer> cir) {
        if (stack == null) {
            cir.setReturnValue(0);
        }
    }

}
