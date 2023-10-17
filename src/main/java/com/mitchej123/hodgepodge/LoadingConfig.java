package com.mitchej123.hodgepodge;

import java.io.File;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

import com.mitchej123.hodgepodge.util.BlockMatcher;

public class LoadingConfig {

    // Adjustments
    public boolean addSystemInfo;
    public boolean arabicNumbersForEnchantsPotions;
    public int chatLength;
    public int ic2SeedMaxStackSize;
    public int atropineHighID;
    public int itemStacksPickedUpPerTick;
    public int particleLimit;

    // Mixins

    public boolean addCVSupportToWandPedestal;
    public boolean addToggleDebugMessage;
    public boolean deduplicateForestryCompatInBOP;
    public boolean dimensionManagerDebug;
    public boolean disableWitcheryPotionExtender;
    public boolean displayIc2FluidLocalizedName;
    public boolean dropPickedLootOnDespawn;
    public boolean enableDefaultLanPort;

    public boolean changeSprintCategory;
    public boolean enlargePotionArray;
    public boolean fixBetterHUDArmorDisplay;
    public boolean fixBetterHUDHPDisplay;
    public int betterHUDHPRenderLimit;
    public boolean fixBibliocraftPackets;
    public boolean fixBibliocraftPathSanitization;
    public boolean fixChatWrappedColors;
    public boolean fixComponentsPoppingOff;
    public boolean fixContainerPutStacksInSlots;
    public boolean fixDebugBoundingBox;
    public boolean fixDimensionChangeHearts;
    public boolean fixEatingStackedStew;
    public boolean fixEnchantmentNumerals;
    public boolean fixExtraUtilitiesItemRendering;
    public boolean fixExtraUtilitiesUnEnchanting;
    public boolean fixExtraUtilitiesDrumEatingCells;
    public boolean fixFenceConnections;
    public boolean fixFireSpread;
    public boolean fixFluidContainerRegistryKey;
    public boolean fixForgeOpenGuiHandlerWindowId;
    public boolean fixFriendlyCreatureSounds;
    public boolean fixGetBlockLightValue;
    public boolean fixGlStateBugs;
    public boolean fixGuiGameOver;
    public boolean fixHasteArmSwing;
    public boolean fixHopperHitBox;
    public boolean fixHopperVoidingItems;
    public boolean fixHudLightingGlitch;
    public boolean fixHugeChatKick;
    public boolean fixHungerOverhaul;
    public boolean fixHungerOverhaulRestore0Hunger;
    public boolean fixIc2ArmorLag;
    public boolean fixIc2DirectInventoryAccess;
    public boolean fixIc2Hazmat;
    public boolean fixIc2HoverMode;
    public boolean fixIc2Nightvision;
    public boolean fixIc2ReactorDupe;
    public boolean fixIc2UnprotectedGetBlock;
    public boolean fixIgnisFruitAABB;
    public boolean fixImmobileFireballs;
    public boolean fixJourneymapKeybinds;
    public boolean fixJourneymapJumpyScrolling;
    public boolean fixJourneymapFilePath;
    public boolean fixNetHandlerLoginServerOfflineMode;
    public boolean fixNetHandlerPlayClientHandleSetSlot;
    public boolean fixNetherLeavesFaceRendering;
    public boolean fixNorthWestBias;
    public boolean fixOptifineChunkLoadingCrash;
    public boolean fixPerspectiveCamera;
    public boolean fixPlayerSkinFetching;
    public boolean fixPotionEffectNumerals;
    public boolean fixPotionEffectRender;
    public boolean fixPotionIterating;
    public boolean fixPotionLimit;
    public boolean fixPotionRenderOffset;
    public boolean fixResizableFullscreen;
    public boolean fixResourcePackOpening;
    public boolean fixThaumcraftAspectSorting;
    public boolean fixThaumcraftGolemMarkerLoading;
    public boolean fixTimeCommandWithGC;
    public boolean fixUnfocusedFullscreen;
    public boolean fixForgeUpdateChecker;
    public boolean fixUrlDetection;
    public boolean fixVanillaUnprotectedGetBlock;
    public boolean fixVillageUncheckedGetBlock;
    public boolean fixWorldGetBlock;
    public boolean fixWorldServerLeakingUnloadedEntities;
    public boolean fixXaerosWorldMapScroll;
    public boolean fixZTonesPackets;
    public boolean hideCrosshairInThirdPerson;
    public boolean hideIc2ReactorSlots;
    public boolean hidePotionParticlesFromSelf;
    public boolean increaseParticleLimit;
    public boolean installAnchorAlarm;
    public boolean java12BopCompat;
    public boolean java12ImmersiveEngineeringCompat;
    public boolean java12LotrCompat;
    public boolean java12MineChemCompat;
    public boolean logHugeChat;
    public boolean longerChat;
    public boolean longerSentMessages;
    public boolean makeBigFirsPlantable;
    public boolean optimizeASMDataTable;
    public boolean optimizeIc2ReactorInventoryAccess;
    public boolean optimizeTileentityRemoval;
    public boolean optimizeWorldUpdateLight;
    public boolean preventPickupLoot;
    public boolean removeSpawningMinecartSound;
    public boolean removeCreativeSearchTab;

