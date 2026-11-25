package com.mitchej123.hodgepodge.mixins.early.minecraft.potion;

import net.minecraft.potion.Potion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** Mixin that expands the potion list to 256. */
@Mixin(Potion.class)
public class MixinPotion {

    /**
     * Modifies the length of the vanilla potion array to have 256 elements. Better than creating a new array and
     * copying the vanilla one as this doesn't log a warning.
     */
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 32), require = 1, allow = 1)
    private static int hodgepodge$increasePotionArraySize(int originalSize) {
        return Math.max(originalSize, 256);
    }
}
