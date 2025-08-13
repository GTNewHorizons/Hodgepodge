package com.mitchej123.hodgepodge.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "hodgepodge", category = "fixes")
@Config.RequiresMcRestart
public class FixesConfig {

    /* ====== Minecraft fixes start ===== */

    @Config.Comment("Only load languages once per File instead of once per Mod")
    @Config.DefaultBoolean(true)
    public static boolean onlyLoadLanguagesOnce;

    @Config.Comment("Modify the maximum NBT size limit when received as a network packet, to avoid large NBT-related crashes")
    @Config.DefaultBoolean(true)
    public static boolean changeMaxNetworkNbtSizeLimit;

    @Config.Comment("Safely enlarge the potion array before other mods")
    @Config.DefaultBoolean(true)
    public static boolean enlargePotionArray;

    @Config.Comment("Fix bogus FMLProxyPacket NPEs on integrated server crashes.")
    @Config.DefaultBoolean(true)
    public static boolean fixBogusIntegratedServerNPEs;

    @Config.Comment("Fix wrapped chat lines missing colors")
    @Config.DefaultBoolean(true)
    public static boolean fixChatWrappedColors;

    @Config.Comment("Prevents crash if server sends container with wrong itemStack size")
    @Config.DefaultBoolean(true)
    public static boolean fixContainerPutStacksInSlots;

    @Config.Comment("Backports 1.12's slot shift clicking to prevent recursion when crafting items")
    @Config.DefaultBoolean(true)
    public static boolean fixContainerShiftClickRecursion;

    @Config.Comment("Fixes the debug hitbox of the player beeing offset")
    @Config.DefaultBoolean(true)
    public static boolean fixDebugBoundingBox;

    @Config.Comment("Fix losing attributes on dimension change")
    @Config.DefaultBoolean(true)
    public static boolean fixDimensionChangeAttributes;

    @Config.Comment("Fix duplicate sounds from playing when closing a gui.")
    @Config.DefaultBoolean(true)
    public static boolean fixDuplicateSounds;

    @Config.Comment("Fix deleting stack when eating mushroom stew")
    @Config.DefaultBoolean(true)
    public static boolean fixEatingStackedStew;

    @Config.Comment("Fix a class name typo in MinecraftForge's initialize method")
    @Config.DefaultBoolean(true)
    public static boolean fixEffectRendererClassTypo;

    @Config.Comment("Fix enchantment levels not displaying properly above a certain value")
    @Config.DefaultBoolean(true)
    public static boolean fixEnchantmentNumerals;

    @Config.Comment("Fix fence connections with other types of fence")
    @Config.DefaultBoolean(true)
    public static boolean fixFenceConnections;

    @Config.Comment("Fix vanilla fire spread sometimes causing NPE on thermos")
    @Config.DefaultBoolean(true)
    public static boolean fixFireSpread;

    @Config.Comment("Fix Forge fluid container registry key")
    @Config.DefaultBoolean(true)
    public static boolean fixFluidContainerRegistryKey;

    @Config.Comment("Replace recursion with iteration in FontRenderer line wrapping code")
    @Config.DefaultBoolean(true)
    public static boolean fixFontRendererLinewrapRecursion;

    @Config.Comment("Fix windowId being set on openContainer even if openGui failed")
    @Config.DefaultBoolean(true)
    public static boolean fixForgeOpenGuiHandlerWindowId;

    @Config.Comment("Fix the Forge update checker")
    @Config.DefaultBoolean(true)
    public static boolean fixForgeUpdateChecker;

    @Config.Comment("Fix vanilla issue where player sounds register as animal sounds")
    @Config.DefaultBoolean(true)
    public static boolean fixFriendlyCreatureSounds;

    @Config.Comment("Fix Volume Slider is ineffective until reaching the lower end")
    @Config.DefaultBoolean(true)
    public static boolean logarithmicVolumeControl;

    @Config.Comment("Fix vanilla light calculation sometimes cause NPE on thermos")
    @Config.DefaultBoolean(true)
    public static boolean fixGetBlockLightValue;

    @Config.Comment("Fix vanilla GL state bugs causing lighting glitches in various perspectives (MC-10135).")
    @Config.DefaultBoolean(true)
    public static boolean fixGlStateBugs;

