package com.mitchej123.hodgepodge.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "hodgepodge", category = "asm")
public class ASMConfig {

    @Config.Comment("Enable Glease's ASM patch to disable unused CoFH tileentity cache")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean cofhWorldTransformer;

    @Config.Comment("Speedup progressbar")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean speedupProgressBar;

    @Config.Comment("If using Bukkit/Thermos, the CraftServer package.")
    @Config.DefaultString("org.bukkit.craftbukkit.v1_7_R4.CraftServer")
    public static String thermosCraftServerClass;

}
