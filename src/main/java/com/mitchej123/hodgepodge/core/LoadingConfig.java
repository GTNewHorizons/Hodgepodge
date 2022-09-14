package com.mitchej123.hodgepodge.core;

import com.mitchej123.hodgepodge.core.util.BlockMatcher;
import com.mitchej123.hodgepodge.mixins.TargetedMod;
import java.io.File;
import java.util.Arrays;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

public class LoadingConfig {
    public String[] requiredMods;
    public boolean requiredModsInDev;

    // Adjustments
    public int ic2SeedMaxStackSize;
    public int particleLimit;
    public boolean addSystemInfo;

    // Mixins
    public boolean addCVSupportToWandPedestal;
    public boolean dropPickedLootOnDespawn;
    public boolean fixFenceConnections;
    public boolean fixFireSpread;
    public boolean fixGetBlockLightValue;
    public boolean fixGuiGameOver;
    public boolean fixHopperHitBox;
    public boolean fixHungerOverhaul;
    public boolean fixIc2DirectInventoryAccess;
    public boolean fixIc2Hazmat;
    public boolean fixIc2Nightvision;
    public boolean fixIc2ReactorDupe;
    public boolean optimizeIc2ReactorInventoryAccess;
    public boolean fixIc2UnprotectedGetBlock;
    public boolean fixNorthWestBias;
    public boolean fixPotionEffectRender;
    public boolean fixPotionRenderOffset;
    public boolean fixPotionLimit;
    public boolean fixImmobileFireballs;
    public boolean fixPerspectiveCamera;
    public boolean fixDebugBoundingBox;
    public boolean fixHopperVoidingItems;
    public boolean fixHugeChatKick;
    public boolean logHugeChat;
    public boolean fixThaumcraftUnprotectedGetBlock;
    public boolean fixGTSawSpawningWaterWithIceBLock;
    public boolean fixUrlDetection;
    public boolean fixVanillaUnprotectedGetBlock;
    public boolean fixWorldGetBlock;
    public boolean fixDimensionChangeHearts;
    public boolean increaseParticleLimit;
    public boolean fixGlStateBugs;
    public boolean hideIc2ReactorSlots;
    public boolean displayIc2FluidLocalizedName;
    public boolean installAnchorAlarm;
    public boolean preventPickupLoot;
    public boolean removeUpdateChecks;
    public boolean tcpNoDelay;
    public boolean speedupChunkCoordinatesHashCode;
    public boolean speedupProgressBar;
    public boolean speedupVanillaFurnace;
    public boolean fixJourneymapKeybinds;
    public boolean deduplicateForestryCompatInBOP;
    public boolean speedupBOPFogHandling;
    public boolean makeBigFirsPlantable;
    public boolean fixHudLightingGlitch;
    public boolean fixComponentsPoppingOff;
    public boolean thirstyTankContainer;
    public boolean fixWorldServerLeakingUnloadedEntities;
    public boolean fixResizableFullscreen;
    public boolean fixUnfocusedFullscreen;
    public boolean addToggleDebugMessage;
    public boolean speedupAnimations;
    public boolean fixPotionIterating;
    public boolean fixIgnisFruitAABB;
    public boolean fixNetherLeavesFaceRendering;
    public boolean optimizeASMDataTable;
    public boolean squashBedErrorMessage;

    public boolean enableDefaultLanPort;

    public int defaultLanPort;

    // render debug
    public boolean renderDebug;
    public int renderDebugMode;

    // ASM
    public boolean pollutionAsm;
    public boolean cofhWorldTransformer;
    public boolean enableTileRendererProfiler;
    public boolean biblocraftRecipes;

    public String thermosCraftServerClass;

    public static Configuration config;

    public static BlockMatcher standardBlocks = new BlockMatcher();
    public static BlockMatcher liquidBlocks = new BlockMatcher();
    public static BlockMatcher doublePlants = new BlockMatcher();
    public static BlockMatcher crossedSquares = new BlockMatcher();
    public static BlockMatcher blockVine = new BlockMatcher();

