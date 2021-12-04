package com.mitchej123.hodgepodge.core;

import com.mitchej123.hodgepodge.core.util.BlockMatcher;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class LoadingConfig {
    // Adjustments
    public int ic2SeedMaxStackSize;
    // Mixins
    public boolean fixNorthWestBias;
    public boolean fixFenceConnections;
    public boolean fixIc2DirectInventoryAccess;
    public boolean fixIc2ReactorDupe;
    public boolean fixIc2Nightvision;
    public boolean fixIc2Hazmat;
    public boolean fixVanillaUnprotectedGetBlock;
    public boolean fixIc2UnprotectedGetBlock;
    public boolean fixThaumcraftUnprotectedGetBlock;
    public boolean speedupChunkCoordinatesHashCode;
    public boolean fixHungerOverhaul;
    public boolean fixGuiGameOver;
    public boolean fixHopperHitBox;
    public boolean removeUpdateChecks;
    public boolean preventPickupLoot;
    public boolean dropPickedLootOnDespawn;
    public boolean installAnchorAlarm;
    public boolean hideIc2ReactorSlots;
    public boolean fixGetBlockLightValue;
    public boolean fixFireSpread;
    public boolean fixPotionEffectRender;
    public boolean addCVSupportToWandPedestal;
    public boolean speedupProgressBar;
    // ASM
    public boolean pollutionAsm;
    public boolean cofhWorldTransformer;
    public boolean enableTileRendererProfiler;
    
    public static Configuration config;

    public static BlockMatcher standardBlocks = new BlockMatcher();
    public static BlockMatcher liquidBlocks = new BlockMatcher();
    public static BlockMatcher doublePlants = new BlockMatcher();
    public static BlockMatcher crossedSquares = new BlockMatcher();
    public static BlockMatcher blockVine = new BlockMatcher();

    public LoadingConfig(File file) {
        config = new Configuration(file);
        fixNorthWestBias = config.get("fixes", "fixNorthWestBias", true, "Fix northwest bias on RandomPositionGenerator").getBoolean();
        fixFenceConnections = config.get("fixes", "fixFenceConnections", true, "Fix fence connections with other types of fence").getBoolean();
        fixIc2DirectInventoryAccess = config.get("fixes", "fixIc2DirectInventoryAccess", true, "Fix IC2's direct inventory access").getBoolean();
        fixIc2ReactorDupe = config.get("fixes", "fixIc2ReactorDupe", true, "Fix IC2's reactor dupe").getBoolean();
        fixIc2Nightvision = config.get("fixes", "fixIc2Nightvision", true, "Prevent IC2's nightvision from blinding you").getBoolean();
        fixIc2Hazmat = config.get("fixes", "fixIc2Hazmat", true,"Fix IC2 armors to avoid giving poison").getBoolean();
        fixVanillaUnprotectedGetBlock = config.get("fixes", "fixVanillaUnprotectedGetBlock", true, "Fixes various unchecked vanilla getBlock() methods").getBoolean();
        fixIc2UnprotectedGetBlock = config.get("fixes", "fixIc2UnprotectedGetBlock", true, "Fixes various unchecked IC2 getBlock() methods").getBoolean();
        fixThaumcraftUnprotectedGetBlock = config.get("fixes", "fixThaumcraftUnprotectedGetBlock", true, "Various Thaumcraft unchecked getBlock() patches").getBoolean();
        fixHungerOverhaul = config.get("fixes", "fixHungerOverhaul", true, "Fix hunger overhaul low stat effects").getBoolean();
        removeUpdateChecks = config.get("fixes", "removeUpdateChecks", true, "Remove old/stale/outdated update checks.").getBoolean();
        fixGuiGameOver = config.get("fixes", "fixGuiGameOver", true, "Fix Game Over GUI buttons disabled if switching fullscreen").getBoolean();
        fixHopperHitBox = config.get("fixes", "fixHopperHitBox", true, "Fix vanilla hopper hit box").getBoolean();
        fixGetBlockLightValue = config.get("fixes", "fixGetBlockLightValue", true, "Fix vanilla light calculation sometimes cause NPE on thermos").getBoolean();
        fixFireSpread = config.get("fixes", "fixFireSpread", true, "Fix vanilla fire spread sometimes cause NPE on thermos").getBoolean();
        
        fixPotionEffectRender = config.get("tweaks", "fixPotionEffectRender", true, "Move vanilla potion effect status rendering before everything else").getBoolean();
        installAnchorAlarm = config.get("tweaks", "installAnchorAlarm", true, "Wake up passive & personal anchors on player login").getBoolean();
        preventPickupLoot = config.get("tweaks", "preventPickupLoot", true, "Prevent monsters from picking up loot.").getBoolean();
        dropPickedLootOnDespawn = config.get("tweaks", "dropPickedLootOnDespawn", true, "Drop picked loot on entity despawn").getBoolean();
        hideIc2ReactorSlots = config.get("tweaks", "hideIc2ReactorSlots", true, "Prevent IC2's reactor's coolant slots from being accessed by automations if not a fluid reactor").getBoolean();
        enableTileRendererProfiler = config.get("tweaks", "enableTileRendererProfiler", true, "Shows renderer's impact on FPS in vanilla lagometer").getBoolean();
        addCVSupportToWandPedestal = config.get("tweaks", "addCVSupportToWandPedestal", true, "Add CV support to Thaumcraft wand recharge pedestal").getBoolean();

        ic2SeedMaxStackSize = config.get("tweaks", "ic2SeedMaxStackSize", 64, "IC2 seed max stack size").getInt();
        
        speedupChunkCoordinatesHashCode = config.get("speedups", "speedupChunkCoordinatesHashCode", true, "Speedup ChunkCoordinates hashCode").getBoolean();
        
        speedupProgressBar = config.get("asm", "speedupProgressBar", true, "Speedup progressbar").getBoolean();
        pollutionAsm = config.get("asm", "pollutionAsm", true, "Enable pollution rendering ASM").getBoolean();
        cofhWorldTransformer = config.get("asm", "cofhWorldTransformer", true, "Enable Glease's ASM patch to disable unused CoFH tileentity cache").getBoolean();

        if (config.hasChanged())
            config.save();
    }

    public static void postInitClient() {
        //need to be done later cause it initializes classes
        if (config == null) {
            System.err.println("Didnt load HODGE");
            config = new Configuration(new File(Launch.minecraftHome, "config/hodgepodge.cfg"));
        }


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

        config.getCategory("pollutionrecolor").setComment(
                "Blocks that should be colored by pollution. \n" +
                        "\tGrouped by the render type. \n" +
                        "\tFormat: [BlockClass]:[colortype] \n" +
                        "\tValid types: GRASS, LEAVES, FLOWER, LIQUID \n" +
                        "\tAdd [-] first to blacklist.");

        if (config.hasChanged())
            config.save();
    }
}
