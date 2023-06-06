package com.mitchej123.hodgepodge.mixins.early.forge;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.BiMap;

@Mixin(FluidRegistry.class)
public class MixinFluidRegistry {

    /**
     * When putting `ContainerKey` to map, hash is calculated using the fluid set as default at the time. However,
     * default fluid might change after world load. In that case, certain data will never be accessible. Simply putting
     * all elements back after `FluidDelegate`s are rebound will generate correct hash.
     */
    @Inject(
            method = "loadFluidDefaults(Lcom/google/common/collect/BiMap;Ljava/util/Set;)V",
            at = @At(value = "TAIL"),
            remap = false)
    private static void hodgepodge$afterLoadFluidDefaults(BiMap<Fluid, Integer> localFluidIDs, Set<String> defaultNames,
            CallbackInfo ci) {
        Map<Object, FluidContainerData> filledContainerMap = FluidContainerRegistryAccessor.getFilledContainerMap();
        Map<Object, FluidContainerData> copiedFilledContainerMap = new HashMap<>(filledContainerMap);
        filledContainerMap.clear();
        filledContainerMap.putAll(copiedFilledContainerMap);
    }
}
