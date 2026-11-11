package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.HashMap;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.SpawnerAnimals;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpawnerAnimals.class)
public class MixinSpawnerAnimals_nullVanillaMap {

    @SuppressWarnings("unused")
    @Shadow
    private final HashMap<ChunkCoordIntPair, Boolean> eligibleChunksForSpawning = null;
}
