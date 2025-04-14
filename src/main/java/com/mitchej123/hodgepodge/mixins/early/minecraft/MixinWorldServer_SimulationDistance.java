package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.Compat;
import com.mitchej123.hodgepodge.CoreTweaksCompat;
import com.mitchej123.hodgepodge.ISimulationDistanceWorld;
import com.mitchej123.hodgepodge.ISimulationDistanceWorldServer;
import com.mitchej123.hodgepodge.mixins.interfaces.MutableChunkCoordIntPair;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Must run after MixinWorldClient_FixAllocations
 */
@Mixin(value = WorldServer.class, priority = 1001)
public abstract class MixinWorldServer_SimulationDistance extends World implements ISimulationDistanceWorldServer {

    @Shadow
    private TreeSet<NextTickListEntry> pendingTickListEntriesTreeSet;

    @Shadow
    private Set<NextTickListEntry> pendingTickListEntriesHashSet;

    @Shadow
    private List<NextTickListEntry> pendingTickListEntriesThisTick;

    @Unique
    private TreeSet<NextTickListEntry> hodgepodge$pendingTickCandidates = new TreeSet<>();

    @Unique
    Long2ObjectMap<HashSet<NextTickListEntry>> hodgepodge$chunkTickMap = new Long2ObjectArrayMap<>();

    @Unique
    private boolean hodgepodge$processCurrentChunk;

    @Unique
    private ExtendedBlockStorage[] hodgepodge$emptyBlockStorage = new ExtendedBlockStorage[0];

    public MixinWorldServer_SimulationDistance() {
        super(null, null, (WorldProvider) null, null, null);
    }

    /**
     * Fake HashSet size so the original MC code doesn't process any ticks
     */
    @Redirect(method = "tickUpdates", at = @At(value = "INVOKE", target = "Ljava/util/TreeSet;size()I"))
    private int hodgepodge$fakeTreeSetSize(TreeSet<NextTickListEntry> instance) {
        return 0;
    }

    /**
     * Fake TreeSet size to match HashSet size to prevent IllegalStateException
     */
    @Redirect(method = "tickUpdates", at = @At(value = "INVOKE", target = "Ljava/util/Set;size()I"))
    private int hodgepodge$fakeHashSetSize(Set<NextTickListEntry> instance) {
        return 0;
    }

