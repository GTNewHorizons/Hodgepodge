package com.mitchej123.hodgepodge.mixins.preinit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.mixins.hooks.ModProfileResult;

import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@Mixin(value = LoadController.class, remap = false)
public class MixinLoadController_logModTimes {

    private static Map<String, ArrayList<ModProfileResult>> results = new Object2ObjectOpenHashMap<>();

    @WrapOperation(
            method = "propogateStateMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/LoadController;sendEventToModContainer(Lcpw/mods/fml/common/event/FMLEvent;Lcpw/mods/fml/common/ModContainer;)V"))
    private void timeModEvent(LoadController instance, FMLEvent event, ModContainer modContainer,
            Operation<Void> original) {

        final long start = System.nanoTime();

        original.call(instance, event, modContainer);
        if (results == null) return;

        final long timeTaken = System.nanoTime() - start;
        results.computeIfAbsent(event.getEventType(), k -> new ArrayList<>())
                .add(new ModProfileResult(timeTaken, modContainer));
    }

    @Inject(
            method = "Lcpw/mods/fml/common/LoadController;distributeStateMessage(Lcpw/mods/fml/common/LoaderState;[Ljava/lang/Object;)V",
            at = @At(value = "TAIL"))
    public void printResults(LoaderState state, Object[] eventData, CallbackInfo ci) {
        if (results == null || state != LoaderState.AVAILABLE) return;
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("modtimes.csv", false)));

            writer.println("event;modid;modname;time");
            results.forEach((type, results) -> {
                results.sort(null);
                results.forEach(result -> {
                    final String modid = result.mod.getModId();
                    writer.println(type + ";" + modid + ";" + result.mod.getName() + ";" + result.time / 1000000);
                });

            });
            writer.close();
        } catch (IOException e) {
            Common.log.error("Failed to write modtimes.csv", e);
        }
        results = null;
    }

}
