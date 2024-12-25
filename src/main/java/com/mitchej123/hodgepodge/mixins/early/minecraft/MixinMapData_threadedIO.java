package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.storage.MapData;

import org.spongepowered.asm.mixin.Mixin;

import com.mitchej123.hodgepodge.mixins.interfaces.SafeWriteNBT;

@Mixin(MapData.class)
public class MixinMapData_threadedIO implements SafeWriteNBT {
    // Added interface
}
