package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.util.RomanNumerals;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InventoryEffectRenderer.class)
public class MixinInventoryEffectRenderer_FixPotionEffectNumerals {

    @ModifyExpressionValue(
            method = "func_147044_g",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;getAmplifier()I", ordinal = 0))
    private int hodgepodge$skipOriginalCode(int amplifier,
            @Share("potionAmplifierLevel") LocalIntRef potionAmplifierLevel) {
        potionAmplifierLevel.set(amplifier);
        return 1;
    }

    @Redirect(
            method = "func_147044_g",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/I18n;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;",
                    ordinal = 1))
    private String hodgepodge$addRomanNumeral(String string, Object[] objects,
            @Share("potionAmplifierLevel") LocalIntRef potionAmplifierLevel) {
        if (potionAmplifierLevel.get() > 0) {
            if (TweaksConfig.arabicNumbersForEnchantsPotions) {
                return String.valueOf(potionAmplifierLevel.get() + 1);
            } else {
                final String translation = I18n
                        .format("enchantment.level." + (potionAmplifierLevel.get() + 1), objects);
                if (translation != null && translation.startsWith("enchantment.level.")) {
                    return RomanNumerals.toRomanLimited(potionAmplifierLevel.get() + 1, 20);
                } else {
                    return translation;
                }
            }
        }
        return "";
    }
}
