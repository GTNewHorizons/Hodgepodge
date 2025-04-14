package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.ISimulationDistanceWorld;
import com.mitchej123.hodgepodge.ISimulationDistanceWorldServer;
import com.mitchej123.hodgepodge.SimulationDistanceHelper;
import com.mitchej123.hodgepodge.mixins.interfaces.MutableChunkCoordIntPair;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongBooleanPair;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(World.class)
public abstract class MixinWorld_SimulationDistance implements ISimulationDistanceWorld {

    @Shadow
    public List<EntityPlayer> playerEntities;

    @Unique
    private List<LongBooleanPair> hodgepodge$noTickChunksChanges = Collections.synchronizedList(new ArrayList<>());

    @Unique
    private Long2ByteOpenHashMap hodgepodge$noTickChunks = new Long2ByteOpenHashMap();

    @Unique
    private LongOpenHashSet hodgepodge$forcedChunksMap = new LongOpenHashSet();

    @Unique
    private MutableChunkCoordIntPair hodgepodge$reusableChunkCoord = (MutableChunkCoordIntPair) (new ChunkCoordIntPair(0, 0));

    @Unique
    private Long2BooleanOpenHashMap hodgepodge$shouldProcessTickCache = new Long2BooleanOpenHashMap();

    @Unique
    private Map<EntityPlayer, ChunkCoordIntPair> hodgepodge$playerPosOld = new HashMap<>();

    public MixinWorld_SimulationDistance() {
        // For Mixin
    }

    @Override
    public void hodgepodge$preventChunkSimulation(ChunkCoordIntPair chunk, boolean prevent) {
        long key = ChunkCoordIntPair.chunkXZ2Int(chunk.chunkXPos, chunk.chunkZPos);
        hodgepodge$noTickChunksChanges.add(LongBooleanPair.of(key, prevent));
    }

    @Unique
    private boolean hodgepodge$closeToPlayer(int x, int z) {
        int simulationDistance = SimulationDistanceHelper.getSimulationDistance();
        for (EntityPlayer player : playerEntities) {
            if (player.getEntityWorld() != (Object) this) {
                continue;
            }
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
        return hodgepodge$forcedChunksMap.contains(ChunkCoordIntPair.chunkXZ2Int(pos.chunkXPos, pos.chunkZPos));
    }

    /**
     * Check if a chunk should get processed
     */
    @Unique
    @Override
    public boolean hodgepodge$shouldProcessTick(ChunkCoordIntPair pos) {
        long key = ChunkCoordIntPair.chunkXZ2Int(pos.chunkXPos, pos.chunkZPos);
        return hodgepodge$shouldProcessTickCache.computeIfAbsent(key, dummy -> {
            if (hodgepodge$closeToPlayer(pos.chunkXPos, pos.chunkZPos)) {
                return true;
            }

            if (hodgepodge$noTickChunks.containsKey(key)) {
                return false;
            }
            return hodgepodge$isForceLoaded(pos);
        });
    }

    @Unique
    private LongOpenHashSet hodgepodge$getPlayerChunksForPos(ChunkCoordIntPair pos) {
        int simulationDistance = SimulationDistanceHelper.getSimulationDistance();
        LongOpenHashSet chunks = new LongOpenHashSet();
        for (int x = -simulationDistance; x <= simulationDistance; x++) {
            for (int z = -simulationDistance; z <= simulationDistance; z++) {
                chunks.add(ChunkCoordIntPair.chunkXZ2Int(pos.chunkXPos + x, pos.chunkZPos + z));
            }
        }
        return chunks;
    }

    /**
     * Reset internal cache on start of tick
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void hodgepodge$tick(CallbackInfo ci) {
        LongOpenHashSet forcedChunksOld = new LongOpenHashSet(hodgepodge$forcedChunksMap);

        List<LongBooleanPair> changes = hodgepodge$noTickChunksChanges;
        hodgepodge$noTickChunksChanges = Collections.synchronizedList(new ArrayList<>());
        for (LongBooleanPair update : changes) {
            long key = update.keyLong();
            boolean prevent = update.valueBoolean();
            byte value = hodgepodge$noTickChunks.getOrDefault(key, (byte) 0);
            value += (byte) (prevent ? 1 : -1);
            if (value > 0) {
                hodgepodge$noTickChunks.put(key, value);
            } else {
                hodgepodge$noTickChunks.remove(key);
            }
        }
        hodgepodge$shouldProcessTickCache.clear();
        hodgepodge$forcedChunksMap.clear();
        for (ChunkCoordIntPair key : ForgeChunkManager.getPersistentChunksFor((World) (Object) this).keys()) {
            hodgepodge$forcedChunksMap.add(ChunkCoordIntPair.chunkXZ2Int(key.chunkXPos, key.chunkZPos));
        }

        if (!((Object) this instanceof WorldServer)) {
            return;
        }
        // Handle added chunks

        LongOpenHashSet added = new LongOpenHashSet();
        for (long chunk : hodgepodge$forcedChunksMap) {
            if (!forcedChunksOld.contains(chunk)) {
                added.add(chunk);
            }
        }

        for (EntityPlayer player : playerEntities) {
            if (player.getEntityWorld() != (Object) this) {
                continue;
            }
            ChunkCoordIntPair playerPos = new ChunkCoordIntPair((int) player.posX >> 4, (int) player.posZ >> 4);
            ChunkCoordIntPair playerPosOld = hodgepodge$playerPosOld.getOrDefault(player, null);
            LongOpenHashSet chunksNewPos = hodgepodge$getPlayerChunksForPos(playerPos);
            if (playerPosOld == null) {
                added.addAll(chunksNewPos);
            } else if (!playerPos.equals(playerPosOld)) {
                LongOpenHashSet chunksOldPos = hodgepodge$getPlayerChunksForPos(playerPosOld);
                for (long pos : chunksNewPos) {
                    if (!chunksOldPos.contains(pos)) {
                        added.add(pos);
                    }
                }
            }
        }

        ((ISimulationDistanceWorldServer) this).hodgepodge$addTickCandidatesForAddedChunks(added);

        hodgepodge$playerPosOld.clear();
        for (EntityPlayer player : playerEntities) {
            hodgepodge$playerPosOld.put(player, new ChunkCoordIntPair((int) player.posX >> 4, (int) player.posZ >> 4));
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