    public LoadingConfig(File file) {
        config = new Configuration(file);

        final String CATEGORY_ASM = "asm";
        final String CATEGORY_DEBUG = "debug";
        final String CATEGORY_FIXES = "fixes";
        final String CATEGORY_OVERALL = "overall";
        final String CATEGORY_SPEEDUPS = "speedups";
        final String CATEGORY_TWEAKS = "tweaks";

        requiredMods = config.get(
                        CATEGORY_OVERALL, "requiredMods", defaultRequiredMods, "Subset of TargetMods that are required")
                .getStringList();
        requiredModsInDev = config.get(CATEGORY_OVERALL, "requiredModsInDev", false, "Require the Required Mods in dev")
                .getBoolean();
        fixWorldGetBlock = config.get(CATEGORY_FIXES, "fixWorldGetBlock", true, "Fix unprotected getBlock() in World")
                .getBoolean();
        fixNorthWestBias = config.get(
                        CATEGORY_FIXES, "fixNorthWestBias", true, "Fix northwest bias on RandomPositionGenerator")
                .getBoolean();
        fixFenceConnections = config.get(
                        CATEGORY_FIXES, "fixFenceConnections", true, "Fix fence connections with other types of fence")
                .getBoolean();
        fixIc2DirectInventoryAccess = config.get(
                        CATEGORY_FIXES, "fixIc2DirectInventoryAccess", true, "Fix IC2's direct inventory access")
                .getBoolean();
        fixIc2ReactorDupe = config.get(CATEGORY_FIXES, "fixIc2ReactorDupe", true, "Fix IC2's reactor dupe")
                .getBoolean();
        optimizeIc2ReactorInventoryAccess = config.get(
                        CATEGORY_FIXES,
                        "optimizeIc2ReactorInventoryAccess",
                        true,
                        "Optimize inventory access to IC2 nuclear reactor")
                .getBoolean();
        fixIc2Nightvision = config.get(
                        CATEGORY_FIXES, "fixIc2Nightvision", true, "Prevent IC2's nightvision from blinding you")
                .getBoolean();
        fixIc2Hazmat = config.get(CATEGORY_FIXES, "fixIc2Hazmat", true, "Fix IC2 armors to avoid giving poison")
                .getBoolean();
        fixVanillaUnprotectedGetBlock = config.get(
                        CATEGORY_FIXES,
                        "fixVanillaUnprotectedGetBlock",
                        true,
                        "Fixes various unchecked vanilla getBlock() methods")
                .getBoolean();
        fixIc2UnprotectedGetBlock = config.get(
                        CATEGORY_FIXES,
                        "fixIc2UnprotectedGetBlock",
                        true,
                        "Fixes various unchecked IC2 getBlock() methods")
                .getBoolean();
        fixThaumcraftUnprotectedGetBlock = config.get(
                        CATEGORY_FIXES,
                        "fixThaumcraftUnprotectedGetBlock",
                        true,
                        "Various Thaumcraft unchecked getBlock() patches")
                .getBoolean();
        fixGTSawSpawningWaterWithIceBLock = config.get(
                        CATEGORY_FIXES,
                        "fixGTSawSpawningWaterWithIceBLock",
                        true,
                        "Fixes GT bug that spawns a water source after breaking an ice block with a GT Saw")
                .getBoolean();
        fixHungerOverhaul = config.get(
                        CATEGORY_FIXES, "fixHungerOverhaul", true, "Fix hunger overhaul low stat effects")
                .getBoolean();
        removeUpdateChecks = config.get(
                        CATEGORY_FIXES, "removeUpdateChecks", true, "Remove old/stale/outdated update checks.")
                .getBoolean();
        fixGuiGameOver = config.get(
                        CATEGORY_FIXES,
                        "fixGuiGameOver",
                        true,
                        "Fix Game Over GUI buttons disabled if switching fullscreen")
                .getBoolean();
        fixHopperHitBox = config.get(CATEGORY_FIXES, "fixHopperHitBox", true, "Fix vanilla hopper hit box")
                .getBoolean();
        fixGetBlockLightValue = config.get(
                        CATEGORY_FIXES,
                        "fixGetBlockLightValue",
                        true,
                        "Fix vanilla light calculation sometimes cause NPE on thermos")
                .getBoolean();
        fixFireSpread = config.get(
                        CATEGORY_FIXES, "fixFireSpread", true, "Fix vanilla fire spread sometimes cause NPE on thermos")
                .getBoolean();
        fixUrlDetection = config.get(CATEGORY_FIXES, "fixUrlDetection", true, "Fix URISyntaxException in forge.")
                .getBoolean();
        fixDimensionChangeHearts = config.get(
                        CATEGORY_FIXES, "fixDimensionChangeHearts", true, "Fix losing bonus hearts on dimension change")
                .getBoolean();
        fixPotionLimit = config.get(CATEGORY_FIXES, "fixPotionLimit", true, "Fix potions >= 128")
                .getBoolean();
        fixImmobileFireballs = config.get(
                        CATEGORY_FIXES,
                        "fixImmobileFireballs",
                        true,
                        "Fix the bug that makes fireballs stop moving when chunk unloads")
                .getBoolean();
        fixPerspectiveCamera = config.get(
                        CATEGORY_FIXES,
                        "fixPerspectiveCamera",
                        true,
                        "Prevent tall grass and such to affect the perspective camera")
                .getBoolean();
        fixDebugBoundingBox = config.get(
                        CATEGORY_FIXES,
                        "fixDebugBoundingBox",
                        true,
                        "Fixes the debug hitbox of the player beeing offset")
                .getBoolean();
        fixGlStateBugs = config.get(
                        CATEGORY_FIXES,
                        "fixGlStateBugs",
                        true,
                        "Fix vanilla GL state bugs causing lighting glitches in various perspectives (MC-10135).")
                .getBoolean();
        deduplicateForestryCompatInBOP = config.get(
                        CATEGORY_FIXES,
                        "deduplicateForestryCompatInBOP",
                        true,
                        "Removes duplicate Fermenter and Squeezer recipes and flower registration")
                .getBoolean();
        fixHopperVoidingItems = config.get(
                        CATEGORY_FIXES, "fixHopperVoidingItems", true, "Fix Drawer + Hopper voiding items")
                .getBoolean();
        fixHugeChatKick = config.get(
                        CATEGORY_FIXES, "fixHugeChatKick", true, "Fix oversized chat message kicking player.")
                .getBoolean();
        logHugeChat = config.get(
                        CATEGORY_FIXES,
                        "logHugeChat",
                        true,
                        "Log oversized chat message to console. WARNING: might create huge log files if this happens very often.")
                .getBoolean();
        fixWorldServerLeakingUnloadedEntities = config.get(
                        CATEGORY_FIXES,
                        "fixWorldServerLeakingUnloadedEntities",
                        true,
                        "Fix WorldServer leaking entities when no players are present in a dimension")
                .getBoolean();
        fixResizableFullscreen = config.get(
                        CATEGORY_FIXES,
                        "fixResizableFullscreen",
                        true,
                        "Fix game window becoming not resizable after toggling fullscrean in any way")
                .getBoolean();
        fixUnfocusedFullscreen = config.get(
                        CATEGORY_FIXES,
                        "fixUnfocusedFullscreen",
                        true,
                        "Fix exiting fullscreen when you tab out of the game")
                .getBoolean();
        addToggleDebugMessage = config.get(
                        CATEGORY_TWEAKS,
                        "addToggleDebugMessage",
                        true,
                        "Add a debug message in the chat when toggling vanilla debug options")
                .getBoolean();
        speedupAnimations = config.get(
                        CATEGORY_FIXES,
                        "speedupAnimations",
                        true,
                        "Drastically speedup animated textures (Basically the same as with optifine animations off but animations are working)")
                .getBoolean();
        fixPotionIterating = config.get(
                        CATEGORY_FIXES,
                        "fixPotionIterating",
                        true,
                        "Fix crashes with ConcurrentModificationException because of incorrectly iterating over active potions")
                .getBoolean();
        fixIgnisFruitAABB = config.get(
                        CATEGORY_FIXES, "fixIgnisFruitAABB", true, "Fix Axis aligned Bounding Box of Ignis Fruit")
                .getBoolean();
        fixNetherLeavesFaceRendering = config.get(
                        CATEGORY_FIXES,
                        "fixNetherLeavesFaceRendering",
                        true,
                        "If fancy graphics are enabled, Nether Leaves render sides with other Nether Leaves adjacent too")
                .getBoolean();
        optimizeASMDataTable = config.get(
                        CATEGORY_SPEEDUPS,
                        "optimizeASMDataTable",
                        true,
                        "Optimize ASMDataTable getAnnotationsFor for faster startup")
                .getBoolean();
        squashBedErrorMessage = config.get(
                        "fixes",
                        "squashBedErrorMessage",
                        true,
                        "Stop \"You can only sleep at night\" message filling the chat")
                .getBoolean();

        increaseParticleLimit = config.get(CATEGORY_TWEAKS, "increaseParticleLimit", true, "Increase particle limit")
                .getBoolean();
        particleLimit = Math.max(
                Math.min(
                        config.get(CATEGORY_TWEAKS, "particleLimit", 8000, "Particle limit [4000-16000]")
                                .getInt(),
                        16000),
                4000);
        fixPotionEffectRender = config.get(
                        CATEGORY_TWEAKS,
                        "fixPotionEffectRender",
                        true,
                        "Fix vanilla potion effects rendering above the NEI tooltips in the inventory")
                .getBoolean();
        fixPotionRenderOffset = config.get(
                        CATEGORY_TWEAKS,
                        "fixPotionRenderOffset",
                        true,
                        "Prevents the inventory from shifting when the player has active potion effects")
                .getBoolean();
        installAnchorAlarm = config.get(
                        CATEGORY_TWEAKS,
                        "installAnchorAlarm",
                        true,
                        "Wake up passive & personal anchors on player login")
                .getBoolean();
        preventPickupLoot = config.get(
                        CATEGORY_TWEAKS, "preventPickupLoot", true, "Prevent monsters from picking up loot.")
                .getBoolean();
        dropPickedLootOnDespawn = config.get(
                        CATEGORY_TWEAKS, "dropPickedLootOnDespawn", true, "Drop picked loot on entity despawn")
                .getBoolean();
        hideIc2ReactorSlots = config.get(
                        CATEGORY_TWEAKS,
                        "hideIc2ReactorSlots",
                        true,
                        "Prevent IC2's reactor's coolant slots from being accessed by automations if not a fluid reactor")
                .getBoolean();
        displayIc2FluidLocalizedName = config.get(
                        CATEGORY_TWEAKS,
                        "displayIc2FluidLocalizedName",
                        true,
                        "Display fluid localized name in IC2 fluid cell tooltip")
                .getBoolean();
        enableTileRendererProfiler = config.get(
                        CATEGORY_TWEAKS,
                        "enableTileRendererProfiler",
                        true,
                        "Shows renderer's impact on FPS in vanilla lagometer")
                .getBoolean();
        addCVSupportToWandPedestal = config.get(
                        CATEGORY_TWEAKS,
                        "addCVSupportToWandPedestal",
                        true,
                        "Add CV support to Thaumcraft wand recharge pedestal")
                .getBoolean();
        makeBigFirsPlantable = config.get(
                        CATEGORY_TWEAKS,
                        "makeBigFirsPlantable",
                        true,
                        "Allow 5 Fir Sapling planted together ('+' shape) to grow to a big fir tree")
                .getBoolean();
        ic2SeedMaxStackSize = config.get(CATEGORY_TWEAKS, "ic2SeedMaxStackSize", 64, "IC2 seed max stack size")
                .getInt();
        addSystemInfo = config.get(
                        CATEGORY_TWEAKS,
                        "addSystemInfo",
                        true,
                        "Adds system info to the F3 overlay (Java version and vendor; GPU name; OpenGL version; CPU cores; OS name, version and architecture)")
                .getBoolean();
        fixHudLightingGlitch = config.get(
                        CATEGORY_TWEAKS,
                        "fixHudLightingGlitch",
                        true,
                        "Fix hotbars being dark when Project Red is installed")
                .getBoolean();
        fixComponentsPoppingOff = config.get(
                        CATEGORY_TWEAKS,
                        "fixComponentsPoppingOff",
                        true,
                        "Fix Project Red components popping off on unloaded chunks")
                .getBoolean();
        thirstyTankContainer = config.get(
                        CATEGORY_TWEAKS, "thirstyTankContainer", true, "Implement container for thirsty tank")
                .getBoolean();
        enableDefaultLanPort = config.get(
                        CATEGORY_TWEAKS, "enableDefaultLanPort", true, "Open an integrated server on a static port.")
                .getBoolean();
        defaultLanPort = config.get(
                        CATEGORY_TWEAKS,
                        "defaultLanPort",
                        25565,
                        "Specify default LAN port to open an integrated server on. Set to 0 to always open the server on an automatically allocated port.")
                .getInt();

        tcpNoDelay = config.get(
                        CATEGORY_SPEEDUPS,
                        "tcpNoDelay",
                        true,
                        "Sets TCP_NODELAY to true, reducing network latency in multiplayer. Works on server as well as client. From makamys/CoreTweaks")
                .getBoolean();

        speedupChunkCoordinatesHashCode = config.get(
                        CATEGORY_SPEEDUPS, "speedupChunkCoordinatesHashCode", true, "Speedup ChunkCoordinates hashCode")
                .getBoolean();
        speedupVanillaFurnace = config.get(
                        CATEGORY_SPEEDUPS, "speedupVanillaFurnace", true, "Speedup Vanilla Furnace recipe lookup")
                .getBoolean();
        fixJourneymapKeybinds = config.get(
                        CATEGORY_FIXES,
                        "fixJourneymapKeybinds",
                        true,
                        "Prevent unbinded keybinds from triggering when pressing certain keys")
                .getBoolean();
        speedupBOPFogHandling = config.get(
                        CATEGORY_SPEEDUPS,
                        "speedupBOPFogHandling",
                        true,
                        "Speedup biome fog rendering in BiomesOPlenty")
                .getBoolean();

        speedupProgressBar = config.get(CATEGORY_ASM, "speedupProgressBar", true, "Speedup progressbar")
                .getBoolean();
        // Disable for now as it is not compatible with anything modifying RenderBlocks
        pollutionAsm = config.get(CATEGORY_ASM, "pollutionAsm", false, "Enable pollution rendering ASM")
                .getBoolean();
        cofhWorldTransformer = config.get(
                        CATEGORY_ASM,
                        "cofhWorldTransformer",
                        true,
                        "Enable Glease's ASM patch to disable unused CoFH tileentity cache")
                .getBoolean();
        biblocraftRecipes = config.get(
                        CATEGORY_ASM, "biblocraftRecipes", true, "Remove recipe generation from BiblioCraft")
                .getBoolean();

        thermosCraftServerClass = config.get(
                        CATEGORY_ASM,
                        "thermosCraftServerClass",
                        "org.bukkit.craftbukkit.v1_7_R4.CraftServer",
                        "If using Bukkit/Thermos, the CraftServer package.")
                .getString();
        renderDebug = config.get(
                        CATEGORY_DEBUG,
                        "renderDebug",
                        true,
                        "Enable GL state debug hooks. Will not do anything useful unless mode is changed to nonzero.")
                .getBoolean();
        renderDebugMode = config.get(
                        CATEGORY_DEBUG,
                        "renderDebugMode",
                        0,
                        "Default GL state debug mode. 0 - off, 1 - reduced, 2 - full")
                .setMinValue(0)
                .setMaxValue(2)
                .getInt();
        if (config.hasChanged()) config.save();
    }

