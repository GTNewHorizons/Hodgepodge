package com.mitchej123.hodgepodge.core;

import com.mitchej123.hodgepodge.core.util.BlockMatcher;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class LoadingConfig {
    public boolean fixNorthWestBias;
    public boolean fixGrassChunkLoads;
    public boolean fixFenceConnections;
    public boolean fixIc2DirectInventoryAccess;
    public static Configuration config;

    public static BlockMatcher standardBlocks = new BlockMatcher();
    public static BlockMatcher liquidBlocks = new BlockMatcher();
    public static BlockMatcher doublePlants = new BlockMatcher();
    public static BlockMatcher crossedSquares = new BlockMatcher();
    public static BlockMatcher blockVine = new BlockMatcher();

    public LoadingConfig(File file) {
        config = new Configuration(file);
        fixNorthWestBias = config.get("fixes", "fixNorthWestBias", true, "Fix northwest bias on RandomPositionGenerator").getBoolean();
        fixGrassChunkLoads = config.get("fixes", "fixGrassChunkLoads", true, "Fix grass spreading from loading chunks").getBoolean();
        fixFenceConnections = config.get("fixes", "fixFenceConnections", true, "Fix fence connections with other types of fence").getBoolean();
        fixIc2DirectInventoryAccess = config.get("fixes", "fixIc2DirectInventoryAccess", true, "Fix IC2's direct inventory access").getBoolean();

        if (config.hasChanged())
            config.save();
    }

    public void postInitClient() {
        //need to be done later cause it initializes classes
        config.getCategory("pollutionrecolor").clear();
        if (!FMLLaunchHandler.side().name().equals("SERVER")) {
            config.getCategory("Color by pollution").setComment(
                    "Blocks that should be colored by pollution. \n" +
                    "\tGrouped by the render type. \n" +
                    "\tFormat: [BlockClass]:[colortype] \n" +
                    "\tValid types: GRASS, LEAVES, FLOWERS, LIQUID \n" +
                    "\tAdd [-] first to blacklist.");
            standardBlocks.updateClassList(config.get("pollutionrecolor", "renderStandardBlock",
                    new String[]{
                            "net.minecraft.block.BlockGrass:GRASS",
                            "net.minecraft.block.BlockLeavesBase:LEAVES",
                            "biomesoplenty.common.blocks.BlockOriginGrass:GRASS",
                            "biomesoplenty.common.blocks.BlockLongGrass:GRASS",
                            "biomesoplenty.common.blocks.BlockNewGrass:GRASS",
                            "tconstruct.blocks.slime.SlimeGrass:GRASS",
                            "thaumcraft.common.blocks.BlockMagicalLeaves:LEAVES",
                    }).getStringList());
            liquidBlocks.updateClassList(config.get("pollutionrecolor", "renderBlockLiquid",
                    new String[]{
                            "net.minecraft.block.BlockLiquid:LIQUID",
                    }).getStringList());
            doublePlants.updateClassList(config.get("pollutionrecolor", "renderBlockDoublePlant",
                    new String[]{
                            "net.minecraft.block.BlockDoublePlant:FLOWER",
                    }).getStringList());
            crossedSquares.updateClassList(config.get("pollutionrecolor", "renderCrossedSquares",
                    new String[]{
                            "net.minecraft.block.BlockTallGrass:FLOWER",
                            "net.minecraft.block.BlockFlower:FLOWER",
                            "biomesoplenty.common.blocks.BlockBOPFlower:FLOWER",
                            "biomesoplenty.common.blocks.BlockBOPFlower2:FLOWER",
                            "biomesoplenty.common.blocks.BlockBOPFoliage:FLOWER",
                    }).getStringList());
            blockVine.updateClassList(config.get("pollutionrecolor", "renderblockVine",
                    new String[]{
                            "net.minecraft.block.BlockVine:FLOWER",
                    }).getStringList());
        }

        if (config.hasChanged())
            config.save();
    }
}
