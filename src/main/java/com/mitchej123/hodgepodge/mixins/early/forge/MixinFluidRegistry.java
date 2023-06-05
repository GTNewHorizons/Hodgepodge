package com.mitchej123.hodgepodge.mixins.early.forge;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.BiMap;

@Mixin(FluidRegistry.class)
public class MixinFluidRegistry {

    private static Class<?> classContainerKey;
    private static Field fieldContainerKeyStack;
    private static Field fieldFilledContainerMap;

    @Inject(
            method = "loadFluidDefaults(Lcom/google/common/collect/BiMap;Ljava/util/Set;)V",
            at = @At(value = "TAIL"),
            remap = false)
    private static void hodgepodge$afterLoadFluidDefaults(BiMap<Fluid, Integer> localFluidIDs, Set<String> defaultNames,
            CallbackInfo ci) {
        hodgepodge$loadReflections();
        hodgepodge$reloadFluidContainerRegistry();
    }

    private static void hodgepodge$loadReflections() {
        try {
            if (classContainerKey == null) {
                classContainerKey = Class.forName("net.minecraftforge.fluids.FluidContainerRegistry$ContainerKey");
            }
            if (fieldContainerKeyStack == null) {
                fieldContainerKeyStack = classContainerKey.getDeclaredField("stack");
                fieldContainerKeyStack.setAccessible(true);
            }
            if (fieldFilledContainerMap == null) {
                fieldFilledContainerMap = FluidContainerRegistry.class.getDeclaredField("filledContainerMap");
                fieldFilledContainerMap.setAccessible(true);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * When putting `ContainerKey` to map, hash is calculated using the fluid set as default at the time. However,
     * default fluid might change after world load. In that case, certain data will never be accessible. Simply putting
     * all elements back after `FluidDelegate`s are rebound will generate correct hash.
     */
    @SuppressWarnings("unchecked")
    private static void hodgepodge$reloadFluidContainerRegistry() {
        Map<Object, FluidContainerData> filledContainerMap;
        try {
            filledContainerMap = (Map<Object, FluidContainerData>) fieldFilledContainerMap.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Map<Object, FluidContainerData> copiedFilledContainerMap = new HashMap<>(filledContainerMap);
        filledContainerMap.clear();

        try {
            for (Map.Entry<Object, FluidContainerData> entry : copiedFilledContainerMap.entrySet()) {
                Object containerKey = entry.getKey();
                filledContainerMap.put(containerKey, entry.getValue());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
