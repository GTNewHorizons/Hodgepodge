package com.mitchej123.hodgepodge.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "hodgepodge", category = "speedups")
@Config.RequiresMcRestart
public class SpeedupsConfig {

    // Minecraft

    @Config.Comment("Optimize ASMDataTable getAnnotationsFor for faster startup")
    @Config.DefaultBoolean(true)
    public static boolean optimizeASMDataTable;

    @Config.Comment("Optimize texture loading")
    @Config.DefaultBoolean(true)
    public static boolean optimizeTextureLoading;

    @Config.Comment("Optimize tileEntity removal in World.class")
    @Config.DefaultBoolean(true)
    public static boolean optimizeTileentityRemoval;

    @Config.Comment("Speedup ChunkCoordinates hashCode")
    @Config.DefaultBoolean(true)
    public static boolean speedupChunkCoordinatesHashCode;

    @Config.Comment("Speed up grass block random ticking")
    @Config.DefaultBoolean(true)
    public static boolean speedupGrassBlockRandomTicking;

    @Config.Comment("Speedup Vanilla Furnace recipe lookup")
    @Config.DefaultBoolean(true)
    public static boolean speedupVanillaFurnace;

    @Config.Comment("Sets TCP_NODELAY to true, reducing network latency in multiplayer. Works on server as well as client. From makamys/CoreTweaks")
    @Config.DefaultBoolean(true)
    public static boolean tcpNoDelay;

    @Config.Comment("Speeds up ChunkProviderClient by removing chunkListing.  Note: Depends on asm.speedupLongIntHashMap")
    @Config.DefaultBoolean(true)
    public static boolean speedupChunkProviderClient;

    // Biomes O' Plenty

    @Config.Comment("Speedup biome fog rendering in BiomesOPlenty")
    @Config.DefaultBoolean(true)
    public static boolean speedupBOPFogHandling;

    // VoxelMap

    @Config.Comment("Replace reflection in VoxelMap to directly access the fields instead.")
    @Config.DefaultBoolean(true)
    public static boolean replaceVoxelMapReflection;

}
