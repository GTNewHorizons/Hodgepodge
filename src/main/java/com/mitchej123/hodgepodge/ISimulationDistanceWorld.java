package com.mitchej123.hodgepodge;

public interface ISimulationDistanceWorld {

    void hodgepodge$preventChunkSimulation(long packedChunkPos, boolean prevent);

    SimulationDistanceHelper hodgepodge$getSimulationDistanceHelper();

    void hodgepodge$SetProcessCurrentChunk(boolean value);
}
