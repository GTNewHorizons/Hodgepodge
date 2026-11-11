package com.mitchej123.hodgepodge.mixins.early.memory;

import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = World.class, remap = false)
public interface WorldAccessor_ClearMapStorage {

    @Accessor("s_mapStorage")
    static void setMapStorage(MapStorage mapStorage) {
        throw new IllegalStateException("Mixin stub invoked");
    }

    @Accessor("s_savehandler")
    static void setSaveHandler(ISaveHandler saveHandler) {
        throw new IllegalStateException("Mixin stub invoked");
    }
}
