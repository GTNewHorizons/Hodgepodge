package com.mitchej123.hodgepodge.mixin.mixins.late.ic2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import ic2.core.item.armor.ItemArmorQuantumSuit;

@Mixin(ItemArmorQuantumSuit.class)
public class MixinIc2QuantumSuitHoverMode {

    @ModifyConstant(method = "useJetpack", constant = @Constant(floatValue = -0.025F), remap = false)
    private float hodgepodge$fixHoverMode(float original) {
        return 0.0F;
    }
}
