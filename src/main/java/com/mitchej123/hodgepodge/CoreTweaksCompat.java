package com.mitchej123.hodgepodge;

import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;

import makamys.coretweaks.ducks.optimization.IPendingBlockUpdatesWorldServer;
import makamys.coretweaks.optimization.ChunkPendingBlockUpdateMap;

public class CoreTweaksCompat {

    public static void removeTickEntry(World world, NextTickListEntry entry) {
        if (world instanceof IPendingBlockUpdatesWorldServer coretweaksWorld) {
            ChunkPendingBlockUpdateMap.remove(coretweaksWorld, entry);
        }
    }
}