    public boolean compactChat;
    public boolean dontInvertCrosshairColor;
    public boolean enhanceNightVision;
    public boolean enableMacosCmdShortcuts;
    public boolean fixFontRendererLinewrapRecursion;
    public boolean removeUpdateChecks;
    public boolean speedupAnimations;
    public boolean speedupBOPFogHandling;
    public boolean speedupGrassBlockRandomTicking;
    public boolean speedupChunkCoordinatesHashCode;
    public boolean speedupProgressBar;
    public boolean speedupVanillaFurnace;
    public boolean optimizeTextureLoading;
    public boolean squashBedErrorMessage;
    public boolean tcpNoDelay;
    public boolean thirstyTankContainer;
    public boolean throttleItemPickupEvent;
    public boolean transparentChat;
    public boolean triggerAllConflictingKeybindings;
    public boolean unbindKeybindsByDefault;
    public int defaultLanPort;
    public boolean bedMessageAboveHotbar;
    public boolean validatePacketEncodingBeforeSending;
    public boolean validatePacketEncodingBeforeSendingShouldCrash;
    public boolean chunkSaveCMEDebug;
    public boolean ic2CellWithContainer;

    // render debug
    public boolean renderDebug;
    public int renderDebugMode;

    // Pollution :nauseous:
    public boolean furnacesPollute;
    public boolean rocketsPollute;
    public boolean railcraftPollutes;
    public int furnacePollutionAmount;
    public int fireboxPollutionAmount;
    public int rocketPollutionAmount;
    public int cokeOvenPollutionAmount;
    public int advancedCokeOvenPollutionAmount;
    public int hobbyistEnginePollutionAmount;
    public int tunnelBorePollutionAmount;
    public double explosionPollutionAmount;

    // ASM
    public boolean cofhWorldTransformer;
    public boolean enableTileRendererProfiler;
    public boolean pollutionAsm;

    public boolean disableAidSpawnByXUSpikes;

    public String thermosCraftServerClass;

    public static Configuration config;

    public static BlockMatcher standardBlocks = new BlockMatcher();
    public static BlockMatcher liquidBlocks = new BlockMatcher();
    public static BlockMatcher doublePlants = new BlockMatcher();
    public static BlockMatcher crossedSquares = new BlockMatcher();
    public static BlockMatcher blockVine = new BlockMatcher();

    enum Category {