    @Config.Comment("Fix Game Over GUI buttons disabled if switching fullscreen")
    @Config.DefaultBoolean(true)
    public static boolean fixGuiGameOver;

    @Config.Comment("Fix arm not swinging when having too much haste")
    @Config.DefaultBoolean(true)
    public static boolean fixHasteArmSwing;

    @Config.Comment("Fix vanilla Hopper hit box")
    @Config.DefaultBoolean(true)
    public static boolean fixHopperHitBox;

    @Config.Comment("Fix Drawer + Hopper voiding items")
    @Config.DefaultBoolean(true)
    public static boolean fixHopperVoidingItems;

    @Config.Comment("Fix oversized chat message kicking player.")
    @Config.DefaultBoolean(true)
    public static boolean fixHugeChatKick;

    @Config.Comment("Fix the bug that makes fireballs stop moving when chunk unloads")
    @Config.DefaultBoolean(true)
    public static boolean fixImmobileFireballs;

    @Config.Comment("Fix Sugar Cane inability to replace replaceable blocks indirectly.")
    @Config.DefaultBoolean(true)
    public static boolean fixSugarCanePlacement;

    @Config.Comment("Fix an overflow of the dimension id when a player logins on a server")
    @Config.DefaultBoolean(true)
    public static boolean fixLoginDimensionIDOverflow;

    @Config.Comment("Fixes the damage of the Thick Neutron Reflectors in the MT Core recipe (Advanced Solar Panels)")
    @Config.DefaultBoolean(true)
    public static boolean fixMTCoreRecipe;

    @Config.Comment("Allows the server to assign the logged in UUID to the same username when online_mode is false")
    @Config.DefaultBoolean(true)
    public static boolean fixNetHandlerLoginServerOfflineMode;

    @Config.Comment("Prevents crash if server sends itemStack with index larger than client's container")
    @Config.DefaultBoolean(true)
    public static boolean fixNetHandlerPlayClientHandleSetSlot;

    @Config.Comment("Fix NPE in Netty's Bootstrap class")
    @Config.DefaultBoolean(true)
    public static boolean fixNettyNPE;

    @Config.Comment("Fix northwest bias on RandomPositionGenerator")
    @Config.DefaultBoolean(true)
    public static boolean fixNorthWestBias;

    @Config.Comment("Prevent tall grass and such to affect the perspective camera")
    @Config.DefaultBoolean(true)
    public static boolean fixPerspectiveCamera;

    @Config.Comment("Allow some mods to properly fetch the player skin")
    @Config.DefaultBoolean(true)
    public static boolean fixPlayerSkinFetching;

    @Config.Comment("Preserve the order of quads in terrain pass 1")
    @Config.DefaultBoolean(true)
    public static boolean fixPreserveQuadOrder;

    @Config.Comment("Properly display level of potion effects in the inventory and on tooltips")
    @Config.DefaultBoolean(true)
    public static boolean fixPotionEffectNumerals;

    @Config.Comment("Fix crashes with ConcurrentModificationException because of incorrectly iterating over active potions")
    @Config.DefaultBoolean(true)
    public static boolean fixPotionIterating;

    @Config.Comment("Fix potions >= 128")
    @Config.DefaultBoolean(true)
    public static boolean fixPotionLimit;

    @Config.Comment("Fix redstone torch leaking world")
    @Config.DefaultBoolean(true)
    public static boolean fixRedstoneTorchWorldLeak;

    @Config.Comment("Fix EffectRenderer and RenderGlobal leaking world instance when leaving world")
    @Config.DefaultBoolean(true)
    public static boolean fixRenderersWorldLeak;

    @Config.Comment("Fix game window becoming not resizable after toggling fullscrean in any way")
    @Config.DefaultBoolean(true)
    public static boolean fixResizableFullscreen;

    @Config.Comment("Fix resource pack folder not opening on Windows if file path has a space")
    @Config.DefaultBoolean(true)
    public static boolean fixResourcePackOpening;

    @Config.Comment("Fix too many allocations from Chunk Coordinate Int Pair")
    @Config.DefaultBoolean(true)
    public static boolean fixTooManyAllocationsChunkPositionIntPair;

