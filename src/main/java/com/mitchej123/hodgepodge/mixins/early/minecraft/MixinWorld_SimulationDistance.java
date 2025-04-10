package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.google.common.collect.ImmutableSetMultimap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.ISimulationDistanceWorld;
import com.mitchej123.hodgepodge.SimulationDistanceHelper;
import com.mitchej123.hodgepodge.mixins.interfaces.MutableChunkCoordIntPair;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.*;
import net.minecraftforge.common.ForgeChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(World.class)
public abstract class MixinWorld_SimulationDistance implements ISimulationDistanceWorld {

    @Shadow
    public List<EntityPlayer> playerEntities;

    @Unique
    private Set<ChunkCoordIntPair> hodgepodge$noTickChunks = new HashSet<>();

    @Unique
    private LongOpenHashSet hodgepodge$forcedChunksMap = LongOpenHashSet.of();

    @Unique
    private MutableChunkCoordIntPair hodgepodge$reusableChunkCoord = (MutableChunkCoordIntPair) (new ChunkCoordIntPair(0, 0));

    @Unique
    private Long2BooleanOpenHashMap hodgepodge$shouldProcessTickCache = new Long2BooleanOpenHashMap();

    public MixinWorld_SimulationDistance() {
        // For Mixin
    }

    @Override
    public void hodgepodge$preventChunkSimulation(ChunkCoordIntPair chunk, boolean prevent) {
        if (prevent) {
            hodgepodge$noTickChunks.add(chunk);
        } else {
            hodgepodge$noTickChunks.remove(chunk);
        }
    }

    @Unique
    private boolean hodgepodge$closeToPlayer(int x, int z) {
        int simulationDistance = SimulationDistanceHelper.getSimulationDistance();
        for (EntityPlayer player : playerEntities) {
            int playerX = (int) player.posX >> 4;
            int playerZ = (int) player.posZ >> 4;
            if (Math.abs(playerX - x) <= simulationDistance && Math.abs(playerZ - z) <= simulationDistance) {
                return true;
            }
        }
        return false;
    }

    /**
     * Cache for speed, profiler says this helps
     */
    @Unique
    private boolean hodgepodge$isForceLoaded(ChunkCoordIntPair pos) {
        return hodgepodge$forcedChunksMap.contains(pos.chunkXPos | ((long) pos.chunkZPos << 32));
    }

    /**
     * Check if a chunk should get processed
     */
    @Unique
    @Override
    public boolean hodgepodge$shouldProcessTick(ChunkCoordIntPair pos) {
        long key = pos.chunkXPos | ((long) pos.chunkZPos << 32);
        return hodgepodge$shouldProcessTickCache.computeIfAbsent(key, dummy -> {
            if (hodgepodge$closeToPlayer(pos.chunkXPos, pos.chunkZPos)) {
                return true;
            }

            if (hodgepodge$noTickChunks.contains(pos)) {
                return false;
            }
            return hodgepodge$isForceLoaded(pos);
        });
    }

    /**
     * Reset internal cache on start of tick
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void hodgepodge$tick(CallbackInfo ci) {
        hodgepodge$shouldProcessTickCache.clear();
        hodgepodge$forcedChunksMap.clear();
        for (ChunkCoordIntPair key : ForgeChunkManager.getPersistentChunksFor((World) (Object) this).keys()) {
            hodgepodge$forcedChunksMap.add(key.chunkXPos | ((long) key.chunkZPos << 32));
        }
    }

    /**
     * Skip entities that are outside of simulation distance
     */
    @WrapOperation(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;updateEntity(Lnet/minecraft/entity/Entity;)V"))
    private void hodgePodge$updateEntities(World instance, Entity entity, Operation<Void> original) {
        hodgepodge$reusableChunkCoord.setChunkPos((int) entity.posX >> 4, (int) entity.posZ >> 4);
        if (hodgepodge$shouldProcessTick((ChunkCoordIntPair) hodgepodge$reusableChunkCoord)) {
            original.call(instance, entity);
        }
    }

    /**
     * Skip tile entities that are outside of simulation distance
     */
    @WrapOperation(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;updateEntity()V"))
    private void hodgePodge$updateTileEntities(TileEntity entity, Operation<Void> original) {
        hodgepodge$reusableChunkCoord.setChunkPos(entity.xCoord >> 4, entity.zCoord >> 4);
        if (hodgepodge$shouldProcessTick((ChunkCoordIntPair) hodgepodge$reusableChunkCoord)) {
            original.call(entity);
        }
    }
}
