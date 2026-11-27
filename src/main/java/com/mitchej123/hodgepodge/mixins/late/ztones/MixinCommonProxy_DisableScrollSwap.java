package com.mitchej123.hodgepodge.mixins.late.ztones;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.riciJak.Ztones.handler.EventHandler;
import com.riciJak.Ztones.proxy.CommonProxy;

import cpw.mods.fml.common.eventhandler.EventBus;

@Mixin(value = CommonProxy.class, remap = false)
public class MixinCommonProxy_DisableScrollSwap {

    // EventHandler only handles scroll swapping, so don't register it at all.
    @WrapWithCondition(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/eventhandler/EventBus;register(Ljava/lang/Object;)V"))
    private boolean hodgepodge$skipScrollSwapHandler(EventBus bus, Object handler) {
        return !(handler instanceof EventHandler);
    }
}
