package com.mitchej123.hodgepodge.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "hodgepodge", category = "asm")
@Config.RequiresMcRestart
public class ASMConfig {

    @Config.Comment("Disable CoFH TileEntity cache (and patch MineFactory Reloaded and Thermal Expansion with a workaround)")
    @Config.DefaultBoolean(true)
    public static boolean cofhWorldTransformer;

    @Config.Comment("Speedup progressbar")
    @Config.DefaultBoolean(true)
    public static boolean speedupProgressBar;

    @Config.Comment("Speedup LongInt HashMap")
    @Config.DefaultBoolean(true)
    public static boolean speedupLongIntHashMap;

    @Config.Comment("If using Bukkit/Thermos, the CraftServer package.")
    @Config.DefaultString("org.bukkit.craftbukkit.v1_7_R4.CraftServer")
    public static String thermosCraftServerClass;

}
