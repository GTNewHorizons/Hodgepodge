package com.mitchej123.hodgepodge;

import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public class SimulationDistanceHelper {
    private static int simulationDistance;

    /**
     * Mark a chunk as no to be simulated, or reset that state. Not thread safe.
     */
    public static void preventChunkSimulation(World world, ChunkCoordIntPair chunk, boolean prevent) {
        if (!FixesConfig.addSimulationDistance) {
            return;
        }
        ISimulationDistanceWorld mixin = (ISimulationDistanceWorld) world;
        mixin.hodgepodge$preventChunkSimulation(chunk, prevent);
    }

    public static void setSimulationDistance(int distance) {
        TweaksConfig.simulationDistance = distance;
    }

    public static int getSimulationDistance() {
        return TweaksConfig.simulationDistance;
    }
}
