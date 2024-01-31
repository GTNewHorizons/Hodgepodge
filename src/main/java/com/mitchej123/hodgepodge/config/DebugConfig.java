package com.mitchej123.hodgepodge.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "hodgepodge", category = "debug")
public class DebugConfig {

    @Config.Comment("Prints debug log if DimensionManager got crashed")
    @Config.DefaultBoolean(true)
    public static boolean dimensionManagerDebug;
    @Config.Comment("Enable chunk save cme debugging code.")
    @Config.DefaultBoolean(false)
    public static boolean chunkSaveCMEDebug;
    @Config.Comment("Enable GL state debug hooks. Will not do anything useful unless mode is changed to nonzero.")
    @Config.DefaultBoolean(true)
    public static boolean renderDebug;

    @Config.Comment("Default GL state debug mode. 0 - off, 1 - reduced, 2 - full")
    @Config.RangeInt(min = 0, max = 2)
    @Config.DefaultInt(0)
    public static int renderDebugMode;

}