        ASM,
        DEBUG,
        FIXES,
        OVERALL,
        SPEEDUPS,
        TWEAKS,
        POLLUTION_RECOLOR,
        POLLUTION;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public LoadingConfig(File file) {
        config = new Configuration(file);

        // spotless:off

        addCVSupportToWandPedestal = config.get(Category.TWEAKS.toString(), "addCVSupportToWandPedestal", true, "Add CV support to Thaumcraft wand recharge pedestal").getBoolean();
        addSystemInfo = config.get(Category.TWEAKS.toString(), "addSystemInfo", true, "Adds system info to the F3 overlay (Java version and vendor; GPU name; OpenGL version; CPU cores; OS name, version and architecture)").getBoolean();
        addToggleDebugMessage = config.get(Category.TWEAKS.toString(), "addToggleDebugMessage", true, "Add a debug message in the chat when toggling vanilla debug options").getBoolean();
        arabicNumbersForEnchantsPotions = config.get(Category.TWEAKS.toString(), "arabicNumbersForEnchantsPotions", false, "Uses arabic numbers for enchantment levels and potion amplifier levels instead of roman numbers").getBoolean();
        bedMessageAboveHotbar = config.get(Category.TWEAKS.toString(), "bedMessageAboveHotbar", true, "Show \"cannot sleep\" messages above hotbar").getBoolean();
        chatLength = Math.max(100, Math.min(32767, config.get(Category.TWEAKS.toString(), "chatLength", 8191, "Amount of chat lines kept [100(Vanilla) - 32767]").getInt()));
        cofhWorldTransformer = config.get(Category.ASM.toString(), "cofhWorldTransformer", true, "Enable Glease's ASM patch to disable unused CoFH tileentity cache").getBoolean();
        deduplicateForestryCompatInBOP = config.get(Category.FIXES.toString(), "deduplicateForestryCompatInBOP", true, "Removes duplicate Fermenter and Squeezer recipes and flower registration").getBoolean();
        defaultLanPort = config.get(Category.TWEAKS.toString(), "defaultLanPort", 25565, "Specify default LAN port to open an integrated server on. Set to 0 to always open the server on an automatically allocated port.").getInt();
        dimensionManagerDebug = config.get(Category.DEBUG.toString(), "dimensionManagerDebug", true, "Prints debug log if DimensionManager got crashed").getBoolean();
        disableWitcheryPotionExtender = config.get(Category.FIXES.toString(), "disableWitcheryPotionExtender", true, "Disable Witchery potion extender for Java 12 compat").getBoolean();
        displayIc2FluidLocalizedName = config.get(Category.TWEAKS.toString(), "displayIc2FluidLocalizedName", true, "Display fluid localized name in IC2 fluid cell tooltip").getBoolean();
        dropPickedLootOnDespawn = config.get(Category.TWEAKS.toString(), "dropPickedLootOnDespawn", true, "Drop picked loot on entity despawn").getBoolean();
        enableDefaultLanPort = config.get(Category.TWEAKS.toString(), "enableDefaultLanPort", true, "Open an integrated server on a static port.").getBoolean();
        enableTileRendererProfiler = config.get(Category.TWEAKS.toString(), "enableTileRendererProfiler", true, "Shows renderer's impact on FPS in vanilla lagometer").getBoolean();

        changeSprintCategory = config.get(Category.TWEAKS.toString(), "changeSprintCategory", true, "Moves the sprint keybind to the movement category").getBoolean();
        fixContainerPutStacksInSlots = config.get(Category.FIXES.toString(), "fixContainerPutStacksInSlots", true, "Prevents crash if server sends container with wrong itemStack size").getBoolean();
        fixChatWrappedColors = config.get(Category.FIXES.toString(), "fixChatWrappedColors", true, "Fix wrapped chat lines missing colors").getBoolean();
        fixComponentsPoppingOff = config.get(Category.TWEAKS.toString(), "fixComponentsPoppingOff", true, "Fix Project Red components popping off on unloaded chunks").getBoolean();
        fixDebugBoundingBox = config.get(Category.FIXES.toString(), "fixDebugBoundingBox", true, "Fixes the debug hitbox of the player beeing offset").getBoolean();
        fixDimensionChangeHearts = config.get(Category.FIXES.toString(), "fixDimensionChangeHearts", true, "Fix losing bonus hearts on dimension change").getBoolean();
        fixEatingStackedStew = config.get(Category.FIXES.toString(), "fixEatingStackedStew", true, "Fix deleting stack when eating mushroom stew").getBoolean();
        fixEnchantmentNumerals = config.get(Category.FIXES.toString(), "fixEnchantmentNumerals", true, "Fix enchantment levels not displaying properly above a certain value").getBoolean();
        fixExtraUtilitiesItemRendering = config.get(Category.FIXES.toString(), "fixExtraUtilitiesItemRendering", true, "Fixes rendering issues with transparent items from extra utilities").getBoolean();
        fixExtraUtilitiesUnEnchanting = config.get(Category.FIXES.toString(), "fixExtraUtilitiesUnEnchanting", true, "Fix dupe bug with division sigil removing enchantment").getBoolean();
        fixExtraUtilitiesDrumEatingCells = config.get(Category.FIXES.toString(), "fixExtraUtilitiesDrumEatingCells", true, "Fix Extra Utilities drums eating ic2 cells and forestry capsules").getBoolean();
        fixFenceConnections = config.get(Category.FIXES.toString(), "fixFenceConnections", true, "Fix fence connections with other types of fence").getBoolean();
        fixFireSpread = config.get(Category.FIXES.toString(), "fixFireSpread", true, "Fix vanilla fire spread sometimes cause NPE on thermos").getBoolean();
        fixFluidContainerRegistryKey = config.get(Category.FIXES.toString(), "fixFluidContainerRegistryKey", true, "Fix Forge fluid container registry key").getBoolean();
        fixFontRendererLinewrapRecursion = config.get(Category.FIXES.toString(), "fixFontRendererLinewrapRecursion", true, "Replace recursion with iteration in FontRenderer line wrapping code").getBoolean();
        fixForgeOpenGuiHandlerWindowId = config.get(Category.FIXES.toString(), "fixForgeOpenGuiHandlerWindowId", true, "Fix windowId being set on openContainer even if openGui failed").getBoolean();
        fixForgeUpdateChecker = config.get(Category.FIXES.toString(), "fixForgeUpdateChecker", true, "Fix the forge update checker").getBoolean();
        fixFriendlyCreatureSounds = config.get(Category.FIXES.toString(), "fixFriendlyCreatureSounds", true, "Fix vanilla issue where player sounds register as animal sounds").getBoolean();
        fixGetBlockLightValue = config.get(Category.FIXES.toString(), "fixGetBlockLightValue", true, "Fix vanilla light calculation sometimes cause NPE on thermos").getBoolean();
        fixGlStateBugs = config.get(Category.FIXES.toString(), "fixGlStateBugs", true, "Fix vanilla GL state bugs causing lighting glitches in various perspectives (MC-10135).").getBoolean();
        fixGuiGameOver = config.get(Category.FIXES.toString(), "fixGuiGameOver", true, "Fix Game Over GUI buttons disabled if switching fullscreen").getBoolean();
        fixHasteArmSwing = config.get(Category.FIXES.toString(), "fixHasteArmSwing", true, "Fix arm not swinging when having too much haste").getBoolean();
        fixTimeCommandWithGC = config.get(Category.FIXES.toString(), "fixTimeCommandWithGC", true, "Fix time commands with GC").getBoolean();
        fixBetterHUDArmorDisplay = config.get(Category.FIXES.toString(), "fixBetterHUDArmorDisplay",true, "Fix BetterHUD armor bar rendering breaking with skulls").getBoolean();
        fixBetterHUDHPDisplay = config.get(Category.FIXES.toString(), "fixBetterHUDHPDisplay",true, "Fix BetterHUD freezing the game when trying to render high amounts of hp").getBoolean();
        betterHUDHPRenderLimit = config.get(Category.FIXES.toString(),"betterHUDHPRenderLimit", 5000, "Maximum hp for BetterHUD to render as hearts").getInt();
        fixBibliocraftPackets = config.get(Category.FIXES.toString(), "fixBibliocraftPackets", true, "Fix Bibliocraft packet exploits").getBoolean();
        fixBibliocraftPathSanitization = config.get(Category.FIXES.toString(), "fixBibliocraftPathSanitization", true, "Fix Bibliocraft path sanitization").getBoolean();
        fixZTonesPackets = config.get(Category.FIXES.toString(), "fixZTonesPackets", true, "Fix ZTones packet exploits").getBoolean();
        fixHopperHitBox = config.get(Category.FIXES.toString(), "fixHopperHitBox", true, "Fix vanilla hopper hit box").getBoolean();
        fixHopperVoidingItems = config.get(Category.FIXES.toString(), "fixHopperVoidingItems", true, "Fix Drawer + Hopper voiding items").getBoolean();
        fixHudLightingGlitch = config.get(Category.TWEAKS.toString(), "fixHudLightingGlitch", true, "Fix hotbars being dark when Project Red is installed").getBoolean();
        fixHugeChatKick = config.get(Category.FIXES.toString(), "fixHugeChatKick", true, "Fix oversized chat message kicking player.").getBoolean();
        fixHungerOverhaul = config.get(Category.FIXES.toString(), "fixHungerOverhaul", true, "Fix hunger overhaul low stat effects").getBoolean();
        fixHungerOverhaulRestore0Hunger = config.get(Category.FIXES.toString(), "fixHungerOverhaulRestore0Hunger", true, "Fix some items restore 0 hunger").getBoolean();
        fixIc2ArmorLag = config.get(Category.FIXES.toString(), "fixIc2ArmorLag", true, "Fix lag caused by IC2 armor tick").getBoolean();
        fixIc2DirectInventoryAccess = config.get(Category.FIXES.toString(), "fixIc2DirectInventoryAccess", true, "Fix IC2's direct inventory access").getBoolean();
        fixIc2Hazmat = config.get(Category.FIXES.toString(), "fixIc2Hazmat", true, "Fix IC2 armors to avoid giving poison").getBoolean();
        fixIc2HoverMode = config.get(Category.FIXES.toString(), "fixIc2HoverMode", true, "Fix IC2's armor hover mode").getBoolean();
        fixIc2Nightvision = config.get(Category.FIXES.toString(), "fixIc2Nightvision", true, "Prevent IC2's nightvision from blinding you").getBoolean();
        fixIc2ReactorDupe = config.get(Category.FIXES.toString(), "fixIc2ReactorDupe", true, "Fix IC2's reactor dupe").getBoolean();
        fixIc2UnprotectedGetBlock = config.get(Category.FIXES.toString(), "fixIc2UnprotectedGetBlock", true, "Fixes various unchecked IC2 getBlock() methods").getBoolean();
        fixIgnisFruitAABB = config.get(Category.FIXES.toString(), "fixIgnisFruitAABB", true, "Fix Axis aligned Bounding Box of Ignis Fruit").getBoolean();
        fixImmobileFireballs = config.get(Category.FIXES.toString(), "fixImmobileFireballs", true, "Fix the bug that makes fireballs stop moving when chunk unloads").getBoolean();
        fixJourneymapKeybinds = config.get(Category.FIXES.toString(), "fixJourneymapKeybinds", true, "Prevent unbinded keybinds from triggering when pressing certain keys").getBoolean();
        fixJourneymapJumpyScrolling = config.get(Category.FIXES.toString(), "fixJourneymapJumpyScrolling", true, "Fix jumpy scrolling in the waypoint manager screen").getBoolean();
        fixJourneymapFilePath = config.get(Category.FIXES.toString(), "fixJourneymapFilePath", true, "Prevents journeymap from using illegal character in file paths").getBoolean();
        fixNetHandlerLoginServerOfflineMode = config.get(Category.FIXES.toString(), "fixNetHandlerLoginServerOfflineMode", true, "Allows the server to assign the logged in UUID to the same username when online_mode is false").getBoolean();
        fixNetHandlerPlayClientHandleSetSlot = config.get(Category.FIXES.toString(), "fixNetHandlerPlayClientHandleSetSlot", true, "Prevents crash if server sends itemStack with index larger than client's container").getBoolean();
        fixNetherLeavesFaceRendering = config.get(Category.FIXES.toString(), "fixNetherLeavesFaceRendering", true, "If fancy graphics are enabled, Nether Leaves render sides with other Nether Leaves adjacent too").getBoolean();
        fixNorthWestBias = config.get(Category.FIXES.toString(), "fixNorthWestBias", true, "Fix northwest bias on RandomPositionGenerator").getBoolean();
        fixOptifineChunkLoadingCrash = config.get(Category.FIXES.toString(), "fixOptifineChunkLoadingCrash", true, "Forces the chunk loading option from optifine to default since other values can crash the game").getBoolean();
        fixPerspectiveCamera = config.get(Category.FIXES.toString(), "fixPerspectiveCamera", true, "Prevent tall grass and such to affect the perspective camera").getBoolean();
        fixPlayerSkinFetching = config.get(Category.FIXES.toString(), "fixPlayerSkinFetching", true, "Allow some mods to properly fetch the player skin").getBoolean();
        fixPotionEffectNumerals = config.get(Category.FIXES.toString(), "fixPotionEffectNumerals", true, "Properly display level of potion effects in the inventory and on tooltips").getBoolean();
        fixPotionEffectRender = config.get(Category.TWEAKS.toString(), "fixPotionEffectRender", true, "Fix vanilla potion effects rendering above the NEI tooltips in the inventory").getBoolean();
        fixPotionIterating = config.get(Category.FIXES.toString(), "fixPotionIterating", true, "Fix crashes with ConcurrentModificationException because of incorrectly iterating over active potions").getBoolean();
        enlargePotionArray = config.get(Category.FIXES.toString(), "enlargePotionArray", true, "Safely enlarge the potion array before other mods").getBoolean();
        fixPotionLimit = config.get(Category.FIXES.toString(), "fixPotionLimit", true, "Fix potions >= 128").getBoolean();
        fixPotionRenderOffset = config.get(Category.TWEAKS.toString(), "fixPotionRenderOffset", true, "Prevents the inventory from shifting when the player has active potion effects").getBoolean();
        fixResizableFullscreen = config.get(Category.FIXES.toString(), "fixResizableFullscreen", true, "Fix game window becoming not resizable after toggling fullscrean in any way").getBoolean();
        fixResourcePackOpening = config.get(Category.FIXES.toString(), "fixResourcePackOpening", true, "Fix resource pack folder not opening on Windows if file path has a space").getBoolean();
        fixThaumcraftAspectSorting = config.get(Category.FIXES.toString(), "fixThaumcraftAspectSorting", true, "Fix Thaumcraft Aspects being sorted by tag instead of by name").getBoolean();
        fixThaumcraftGolemMarkerLoading = config.get(Category.FIXES.toString(), "fixThaumcraftGolemMarkerLoading", true, "Fix golem's marker loading failure when dimensionId larger than MAX_BYTE").getBoolean();
        fixUnfocusedFullscreen = config.get(Category.FIXES.toString(), "fixUnfocusedFullscreen", true, "Fix exiting fullscreen when you tab out of the game").getBoolean();
        fixUrlDetection = config.get(Category.FIXES.toString(), "fixUrlDetection", true, "Fix URISyntaxException in forge.").getBoolean();
        fixVanillaUnprotectedGetBlock = config.get(Category.FIXES.toString(), "fixVanillaUnprotectedGetBlock", true, "Fixes various unchecked vanilla getBlock() methods").getBoolean();
        fixVillageUncheckedGetBlock = config.get(Category.FIXES.toString(), "fixVillageUncheckedGetBlock", true, "Fixes village unchecked getBlock() calls").getBoolean();
        fixWorldGetBlock = config.get(Category.FIXES.toString(), "fixWorldGetBlock", true, "Fix unprotected getBlock() in World").getBoolean();
        fixWorldServerLeakingUnloadedEntities = config.get(Category.FIXES.toString(), "fixWorldServerLeakingUnloadedEntities", true, "Fix WorldServer leaking entities when no players are present in a dimension").getBoolean();
        fixXaerosWorldMapScroll = config.get(Category.FIXES.toString(), "fixXaerosWorldMapScrolling", true, "Fix scrolling in the world map screen").getBoolean();
        validatePacketEncodingBeforeSending = config.get(Category.FIXES.toString(), "validatePacketEncodingBeforeSending", true, "Validate vanilla packet encodings before sending in addition to on reception").getBoolean();
        validatePacketEncodingBeforeSendingShouldCrash = config.get(Category.FIXES.toString(), "validatePacketEncodingBeforeSendingShouldCrash", false, "Should the extended packet validation error cause a crash (true) or just print out an error to the log (false)").getBoolean();

        compactChat = config.get(Category.TWEAKS.toString(), "Compact chat", true, "Compacts identical consecutive chat messages together").getBoolean();
        dontInvertCrosshairColor = config.get(Category.TWEAKS.toString(), "dontInvertCrosshairColor", false, "Stop inverting colors of crosshair").getBoolean();
        enhanceNightVision = config.get(Category.TWEAKS.toString(), "enhanceNightVision", false, "Remove the blueish sky tint from night vision").getBoolean();
        hideCrosshairInThirdPerson = config.get(Category.TWEAKS.toString(), "hideCrosshairInThirdPerson", true, "Stops rendering the crosshair when you are playing in third person").getBoolean();
        hideIc2ReactorSlots = config.get(Category.TWEAKS.toString(), "hideIc2ReactorSlots", true, "Prevent IC2's reactor's coolant slots from being accessed by automations if not a fluid reactor").getBoolean();
        hidePotionParticlesFromSelf = config.get(Category.TWEAKS.toString(), "hidePotionParticlesFromSelf", true, "Stops rendering potion particles from yourself").getBoolean();
        ic2SeedMaxStackSize = config.get(Category.TWEAKS.toString(), "ic2SeedMaxStackSize", 64, "IC2 seed max stack size", 1, 64).getInt();
        atropineHighID = config.get(Category.TWEAKS.toString(), "atropineHighID", 255, "Minechem Atropine High (Delirium) effect ID", 1, 255).getInt();
        increaseParticleLimit = config.get(Category.TWEAKS.toString(), "increaseParticleLimit", true, "Increase particle limit").getBoolean();
        installAnchorAlarm = config.get(Category.TWEAKS.toString(), "installAnchorAlarm", true, "Wake up passive & personal anchors on player login").getBoolean();
        itemStacksPickedUpPerTick = Math.max(1, config.get(Category.FIXES.toString(), "itemStacksPickedUpPerTick", 36, "Stacks picked up per tick").getInt());
        java12BopCompat = config.get(Category.FIXES.toString(), "java12BopCompat", true, "BiomesOPlenty Java 12 compatibility patches.").getBoolean();
        java12ImmersiveEngineeringCompat = config.get(Category.FIXES.toString(), "java12ImmersiveEngineeringCompat", true, "Immersive Engineering Java 12 compatibility patch").getBoolean();
        java12LotrCompat = config.get(Category.FIXES.toString(), "java12LotrCompat", true, "Lotr Java 12 compatibility patch").getBoolean();
        java12MineChemCompat = config.get(Category.FIXES.toString(), "java12MineChemCompat", true, "Minechem Java 12 compatibility patch").getBoolean();
        logHugeChat = config.get(Category.FIXES.toString(), "logHugeChat", true, "Log oversized chat message to console. WARNING: might create huge log files if this happens very often.").getBoolean();
        longerChat = config.get(Category.TWEAKS.toString(), "longerChat", true, "Makes the chat history longer instead of 100 lines").getBoolean();
        longerSentMessages = config.get(Category.TWEAKS.toString(), "longerSentMessages", true, "Allows you to send longer chat messages, up to 256 characters, instead of 100 in vanilla.").getBoolean();
        makeBigFirsPlantable = config.get(Category.TWEAKS.toString(), "makeBigFirsPlantable", true, "Allow 5 Fir Sapling planted together ('+' shape) to grow to a big fir tree").getBoolean();
        optimizeASMDataTable = config.get(Category.SPEEDUPS.toString(), "optimizeASMDataTable", true, "Optimize ASMDataTable getAnnotationsFor for faster startup").getBoolean();
        optimizeIc2ReactorInventoryAccess = config.get(Category.FIXES.toString(), "optimizeIc2ReactorInventoryAccess", true, "Optimize inventory access to IC2 nuclear reactor").getBoolean();
        optimizeTileentityRemoval = config.get(Category.SPEEDUPS.toString(), "optimizeTileentityRemoval", true, "Optimize tileEntity removal in World.class").getBoolean();
        optimizeWorldUpdateLight = config.get(Category.FIXES.toString(), "optimizeWorldUpdateLight", true, "Fix too early light initialization").getBoolean();
        particleLimit = Math.max(Math.min(config.get(Category.TWEAKS.toString(), "particleLimit", 8000, "Particle limit [4000-16000]").getInt(), 16000), 4000);
        preventPickupLoot = config.get(Category.TWEAKS.toString(), "preventPickupLoot", true, "Prevent monsters from picking up loot.").getBoolean();
        enableMacosCmdShortcuts = config.get(Category.TWEAKS.toString(), "enableMacosCmdShortcuts", true, "Use CMD key on MacOS to COPY / INSERT / SELECT in text fields (Chat, NEI, Server IP etc.)").getBoolean();
        removeSpawningMinecartSound = config.get(Category.TWEAKS.toString(), "removeSpawningMinecartSound", true, "Stop playing a sound when spawning a minecart in the world").getBoolean();
        removeCreativeSearchTab = config.get(Category.FIXES.toString(), "removeCreativeSearchTab", true, "Disable the creative search tab since it can be very laggy in large modpacks").getBoolean();
        removeUpdateChecks = config.get(Category.FIXES.toString(), "removeUpdateChecks", true, "Remove old/stale/outdated update checks.").getBoolean();
        chunkSaveCMEDebug = config.get(Category.DEBUG.toString(), "chunkSaveCMEDebug", false, "Enable chunk save cme debugging code.").getBoolean();
        renderDebug = config.get(Category.DEBUG.toString(), "renderDebug", true, "Enable GL state debug hooks. Will not do anything useful unless mode is changed to nonzero.").getBoolean();
        renderDebugMode = config.get(Category.DEBUG.toString(), "renderDebugMode", 0, "Default GL state debug mode. 0 - off, 1 - reduced, 2 - full").setMinValue(0).setMaxValue(2).getInt();
        speedupAnimations = config.get(Category.FIXES.toString(), "speedupAnimations", true, "Drastically speedup animated textures (Basically the same as with optifine animations off but animations are working)").getBoolean();
        speedupBOPFogHandling = config.get(Category.SPEEDUPS.toString(), "speedupBOPFogHandling", true, "Speedup biome fog rendering in BiomesOPlenty").getBoolean();
        speedupGrassBlockRandomTicking = config.get(Category.SPEEDUPS.toString(), "speedupGrassBlockRandomTicking", true, "Speed up grass block random ticking").getBoolean();
        speedupChunkCoordinatesHashCode = config.get(Category.SPEEDUPS.toString(), "speedupChunkCoordinatesHashCode", true, "Speedup ChunkCoordinates hashCode").getBoolean();
        speedupProgressBar = config.get(Category.ASM.toString(), "speedupProgressBar", true, "Speedup progressbar").getBoolean();
        speedupVanillaFurnace = config.get(Category.SPEEDUPS.toString(), "speedupVanillaFurnace", true, "Speedup Vanilla Furnace recipe lookup").getBoolean();
        optimizeTextureLoading = config.get(Category.SPEEDUPS.toString(), "optimizeTextureLoading", true, "Optimize texture loading").getBoolean();
        squashBedErrorMessage = config.get(Category.FIXES.toString(), "squashBedErrorMessage", true, "Stop \"You can only sleep at night\" message filling the chat").getBoolean();
        tcpNoDelay = config.get(Category.SPEEDUPS.toString(), "tcpNoDelay", true, "Sets TCP_NODELAY to true, reducing network latency in multiplayer. Works on server as well as client. From makamys/CoreTweaks").getBoolean();
        thermosCraftServerClass = config.get(Category.ASM.toString(), "thermosCraftServerClass", "org.bukkit.craftbukkit.v1_7_R4.CraftServer", "If using Bukkit/Thermos, the CraftServer package.").getString();
        thirstyTankContainer = config.get(Category.TWEAKS.toString(), "thirstyTankContainer", true, "Implement container for thirsty tank").getBoolean();
        throttleItemPickupEvent = config.get(Category.FIXES.toString(), "throttleItemPickupEvent", true, "Limits the amount of times the ItemPickupEvent triggers per tick since it can lead to a lot of lag").getBoolean();
        transparentChat = config.get(Category.TWEAKS.toString(), "transparentChat", true, "Doesn't render the black box behind messages when the chat is closed").getBoolean();
        triggerAllConflictingKeybindings = config.get(Category.FIXES.toString(), "triggerAllConflictingKeybindings", true, "Triggers all conflicting key bindings on key press instead of a random one").getBoolean();
        unbindKeybindsByDefault = config.get(Category.TWEAKS.toString(), "unbindKeybindsByDefault", true, "Unbinds keybinds of certain ARR mods to avoid keybinds conflicts").getBoolean();
        ic2CellWithContainer = config.get(Category.TWEAKS.toString(), "ic2CellWithContainer ", false, "give ic2 cells containers like gregtech cells do").getBoolean();

        // Disable for now as it is not compatible with anything modifying RenderBlocks
        pollutionAsm = config.get(Category.ASM.toString(), "pollutionAsm", false, "Enable pollution rendering ASM").getBoolean();

        // Pollution :nauseous:
        furnacesPollute = config.get(Category.POLLUTION.toString(), "furnacesPollute", true, "Make furnaces Pollute").getBoolean();
        rocketsPollute = config.get(Category.POLLUTION.toString(), "rocketsPollute", true, "Make rockets Pollute").getBoolean();
        railcraftPollutes = config.get(Category.POLLUTION.toString(), "railcraftPollutes", true, "Make Railcraft Pollute").getBoolean();

        disableAidSpawnByXUSpikes = config.get(Category.TWEAKS.toString(), "disableAidSpawnByXUSpikes", true, "Disables the spawn of zombie aid when zombie is killed by Extra Utilities Spikes, since it can spawn them too far.").getBoolean();

        furnacePollutionAmount = config.get(Category.POLLUTION.toString(), "furnacePollution", 20, "Furnace pollution per second, min 1!", 1, Integer.MAX_VALUE).getInt();
        fireboxPollutionAmount = config.get(Category.POLLUTION.toString(), "fireboxPollution", 15, "Pollution Amount for RC Firebox", 1, Integer.MAX_VALUE).getInt();
        rocketPollutionAmount = config.get(Category.POLLUTION.toString(), "rocketPollution", 1000, "Pollution Amount for Rockets", 1, Integer.MAX_VALUE).getInt();
        cokeOvenPollutionAmount = config.get(Category.POLLUTION.toString(), "cokeOvenPollution", 3, "Pollution Amount for Coke Ovens", 1, Integer.MAX_VALUE).getInt();
        advancedCokeOvenPollutionAmount = config.get(Category.POLLUTION.toString(), "advancedCokeOvenPollution", 80, "Pollution Amount for Advanced Coke Ovens", 1, Integer.MAX_VALUE).getInt();
        hobbyistEnginePollutionAmount = config.get(Category.POLLUTION.toString(), "hobbyistEnginePollution", 20, "Pollution Amount for hobbyist steam engine", 1, Integer.MAX_VALUE).getInt();
        tunnelBorePollutionAmount = config.get(Category.POLLUTION.toString(), "tunnelBorePollution", 2, "Pollution Amount for tunnel bore", 1, Integer.MAX_VALUE).getInt();
        explosionPollutionAmount = config.get(Category.POLLUTION.toString(), "explosionPollution", 33.34, "Explosion pollution").getDouble();


        // spotless:on
        if (config.hasChanged()) config.save();
    }

