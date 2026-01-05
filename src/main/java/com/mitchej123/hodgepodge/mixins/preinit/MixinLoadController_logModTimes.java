package com.mitchej123.hodgepodge.mixins.preinit;

import java.util.ArrayList;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.core.shared.FileLogger;
import com.mitchej123.hodgepodge.mixins.hooks.ModProfileResult;

import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@Mixin(value = LoadController.class, remap = false)
public class MixinLoadController_logModTimes {

    @Unique
    private static final Map<String, ArrayList<ModProfileResult>> hodgepodge$results = new Object2ObjectOpenHashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> hp$printResults("modtimes_shutdown.csv")));
    }

    @WrapOperation(
            method = "propogateStateMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/LoadController;sendEventToModContainer(Lcpw/mods/fml/common/event/FMLEvent;Lcpw/mods/fml/common/ModContainer;)V"))
    private void timeModEvent(LoadController instance, FMLEvent event, ModContainer modContainer,
            Operation<Void> original) {

        final long start = System.nanoTime();
        original.call(instance, event, modContainer);
        final long timeTaken = System.nanoTime() - start;

        hodgepodge$results.computeIfAbsent(event.getEventType(), k -> new ArrayList<>())
                .add(new ModProfileResult(timeTaken, modContainer));
    }

    @Inject(
            method = "distributeStateMessage(Lcpw/mods/fml/common/LoaderState;[Ljava/lang/Object;)V",
            at = @At(value = "TAIL"))
    private void printResults(LoaderState state, Object[] eventData, CallbackInfo ci) {
        if (state != LoaderState.AVAILABLE) return;
        hp$printResults("modtimes_startup.csv");
    }

    @Unique
    private static void hp$printResults(String filename) {
        try (FileLogger logger = new FileLogger(filename)) {
            logger.log("event;modid;modname;time");
            hodgepodge$results.forEach((type, results) -> {
                results.sort(null);
                results.forEach(result -> {
                    final String modid = result.mod.getModId();
                    logger.log(type + ";" + modid + ";" + result.mod.getName() + ";" + result.time / 1_000_000);
                });
            });
        } catch (Throwable ignored) {}
    }

}
