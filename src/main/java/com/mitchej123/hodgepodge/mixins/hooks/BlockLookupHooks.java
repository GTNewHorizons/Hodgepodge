package com.mitchej123.hodgepodge.mixins.hooks;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import net.minecraft.block.Block;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mitchej123.hodgepodge.mixins.interfaces.BlockExt_FastLookup;

public final class BlockLookupHooks {

    private static final Logger LOGGER = LogManager.getLogger("BlockLookupHooks");

    // Track by identity: different Block instances must not be collapsed by equals().
    private static final Set<Block> loggedUncachedBlocks = Collections.newSetFromMap(new IdentityHashMap<>());

    // Published as a whole snapshot. Readers either use the previous complete cache,
    // the next complete cache, or fall back to the registry while it is null.
    private static volatile Block[] blockById = null;

    public static Block getBlockById(final int id) {
        final Block[] arr = blockById;

        if (arr == null) return (Block) Block.blockRegistry.getObjectById(id);

        // arr[0] is air. This preserves the vanilla/FML behavior where invalid
        // or missing IDs resolve to the default block instead of null.
        final Block b = (id >= 0 && id < arr.length) ? arr[id] : null;

        return b != null ? b : arr[0];
    }

    public static int getIdFromBlock(final Block block) {
        if (block == null) return -1;

        final Block[] arr = blockById;

        // During registry rebuild/remap the fast cache is deliberately disabled.
        // In that window, the live registry is the source of truth.
        if (arr == null) return Block.blockRegistry.getIDForObject(block);

        final int id = ((BlockExt_FastLookup) block).hodgepodge$getBlockId();

        // Do not trust a cached positive ID blindly.
        // Old-world ID remapping can temporarily leave a Block instance with a stale hodgepodge$blockId.
        // The cache entry must map back to the same Block instance.
        if (id < 0 || id >= arr.length || arr[id] != block) {
            return getIdFromBlockFallback(block);
        }

        return id;
    }

    private static int getIdFromBlockFallback(final Block block) {
        final int registryId = Block.blockRegistry.getIDForObject(block);

        if (registryId >= 0) {
            final Block[] arr = blockById;

            // Only refresh the per-block cached ID if the current array confirms
            // that this registry ID maps back to the same Block instance.
            // Otherwise, keep the per-block cache invalid and let future calls consult the registry again.
            if (arr != null && registryId < arr.length && arr[registryId] == block) {
                ((BlockExt_FastLookup) block).hodgepodge$setBlockId(registryId);
            } else {
                ((BlockExt_FastLookup) block).hodgepodge$setBlockId(-1);
            }

            if (loggedUncachedBlocks.add(block)) {
                LOGGER.warn(
                        "Block {} ({}) has invalid or missing cached ID, registry ID is {}. ",
                        Block.blockRegistry.getNameForObject(block),
                        block.getClass().getName(),
                        registryId);
            }
        } else {
            // The block is not present in the active registry.
            // Keep its cached ID invalid instead of preserving a stale value.
            ((BlockExt_FastLookup) block).hodgepodge$setBlockId(-1);
        }

        return registryId;
    }

    public static void rebuild() {
        // Drop old per-block IDs before constructing the new array.
        // This prevents lookups from observing stale IDs while the active registry is changing.
        invalidate();

        final Block[] arr = buildBlockByIdArray();

        // Populate the embedded ID only after the full array has been built from the active registry.
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] instanceof BlockExt_FastLookup ext) {
                ext.hodgepodge$setBlockId(i);
            }
        }

        // Publish the complete snapshot atomically.
        blockById = arr;
        loggedUncachedBlocks.clear();
    }

    public static void invalidate() {
        final Block[] old = blockById;

        // Disable fast lookups first. Any concurrent lookup will fall back to the
        // registry instead of using a partially invalidated cache.
        blockById = null;

        if (old != null) {
            for (final Block b : old) {
                if (b instanceof BlockExt_FastLookup ext) {
                    ext.hodgepodge$setBlockId(-1);
                }
            }
        }

        loggedUncachedBlocks.clear();
    }

    private static Block[] buildBlockByIdArray() {
        int maxId = -1;

        // Do not assume the registry fits in the vanilla 4096 block ID range (EndlessIDs mod).
        // Build the array size from the actual highest active registry ID.
        for (final Object obj : Block.blockRegistry) {
            if (!(obj instanceof Block)) continue;

            final int id = Block.blockRegistry.getIDForObject(obj);

            if (id > maxId) {
                maxId = id;
            }
        }

        // Keep at least one slot because getBlockById falls back to arr[0],
        // which is expected to be air in a valid block registry.
        final Block[] arr = new Block[Math.max(maxId + 1, 1)];

        for (final Object obj : Block.blockRegistry) {
            if (!(obj instanceof Block block)) continue;

            final int id = Block.blockRegistry.getIDForObject(block);

            if (id >= 0) {
                arr[id] = block;
            }
        }

        return arr;
    }
}
