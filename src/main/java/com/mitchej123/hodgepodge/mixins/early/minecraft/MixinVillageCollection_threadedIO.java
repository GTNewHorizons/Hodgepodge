package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.village.VillageCollection;

import org.spongepowered.asm.mixin.Mixin;

import com.mitchej123.hodgepodge.mixins.interfaces.SafeWriteNBT;

@Mixin(VillageCollection.class)
public class MixinVillageCollection_threadedIO implements SafeWriteNBT {
    // Added interface
}
