package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.gtnewhorizon.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.util.RomanNumerals;
import net.minecraft.item.ItemPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemPotion.class)
public class MixinItemPotion_FixRomanNumerals {

    private int hodgepodge$potionAmplifierLevel;

    @ModifyExpressionValue(
            method = "addInformation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;getAmplifier()I", ordinal = 1))
    private int hodgepodge$getAmplifierLevel(int amplifier) {
        this.hodgepodge$potionAmplifierLevel = amplifier;
        return amplifier;
    }

    @Redirect(
            method = "addInformation",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/util/StatCollector;translateToLocal(Ljava/lang/String;)Ljava/lang/String;",
                            ordinal = 1))
    private String hodgepodge$addRomanNumeral(String string) {
        if (Common.config.arabicNumbersForEnchantsPotions) {
            return String.valueOf(this.hodgepodge$potionAmplifierLevel + 1);
        } else {
            return RomanNumerals.toRoman(this.hodgepodge$potionAmplifierLevel + 1);
        }
    }
}
