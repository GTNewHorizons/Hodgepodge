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

    // The target class holds a list and an array with all the events handlers registered :
    // ArrayList<ArrayList<IEventListener>> priorities;
    // IEventListener[] listeners;
    // - priorities is a list of list<IEventListener> that contains all the event handlers of each priority level
    // - the listeners array is the result of all the event handlers put side by side in the same array according to
    // their priority levels
    // it's the listeners array that is used for fast iteration when firing events
    // However when unregistering events, the handler is correctly removed from the priorities list but not the
    // listeners which is only rebuilt on the next event fire therefore creating a memory leak.

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
