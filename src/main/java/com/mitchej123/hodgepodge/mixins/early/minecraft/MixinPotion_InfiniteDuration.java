package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mitchej123.hodgepodge.util.InfinitePotionDuration;

@Mixin(Potion.class)
public class MixinPotion_InfiniteDuration {

    @ModifyReturnValue(method = "getDurationString", at = @At("RETURN"))
    private static String hodgepodge$showInfinity(String original, PotionEffect effect) {
        return effect.getDuration() == InfinitePotionDuration.INFINITE ? InfinitePotionDuration.SYMBOL : original;
    }
}
