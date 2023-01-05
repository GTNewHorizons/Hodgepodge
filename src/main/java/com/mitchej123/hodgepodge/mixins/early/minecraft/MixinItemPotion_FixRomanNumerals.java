package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.util.RomanNumerals;
import net.minecraft.item.ItemPotion;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemPotion.class)
public class MixinItemPotion_FixRomanNumerals {

    private int hodgepodge$potionAmplifierLevel;

    @Redirect(
            method = "addInformation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;getAmplifier()I", ordinal = 1))
    public int hodgepodge$getAmplifierLevel(PotionEffect potionEffect) {
        this.hodgepodge$potionAmplifierLevel = potionEffect.getAmplifier();
        return potionEffect.getAmplifier();
    }

    @Redirect(
            method = "addInformation",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/util/StatCollector;translateToLocal(Ljava/lang/String;)Ljava/lang/String;",
                            ordinal = 1))
    public String hodgepodge$addRomanNumeral(String string) {
        if (Common.config.arabicNumbersForEnchantsPotions) {
            return String.valueOf(this.hodgepodge$potionAmplifierLevel + 1);
        } else {
            return RomanNumerals.toRoman(this.hodgepodge$potionAmplifierLevel + 1);
        }
    }
}