    @Config.Comment("Add option to separate simulation distance from rendering distance (Incompatible with optifine, will automatically be disabled)")
    @Config.DefaultBoolean(true)
    public static boolean addSimulationDistance;

    @Config.Comment("Fix RCON Threading by forcing it to run on the main thread")
    @Config.DefaultBoolean(true)
    public static boolean fixRconThreading;

    @Config.Comment("Fix exiting fullscreen when you tab out of the game")
    @Config.DefaultBoolean(true)
    public static boolean fixUnfocusedFullscreen;

    @Config.Comment("Fix URISyntaxException in forge.")
    @Config.DefaultBoolean(true)
    public static boolean fixUrlDetection;

    @Config.Comment("Fixes various unchecked vanilla getBlock() methods")
    @Config.DefaultBoolean(true)
    public static boolean fixVanillaUnprotectedGetBlock;

    @Config.Comment("Fixes village unchecked getBlock() calls")
    @Config.DefaultBoolean(true)
    public static boolean fixVillageUncheckedGetBlock;

    @Config.Comment("Fix WorldServer leaking entities when no players are present in a dimension")
    @Config.DefaultBoolean(true)
    public static boolean fixWorldServerLeakingUnloadedEntities;

    @Config.Comment("Fix skin manager leaking client world")
    @Config.DefaultBoolean(true)
    public static boolean fixSkinManagerLeakingClientWorld;

    @Config.Comment("Increase the maximum network packet size from the default of 2MiB")
    @Config.DefaultBoolean(true)
    public static boolean increasePacketSizeLimit;

    @Config.Comment("Stacks picked up per tick")
    @Config.RangeInt(min = 1, max = 64)
    @Config.DefaultInt(36)
    public static int itemStacksPickedUpPerTick;

    @Config.Comment("Log oversized chat message to console. WARNING: might create huge log files if this happens very often.")
    @Config.DefaultBoolean(true)
    public static boolean logHugeChat;

    @Config.Comment("The maximum NBT size limit in bytes when received as a network packet, the vanilla value is 2097152 (2 MiB).")
    @Config.RangeInt(min = 1024, max = 1024 * 1024 * 1024)
    @Config.DefaultInt(256 * 1024 * 1024)
    public static int maxNetworkNbtSizeLimit;

    @Config.Comment("Fix too early light initialization")
    @Config.DefaultBoolean(true)
    public static boolean optimizeWorldUpdateLight;

    @Config.Comment("The maximum size limit in bytes of a network packet to accept, the vanilla value is 2097152 (2 MiB).")
    @Config.RangeInt(min = 1024, max = 1024 * 1024 * 1024)
    @Config.DefaultInt(256 * 1024 * 1024)
    public static int packetSizeLimit;

    @Config.Comment("Spigot-style extended chunk format to remove the 2MB chunk size limit")
    @Config.DefaultBoolean(true)
    public static boolean remove2MBChunkLimit;

    @Config.Comment("Disable the creative search tab since it can be very laggy in large modpacks")
    @Config.DefaultBoolean(true)
    public static boolean removeCreativeSearchTab;

    @Config.Comment("Stop \"You can only sleep at night\" message filling the chat")
    @Config.DefaultBoolean(true)
    public static boolean squashBedErrorMessage;

    @Config.Comment("Limits the amount of times the ItemPickupEvent triggers per tick since it can lead to a lot of lag")
    @Config.DefaultBoolean(true)
    public static boolean throttleItemPickupEvent;

    @Config.Comment("Adds the thrower tag to all dropped EntityItems")
    @Config.DefaultBoolean(true)
    public static boolean addThrowerTagToDroppedItems;

    @Config.Comment("Synchonize from server to client the thrower and pickup delay of an item entity")
    @Config.DefaultBoolean(true)
    public static boolean syncItemThrower;

    @Config.Comment("Triggers all conflicting key bindings on key press instead of a random one")
    @Config.DefaultBoolean(true)
    public static boolean triggerAllConflictingKeybindings;

    @Config.Comment("Validate vanilla packet encodings before sending in addition to on reception")
    @Config.DefaultBoolean(true)
    public static boolean validatePacketEncodingBeforeSending;

