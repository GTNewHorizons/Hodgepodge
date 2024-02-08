package com.mitchej123.hodgepodge.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "hodgepodge", category = "fixes")
public class FixesConfig {

    @Config.Comment("Fix too many allocations from Chunk Coordinate Int Pair")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixTooManyAllocationsChunkPositionIntPair;
    @Config.Comment("Removes duplicate Fermenter and Squeezer recipes and flower registration")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean deduplicateForestryCompatInBOP;
    @Config.Comment("Disable Witchery potion extender for Java 12 compat")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean disableWitcheryPotionExtender;
    @Config.Comment("Fixes Witchery player skins reflections with inhabited mirrors")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixWitcheryReflections;
    @Config.Comment("Prevents crash if server sends container with wrong itemStack size")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixContainerPutStacksInSlots;
    @Config.Comment("Fix wrapped chat lines missing colors")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixChatWrappedColors;
    @Config.Comment("Fixes the debug hitbox of the player beeing offset")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixDebugBoundingBox;
    @Config.Comment("Fix losing bonus hearts on dimension change")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixDimensionChangeHearts;
    @Config.Comment("Fix deleting stack when eating mushroom stew")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixEatingStackedStew;
    @Config.Comment("Fix enchantment levels not displaying properly above a certain value")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixEnchantmentNumerals;
    @Config.Comment("Fixes rendering issues with transparent items from extra utilities")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixExtraUtilitiesItemRendering;
    @Config.Comment("Fix dupe bug with division sigil removing enchantment")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixExtraUtilitiesUnEnchanting;
    @Config.Comment("Fix Extra Utilities drums eating ic2 cells and forestry capsules")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixExtraUtilitiesDrumEatingCells;
    @Config.Comment("Fix fence connections with other types of fence")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixFenceConnections;
    @Config.Comment("Fix vanilla fire spread sometimes cause NPE on thermos")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixFireSpread;
    @Config.Comment("Fix Forge fluid container registry key")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixFluidContainerRegistryKey;
    @Config.Comment("Replace recursion with iteration in FontRenderer line wrapping code")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixFontRendererLinewrapRecursion;
    @Config.Comment("Fix windowId being set on openContainer even if openGui failed")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixForgeOpenGuiHandlerWindowId;
    @Config.Comment("Fix the forge update checker")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixForgeUpdateChecker;
    @Config.Comment("Fix vanilla issue where player sounds register as animal sounds")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixFriendlyCreatureSounds;
    @Config.Comment("Fix vanilla light calculation sometimes cause NPE on thermos")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixGetBlockLightValue;
    @Config.Comment("Fix vanilla GL state bugs causing lighting glitches in various perspectives (MC-10135).")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixGlStateBugs;
    @Config.Comment("Fix Game Over GUI buttons disabled if switching fullscreen")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixGuiGameOver;
    @Config.Comment("Fix arm not swinging when having too much haste")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixHasteArmSwing;
    @Config.Comment("Fix time commands with GC")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixTimeCommandWithGC;
    @Config.Comment("Fix BetterHUD armor bar rendering breaking with skulls")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixBetterHUDArmorDisplay;
    @Config.Comment("Fix BetterHUD freezing the game when trying to render high amounts of hp")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixBetterHUDHPDisplay;
    @Config.Comment("Fix Bibliocraft packet exploits")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixBibliocraftPackets;
    @Config.Comment("Fix Bibliocraft path sanitization")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixBibliocraftPathSanitization;
    @Config.Comment("Fix ZTones packet exploits")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixZTonesPackets;
    @Config.Comment("Fix vanilla hopper hit box")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixHopperHitBox;
    @Config.Comment("Fix Drawer + Hopper voiding items")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixHopperVoidingItems;
    @Config.Comment("Fix oversized chat message kicking player.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixHugeChatKick;
    @Config.Comment("Fix hunger overhaul low stat effects")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixHungerOverhaul;
    @Config.Comment("Fix some items restore 0 hunger")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixHungerOverhaulRestore0Hunger;
    @Config.Comment("Fix lag caused by IC2 armor tick")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixIc2ArmorLag;
    @Config.Comment("Fix IC2's direct inventory access")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixIc2DirectInventoryAccess;
    @Config.Comment("Fix IC2 armors to avoid giving poison")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixIc2Hazmat;
    @Config.Comment("Fix IC2's armor hover mode")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixIc2HoverMode;
    @Config.Comment("Prevent IC2's nightvision from blinding you")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixIc2Nightvision;
    @Config.Comment("Fix IC2's reactor dupe")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixIc2ReactorDupe;
    @Config.Comment("Fixes various unchecked IC2 getBlock() methods")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixIc2UnprotectedGetBlock;
    @Config.Comment("Fix Axis aligned Bounding Box of Ignis Fruit")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixIgnisFruitAABB;
    @Config.Comment("Fix the bug that makes fireballs stop moving when chunk unloads")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixImmobileFireballs;
    @Config.Comment("Prevent unbinded keybinds from triggering when pressing certain keys")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixJourneymapKeybinds;
    @Config.Comment("Fix jumpy scrolling in the waypoint manager screen")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixJourneymapJumpyScrolling;
    @Config.Comment("Prevents journeymap from using illegal character in file paths")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixJourneymapFilePath;
    @Config.Comment("Allows the server to assign the logged in UUID to the same username when online_mode is false")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixNetHandlerLoginServerOfflineMode;
    @Config.Comment("Prevents crash if server sends itemStack with index larger than client's container")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixNetHandlerPlayClientHandleSetSlot;
    @Config.Comment("If fancy graphics are enabled, Nether Leaves render sides with other Nether Leaves adjacent too")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixNetherLeavesFaceRendering;
    @Config.Comment("Fix northwest bias on RandomPositionGenerator")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixNorthWestBias;
    @Config.Comment("Fix NPE in Netty's Bootstrap class")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixNettyNPE;
    @Config.Comment("Forces the chunk loading option from optifine to default since other values can crash the game")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixOptifineChunkLoadingCrash;
    @Config.Comment("Prevent tall grass and such to affect the perspective camera")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixPerspectiveCamera;
    @Config.Comment("Allow some mods to properly fetch the player skin")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixPlayerSkinFetching;
    @Config.Comment("Properly display level of potion effects in the inventory and on tooltips")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixPotionEffectNumerals;
    @Config.Comment("Fix crashes with ConcurrentModificationException because of incorrectly iterating over active potions")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixPotionIterating;
    @Config.Comment("Safely enlarge the potion array before other mods")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enlargePotionArray;
    @Config.Comment("Fix potions >= 128")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixPotionLimit;
    @Config.Comment("Fix game window becoming not resizable after toggling fullscrean in any way")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixResizableFullscreen;
    @Config.Comment("Fix resource pack folder not opening on Windows if file path has a space")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixResourcePackOpening;
    @Config.Comment("Fix Thaumcraft Aspects being sorted by tag instead of by name")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixThaumcraftAspectSorting;
    @Config.Comment("Fix golem's marker loading failure when dimensionId larger than MAX_BYTE")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixThaumcraftGolemMarkerLoading;
    @Config.Comment("Fix exiting fullscreen when you tab out of the game")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixUnfocusedFullscreen;
    @Config.Comment("Fix EffectRenderer and RenderGlobal leaking world instance when leaving world")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixRenderersWorldLeak;
    @Config.Comment("Fix URISyntaxException in forge.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixUrlDetection;
    @Config.Comment("Fixes various unchecked vanilla getBlock() methods")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixVanillaUnprotectedGetBlock;
    @Config.Comment("Fixes village unchecked getBlock() calls")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixVillageUncheckedGetBlock;
    @Config.Comment("Fix unprotected getBlock() in World")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixWorldGetBlock;
    @Config.Comment("Fix WorldServer leaking entities when no players are present in a dimension")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixWorldServerLeakingUnloadedEntities;
    @Config.Comment("Fix scrolling in the world map screen")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixXaerosWorldMapScroll;
    @Config.Comment("Validate vanilla packet encodings before sending in addition to on reception")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean validatePacketEncodingBeforeSending;
    @Config.Comment("Increase the maximum network packet size from the default of 2MiB")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean increasePacketSizeLimit;
    @Config.Comment("Modify the maximum NBT size limit when received as a network packet, to avoid large NBT-related crashes")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean changeMaxNetworkNbtSizeLimit;
    @Config.Comment("BiomesOPlenty Java 12 compatibility patches.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean java12BopCompat;
    @Config.Comment("Immersive Engineering Java 12 compatibility patch")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean java12ImmersiveEngineeringCompat;
    @Config.Comment("Lotr Java 12 compatibility patch")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean java12LotrCompat;
    @Config.Comment("Minechem Java 12 compatibility patch")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean java12MineChemCompat;
    @Config.Comment("Log oversized chat message to console. WARNING: might create huge log files if this happens very often.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean logHugeChat;
    @Config.Comment("Optimize inventory access to IC2 nuclear reactor")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean optimizeIc2ReactorInventoryAccess;
    @Config.Comment("Fix too early light initialization")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean optimizeWorldUpdateLight;
    @Config.Comment("Spigot-style extended chunk format to remove the 2MB chunk size limit")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean remove2MBChunkLimit;
    @Config.Comment("Disable the creative search tab since it can be very laggy in large modpacks")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean removeCreativeSearchTab;
    @Config.Comment("Remove old/stale/outdated update checks.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean removeUpdateChecks;
    @Config.Comment("Drastically speedup animated textures (Basically the same as with optifine animations off but animations are working)")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean speedupAnimations;
    @Config.Comment("Stop \"You can only sleep at night\" message filling the chat")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean squashBedErrorMessage;
    @Config.Comment("Limits the amount of times the ItemPickupEvent triggers per tick since it can lead to a lot of lag")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean throttleItemPickupEvent;
    @Config.Comment("Triggers all conflicting key bindings on key press instead of a random one")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean triggerAllConflictingKeybindings;
    @Config.Comment("Fix Y coordinate being off by one")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixVoxelMapYCoord;
    @Config.Comment("Fix some NullPointerExceptions")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixVoxelMapChunkNPE;
    @Config.Comment("Fix redstone torch leaking world")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixRedstoneTorchWorldLeak;
    @Config.Comment("Prevent crash with Thermal Dynamics from Negative Array Exceptions from item duct transfers")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean preventThermalDynamicsNASE;

    @Config.Comment("Should the extended packet validation error cause a crash (true) or just print out an error to the log (false)")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean validatePacketEncodingBeforeSendingShouldCrash;
    @Config.Comment("Remove the BOP warning on first world generation (ignored when dreamcraft is present)")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean removeBOPWarning;
    @Config.Comment("Disable ExtraTic's Integration with Metallurgy 3 Precious Materials Module: [Brass, Silver, Electrum & Platinum]")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean fixExtraTiCTEConflict;

    @Config.Comment("The maximum NBT size limit in bytes when received as a network packet, the vanilla value is 2097152 (2 MiB).")
    @Config.RangeInt(min = 1024, max = 1024 * 1024 * 1024)
    @Config.DefaultInt(256 * 1024 * 1024)
    public static int maxNetworkNbtSizeLimit;
    @Config.Comment("Stacks picked up per tick")
    @Config.RangeInt(min = 1, max = 64)
    @Config.DefaultInt(36)
    public static int itemStacksPickedUpPerTick;
    @Config.Comment("Maximum hp for BetterHUD to render as hearts")
    @Config.RangeInt(min = 1, max = 100000)
    @Config.DefaultInt(5000)
    public static int betterHUDHPRenderLimit;
    @Config.Comment("The maximum size limit in bytes of a network packet to accept, the vanilla value is 2097152 (2 MiB).")
    @Config.RangeInt(min = 1024, max = 1024 * 1024 * 1024)
    @Config.DefaultInt(256 * 1024 * 1024)
    public static int packetSizeLimit;

}
