package com.mitchej123.hodgepodge.util;

import gregtech.common.GT_Pollution;
import net.minecraft.world.chunk.Chunk;

public class PollutionHelper {
    /*
     * GT might not loaded when the pollution mixins run, so use this shim
     */
    public static void addPollution(Chunk ch, int aPollution) {
        GT_Pollution.addPollution(ch, aPollution);
    }
}
