package com.mitchej123.hodgepodge.mixins.early.forge;

import java.util.Map;

import net.minecraftforge.fluids.FluidContainerRegistry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FluidContainerRegistry.class)
public interface FluidContainerRegistryAccessor {

    @Accessor(remap = false)
    static Map<Object, FluidContainerRegistry.FluidContainerData> getFilledContainerMap() {
        throw new UnsupportedOperationException();
    }
}
