package com.mitchej123.hodgepodge.mixins.interfaces;

import net.minecraft.world.NextTickListEntry;

public interface PendingBlockUpdateIndex {

    void hodgepodge$removeTickFromIndex(NextTickListEntry entry);

    void hodgepodge$removeChunkFromIndex(long chunkKey);
}
