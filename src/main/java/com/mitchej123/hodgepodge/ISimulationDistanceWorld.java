package com.mitchej123.hodgepodge;

import net.minecraft.world.ChunkCoordIntPair;

public interface ISimulationDistanceWorld {
    void hodgepodge$preventChunkSimulation(ChunkCoordIntPair chunk, boolean prevent);
    boolean hodgepodge$shouldProcessTick(ChunkCoordIntPair pos);
}
