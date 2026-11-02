package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.world.chunk.storage.AnvilChunkLoader$PendingChunk")
public interface AccessorAnvilChunkLoaderPendingChunk {

    @Accessor("chunkCoordinate")
    ChunkCoordIntPair hodgepodge$getChunkCoordinate();

    @Accessor("nbtTags")
    NBTTagCompound hodgepodge$getNbtTags();
}
