package com.mitchej123.hodgepodge;

import makamys.coretweaks.ducks.optimization.IPendingBlockUpdatesWorldServer;
import makamys.coretweaks.optimization.ChunkPendingBlockUpdateMap;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;

public class CoreTweaksCompat {
    public static void removeTickEntry(World world, NextTickListEntry entry) {
        IPendingBlockUpdatesWorldServer coretweaksMixin = (IPendingBlockUpdatesWorldServer)world;
        ChunkPendingBlockUpdateMap.remove(coretweaksMixin, entry);
    }
}
