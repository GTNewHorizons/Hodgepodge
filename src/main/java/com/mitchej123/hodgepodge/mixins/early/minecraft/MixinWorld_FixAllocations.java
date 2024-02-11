package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Set;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.hax.LongChunkCoordIntPairSet;
import com.mitchej123.hodgepodge.mixins.interfaces.MutableChunkCoordIntPair;

@Mixin(World.class)
public abstract class MixinWorld_FixAllocations {

    @Shadow
    protected Set<ChunkCoordIntPair> activeChunkSet = new LongChunkCoordIntPairSet();

    @Unique
    private final MutableChunkCoordIntPair reusableCCIP = (MutableChunkCoordIntPair) new ChunkCoordIntPair(0, 0);

    @WrapOperation(
            at = @At(value = "NEW", target = "Lnet/minecraft/world/ChunkCoordIntPair;"),
            method = "setActivePlayerChunksAndCheckLight")
    private ChunkCoordIntPair reuseMutableChunkCoordIntPair(int x, int z, Operation<ChunkCoordIntPair> original) {
        return (ChunkCoordIntPair) reusableCCIP.setChunkPos(x, z);
    }

}
