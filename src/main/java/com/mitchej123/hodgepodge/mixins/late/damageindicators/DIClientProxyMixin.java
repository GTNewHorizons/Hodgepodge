package com.mitchej123.hodgepodge.mixins.late.damageindicators;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import DamageIndicatorsMod.client.DIClientProxy;

@Mixin(DIClientProxy.class)
public class DIClientProxyMixin {

    @Inject(at = @At("HEAD"), cancellable = true, method = "trysendmessage", remap = false)
    private void hodgepodge$exit(CallbackInfo ci) {
        ci.cancel();
    }

}
