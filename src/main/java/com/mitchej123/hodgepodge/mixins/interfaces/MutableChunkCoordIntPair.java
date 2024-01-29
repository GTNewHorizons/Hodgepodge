package com.mitchej123.hodgepodge.mixins.interfaces;

public interface MutableChunkCoordIntPair {

    void setChunkXPos(int chunkXPos);

    void setChunkZPos(int chunkZPos);

    MutableChunkCoordIntPair setChunkPos(int chunkXPos, int chunkZPos);
}
