package com.mitchej123.hodgepodge.mixins.late.witchery;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "com.emoniph.witchery.brewing.potions.WitcheryPotions$PotionArrayExtender", remap = false)
public class MixinPotionArrayExtender {
    @Shadow(remap = false)
    private static boolean potionArrayExtended;

    static {
        // We extend the potion array in a mixin
        potionArrayExtended = true;
    }
}
