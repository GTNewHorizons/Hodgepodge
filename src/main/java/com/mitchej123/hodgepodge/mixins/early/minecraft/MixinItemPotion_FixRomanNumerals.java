package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.item.ItemPotion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.util.RomanNumerals;

@Mixin(ItemPotion.class)
public class MixinItemPotion_FixRomanNumerals {

    @ModifyExpressionValue(
            method = "addInformation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;getAmplifier()I", ordinal = 1))
    private int hodgepodge$getAmplifierLevel(int amplifier,
            @Share("potionAmplifierLevel") LocalIntRef potionAmplifierLevel) {
        potionAmplifierLevel.set(amplifier);
        return amplifier;
    }

    @ModifyExpressionValue(
            method = "addInformation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/StatCollector;translateToLocal(Ljava/lang/String;)Ljava/lang/String;",
                    ordinal = 1))
    private String hodgepodge$addRomanNumeral(String translation,
            @Share("potionAmplifierLevel") LocalIntRef potionAmplifierLevel) {
        if (Common.config.arabicNumbersForEnchantsPotions) {
            return String.valueOf(potionAmplifierLevel.get() + 1);
        } else if (translation != null && translation.startsWith("potion.potency.")) {
            return RomanNumerals.toRomanLimited(potionAmplifierLevel.get() + 1, 20);
        } else {
            return translation;
        }
    }
}
