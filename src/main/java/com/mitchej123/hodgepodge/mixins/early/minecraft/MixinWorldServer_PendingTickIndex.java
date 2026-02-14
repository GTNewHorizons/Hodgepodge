package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.mixins.interfaces.PendingBlockUpdateIndex;
import com.mitchej123.hodgepodge.util.ChunkPosUtil;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

// Overrides CoreTweaks' equivalent @Redirect + @Overwrite via higher priority
// Our implementation should be strictly better
@Mixin(value = WorldServer.class, priority = 1100)
public class MixinWorldServer_PendingTickIndex implements PendingBlockUpdateIndex {

    @Shadow
    private TreeSet<NextTickListEntry> pendingTickListEntriesTreeSet;

    @Shadow
    private Set<NextTickListEntry> pendingTickListEntriesHashSet;

    @Shadow
    private List<NextTickListEntry> pendingTickListEntriesThisTick;

    @Unique
    private final Long2ObjectOpenHashMap<ObjectOpenHashSet<NextTickListEntry>> hodgepodge$tickIndex = new Long2ObjectOpenHashMap<>();

    @Override
    public void hodgepodge$removeTickFromIndex(NextTickListEntry entry) {
        final long key = ChunkPosUtil.toLong(entry.xCoord >> 4, entry.zCoord >> 4);
        final ObjectOpenHashSet<NextTickListEntry> bucket = hodgepodge$tickIndex.get(key);
        if (bucket != null) {
            bucket.remove(entry);
            if (bucket.isEmpty()) hodgepodge$tickIndex.remove(key);
        }
    }

    @Override
    public void hodgepodge$removeChunkFromIndex(long chunkKey) {
        hodgepodge$tickIndex.remove(chunkKey);
    }

    @Redirect(
            method = { "scheduleBlockUpdateWithPriority", "func_147446_b" },
            at = @At(value = "INVOKE", target = "Ljava/util/TreeSet;add(Ljava/lang/Object;)Z"))
    private boolean hodgepodge$onTickAdded(TreeSet<NextTickListEntry> instance, Object e) {
        final NextTickListEntry entry = (NextTickListEntry) e;
        final long key = ChunkPosUtil.toLong(entry.xCoord >> 4, entry.zCoord >> 4);
        hodgepodge$tickIndex.computeIfAbsent(key, k -> new ObjectOpenHashSet<>()).add(entry);
        return instance.add(entry);
    }

    @Redirect(
            method = "tickUpdates",
            at = @At(value = "INVOKE", target = "Ljava/util/TreeSet;remove(Ljava/lang/Object;)Z"))
    private boolean hodgepodge$onTickRemoved(TreeSet<NextTickListEntry> instance, Object e) {
        final NextTickListEntry entry = (NextTickListEntry) e;
        final long key = ChunkPosUtil.toLong(entry.xCoord >> 4, entry.zCoord >> 4);
        final ObjectOpenHashSet<NextTickListEntry> bucket = hodgepodge$tickIndex.get(key);
        if (bucket != null) {
            bucket.remove(entry);
            if (bucket.isEmpty()) hodgepodge$tickIndex.remove(key);
        }
        return instance.remove(entry);
    }

    /**
     * @author mitchej123
     * @reason Spatial index lookup
     */
    @Overwrite
    public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunk, boolean remove) {
        ArrayList<NextTickListEntry> result = null;
        int chunkX = chunk.xPosition;
        int chunkZ = chunk.zPosition;
        int minX = (chunkX << 4) - 2;
        int maxX = minX + 18;
        int minZ = (chunkZ << 4) - 2;
        int maxZ = minZ + 18;

        for (int dx = -1; dx <= 0; dx++) {
            for (int dz = -1; dz <= 0; dz++) {
                final long key = ChunkPosUtil.toLong(chunkX + dx, chunkZ + dz);
                final ObjectOpenHashSet<NextTickListEntry> bucket = hodgepodge$tickIndex.get(key);
                if (bucket == null) continue;

                final Iterator<NextTickListEntry> it = bucket.iterator();
                while (it.hasNext()) {
                    final NextTickListEntry entry = it.next();

                    if (!pendingTickListEntriesHashSet.contains(entry)) {
                        it.remove();
                        continue;
                    }

                    if (entry.xCoord >= minX && entry.xCoord < maxX && entry.zCoord >= minZ && entry.zCoord < maxZ) {
                        if (remove) {
                            pendingTickListEntriesHashSet.remove(entry);
                            pendingTickListEntriesTreeSet.remove(entry);
                            it.remove();
                        }
                        if (result == null) result = new ArrayList<>();
                        result.add(entry);
                    }
                }
                if (bucket.isEmpty()) hodgepodge$tickIndex.remove(key);
            }
        }

        // Vanilla's second loop: scan pendingTickListEntriesThisTick
        for (NextTickListEntry entry : pendingTickListEntriesThisTick) {
            if (entry.xCoord >= minX && entry.xCoord < maxX && entry.zCoord >= minZ && entry.zCoord < maxZ) {
                if (result == null) result = new ArrayList<>();
                result.add(entry);
            }
        }

        if (result != null) Collections.sort(result);
        return result;
    }
}
