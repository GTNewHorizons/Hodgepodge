package com.mitchej123.hodgepodge.server;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;

public class ChunkAndNbt {
    private final Chunk chunk;
    private final NBTTagCompound nbt;

    public ChunkAndNbt(Chunk chunk, NBTTagCompound nbt) {
        this.chunk = chunk;
        this.nbt = nbt;
    }

    public Chunk getChunk() { return this.chunk; }

    public NBTTagCompound getNbt() { return this.nbt; }
}
