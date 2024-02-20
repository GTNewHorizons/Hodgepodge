package com.mitchej123.hodgepodge.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "hodgepodge", category = "pollution_recolor")
public class PollutionRecolorConfig {

    @Config.Comment("Changes colors of certain blocks based on pollution levels")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean pollutionBlockRecolor;

    @Config.Comment("Double Plant Blocks - Recolor Block List")
    @Config.DefaultStringList({ "net.minecraft.block.BlockDoublePlant:FLOWER", })
    @Config.RequiresMcRestart
    public static String[] renderBlockDoublePlant;

    @Config.Comment("Liquid Blocks - Recolor Block List")
    @Config.DefaultStringList({ "net.minecraft.block.BlockLiquid:LIQUID" })
    @Config.RequiresMcRestart
    public static String[] renderBlockLiquid;

    @Config.Comment("Block Vine - Recolor Block List")
    @Config.DefaultStringList({ "net.minecraft.block.BlockVine:FLOWER", })
    @Config.RequiresMcRestart
    public static String[] renderblockVine;

    @Config.Comment("Crossed Squares - Recolor Block List")
    @Config.DefaultStringList({ "net.minecraft.block.BlockTallGrass:FLOWER", "net.minecraft.block.BlockFlower:FLOWER",
            "biomesoplenty.common.blocks.BlockBOPFlower:FLOWER", "biomesoplenty.common.blocks.BlockBOPFlower2:FLOWER",
            "biomesoplenty.common.blocks.BlockBOPFoliage:FLOWER", })
    @Config.RequiresMcRestart
    public static String[] renderCrossedSquares;

    @Config.Comment("Standard Blocks - Recolor Block List")
    @Config.DefaultStringList({ "net.minecraft.block.BlockGrass:GRASS", "net.minecraft.block.BlockLeavesBase:LEAVES",
            "biomesoplenty.common.blocks.BlockOriginGrass:GRASS", "biomesoplenty.common.blocks.BlockLongGrass:GRASS",
            "biomesoplenty.common.blocks.BlockNewGrass:GRASS", "tconstruct.blocks.slime.SlimeGrass:GRASS",
            "thaumcraft.common.blocks.BlockMagicalLeaves:LEAVES", })
    @Config.RequiresMcRestart
    public static String[] renderStandardBlock;

}