    @Config.Comment("Should the extended packet validation error cause a crash (true) or just print out an error to the log (false)")
    @Config.DefaultBoolean(false)
    public static boolean validatePacketEncodingBeforeSendingShouldCrash;

    @Config.Comment("Checks saved TileEntity coordinates earlier to provide a more descriptive error message")
    @Config.DefaultBoolean(true)
    public static boolean earlyChunkTileCoordinateCheck;

    @Config.Comment("Fix the temperature can go below absolute zero at very high place")
    @Config.DefaultBoolean(true)
    public static boolean fixNegativeKelvin;

    @Config.Comment("Destroy and log TileEntities failing the safe coordinate instead of crashing the game (can cause loss of data)")
    @Config.DefaultBoolean(false)
    public static boolean earlyChunkTileCoordinateCheckDestructive;

    @Config.Comment("Fix forge command handler not checking for a / and also not running commands with any case")
    @Config.DefaultBoolean(true)
    public static boolean fixSlashCommands;

    @Config.Comment("Fix the command handler not allowing you to run commands typed in any case")
    @Config.DefaultBoolean(true)
    public static boolean fixCaseCommands;

    @Config.Comment("Limit the number of recursive cascading block updates during world generation to prevent stack overflow crashes, set to -1 to disable the limit.")
    @Config.RangeInt(min = -1)
    @Config.DefaultInt(256) // A stack overflow with water updates happens somewhere above 300 updates with default Java
                            // settings
    public static int limitRecursiveBlockUpdateDepth;

    @Config.Comment("Fix the buttons not being centered in the GuiConfirmOpenLink")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixButtonsGuiConfirmOpenLink;

    @Config.Comment("Fix an array out of bounds caused by the GameSettings getKeyDisplayString method")
    @Config.DefaultBoolean(true)
    public static boolean fixGameSettingsArrayOutOfBounds;

    @Config.Comment("Fix the vanilla method to open chat links not working for every OS")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixChatOpenLink;

    @Config.Comment("Fix nametags of spiders, endermen and ender dragons being rendered too dark")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixNametagBrightness;

    @Config.Comment("Fix spiders, endermen and ender dragons being rendered too red when hit")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixHitEffectBrightness;

    @Config.Comment("Fix server-side check of block placement distance by players being not identical client-side checks")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixWrongBlockPlacementDistanceCheck;

