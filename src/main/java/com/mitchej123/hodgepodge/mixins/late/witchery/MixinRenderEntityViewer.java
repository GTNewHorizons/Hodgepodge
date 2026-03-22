package com.mitchej123.hodgepodge.mixins.late.witchery;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.emoniph.witchery.client.RenderEntityViewer;
import com.mitchej123.hodgepodge.compat.WitcheryCompatBridge;

@Mixin(value = RenderEntityViewer.class, remap = false)
public class MixinRenderEntityViewer {

    @Inject(method = "setOffset", at = @At(value = "TAIL"), remap = false)
    public void setOffset(float offset, CallbackInfo ci) {
        WitcheryCompatBridge.yOffset = offset;
    }
}
