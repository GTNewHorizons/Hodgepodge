package com.mitchej123.hodgepodge.mixins.early.minecraft.villager;

import net.minecraft.entity.NpcMerchant;
import net.minecraft.village.MerchantRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NpcMerchant.class)
public class MixinNpcMerchant {

    @Inject(method = "useRecipe", at = @At(value = "HEAD"))
    public void hodgepodge$useRecipe(MerchantRecipe recipe, CallbackInfo ci) {
        recipe.incrementToolUses();
    }
}
