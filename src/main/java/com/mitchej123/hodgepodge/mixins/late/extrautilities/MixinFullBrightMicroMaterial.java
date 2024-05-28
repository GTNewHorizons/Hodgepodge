package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = com.rwtema.extrautils.multipart.FullBrightMicroMaterial.Lighting.class, remap = false)
public class MixinFullBrightMicroMaterial {

    @ModifyConstant(method = "operate", constant = @Constant(intValue = 16711935))
    int injected(int v) {
        return 240;
    }
}
