package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.mitchej123.hodgepodge.util.RomanNumerals;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InventoryEffectRenderer.class)
public class MixinInventoryEffectRenderer_FixPotionEffectNumerals {

    private int hodgepodge$potionAmplifierLevel;

    @Redirect(
            method = "func_147044_g",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;getAmplifier()I", ordinal = 0))
    public int hodgepodge$skipOriginalCode(PotionEffect potionEffect) {
        this.hodgepodge$potionAmplifierLevel = potionEffect.getAmplifier();
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
    public String hodgepodge$addRomanNumeral(String string, Object... objects) {
        return this.hodgepodge$potionAmplifierLevel > 0
                ? RomanNumerals.toRoman(this.hodgepodge$potionAmplifierLevel + 1)
                : "";
    }
}
