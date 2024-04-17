package com.mitchej123.hodgepodge.mixins.late.voxelmap.cache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "com.thevoxelbox.voxelmap.b.c")
public class MixinCachedRegion$1 {

    @ModifyConstant(constant = @Constant(stringValue = ".zip"), method = "run()V", remap = false)
    private String hodgepodge$modifyExtension(String original) {
        return ".data";
    }
}
