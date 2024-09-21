package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.ItemWispEssence;

@Mixin(ItemWispEssence.class)
public class MixinItemWispEssence_Both {

    @Inject(method = "getAspects", at = @At("HEAD"), cancellable = true, remap = false)
    private void hodgpodge$fixNullAspect(ItemStack stack, CallbackInfoReturnable<AspectList> cir) {
        if (stack == null) {
            cir.setReturnValue(null);
        }
    }

}
