package com.mitchej123.hodgepodge.util;

import com.mitchej123.hodgepodge.Compat;
import com.mitchej123.hodgepodge.config.PollutionConfig;
import gregtech.common.GT_Pollution;
import net.minecraft.world.chunk.Chunk;

public class PollutionHelper {

    /*
     * GT might not loaded when the pollution mixins run, so use this shim
     */
    public static void addPollution(Chunk ch, int aPollution) {
        if (Compat.isGT5Present()) {
            GT_Pollution.addPollution(ch, aPollution);
        }
    }

    // Note: Linear instead of growing by powers of 2
    public static int rocketIgnitionPollutionAmount(int rocketTier) {
        return PollutionConfig.rocketPollutionAmount * rocketTier / 100;
    }

    // Note: Linear instead of growing by powers of 2
    public static int flyingRocketPollutionAmount(int rocketTier) {
        return PollutionConfig.rocketPollutionAmount * rocketTier;
    }
}
