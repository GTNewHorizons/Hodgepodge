package com.mitchej123.hodgepodge.mixins.hooks;

import java.util.Arrays;

import net.minecraft.block.Block;

import com.mitchej123.hodgepodge.mixins.interfaces.BlockExt_FastLookup;

public final class BlockLookupHooks {

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
        return ((BlockExt_FastLookup) block).hodgepodge$getBlockId();
    }

    public static void rebuild() {
        Block[] arr = new Block[4096];
        for (final Object obj : Block.blockRegistry) {
            final int id = Block.blockRegistry.getIDForObject(obj);
            if (id >= 0) {
                if (id >= arr.length) arr = Arrays.copyOf(arr, Math.max(arr.length * 2, id + 1));
                arr[id] = (Block) obj;
            }
        }

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] instanceof BlockExt_FastLookup ext) ext.hodgepodge$setBlockId(i);
        }

        final Block[] old = blockById;
        blockById = arr;

        // Clear embedded IDs for blocks no longer in the registry to avoid stale ID->block mismatches
        if (old != null) {
            for (final Block b : old) {
                if (b instanceof BlockExt_FastLookup ext) {
                    final int id = ext.hodgepodge$getBlockId();
                    if (id < 0 || id >= arr.length || arr[id] != b) {
                        ext.hodgepodge$setBlockId(-1);
                    }
                }
            }
        }
    }
}