    /**
     * Skip ticks that are outside of simulation distance, and delete ticks for unloaded chunks
     */
    @Inject(method = "tickUpdates", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.AFTER))
    private void hodgepodge$tickUpdates(boolean p_72955_1_, CallbackInfoReturnable<Boolean> cir) {
        if (pendingTickListEntriesTreeSet.size() != pendingTickListEntriesHashSet.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        }

        MutableChunkCoordIntPair reusableChunkCoord = (MutableChunkCoordIntPair) (new ChunkCoordIntPair(0, 0));
        Iterator<NextTickListEntry> iterator = hodgepodge$pendingTickCandidates.iterator();
        while (iterator.hasNext()) {
            NextTickListEntry entry = iterator.next();
            if (!p_72955_1_ && entry.scheduledTime > worldInfo.getWorldTotalTime()) {
                break;
            }

            reusableChunkCoord.setChunkPos(entry.xCoord >> 4, entry.zCoord >> 4);
            iterator.remove(); // Remove ignored entries as well
            if (!chunkExists(entry.xCoord >> 4, entry.zCoord >> 4)) {
                pendingTickListEntriesTreeSet.remove(entry);
                pendingTickListEntriesHashSet.remove(entry);
                if (Compat.isCoreTweaksPresent()) {
                    CoreTweaksCompat.removeTickEntry(this, entry);
                }
                long key = ChunkCoordIntPair.chunkXZ2Int(entry.xCoord, entry.zCoord);
                HashSet<NextTickListEntry> entries = hodgepodge$chunkTickMap.get(key);
                if (entries != null) {
                    entries.remove(entry);
                }
            } else if (((ISimulationDistanceWorld) this).hodgepodge$shouldProcessTick((ChunkCoordIntPair) reusableChunkCoord)) {
                pendingTickListEntriesTreeSet.remove(entry);
                pendingTickListEntriesHashSet.remove(entry);
                if (Compat.isCoreTweaksPresent()) {
                    CoreTweaksCompat.removeTickEntry(this, entry);
                }
                pendingTickListEntriesThisTick.add(entry);
                if (pendingTickListEntriesThisTick.size() >= 1000) {
                    break;
                }
            }
        }
    }

    @Override
    public void hodgepodge$chunkUnloaded(long chunk) {
        HashSet<NextTickListEntry> entries = hodgepodge$chunkTickMap.get(chunk);
        hodgepodge$chunkTickMap.remove(chunk);
        for (NextTickListEntry entry : entries) {
            pendingTickListEntriesTreeSet.remove(entry);
            pendingTickListEntriesHashSet.remove(entry);
            if (Compat.isCoreTweaksPresent()) {
                CoreTweaksCompat.removeTickEntry(this, entry);
            }
        }
    }

    // Called by MixinWorld_SimulationDistance mixin
    @Override
    public void hodgepodge$addTickCandidatesForAddedChunks(LongOpenHashSet added) {
        for (long chunk : added) {
            HashSet<NextTickListEntry> entries = hodgepodge$chunkTickMap.get(chunk);
            hodgepodge$pendingTickCandidates.addAll(entries);
        }
    }

    /**
     * Cache if the current chunk is outside of simulation distance
     */
    @WrapOperation(method = "func_147456_g", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private Object hodgepodge$chunkTicks(Iterator<ChunkCoordIntPair> instance, Operation<ChunkCoordIntPair> original) {
        ChunkCoordIntPair result = original.call(instance);
        hodgepodge$processCurrentChunk = ((ISimulationDistanceWorld) this).hodgepodge$shouldProcessTick(result);
        return result;
    }

    /**
     * Skip lighting outside of simulation distance
     */
    @WrapOperation(method = "func_147456_g", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canDoLightning(Lnet/minecraft/world/chunk/Chunk;)Z"))
    private boolean hodgepodge$canDoLighting(WorldProvider instance, Chunk chunk, Operation<Boolean> original) {
        if (hodgepodge$processCurrentChunk) {
            return original.call(instance, chunk);
        }

        return false;
    }

    /**
     * Skip rain/icde logic outside of simulation distance
     */
    @WrapOperation(method = "func_147456_g", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canDoRainSnowIce(Lnet/minecraft/world/chunk/Chunk;)Z"))
    private boolean hodgepodge$canDoRainSnowIce(WorldProvider instance, Chunk chunk, Operation<Boolean> original) {
        if (hodgepodge$processCurrentChunk) {
            return original.call(instance, chunk);
        }

        return false;
    }

    /**
     * Skip random ticks outside of simulation distance
     */
    @WrapOperation(method = "func_147456_g", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBlockStorageArray()[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;"))
    private ExtendedBlockStorage[] hodgepodge$randomTicks(Chunk instance, Operation<ExtendedBlockStorage[]> original) {
        if (hodgepodge$processCurrentChunk) {
            return original.call(instance);
        }

        return hodgepodge$emptyBlockStorage;
    }

    /**
     * Handle adding ticks
     */
    @WrapOperation(method = "scheduleBlockUpdateWithPriority", at = @At(value = "INVOKE", target = "Ljava/util/TreeSet;add(Ljava/lang/Object;)Z"))
    private boolean hodgepodge$addTick1(TreeSet<NextTickListEntry> instance, Object e, Operation<Boolean> original) {
        NextTickListEntry entry = (NextTickListEntry) e;
        hodgepodge$pendingTickCandidates.add((NextTickListEntry) e);
        long key = ChunkCoordIntPair.chunkXZ2Int(entry.xCoord, entry.zCoord);
        HashSet<NextTickListEntry> entries = hodgepodge$chunkTickMap.computeIfAbsent(key, dummy -> new HashSet<>());
        entries.add(entry);
        return original.call(instance, e);
    }

    /**
     * Handle adding ticks
     */
    @WrapOperation(method = "func_147446_b", at = @At(value = "INVOKE", target = "Ljava/util/TreeSet;add(Ljava/lang/Object;)Z"))
    private boolean hodgepodge$addTick2(TreeSet<NextTickListEntry> instance, Object e, Operation<Boolean> original) {
        NextTickListEntry entry = (NextTickListEntry) e;
        hodgepodge$pendingTickCandidates.add((NextTickListEntry) e);
        long key = ChunkCoordIntPair.chunkXZ2Int(entry.xCoord, entry.zCoord);
        HashSet<NextTickListEntry> entries = hodgepodge$chunkTickMap.computeIfAbsent(key, dummy -> new HashSet<>());
        entries.add(entry);
        return original.call(instance, e);
    }


}
