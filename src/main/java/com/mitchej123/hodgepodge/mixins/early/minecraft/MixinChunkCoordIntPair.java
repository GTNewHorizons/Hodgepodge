package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.ChunkCoordIntPair;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mitchej123.hodgepodge.mixins.interfaces.MutableChunkCoordIntPair;

@Mixin(ChunkCoordIntPair.class)
public class MixinChunkCoordIntPair implements MutableChunkCoordIntPair {

    @Shadow
    public int chunkXPos;
    @Shadow
    public int chunkZPos;

    public void setChunkXPos(int chunkXPos) {
        this.chunkXPos = chunkXPos;
    }

    public void setChunkZPos(int chunkZPos) {
        this.chunkZPos = chunkZPos;
    }

    public MutableChunkCoordIntPair setChunkPos(int chunkXPos, int chunkZPos) {
        this.chunkXPos = chunkXPos;
        this.chunkZPos = chunkZPos;
        return this;
    }

}
