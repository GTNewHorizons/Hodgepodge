package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.mitchej123.hodgepodge.mixins.interfaces.SafeWriteNBT;
import net.minecraft.world.storage.MapData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MapData.class)
public class MixinMapData_threadedIO implements SafeWriteNBT {
    // Added interface
}
