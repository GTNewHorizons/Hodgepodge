package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.enchantment.Enchantment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.util.RomanNumerals;

/**
 * Mixin from Sk1erLLC/Patcher
 */
@Mixin(Enchantment.class)
public abstract class MixinEnchantment_FixRomanNumerals {

    @Shadow
    public abstract String getName();

    @ModifyExpressionValue(
            method = "getTranslatedName",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/StatCollector;translateToLocal(Ljava/lang/String;)Ljava/lang/String;",
                    ordinal = 1))
    private String hodgepodge$modifyRomanNumerals(String translation, int level) {
        if (Common.config.arabicNumbersForEnchantsPotions) {
            return String.valueOf(level);
        }
        if (translation != null && translation.startsWith("enchantment.level.")) {
            return RomanNumerals.toRomanLimited(level, 20);
        } else {
            return translation;
        }
    }
}
