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
    private static final Set<Block> loggedUncachedBlocks = Collections.newSetFromMap(new IdentityHashMap<>());

    private static volatile Block[] blockById = null;

    public static Block getBlockById(final int id) {
        final Block[] arr = blockById;

        if (arr == null) return (Block) Block.blockRegistry.getObjectById(id);

        // arr[0] is air — match FML's default-object behavior for unknown/unregistered IDs
        final Block b = (id >= 0 && id < arr.length) ? arr[id] : null;

        return b != null ? b : arr[0];
    }

    public static int getIdFromBlock(final Block block) {
        if (block == null) return -1;

        final Block[] arr = blockById;

        if (arr == null) return Block.blockRegistry.getIDForObject(block);

        final int id = ((BlockExt_FastLookup) block).hodgepodge$getBlockId();

        if (id < 0 || id >= arr.length || arr[id] != block) {
            return getIdFromBlockFallback(block);
        }

        return id;
    }

    private static int getIdFromBlockFallback(final Block block) {
        final int registryId = Block.blockRegistry.getIDForObject(block);

        if (registryId >= 0) {
            final Block[] arr = blockById;

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
            ((BlockExt_FastLookup) block).hodgepodge$setBlockId(-1);
        }

        return registryId;
    }

    public static void rebuild() {
        invalidate();

        final Block[] arr = buildBlockByIdArray();

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] instanceof BlockExt_FastLookup ext) {
                ext.hodgepodge$setBlockId(i);
            }
        }

        blockById = arr;
        loggedUncachedBlocks.clear();
    }

    public static void invalidate() {
        final Block[] old = blockById;
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

        for (final Object obj : Block.blockRegistry) {
            if (!(obj instanceof Block)) continue;

            final int id = Block.blockRegistry.getIDForObject(obj);

            if (id > maxId) {
                maxId = id;
            }
        }

        final Block[] arr = maxId >= 0 ? new Block[maxId + 1] : new Block[0];

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
