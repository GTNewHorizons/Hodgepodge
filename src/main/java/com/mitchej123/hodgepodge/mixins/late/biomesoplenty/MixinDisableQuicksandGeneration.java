package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = biomesoplenty.api.biome.BiomeFeatures.class, remap = false)
public class MixinDisableQuicksandGeneration {

    @Inject(method = "getFeature", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void hodgepodge$getFeature(String featureName, CallbackInfoReturnable<Object> cir) {
        if (featureName != null && featureName.equals("generateQuicksand")) cir.setReturnValue(false);
    }
}
