package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mitchej123.hodgepodge.mixins.interfaces.GameRuleExt;

// applecore overwrites the method at
// priority = 1000, so we inject after
@Mixin(value = FoodStats.class, priority = 1500)
public class MixinFoodStats_HungerRule {

    @WrapWithCondition(
            method = "onUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FoodStats;addExhaustion(F)V"))
    private boolean hodgepodge$hungerRule(FoodStats foodStats, float amount, EntityPlayer player) {
        return !((GameRuleExt) player.worldObj.getGameRules()).hodgepodge$isHungerDisabled();
    }
}