    public static void postInitClient() {
        // need to be done later cause it initializes classes
        if (config == null) {
            System.err.println("Didn't load HODGEPODGE");
            config = new Configuration(new File(Launch.minecraftHome, "config/hodgepodge.cfg"));
        }

        // spotless:off
        standardBlocks.updateClassList(config.get(Category.POLLUTION_RECOLOR.toString(), "renderStandardBlock", defaultPollutionRenderStandardBlock).getStringList());
        liquidBlocks.updateClassList(config.get(Category.POLLUTION_RECOLOR.toString(), "renderBlockLiquid", defaultPollutionRenderLiquidBlocks).getStringList());
        doublePlants.updateClassList(config.get(Category.POLLUTION_RECOLOR.toString(), "renderBlockDoublePlant", defaultPollutionRenderDoublePlant).getStringList());
        crossedSquares.updateClassList(config.get(Category.POLLUTION_RECOLOR.toString(), "renderCrossedSquares", defaultPollutionRenderCrossedSquares).getStringList());
        blockVine.updateClassList(config.get(Category.POLLUTION_RECOLOR.toString(), "renderblockVine", defaultPollutionRenderblockVine).getStringList());

        config.getCategory(Category.POLLUTION_RECOLOR.toString()).setComment(pollutionRecolorComment);
        // spotless:on
        if (config.hasChanged()) config.save();
    }

