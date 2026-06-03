package com.mitchej123.hodgepodge.mixins.early.minecraft.crashfixes;

import static java.lang.Math.max;

import net.minecraft.enchantment.EnchantmentHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {

    @ModifyExpressionValue(
            method = "getLootingModifier",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;getEnchantmentLevel(ILnet/minecraft/item/ItemStack;)I"))
    private static int hodge$noNegativeLooting(int original) {
        return max(0, original);
    }
}
