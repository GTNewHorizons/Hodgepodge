package com.mitchej123.hodgepodge.mixins.late.voxelmap.cache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.thevoxelbox.voxelmap.b.h;

@Mixin(h.class)
public class MixinComparisonCachedRegion {

    @ModifyConstant(constant = @Constant(stringValue = ".zip"), method = "if()V" /* void loadStored() */, remap = false)
    private String hodgepodge$modifyExtension(String original) {
        return ".data";
    }
}