    /*
     * Defaults
     */

    public static final String[] defaultPollutionRenderStandardBlock = new String[] {
            "net.minecraft.block.BlockGrass:GRASS", "net.minecraft.block.BlockLeavesBase:LEAVES",
            "biomesoplenty.common.blocks.BlockOriginGrass:GRASS", "biomesoplenty.common.blocks.BlockLongGrass:GRASS",
            "biomesoplenty.common.blocks.BlockNewGrass:GRASS", "tconstruct.blocks.slime.SlimeGrass:GRASS",
            "thaumcraft.common.blocks.BlockMagicalLeaves:LEAVES", };

    public static final String[] defaultPollutionRenderLiquidBlocks = new String[] {
            "net.minecraft.block.BlockLiquid:LIQUID", };

    public static final String[] defaultPollutionRenderDoublePlant = new String[] {
            "net.minecraft.block.BlockDoublePlant:FLOWER", };

    public static final String[] defaultPollutionRenderCrossedSquares = new String[] {
            "net.minecraft.block.BlockTallGrass:FLOWER", "net.minecraft.block.BlockFlower:FLOWER",
            "biomesoplenty.common.blocks.BlockBOPFlower:FLOWER", "biomesoplenty.common.blocks.BlockBOPFlower2:FLOWER",
            "biomesoplenty.common.blocks.BlockBOPFoliage:FLOWER", };
    public static final String[] defaultPollutionRenderblockVine = new String[] {
            "net.minecraft.block.BlockVine:FLOWER", };

    public static final String pollutionRecolorComment = "Blocks that should be colored by pollution. \n"
            + "\tGrouped by the render type. \n"
            + "\tFormat: [BlockClass]:[colortype] \n"
            + "\tValid types: GRASS, LEAVES, FLOWER, LIQUID \n"
            + "\tAdd [-] first to blacklist.";
}
