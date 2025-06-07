package com.mitchej123.hodgepodge.mixins.early.fml;

import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.eventhandler.IEventListener;

@Mixin(targets = "cpw.mods.fml.common.eventhandler.ListenerList$ListenerListInst", remap = false)
public abstract class MixinListenerListInst {

    @Unique
    private static final IEventListener DUMMY_EVENT_LISTENER = event -> {};

    @Shadow
    private IEventListener[] listeners;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "unregister(Lcpw/mods/fml/common/eventhandler/IEventListener;)V", at = @At(value = "TAIL"))
    private void hodgepodge$fixUnregisterMemLeak(IEventListener object, CallbackInfo ci) {
        int index = ArrayUtils.indexOf(listeners, object);
        if (index == -1) {
            return;
        }
        listeners[index] = DUMMY_EVENT_LISTENER;
    }
}
