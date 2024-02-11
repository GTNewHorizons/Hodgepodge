package com.mitchej123.hodgepodge.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "hodgepodge", category = "speedups")
public class SpeedupsConfig {

    @Config.Comment("Optimize ASMDataTable getAnnotationsFor for faster startup")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean optimizeASMDataTable;
    @Config.Comment("Optimize tileEntity removal in World.class")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean optimizeTileentityRemoval;
    @Config.Comment("Speedup biome fog rendering in BiomesOPlenty")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean speedupBOPFogHandling;
    @Config.Comment("Speed up grass block random ticking")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean speedupGrassBlockRandomTicking;
    @Config.Comment("Speedup ChunkCoordinates hashCode")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean speedupChunkCoordinatesHashCode;
    @Config.Comment("Speedup Vanilla Furnace recipe lookup")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean speedupVanillaFurnace;
    @Config.Comment("Optimize texture loading")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean optimizeTextureLoading;
    @Config.Comment("Sets TCP_NODELAY to true, reducing network latency in multiplayer. Works on server as well as client. From makamys/CoreTweaks")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean tcpNoDelay;
    @Config.Comment("Replace reflection in VoxelMap to directly access the fields instead.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean replaceVoxelMapReflection;

}