    @Config.Comment("Fix inventory sync lag: prevents client to check recipes on empty slots. Particularly fixes lag when trying to eat food when full.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixInventorySyncLag;

    @Config.Comment("Prevent the client from crashing due to invalid entity attributes range (MC-150405)")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixEntityAttributesRange;

    @Config.Comment("Fix Glass Bottles filling with Water from some other Fluid blocks")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixGlassBottleWaterFilling;

    @Config.Comment("Use correct egg particles instead of snowball ones (MC-7807)")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixEggParticles;

    @Config.Comment("Fix EventBus keeping object references after unregistering event handlers.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixEventBusMemoryLeak;

    @Config.Comment("Skips playing empty sounds.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean skipEmptySounds;

    @Config.Comment("Render the house character (\u2302 - Unicode index 2302) in the Minecraft font.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixHouseCharRendering;

    /* ====== Minecraft fixes end ===== */

    // bukkit fixes

    @Config.Comment("Fix crash on Bukkit with BetterQuesting")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixBukkitBetterQuestingCrash;

    // affecting multiple mods

    @Config.Comment("Remove old/stale/outdated update checks.")
    @Config.DefaultBoolean(true)
    public static boolean removeUpdateChecks;

    @Config.Comment("Enable multiple fixes to reduce RAM usage")
    @Config.DefaultBoolean(true)
    public static boolean enableMemoryFixes;

    @Config.Comment("Fix broken modlist entries due to wrong mcmod.info files")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixModlistEntries;

    // BetterHUD

    @Config.Comment("Maximum hp for BetterHUD to render as hearts")
    @Config.RangeInt(min = 1, max = 100000)
    @Config.DefaultInt(5000)
    public static int betterHUDHPRenderLimit;

    @Config.Comment("Fix BetterHUD armor bar rendering breaking with skulls")
    @Config.DefaultBoolean(true)
    public static boolean fixBetterHUDArmorDisplay;

    @Config.Comment("Fix BetterHUD freezing the game when trying to render high amounts of hp")
    @Config.DefaultBoolean(true)
    public static boolean fixBetterHUDHPDisplay;

    // Bibliocraft

    @Config.Comment("Fix Bibliocraft packet exploits")
    @Config.DefaultBoolean(true)
    public static boolean fixBibliocraftPackets;

    @Config.Comment("Fix Bibliocraft path sanitization")
    @Config.DefaultBoolean(true)
    public static boolean fixBibliocraftPathSanitization;

    // Biomes O' Plenty

    @Config.Comment("Removes duplicate Fermenter and Squeezer recipes and flower registration")
    @Config.DefaultBoolean(true)
    public static boolean deduplicateForestryCompatInBOP;

    @Config.Comment("BiomesOPlenty Java 12 compatibility patches.")
    @Config.DefaultBoolean(true)
    public static boolean java12BopCompat;

    @Config.Comment("Remove the BOP warning on first world generation (ignored when dreamcraft is present)")
    @Config.DefaultBoolean(false)
    public static boolean removeBOPWarning;

    // Candycraft

    @Config.Comment("Fix NPE when interacting with sugar block")
    @Config.DefaultBoolean(true)
    public static boolean fixCandycraftBlockSugarNPE;

    // Cofh

    @Config.Comment("Fix NPE in COFH's oredict")
    @Config.DefaultBoolean(true)
    public static boolean fixCofhOreDictNPE;

    @Config.Comment("Fix race condition in COFH's oredict")
    @Config.DefaultBoolean(true)
    public static boolean fixCofhOreDictCME;

    // Extra TiC

    @Config.Comment("Disable ExtraTic's Integration with Metallurgy 3 Precious Materials Module: (Brass, Silver, Electrum & Platinum)")
    @Config.DefaultBoolean(false)
    public static boolean fixExtraTiCTEConflict;

    // Extra Utilities

    @Config.Comment("Fix Extra Utilities drums eating IC2 cells and Forestry capsules")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesDrumEatingCells;

    @Config.Comment("Fix Extra Utilities Lapis Caelestis microblocks rendering")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesGreenscreenMicroblocks;

    @Config.Comment("Fixes rendering issues with transparent items from Extra Utilities")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesItemRendering;

    @Config.Comment("Fix dupe bug with Division Sigil removing enchantment")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesUnEnchanting;

    @Config.Comment("Remove rain from the Last Millenium (Extra Utilities)")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesLastMilleniumRain;

    @Config.Comment("Remove creatures from the Last Millenium (Extra Utilities)")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesLastMilleniumCreatures;

    @Config.Comment("Prevent fluid retrieval node from voiding (Extra Utilities)")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesFluidRetrievalNode;

    @Config.Comment("Caps hotkey'd stacks to their maximum stack size in filing cabinets")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesFilingCabinetDupe;

    @Config.Comment("Prevent hotkeying other items onto item filters while they are open")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesFilterDupe;

    @Config.Comment("Fixes Ender Quarry get stuck at a mostly random location under certain conditions")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesEnderQuarryFreeze;

    @Config.Comment("Fixes the healing axe not healing mobs when attacking them")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesHealingAxeHeal;

    @Config.Comment("Fixes the healing axe to be unbreakable during damage checks that aren't breaking blocks or attacking.")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesHealingAxeUnbreakable;

    @Config.Comment("Fixes the erosion shovel to be unbreakable during damage checks that aren't breaking blocks or attacking.")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesErosionShovelUnbreakable;

    @Config.Comment("Fix Extra Utilities chests not updating comparator redstone signals when their inventories change")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesChestComparatorUpdate;

    @Config.Comment("Make Etheric Sword truly unbreakable")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesEthericSwordUnbreakable;

    @Config.Comment("Prevent Extra Utilities Ender Collector from inserting into auto-dropping Blocks that create a crash-loop")
    @Config.DefaultBoolean(true)
    public static boolean fixExtraUtilitiesEnderCollectorCrash;

    // Galacticraft

    @Config.Comment("Fix time commands with Galacticraft")
    @Config.DefaultBoolean(true)
    public static boolean fixTimeCommandWithGC;

    // Gliby's Voice Chat

    @Config.Comment("Fix Gliby's voice chat not shutting down its thread cleanly")
    @Config.DefaultBoolean(true)
    public static boolean fixGlibysVoiceChatThreadStop;

    // Hunger Overhaul

    @Config.Comment("Fix Hunger Overhaul low stat effects")
    @Config.DefaultBoolean(true)
    public static boolean fixHungerOverhaul;

    @Config.Comment("Fix some items restore 0 hunger")
    @Config.DefaultBoolean(true)
    public static boolean fixHungerOverhaulRestore0Hunger;

    // Immersive Engineering

    @Config.Comment("Immersive Engineering Java 12 compatibility patch")
    @Config.DefaultBoolean(true)
    public static boolean java12ImmersiveEngineeringCompat;

    // Industrialcraft

    @Config.Comment("Fix lag caused by IC2 armor tick")
    @Config.DefaultBoolean(true)
    public static boolean fixIc2ArmorLag;

    @Config.Comment("Fix IC2's direct inventory access")
    @Config.DefaultBoolean(true)
    public static boolean fixIc2DirectInventoryAccess;

    @Config.Comment("Fix IC2's armor hover mode")
    @Config.DefaultBoolean(true)
    public static boolean fixIc2HoverMode;

    @Config.Comment("Prevent IC2's nightvision from blinding you")
    @Config.DefaultBoolean(true)
    public static boolean fixIc2Nightvision;

    @Config.Comment("Fix IC2's reactor dupe")
    @Config.DefaultBoolean(true)
    public static boolean fixIc2ReactorDupe;

    @Config.Comment("Fix IC2 not loading translations from resource packs")
    @Config.DefaultBoolean(true)
    public static boolean fixIc2ResourcePackTranslation;

    @Config.Comment("Fixes various unchecked IC2 getBlock() methods")
    @Config.DefaultBoolean(true)
    public static boolean fixIc2UnprotectedGetBlock;

    @Config.Comment("Optimize inventory access to IC2 nuclear reactor")
    @Config.DefaultBoolean(true)
    public static boolean optimizeIc2ReactorInventoryAccess;

    @Config.Comment("Fix IC2 Crops trampling any types of farmland to dirt when sprinting")
    @Config.DefaultBoolean(true)
    public static boolean fixIc2CropTrampling;

    // Journey Map

    @Config.Comment("Prevents journeymap from using illegal character in file paths")
    @Config.DefaultBoolean(true)
    public static boolean fixJourneymapFilePath;

    @Config.Comment("Fix jumpy scrolling in the waypoint manager screen")
    @Config.DefaultBoolean(true)
    public static boolean fixJourneymapJumpyScrolling;

    @Config.Comment("Prevent unbound keybinds from triggering when pressing certain keys")
    @Config.DefaultBoolean(true)
    public static boolean fixJourneymapKeybinds;

    // The Lord of the Rings Mod

    @Config.Comment("Lotr Java 12 compatibility patch")
    @Config.DefaultBoolean(true)
    public static boolean java12LotrCompat;

    // Minechem

    @Config.Comment("Minechem Java 12 compatibility patch")
    @Config.DefaultBoolean(true)
    public static boolean java12MineChemCompat;

    // Minefactory Reloaded

    @Config.Comment("Prevents Sacred Rubber Tree Generation")
    @Config.DefaultBoolean(false)
    public static boolean disableMassiveSacredTreeGeneration;

    // Morpheus

    @Config.Comment("Fix not properly waking players if not everyone is sleeping")
    @Config.DefaultBoolean(true)
    public static boolean fixMorpheusWaking;

    // Optifine

    @Config.Comment("Forces the chunk loading option from optifine to default since other values can crash the game")
    @Config.DefaultBoolean(true)
    public static boolean fixOptifineChunkLoadingCrash;

    // Pam's Harvest the Nether

    @Config.Comment("Fix Axis aligned Bounding Box of Ignis Fruit")
    @Config.DefaultBoolean(true)
    public static boolean fixIgnisFruitAABB;

    @Config.Comment("If fancy graphics are enabled, Nether Leaves render sides with other Nether Leaves adjacent too")
    @Config.DefaultBoolean(true)
    public static boolean fixNetherLeavesFaceRendering;

    // PortalGun

    @Config.Comment("Fix outdated URLs used in the PortalGun mod to download the sound pack")
    @Config.DefaultBoolean(true)
    public static boolean fixPortalGunURLs;

    // Thaumcraft

    @Config.Comment("Fix Thaumcraft Aspects being sorted by tag instead of by name")
    @Config.DefaultBoolean(true)
    public static boolean fixThaumcraftAspectSorting;

    @Config.Comment("Fix golem's marker loading failure when dimensionId larger than MAX_BYTE")
    @Config.DefaultBoolean(true)
    public static boolean fixThaumcraftGolemMarkerLoading;

    @Config.Comment("Implement a proper hashing method for WorldCoordinates")
    @Config.DefaultBoolean(true)
    public static boolean fixThaumcraftWorldCoordinatesHashingMethod;

    @Config.Comment("Fix Thaumcraft leaves frequent ticking")
    @Config.DefaultBoolean(true)
    public static boolean fixThaumcraftLeavesLag;

    @Config.Comment("Fix Thaumcraft wand pedestal vis duplication")
    @Config.DefaultBoolean(true)
    public static boolean fixWandPedestalVisDuplication;

    @Config.Comment("Fix handling of null stacks in ItemWispEssence")
    @Config.DefaultBoolean(true)
    public static boolean fixNullHandlingItemWispEssence;

    @Config.Comment("Fix check for EE3 item in Thaumcraft to prevent issues on modern Java.")
    @Config.DefaultBoolean(true)
    public static boolean fixThaumcraftEE3Check;

    // Thermal Dynamics

    @Config.Comment("Prevent crash with Thermal Dynamics from Negative Array Exceptions from item duct transfers")
    @Config.DefaultBoolean(true)
    public static boolean preventThermalDynamicsNASE;

    @Config.Comment("Prevent ClassCastException on forming invalid Thermal Dynamic fluid grid")
    @Config.DefaultBoolean(true)
    public static boolean preventFluidGridCrash;

    // Travellers' Gear

    @Config.Comment({ "Return items placed in Traveller's Gear slots after the mod is removed when players log in.",
            "Sends messages to the log that start with \"[Hodgepodge]: [TG Recovery]\".",
            "Removes players from the TG items file after returning items. Deletes it if it's empty.",
            "Automatically disables itself on servers after deleting the TG items file.",
            "Clients leave it on to allow for joining multiple SP worlds with TG items." })
    @Config.DefaultBoolean(true)
    public static boolean returnTravellersGearItems;

    // VoxelMap

    @Config.Comment("Fix some NullPointerExceptions")
    @Config.DefaultBoolean(true)
    public static boolean fixVoxelMapChunkNPE;

    @Config.Comment("Fix Y coordinate being off by one")
    @Config.DefaultBoolean(true)
    public static boolean fixVoxelMapYCoord;

    // Witchery

    @Config.Comment("Disable Witchery potion extender for Java 12 compat")
    @Config.DefaultBoolean(true)
    public static boolean disableWitcheryPotionExtender;

    @Config.Comment("Fixes Witchery player skins reflections with inhabited mirrors")
    @Config.DefaultBoolean(true)
    public static boolean fixWitcheryReflections;

    @Config.Comment("Enhanced Witchery Thunder Detection for rituals and Witch Hunters")
    @Config.DefaultBoolean(true)
    public static boolean fixWitcheryThunderDetection;

    @Config.Comment("Fixes some potential errors in Witchery Rendering")
    @Config.DefaultBoolean(true)
    public static boolean fixWitcheryRendering;

    // Xaero's Minimap
    @Config.Comment("Fixes the player entity dot rendering when arrow is chosen")
    @Config.DefaultBoolean(true)
    public static boolean fixXaerosMinimapEntityDot;

    // Xaero's World Map

    @Config.Comment("Fix scrolling in the world map screen")
    @Config.DefaultBoolean(true)
    public static boolean fixXaerosWorldMapScroll;

    // ZTones

    @Config.Comment("Fix ZTones packet exploits")
    @Config.DefaultBoolean(true)
    public static boolean fixZTonesPackets;

}
