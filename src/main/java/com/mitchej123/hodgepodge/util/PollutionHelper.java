package com.mitchej123.hodgepodge.util;

import net.minecraft.world.chunk.Chunk;

import com.mitchej123.hodgepodge.Compat;
import gregtech.common.GT_Pollution;

public class PollutionHelper {

    /*
     * GT might not loaded when the pollution mixins run, so use this shim
     */
    public static void addPollution(Chunk ch, int aPollution) {
        if (Compat.isGT5Present()) {
            GT_Pollution.addPollution(ch, aPollution);
        }
    }
}
