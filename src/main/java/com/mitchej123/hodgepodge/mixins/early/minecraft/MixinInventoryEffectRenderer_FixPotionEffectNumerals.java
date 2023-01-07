package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.gtnewhorizon.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.util.RomanNumerals;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InventoryEffectRenderer.class)
public class MixinInventoryEffectRenderer_FixPotionEffectNumerals {

    private int hodgepodge$potionAmplifierLevel;

    @ModifyExpressionValue(
            method = "func_147044_g",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;getAmplifier()I", ordinal = 0))
    private int hodgepodge$skipOriginalCode(int amplifier) {
        this.hodgepodge$potionAmplifierLevel = amplifier;
        return 1;
    }

    @Redirect(
            method = "func_147044_g",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/resources/I18n;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;",
                            ordinal = 1))
    private String hodgepodge$addRomanNumeral(String string, Object... objects) {
        if (this.hodgepodge$potionAmplifierLevel > 0) {
            if (Common.config.arabicNumbersForEnchantsPotions) {
                return String.valueOf(this.hodgepodge$potionAmplifierLevel + 1);
            } else {
                return RomanNumerals.toRoman(this.hodgepodge$potionAmplifierLevel + 1);
            }
        }
        return "";
    }
}
