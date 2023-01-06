package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.util.RomanNumerals;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.StatCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin from Sk1erLLC/Patcher
 */
@Mixin(Enchantment.class)
public abstract class MixinEnchantment_FixRomanNumerals {

    @Shadow
    public abstract String getName();

    @Inject(method = "getTranslatedName", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$modifyRomanNumerals(int level, CallbackInfoReturnable<String> cir) {
        String translation = StatCollector.translateToLocal(this.getName()) + " ";
        if (Common.config.arabicNumbersForEnchantsPotions) {
            cir.setReturnValue(translation + level);
        } else {
            cir.setReturnValue(translation + RomanNumerals.toRoman(level));
        }
    }
}
