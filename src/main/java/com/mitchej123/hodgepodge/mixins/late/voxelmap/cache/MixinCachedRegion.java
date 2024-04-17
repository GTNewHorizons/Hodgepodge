package com.mitchej123.hodgepodge.mixins.late.voxelmap.cache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.thevoxelbox.voxelmap.b.b;

@Mixin(b.class)
public class MixinCachedRegion {

    @ModifyConstant(
            constant = @Constant(stringValue = ".zip"),
            method = { "catch()V", // void load()
                    "const()V", // void loadCachedData()
                    "if(Lcom/thevoxelbox/voxelmap/b/b;)V" }, // ? (synthetic method)
            remap = false)
    private static String hodgepodge$modifyExtension(String original) {
        return ".data";
    }
}
