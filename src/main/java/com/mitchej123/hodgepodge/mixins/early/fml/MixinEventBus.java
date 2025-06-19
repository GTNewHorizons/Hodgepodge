package com.mitchej123.hodgepodge.mixins.early.fml;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.EventBus;

@Mixin(value = EventBus.class, remap = false)
public class MixinEventBus {

    @Shadow
    private Map<Object, ModContainer> listenerOwners;

    @Inject(method = "unregister", at = @At(value = "HEAD"))
    private void hodgepodge$removeListenerOwners(Object object, CallbackInfo ci) {
        listenerOwners.remove(object);
    }
}