    public static void postInitClient() {
        // need to be done later cause it initializes classes
        if (config == null) {
            System.err.println("Didn't load HODGEPODGE");
            config = new Configuration(new File(Launch.minecraftHome, "config/hodgepodge.cfg"));
        }

        final String CATEGORY_POLLUTION_RECOLOR = "pollutionrecolor";

        standardBlocks.updateClassList(
                config.get(CATEGORY_POLLUTION_RECOLOR, "renderStandardBlock", defaultPollutionRenderStandardBlock)
                        .getStringList());
        liquidBlocks.updateClassList(
                config.get(CATEGORY_POLLUTION_RECOLOR, "renderBlockLiquid", defaultPollutionRenderLiquidBlocks)
                        .getStringList());
        doublePlants.updateClassList(
                config.get(CATEGORY_POLLUTION_RECOLOR, "renderBlockDoublePlant", defaultPollutionRenderDoublePlant)
                        .getStringList());
        crossedSquares.updateClassList(
                config.get(CATEGORY_POLLUTION_RECOLOR, "renderCrossedSquares", defaultPollutionRenderCrossedSquares)
                        .getStringList());
        blockVine.updateClassList(
                config.get(CATEGORY_POLLUTION_RECOLOR, "renderblockVine", defaultPollutionRenderblockVine)
                        .getStringList());

        config.getCategory(CATEGORY_POLLUTION_RECOLOR).setComment(pollutionRecolorComment);

        if (config.hasChanged()) config.save();
    }

