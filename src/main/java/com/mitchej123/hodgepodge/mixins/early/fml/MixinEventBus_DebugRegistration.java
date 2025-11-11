package com.mitchej123.hodgepodge.mixins.early.fml;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.core.shared.FileLogger;

import cpw.mods.fml.common.eventhandler.EventBus;

@Mixin(value = EventBus.class, remap = false)
public class MixinEventBus_DebugRegistration {

    @Unique
    private static final FileLogger debug$log = new FileLogger("EventRegistrationTime.txt");
    @Unique
    private static long debug$totalTime;

    @Unique
    private int debug$countMethods;
    @Unique
    private int debug$countHandlers;
    @Unique
    private long debug$timeStart;

    @Inject(method = "register(Ljava/lang/Object;)V", at = @At("HEAD"))
    private void debug$atHead(Object target, CallbackInfo ci) {
        debug$countMethods = 0;
        debug$countHandlers = 0;
        debug$timeStart = System.nanoTime();
    }

    @Inject(
            method = "register(Ljava/lang/Object;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Class;getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
                    shift = At.Shift.BEFORE))
    private void debug$CountMethods(Object target, CallbackInfo ci) {
        debug$countMethods++;
    }

    @Inject(
            method = "register(Ljava/lang/Object;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/eventhandler/EventBus;register(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/reflect/Method;Lcpw/mods/fml/common/ModContainer;)V",
                    shift = At.Shift.BEFORE))
    private void debug$CountHandlers(Object target, CallbackInfo ci) {
        debug$countHandlers++;
    }

    @Inject(method = "register(Ljava/lang/Object;)V", at = @At(value = "TAIL"))
    private void debug$atBottom(Object target, CallbackInfo ci) {
        final long timeElapsed = System.nanoTime() - debug$timeStart;
        synchronized (debug$log) {
            debug$totalTime += timeElapsed;
            debug$log.log("Registered event class : " + target.getClass().getName());
            debug$log.log("Methods Visited : " + debug$countMethods);
            debug$log.log("Handlers found : " + debug$countHandlers);
            debug$log.log("Time elapsed : " + (timeElapsed / 1_000_000) + "ms");
            debug$log.log("Total Time elapsed : " + (debug$totalTime / 1_000_000) + "ms");
            if (debug$countHandlers == 0) {
                debug$log.log("No handlers have been found in the registered class!!!");
                debug$log.printStackTrace();
            }
        }
    }
}
