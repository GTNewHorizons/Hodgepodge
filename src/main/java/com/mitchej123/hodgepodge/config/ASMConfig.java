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

    @Config.Comment("Speedup NBTTagCompound copy")
    @Config.DefaultBoolean(true)
    public static boolean speedupNBTTagCompoundCopy;

    @Config.Comment("Speedup PlayerManager")
    @Config.DefaultBoolean(true)
    public static boolean speedupPlayerManager;

    @Config.Comment("Speedup ObjectIntIdentityMap")
    @Config.DefaultBoolean(true)
    public static boolean speedupObjectIntIdentityMap;

    @Config.Comment("Remove various vararg method calls, to make profiling easier.")
    @Config.DefaultBoolean(true)
    public static boolean dissectVarargs;

    @Config.Comment("Speedup OreDictionary")
    @Config.DefaultBoolean(true)
    public static boolean speedupOreDictionary;
}