    /*
     * Defaults
     */

    // Default to all non-optional mods being required
    private static final String[] defaultRequiredMods = Arrays.stream(TargetedMod.values())
            .filter(mod -> !mod.optional)
            .map(f -> f.modName)
            .toArray(String[]::new);

    public static final String[] defaultPollutionRenderStandardBlock = new String[] {
        "net.minecraft.block.BlockGrass:GRASS",
        "net.minecraft.block.BlockLeavesBase:LEAVES",
        "biomesoplenty.common.blocks.BlockOriginGrass:GRASS",
        "biomesoplenty.common.blocks.BlockLongGrass:GRASS",
        "biomesoplenty.common.blocks.BlockNewGrass:GRASS",
        "tconstruct.blocks.slime.SlimeGrass:GRASS",
        "thaumcraft.common.blocks.BlockMagicalLeaves:LEAVES",
    };

    public static final String[] defaultPollutionRenderLiquidBlocks = new String[] {
        "net.minecraft.block.BlockLiquid:LIQUID",
    };

    public static final String[] defaultPollutionRenderDoublePlant = new String[] {
        "net.minecraft.block.BlockDoublePlant:FLOWER",
    };

    public static final String[] defaultPollutionRenderCrossedSquares = new String[] {
        "net.minecraft.block.BlockTallGrass:FLOWER",
        "net.minecraft.block.BlockFlower:FLOWER",
        "biomesoplenty.common.blocks.BlockBOPFlower:FLOWER",
        "biomesoplenty.common.blocks.BlockBOPFlower2:FLOWER",
        "biomesoplenty.common.blocks.BlockBOPFoliage:FLOWER",
    };
    public static final String[] defaultPollutionRenderblockVine = new String[] {
        "net.minecraft.block.BlockVine:FLOWER",
    };

    public static final String pollutionRecolorComment =
            "Blocks that should be colored by pollution. \n" + "\tGrouped by the render type. \n"
                    + "\tFormat: [BlockClass]:[colortype] \n"
                    + "\tValid types: GRASS, LEAVES, FLOWER, LIQUID \n"
                    + "\tAdd [-] first to blacklist.";
}
