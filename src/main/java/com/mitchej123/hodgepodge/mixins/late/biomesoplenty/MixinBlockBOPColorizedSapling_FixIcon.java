package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import biomesoplenty.common.blocks.BlockBOPColorizedSapling;

@Mixin(BlockBOPColorizedSapling.class)
public class MixinBlockBOPColorizedSapling_FixIcon {

    @ModifyVariable(method = "getIcon", at = @At("HEAD"), argsOnly = true, index = 2)
    private int hodgepodge$maskMetaGrowthBit(int meta) {
        return meta & 7;
    }
}
