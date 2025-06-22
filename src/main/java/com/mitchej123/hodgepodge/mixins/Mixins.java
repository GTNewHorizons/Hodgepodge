package com.mitchej123.hodgepodge.mixins;

import static com.gtnewhorizon.gtnhlib.mixin.TargetedMod.ARCHAICFIX;
import static com.gtnewhorizon.gtnhlib.mixin.TargetedMod.FASTCRAFT;
import static com.gtnewhorizon.gtnhlib.mixin.TargetedMod.OPTIFINE;
import static com.mitchej123.hodgepodge.mixins.TargetedMod.ANGELICA;
import static com.mitchej123.hodgepodge.mixins.TargetedMod.BUKKIT;
import static com.mitchej123.hodgepodge.mixins.TargetedMod.FALSETWEAKS;

import java.util.List;
import java.util.function.Supplier;

import com.gtnewhorizon.gtnhlib.mixin.IMixins;
import com.gtnewhorizon.gtnhlib.mixin.ITargetedMod;
import com.gtnewhorizon.gtnhlib.mixin.MixinBuilder;
import com.gtnewhorizon.gtnhlib.mixin.Phase;
import com.gtnewhorizon.gtnhlib.mixin.Side;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.ASMConfig;
import com.mitchej123.hodgepodge.config.DebugConfig;
import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;

public enum Mixins implements IMixins {

    // Vanilla Fixes
    ONLY_LOAD_LANGUAGES_ONCE_PER_FILE(new MixinBuilder("Only load languages once per file").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("minecraft.MixinLanguageRegistry")
            .setApplyIf(() -> FixesConfig.onlyLoadLanguagesOnce).addTargetedMod(TargetedMod.VANILLA)),
    CHANGE_CATEGORY_SPRINT_KEY(
            new MixinBuilder("Moves the sprint keybind to the movement category").addTargetedMod(TargetedMod.VANILLA)
                    .setSide(Side.CLIENT).setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinGameSettings_SprintKey")
                    .setApplyIf(() -> TweaksConfig.changeSprintCategory)),
    FIX_TOO_MANY_ALLOCATIONS_CHUNK_POSITION_INT_PAIR(
            new MixinBuilder("Stops MC from allocating too many ChunkPositionIntPair objects")
                    .addTargetedMod(TargetedMod.VANILLA).setSide(Side.BOTH).setPhase(Phase.EARLY)
                    .addMixinClasses(
                            "minecraft.MixinChunkCoordIntPair_FixAllocations",
                            "minecraft.MixinWorld_FixAllocations",
                            "minecraft.MixinWorldClient_FixAllocations",
                            "minecraft.MixinAnvilChunkLoader_FixAllocations")
                    .setApplyIf(() -> FixesConfig.fixTooManyAllocationsChunkPositionIntPair)),
    FIX_TOO_MANY_ALLOCATIONS_CHUNK_POSITION_INT_PAIR_OPTIFINE_INCOMPAT(
            new MixinBuilder("Stops MC from allocating too many ChunkPositionIntPair objects")
                    .addTargetedMod(TargetedMod.VANILLA).addExcludedMod(TargetedMod.OPTIFINE).setSide(Side.BOTH)
                    .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinWorldServer_FixAllocations")
                    .setApplyIf(() -> FixesConfig.fixTooManyAllocationsChunkPositionIntPair)),
    ADD_SIMULATION_DISTANCE_OPTION(new MixinBuilder("Add option to separate simulation distance from render distance")
            .addTargetedMod(TargetedMod.VANILLA).setSide(Side.BOTH).setPhase(Phase.EARLY)
            .addExcludedMod(TargetedMod.OPTIFINE).addExcludedMod(TargetedMod.ULTRAMINE)
            .addMixinClasses(
                    "minecraft.MixinWorld_SimulationDistance",
                    "minecraft.MixinWorldServer_SimulationDistance",
                    "minecraft.MixinChunk_SimulationDistance")
            .setApplyIf(() -> FixesConfig.addSimulationDistance)),
    ADD_SIMULATION_DISTANCE_OPTION_THERMOS_FIX(
            new MixinBuilder("Add option to separate simulation distance from render distance (Thermos fix)")
                    .addTargetedMod(TargetedMod.VANILLA).setSide(Side.BOTH).setPhase(Phase.EARLY)
                    .addExcludedMod(TargetedMod.OPTIFINE).addExcludedMod(TargetedMod.ULTRAMINE)
                    .addMixinClasses("minecraft.MixinWorldServer_SimulationDistanceThermosFix")
                    .setApplyIf(() -> FixesConfig.addSimulationDistance && Common.thermosTainted)),
    FIX_RESOURCEPACK_FOLDER_OPENING(new MixinBuilder("Fix resource pack folder sometimes not opening on windows")
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinGuiScreenResourcePacks").setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixResourcePackOpening).addTargetedMod(TargetedMod.VANILLA)),
    FIX_ENCHANTMENT_LEVEL_NUMERALS(
            new MixinBuilder("Fix enchantment levels not displaying properly above a certain value")
                    .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinEnchantment_FixRomanNumerals")
                    .setSide(Side.BOTH)
                    .setApplyIf(
                            () -> FixesConfig.fixEnchantmentNumerals || TweaksConfig.arabicNumbersForEnchantsPotions)
                    .addTargetedMod(TargetedMod.VANILLA)),
    FIX_CONTAINER_PUT_STACKS_IN_SLOTS(
            new MixinBuilder("Prevents crash if server sends container with wrong itemStack size").setPhase(Phase.EARLY)
                    .addMixinClasses("minecraft.MixinContainer").setSide(Side.CLIENT)
                    .setApplyIf(() -> FixesConfig.fixContainerPutStacksInSlots).addTargetedMod(TargetedMod.VANILLA)),
    FIX_CONTAINER_SHIFT_CLICK_RECURSION(
            new MixinBuilder("Backports 1.12 logic for shift clicking slots to prevent recursion").setPhase(Phase.EARLY)
                    .addMixinClasses("minecraft.MixinContainer_FixShiftRecursion").setSide(Side.BOTH)
                    .setApplyIf(() -> FixesConfig.fixContainerShiftClickRecursion).addTargetedMod(TargetedMod.VANILLA)),
    FIX_NETHANDLERPLAYCLIENT_HANDLE_SET_SLOT(
            new MixinBuilder("Prevents crash if server sends itemStack with index larger than client's container")
                    .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinNetHandlerPlayClient_FixHandleSetSlot")
                    .setSide(Side.CLIENT).setApplyIf(() -> FixesConfig.fixNetHandlerPlayClientHandleSetSlot)
                    .addTargetedMod(TargetedMod.VANILLA)),
    FIX_NETHANDLERLOGINSERVER_OFFLINEMODE(new MixinBuilder(
            "Allows the server to assign the logged in UUID to the same username when online_mode is false")
                    .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinNetHandlerLoginServer_OfflineMode")
                    .setSide(Side.SERVER).setApplyIf(() -> FixesConfig.fixNetHandlerLoginServerOfflineMode)
                    .addTargetedMod(TargetedMod.VANILLA)),
    FIX_INVENTORY_POTION_EFFECT_NUMERALS(
            new MixinBuilder("Fix potion effects level not displaying properly above a certain value")
                    .setPhase(Phase.EARLY)
                    .addMixinClasses(
                            "minecraft.MixinInventoryEffectRenderer_FixPotionEffectNumerals",
                            "minecraft.MixinItemPotion_FixRomanNumerals")
                    .setSide(Side.CLIENT)
                    .setApplyIf(
                            () -> FixesConfig.fixPotionEffectNumerals || TweaksConfig.arabicNumbersForEnchantsPotions)
                    .addTargetedMod(TargetedMod.VANILLA)),
    FIX_HASTE_ARM_SWING_ANIMATION(new MixinBuilder("Fix arm not swinging when having too much haste")
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinEntityLivingBase_FixHasteArmSwing")
            .setSide(Side.BOTH).setApplyIf(() -> FixesConfig.fixHasteArmSwing).addTargetedMod(TargetedMod.VANILLA)),

    DISABLE_REALMS_BUTTON(new MixinBuilder("Disable Realms button in main menu").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiMainMenu_DisableRealmsButton").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.disableRealmsButton).addTargetedMod(TargetedMod.VANILLA)),
    ADD_TIME_GET(new MixinBuilder("Add /time get command").addMixinClasses("minecraft.MixinCommandTime")
            .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> TweaksConfig.addTimeGet).setPhase(Phase.EARLY)
            .setSide(Side.BOTH)),
    OPTIMIZE_WORLD_UPDATE_LIGHT(new MixinBuilder("Optimize world updateLightByType method").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinWorld_FixLightUpdateLag").setSide(Side.BOTH)
            .addExcludedMod(TargetedMod.ARCHAICFIX).addExcludedMod(ANGELICA).addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> FixesConfig.optimizeWorldUpdateLight)),
    FIX_FRIENDLY_CREATURE_SOUNDS(new MixinBuilder("Fix Friendly Creature Sounds").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinSoundHandler").setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> FixesConfig.fixFriendlyCreatureSounds)),
    LOGARITHMIC_VOLUME_CONTROL(new MixinBuilder("Logarithmic Volume Control").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinSoundManager", "minecraft.MixinSoundManagerLibraryLoader")
            .setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> FixesConfig.logarithmicVolumeControl)),
    THROTTLE_ITEMPICKUPEVENT(new MixinBuilder("Throttle Item Pickup Event").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityPlayer_ThrottlePickup").setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.throttleItemPickupEvent).addTargetedMod(TargetedMod.VANILLA)),
    ADD_THROWER_TO_DROPPED_ITEM(new MixinBuilder("Adds the thrower tag to all dropped EntityItems")
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinEntityPlayer_ItemThrower").setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.addThrowerTagToDroppedItems).addTargetedMod(TargetedMod.VANILLA)),
    SYNC_ITEM_THROWER_COMMON(
            new MixinBuilder("Synchonize from server to client the thrower and pickup delay of an item entity")
                    .setPhase(Phase.EARLY).addMixinClasses("minecraft.packets.MixinS0EPacketSpawnObject_ItemThrower")
                    .setSide(Side.BOTH).setApplyIf(() -> FixesConfig.syncItemThrower)
                    .addTargetedMod(TargetedMod.VANILLA)),
    SYNC_ITEM_THROWER_CLIENT(
            new MixinBuilder("Synchonize from server to client the thrower and pickup delay of an item entity")
                    .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinNetHandlerPlayClient_ItemThrower")
                    .setSide(Side.CLIENT).setApplyIf(() -> FixesConfig.syncItemThrower)
                    .addTargetedMod(TargetedMod.VANILLA)),
    FIX_PERSPECTIVE_CAMERA(new MixinBuilder("Camera Perspective Fix").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityRenderer").setSide(Side.CLIENT)
            .addExcludedMod(TargetedMod.ARCHAICFIX).addExcludedMod(ANGELICA).addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> FixesConfig.fixPerspectiveCamera)),
    FIX_DEBUG_BOUNDING_BOX(new MixinBuilder("Fix Bounding Box").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinRenderManager").setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> FixesConfig.fixDebugBoundingBox)),
    FENCE_CONNECTIONS_FIX(new MixinBuilder("Fix Fence Connections").addMixinClasses("minecraft.MixinBlockFence")
            .setSide(Side.BOTH).setPhase(Phase.EARLY).addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> FixesConfig.fixFenceConnections)),
    FIX_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder("Fix Inventory Offset with Potions").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinInventoryEffectRenderer_PotionOffset").setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> TweaksConfig.fixPotionRenderOffset)),
    FIX_POTION_EFFECT_RENDERING(new MixinBuilder("Fix Potion Effect Rendering").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinInventoryEffectRenderer_PotionEffectRendering").setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> TweaksConfig.fixPotionEffectRender)),
    FIX_IMMOBILE_FIREBALLS(new MixinBuilder("Fix Immobile Fireballs").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityFireball").addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> FixesConfig.fixImmobileFireballs).setSide(Side.BOTH)),
    FIX_REED_PLACING(new MixinBuilder("Fix placement of Sugar Canes").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinItemReed").addTargetedMod(TargetedMod.VANILLA).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixSugarCanePlacement)),
    LONGER_CHAT(new MixinBuilder("Longer Chat").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiNewChat_LongerChat").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.longerChat).addTargetedMod(TargetedMod.VANILLA)),
    TRANSPARENT_CHAT(new MixinBuilder("Transparent Chat").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiNewChat_TransparentChat").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.transparentChat).addTargetedMod(TargetedMod.VANILLA)),
    FIX_ENTITY_ATTRIBUTES_RANGE(new MixinBuilder("Fix Entity Attributes Range").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinNetHandlerPlayClient_FixEntityAttributesRange").setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixEntityAttributesRange).addTargetedMod(TargetedMod.VANILLA)),
    ENDERMAN_BLOCK_GRAB_DISABLE(new MixinBuilder("Disable Endermen Grabbing Blocks").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityEndermanGrab").setSide(Side.BOTH)
            .setApplyIf(() -> TweaksConfig.endermanBlockGrabDisable).addTargetedMod(TargetedMod.VANILLA)),
    ENDERMAN_BLOCK_PLACE_DISABLE(new MixinBuilder("Disable Endermen Placing Held Blocks").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityEndermanPlace").setSide(Side.BOTH)
            .setApplyIf(() -> TweaksConfig.endermanBlockPlaceDisable).addTargetedMod(TargetedMod.VANILLA)),
    ENDERMAN_BLOCK_PLACE_BLACKLIST(new MixinBuilder("Disable Endermen Placing Held Blocks on Configured Blocks")
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinEntityEndermanPlaceBlacklist").setSide(Side.BOTH)
            .setApplyIf(() -> TweaksConfig.endermanBlockPlaceBlacklist).addTargetedMod(TargetedMod.VANILLA)),
    WITCH_POTION_METADATA(new MixinBuilder("Fix Metadata of Witch Potions").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityWitch").setSide(Side.BOTH)
            .setApplyIf(() -> TweaksConfig.witchPotionMetadata).addTargetedMod(TargetedMod.VANILLA)),

    // config handled in mixin due to server->client config sync
    LONGER_MESSAGES_CLIENT(new MixinBuilder("Longer Messages Client Side").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiChat_LongerMessages").setApplyIf(() -> true)
            .addTargetedMod(TargetedMod.VANILLA).setSide(Side.CLIENT)),
    // config handled in mixin due to server->client config sync
    LONGER_MESSAGES_SERVER(new MixinBuilder("Longer Messages Server Side").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinC01PacketChatMessage_LongerMessages").setApplyIf(() -> true)
            .addTargetedMod(TargetedMod.VANILLA).setSide(Side.BOTH)),
    SPEEDUP_GRASS_BLOCK_RANDOM_TICKING(new MixinBuilder("Speed up grass block random ticking").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinBlockGrass").addTargetedMod(TargetedMod.VANILLA).setSide(Side.BOTH)
            .setApplyIf(() -> SpeedupsConfig.speedupGrassBlockRandomTicking)),
    SPEEDUP_CHUNK_PROVIDER_CLIENT(new MixinBuilder("Speed up ChunkProviderClient").setPhase(Phase.EARLY)
            .setSide(Side.CLIENT).addMixinClasses("minecraft.MixinChunkProviderClient_RemoveChunkListing")
            .addTargetedMod(TargetedMod.VANILLA).addExcludedMod(TargetedMod.FASTCRAFT)
            .setApplyIf(() -> SpeedupsConfig.speedupChunkProviderClient && ASMConfig.speedupLongIntHashMap)),
    BETTER_HASHCODES(new MixinBuilder("Optimize various Hashcode").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses(
                    "minecraft.MixinChunkCoordinates_BetterHash",
                    "minecraft.MixinChunkCoordIntPair_BetterHash")
            .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> SpeedupsConfig.speedupChunkCoordinatesHashCode)),
    TCP_NODELAY(new MixinBuilder("Set TCP NODELAY").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinTcpNoDelay").setApplyIf(() -> SpeedupsConfig.tcpNoDelay)
            .addTargetedMod(TargetedMod.VANILLA)),
    WORLD_UNPROTECTED_GET_BLOCK(new MixinBuilder("Fix world unprotected getBlock").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("minecraft.MixinWorldGetBlock")
            .setApplyIf(() -> FixesConfig.fixVanillaUnprotectedGetBlock).addTargetedMod(TargetedMod.VANILLA)),
    WORLD_UNPROTECTED_LIGHT_VALUE(new MixinBuilder("Fix world unprotected light value").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("minecraft.MixinWorldLightValue")
            .setApplyIf(() -> FixesConfig.fixGetBlockLightValue).addTargetedMod(TargetedMod.VANILLA)),
    VILLAGE_UNCHECKED_GET_BLOCK(new MixinBuilder("Fix Village unchecked getBlock").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("minecraft.MixinVillage", "minecraft.MixinVillageCollection")
            .setApplyIf(() -> FixesConfig.fixVillageUncheckedGetBlock).addTargetedMod(TargetedMod.VANILLA)),
    FORGE_HOOKS_URL_FIX(new MixinBuilder("Fix forge URL hooks").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinForgeHooks").setApplyIf(() -> FixesConfig.fixUrlDetection)
            .addTargetedMod(TargetedMod.VANILLA)),
    FORGE_UPDATE_CHECK_FIX(new MixinBuilder("Fix the forge update checker").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("forge.MixinForgeVersion_FixUpdateCheck")
            .setApplyIf(() -> FixesConfig.fixForgeUpdateChecker).addTargetedMod(TargetedMod.VANILLA)),
    FORGE_FIX_CLASS_TYPO(new MixinBuilder("Fix a class name typo in MinecraftForge's initialize method")
            .setPhase(Phase.EARLY).setSide(Side.BOTH).addMixinClasses("forge.MixinMinecraftForge")
            .setApplyIf(() -> FixesConfig.fixEffectRendererClassTypo).addTargetedMod(TargetedMod.VANILLA)),
    NORTHWEST_BIAS_FIX(new MixinBuilder("Fix Northwest Bias").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinRandomPositionGenerator").setApplyIf(() -> FixesConfig.fixNorthWestBias)
            .addTargetedMod(TargetedMod.VANILLA)),
    SPEEDUP_VANILLA_FURNACE(new MixinBuilder("Speedup Vanilla Furnace").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinFurnaceRecipes").setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace)
            .addTargetedMod(TargetedMod.VANILLA).addTargetedMod(TargetedMod.GTNHLIB)),
    GAMEOVER_GUI_LOCKED_DISABLED(new MixinBuilder("Fix Gameover GUI").setPhase(Phase.EARLY).setSide(Side.CLIENT)
            .addMixinClasses("minecraft.MixinGuiGameOver").setApplyIf(() -> FixesConfig.fixGuiGameOver)
            .addTargetedMod(TargetedMod.VANILLA)),
    PREVENT_PICKUP_LOOT(new MixinBuilder("Prevent monsters from picking up loot").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("minecraft.MixinEntityLivingPickup")
            .setApplyIf(() -> TweaksConfig.preventPickupLoot).addTargetedMod(TargetedMod.VANILLA)),
    DROP_PICKED_LOOT_ON_DESPAWN(new MixinBuilder("Drop picked up loot on despawn").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("minecraft.MixinEntityLivingDrop")
            .setApplyIf(() -> TweaksConfig.dropPickedLootOnDespawn).addTargetedMod(TargetedMod.VANILLA)),
    FIX_HOPPER_HIT_BOX(new MixinBuilder("Fix Vanilla Hopper hit box").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinBlockHopper").setApplyIf(() -> FixesConfig.fixHopperHitBox)
            .addTargetedMod(TargetedMod.VANILLA)),
    TILE_RENDERER_PROFILER_DISPATCHER(new MixinBuilder("TileEntity Render Dispatcher Fix").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.profiler.TileEntityRendererDispatcherMixin").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.enableTileRendererProfiler).addTargetedMod(TargetedMod.VANILLA)),
    TILE_RENDERER_PROFILER_MINECRAFT(new MixinBuilder("Tile Entity Render Profiler").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.profiler.MinecraftMixin").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.enableTileRendererProfiler).addTargetedMod(TargetedMod.VANILLA)),
    DIMENSION_CHANGE_FIX(new MixinBuilder("Dimension Change Heart Fix").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinServerConfigurationManager", "minecraft.MixinEntityPlayerMP")
            .setApplyIf(() -> FixesConfig.fixDimensionChangeAttributes).addTargetedMod(TargetedMod.VANILLA)),
    FIX_EATING_STACKED_STEW(new MixinBuilder("Stacked Mushroom Stew Eating Fix").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("minecraft.MixinItemSoup")
            .setApplyIf(() -> FixesConfig.fixEatingStackedStew).addTargetedMod(TargetedMod.VANILLA)),
    INCREASE_PARTICLE_LIMIT(new MixinBuilder("Increase Particle Limit").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEffectRenderer").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.increaseParticleLimit).addTargetedMod(TargetedMod.VANILLA)),
    ENLARGE_POTION_ARRAY(new MixinBuilder("Make the Potion array larger").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinPotion").setApplyIf(() -> FixesConfig.enlargePotionArray)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_POTION_LIMIT(new MixinBuilder("Fix Potion Limit").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinPotionEffect").setApplyIf(() -> FixesConfig.fixPotionLimit)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_HOPPER_VOIDING_ITEMS(new MixinBuilder("Fix Hopper Voiding Items").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinTileEntityHopper").setApplyIf(() -> FixesConfig.fixHopperVoidingItems)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_HUGE_CHAT_KICK(new MixinBuilder("Fix huge chat kick").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinS02PacketChat").setApplyIf(() -> FixesConfig.fixHugeChatKick)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_BOGUS_INTEGRATED_SERVER_NPE(new MixinBuilder("Fix bogus FMLProxyPacket NPEs on integrated server crashes")
            .setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses(
                    "fml.MixinFMLProxyPacket",
                    "fml.MixinNetworkDispatcher",
                    "minecraft.NetworkManagerAccessor")
            .setApplyIf(() -> FixesConfig.fixBogusIntegratedServerNPEs).addTargetedMod(TargetedMod.VANILLA)),
    FIX_LAG_ON_INVENTORY_SYNC(new MixinBuilder("Fix inventory sync lag").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinInventoryCrafting").setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixInventorySyncLag).addTargetedMod(TargetedMod.VANILLA)),

    FIX_LOGIN_DIMENSION_ID_OVERFLOW(
            new MixinBuilder("Fix dimension id overflowing when a player first logins on a server")
                    .setPhase(Phase.EARLY).setSide(Side.BOTH)
                    .addMixinClasses("minecraft.packets.MixinS01PacketJoinGame_FixDimensionID")
                    .setApplyIf(() -> FixesConfig.fixLoginDimensionIDOverflow).addTargetedMod(TargetedMod.VANILLA)),

    FIX_WORLD_SERVER_LEAKING_UNLOADED_ENTITIES(new MixinBuilder("Fix world server leaking unloaded entities")
            .setPhase(Phase.EARLY).setSide(Side.BOTH).addMixinClasses("minecraft.MixinWorldServerUpdateEntities")
            .setApplyIf(() -> FixesConfig.fixWorldServerLeakingUnloadedEntities).addTargetedMod(TargetedMod.VANILLA)),
    FIX_SKIN_MANAGER_CLIENT_WORLD_LEAK(new MixinBuilder("Fix skin manager client world leak").setPhase(Phase.EARLY)
            .setSide(Side.CLIENT).addMixinClasses("minecraft.MixinSkinManager$2")
            .setApplyIf(() -> FixesConfig.fixSkinManagerLeakingClientWorld).addTargetedMod(TargetedMod.VANILLA)),

    FIX_REDSTONE_TORCH_WORLD_LEAK(new MixinBuilder("Fix world leak in redstone torch").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("minecraft.MixinBlockRedstoneTorch")
            .setApplyIf(() -> FixesConfig.fixRedstoneTorchWorldLeak).addTargetedMod(TargetedMod.VANILLA)
            .addExcludedMod(TargetedMod.BUGTORCH)),
    FIX_ARROW_WRONG_LIGHTING(new MixinBuilder("Fix arrow wrong lighting").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinRendererLivingEntity").setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixGlStateBugs).addTargetedMod(TargetedMod.VANILLA)),
    FIX_RESIZABLE_FULLSCREEN(new MixinBuilder("Fix Resizable Fullscreen").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinMinecraft_ResizableFullscreen").setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixResizableFullscreen).addTargetedMod(TargetedMod.VANILLA)),
    FIX_UNFOCUSED_FULLSCREEN(new MixinBuilder("Fix Unfocused Fullscreen").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinMinecraft_UnfocusedFullscreen").setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixUnfocusedFullscreen).addTargetedMod(TargetedMod.VANILLA)
            .addExcludedMod(ARCHAICFIX)),
    FIX_RENDERERS_WORLD_LEAK(new MixinBuilder("Fix Renderers World Leak").setPhase(Phase.EARLY)
            .addMixinClasses(
                    "minecraft.MixinMinecraft_ClearRenderersWorldLeak",
                    "minecraft.MixinRenderGlobal_FixWordLeak")
            .setSide(Side.CLIENT).setApplyIf(() -> FixesConfig.fixRenderersWorldLeak)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_OPTIFINE_CHUNKLOADING_CRASH(new MixinBuilder("Fix Optifine Chunkloading Crash").setPhase(Phase.EARLY)
            .setApplyIf(() -> FixesConfig.fixOptifineChunkLoadingCrash).setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA).addTargetedMod(TargetedMod.OPTIFINE)
            .addMixinClasses("minecraft.MixinGameSettings_FixOFChunkLoading")),
    ADD_TOGGLE_DEBUG_MESSAGE(new MixinBuilder("Toggle Debug Message").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinMinecraft_ToggleDebugMessage").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.addToggleDebugMessage).addTargetedMod(TargetedMod.VANILLA)),
    OPTIMIZE_TEXTURE_LOADING(new MixinBuilder("Optimize Texture Loading").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.textures.client.MixinTextureUtil").addTargetedMod(TargetedMod.VANILLA)
            .addExcludedMod(ANGELICA).setApplyIf(() -> SpeedupsConfig.optimizeTextureLoading).setSide(Side.CLIENT)),
    HIDE_POTION_PARTICLES(new MixinBuilder("Hide Potion Particles").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityLivingBase_HidePotionParticles").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.hidePotionParticlesFromSelf).addTargetedMod(TargetedMod.VANILLA)),
    DIMENSION_MANAGER_DEBUG(new MixinBuilder("Dimension Manager Debug").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinDimensionManager").setApplyIf(() -> DebugConfig.dimensionManagerDebug)
            .addTargetedMod(TargetedMod.VANILLA)),
    OPTIMIZE_TILEENTITY_REMOVAL(new MixinBuilder("Optimize TileEntity Removal").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinWorldUpdateEntities")
            .setApplyIf(() -> SpeedupsConfig.optimizeTileentityRemoval).addTargetedMod(TargetedMod.VANILLA)
            .addExcludedMod(TargetedMod.FASTCRAFT).addExcludedMod(TargetedMod.BUKKIT)),
    FIX_POTION_ITERATING(new MixinBuilder("Fix Potion Iterating").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinEntityLivingBase_FixPotionException")
            .setApplyIf(() -> FixesConfig.fixPotionIterating).addTargetedMod(TargetedMod.VANILLA)),
    ENHANCE_NIGHT_VISION(new MixinBuilder("Remove the blueish sky tint from night vision").setSide(Side.CLIENT)
            .setPhase(Phase.EARLY).addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> TweaksConfig.enhanceNightVision)
            .addMixinClasses("minecraft.MixinEntityRenderer_EnhanceNightVision")),
    OPTIMIZE_ASMDATATABLE_INDEX(new MixinBuilder("Optimize ASM DataTable Index").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("fml.MixinASMDataTable")
            .setApplyIf(() -> SpeedupsConfig.optimizeASMDataTable).addTargetedMod(TargetedMod.VANILLA)),
    SQUASH_BED_ERROR_MESSAGE(new MixinBuilder("Stop \"You can only sleep at night\" message filling the chat")
            .addMixinClasses("minecraft.MixinNetHandlerPlayClient").addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> FixesConfig.squashBedErrorMessage).setPhase(Phase.EARLY).setSide(Side.CLIENT)),
    CHUNK_SAVE_CME_DEBUG(new MixinBuilder("Add debugging code to Chunk Save CME").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("minecraft.MixinNBTTagCompound")
            .setApplyIf(() -> DebugConfig.chunkSaveCMEDebug).addTargetedMod(TargetedMod.VANILLA)),
    SPEEDUP_NBT_COPY(new MixinBuilder("Speed up NBT copy").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinNBTTagCompound_speedup", "minecraft.MixinNBTTagList_speedup")
            .setApplyIf(() -> ASMConfig.speedupNBTTagCompoundCopy).addTargetedMod(TargetedMod.VANILLA)
            .addExcludedMod(TargetedMod.BUKKIT)),
    STRING_POOLER_NBT_TAG(new MixinBuilder("Pool NBT Strings").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinNBTTagCompound_stringPooler")
            .setApplyIf(() -> TweaksConfig.enableTagCompoundStringPooling).addTargetedMod(TargetedMod.VANILLA)),
    STRING_POOLER_NBT_STRING(new MixinBuilder("Pool NBT Strings").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinNBTTagString_stringPooler")
            .setApplyIf(() -> TweaksConfig.enableNBTStringPooling).addTargetedMod(TargetedMod.VANILLA)),
    THREADED_WORLDDATA_SAVING(new MixinBuilder("Threaded WorldData Saving").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses(
                    "minecraft.MixinMapStorage_threadedIO",
                    "minecraft.MixinSaveHandler_threadedIO",
                    "minecraft.MixinScoreboardSaveData_threadedIO",
                    "minecraft.MixinVillageCollection_threadedIO",
                    "minecraft.MixinMapData_threadedIO",
                    "forge.MixinForgeChunkManager_threadedIO")
            .setApplyIf(() -> TweaksConfig.threadedWorldDataSaving).addTargetedMod(TargetedMod.VANILLA)),
    DONT_SLEEP_ON_THREADED_IO(new MixinBuilder("Don't sleep on threaded IO").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinThreadedFileIOBase_noSleep")
            .setApplyIf(() -> TweaksConfig.dontSleepOnThreadedIO).addTargetedMod(TargetedMod.VANILLA)),
    OPTIMIZE_MOB_SPAWNING(new MixinBuilder("Optimize Mob Spawning").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses(
                    "minecraft.MixinSpawnerAnimals_optimizeSpawning",
                    "minecraft.MixinSpawnListEntry_optimizeSpawning")
            .setApplyIf(() -> SpeedupsConfig.optimizeMobSpawning).addTargetedMod(TargetedMod.VANILLA)
            .addExcludedMod(TargetedMod.BUKKIT)),

    RENDER_DEBUG(new MixinBuilder("Render Debug").setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinRenderGlobal")
            .setSide(Side.CLIENT).setApplyIf(() -> DebugConfig.renderDebug).addTargetedMod(TargetedMod.VANILLA)
            .addExcludedMod(TargetedMod.BUKKIT)),
    STATIC_LAN_PORT(new MixinBuilder("Static Lan Port").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.server.MixinHttpUtil").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.enableDefaultLanPort).addTargetedMod(TargetedMod.VANILLA)),
    CROSSHAIR_THIRDPERSON(new MixinBuilder("Crosshairs thirdperson").setPhase(Phase.EARLY)
            .addMixinClasses("forge.MixinGuiIngameForge_CrosshairThirdPerson").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.hideCrosshairInThirdPerson).addTargetedMod(TargetedMod.VANILLA)),
    DONT_INVERT_CROSSHAIR_COLORS(new MixinBuilder("Don't invert crosshair colors").setPhase(Phase.EARLY)
            .addMixinClasses("forge.MixinGuiIngameForge_CrosshairInvertColors").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.dontInvertCrosshairColor).addTargetedMod(TargetedMod.VANILLA)),
    FIX_OPENGUIHANDLER_WINDOWID(new MixinBuilder("Fix OpenGuiHandler").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("fml.MixinOpenGuiHandler").setApplyIf(() -> FixesConfig.fixForgeOpenGuiHandlerWindowId)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_KEYBIND_CONFLICTS(new MixinBuilder("Trigger all conflicting keybinds").setPhase(Phase.EARLY)
            .setSide(Side.CLIENT).addMixinClasses("minecraft.MixinKeyBinding", "minecraft.MixinMinecraft_UpdateKeys")
            .setApplyIf(() -> FixesConfig.triggerAllConflictingKeybindings).addTargetedMod(TargetedMod.VANILLA)),
    REMOVE_SPAWN_MINECART_SOUND(new MixinBuilder("Remove sound when spawning a minecart").setPhase(Phase.EARLY)
            .setSide(Side.CLIENT).addMixinClasses("minecraft.MixinWorldClient").addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> TweaksConfig.removeSpawningMinecartSound)),
    MACOS_KEYS_TEXTFIELD_SHORTCUTS(new MixinBuilder("Macos use CMD to copy/select/delete text")
            .addMixinClasses("minecraft.MixinGuiTextField").addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(
                    () -> System.getProperty("os.name").toLowerCase().contains("mac")
                            && TweaksConfig.enableMacosCmdShortcuts)
            .setPhase(Phase.EARLY).setSide(Side.CLIENT)),
    FIX_FONT_RENDERER_LINEWRAP_RECURSION(
            new MixinBuilder("Replace recursion with iteration in FontRenderer line wrapping code")
                    .addMixinClasses("minecraft.MixinFontRenderer").addTargetedMod(TargetedMod.VANILLA)
                    .setApplyIf(() -> FixesConfig.fixFontRendererLinewrapRecursion).setPhase(Phase.EARLY)
                    .setSide(Side.CLIENT)),
    BED_MESSAGE_ABOVE_HOTBAR(new MixinBuilder("Bed Message Above Hotbar").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinBlockBed").setSide(Side.BOTH)
            .setApplyIf(() -> TweaksConfig.bedMessageAboveHotbar).addTargetedMod(TargetedMod.VANILLA)),
    BED_ALWAYS_SETS_SPAWN(new MixinBuilder("Clicking a bed in a valid dim will always set your spawn immediately")
            .setPhase(Phase.EARLY).setSide(Side.BOTH).addTargetedMod(TargetedMod.VANILLA)
            // .addExcludedMod(TargetedMod.ETFURUMREQUIEM) // uncomment when EFR adds this feature
            .setApplyIf(() -> TweaksConfig.bedAlwaysSetsSpawn)
            .addMixinClasses("minecraft.MixinBlockBed_AlwaysSetsSpawn")),
    FIX_PLAYER_SKIN_FETCHING(new MixinBuilder("Fix player skin fetching").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinAbstractClientPlayer", "minecraft.MixinThreadDownloadImageData")
            .setSide(Side.CLIENT).setApplyIf(() -> FixesConfig.fixPlayerSkinFetching)
            .addTargetedMod(TargetedMod.VANILLA)),
    VALIDATE_PACKET_ENCODING_BEFORE_SENDING(new MixinBuilder("Validate packet encoding before sending")
            .setPhase(Phase.EARLY)
            .addMixinClasses(
                    "minecraft.packets.MixinDataWatcher",
                    "minecraft.packets.MixinS3FPacketCustomPayload_Validation")
            .setSide(Side.BOTH).setApplyIf(() -> FixesConfig.validatePacketEncodingBeforeSending)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_FLUID_CONTAINER_REGISTRY_KEY(new MixinBuilder("Fix Forge fluid container registry key").setPhase(Phase.EARLY)
            .addMixinClasses("forge.FluidContainerRegistryAccessor", "forge.MixinFluidRegistry").setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixFluidContainerRegistryKey).addTargetedMod(TargetedMod.VANILLA)),
    CHANGE_MAX_NETWORK_NBT_SIZE_LIMIT(
            new MixinBuilder("Modify the maximum NBT size limit as received from network packets").setPhase(Phase.EARLY)
                    .addMixinClasses("minecraft.MixinPacketBuffer").setSide(Side.BOTH)
                    .setApplyIf(() -> FixesConfig.changeMaxNetworkNbtSizeLimit).addTargetedMod(TargetedMod.VANILLA)),

    INCREASE_PACKET_SIZE_LIMIT(
            new MixinBuilder("Increase the packet size limit from 2MiB to a theoretical maximum of 4GiB")
                    .setPhase(Phase.EARLY)
                    .addMixinClasses(
                            "minecraft.MixinMessageSerializer2",
                            "minecraft.MixinMessageDeserializer2",
                            "minecraft.packets.MixinS3FPacketCustomPayload_LengthLimit")
                    .setSide(Side.BOTH).setApplyIf(() -> FixesConfig.increasePacketSizeLimit)
                    .addTargetedMod(TargetedMod.VANILLA)),
    FIX_XRAY_BLOCK_WITHOUT_COLLISION_AABB(new MixinBuilder("Fix Xray through block without collision boundingBox")
            .addMixinClasses("minecraft.MixinBlock_FixXray", "minecraft.MixinWorld_FixXray")
            .setApplyIf(() -> FixesConfig.fixPerspectiveCamera).addTargetedMod(TargetedMod.VANILLA)
            .setPhase(Phase.EARLY).setSide(Side.BOTH)),
    DISABLE_CREATIVE_TAB_ALL_SEARCH(new MixinBuilder("Disable the creative tab with search bar").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiContainerCreative").setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.removeCreativeSearchTab).addTargetedMod(TargetedMod.NOTENOUGHITEMS)),
    FIX_CHAT_COLOR_WRAPPING(new MixinBuilder("Fix wrapped chat lines missing colors").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiNewChat_FixColorWrapping").setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixChatWrappedColors).addTargetedMod(TargetedMod.VANILLA)),
    COMPACT_CHAT(new MixinBuilder("Compact chat").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiNewChat_CompactChat").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.compactChat).addTargetedMod(TargetedMod.VANILLA)),
    NETTY_PATCH(new MixinBuilder("Fix NPE in Netty's Bootstrap class").addMixinClasses("netty.MixinBootstrap")
            .setPhase(Phase.EARLY).setSide(Side.CLIENT).setApplyIf(() -> FixesConfig.fixNettyNPE)
            .addTargetedMod(TargetedMod.VANILLA)),
    MODERN_PICK_BLOCK(new MixinBuilder("Allows pick block to pull items from your inventory")
            .addTargetedMod(TargetedMod.VANILLA).setSide(Side.CLIENT).setPhase(Phase.EARLY)
            .addMixinClasses("forge.MixinForgeHooks_ModernPickBlock").setApplyIf(() -> TweaksConfig.modernPickBlock)),
    TESSELATOR_PRESERVE_QUAD_ORDER(new MixinBuilder("Preserve the rendering order of layered quads on terrain pass 1")
            .addTargetedMod(TargetedMod.VANILLA).setSide(Side.CLIENT).setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinTessellator").setApplyIf(() -> FixesConfig.fixPreserveQuadOrder)),
    FAST_BLOCK_PLACING(new MixinBuilder("Allows blocks to be placed faster").addTargetedMod(TargetedMod.VANILLA)
            .setSide(Side.CLIENT).setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinMinecraft_FastBlockPlacing")
            .setApplyIf(() -> true)), // Always apply, config handled in mixin
    FIX_NEGATIVE_KELVIN(new MixinBuilder("Fix the local temperature can go below absolute zero")
            .addTargetedMod(TargetedMod.VANILLA).setSide(Side.BOTH).setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinBiomeGenBase").setApplyIf(() -> FixesConfig.fixNegativeKelvin)),

    SPIGOT_EXTENDED_CHUNKS(new MixinBuilder("Spigot-style extended chunk format to remove the 2MB chunk size limit")
            .addTargetedMod(TargetedMod.VANILLA).addExcludedMod(TargetedMod.BUKKIT).setSide(Side.BOTH)
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinRegionFile")
            .setApplyIf(() -> FixesConfig.remove2MBChunkLimit)),

    AUTOSAVE_INTERVAL(new MixinBuilder("Sets the auto save interval in ticks").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addTargetedMod(TargetedMod.VANILLA)
            .addMixinClasses("minecraft.server.MixinMinecraftServer_AutoSaveInterval")
            .setApplyIf(() -> TweaksConfig.autoSaveInterval != 900)),

    LIGHTER_WATER(new MixinBuilder("Decreases water opacity from 3 to 1, like in modern").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addTargetedMod(TargetedMod.VANILLA).addMixinClasses("minecraft.MixinBlock_LighterWater")
            .setApplyIf(() -> TweaksConfig.useLighterWater)),

    EARLY_CHUNK_TILE_COORDINATE_CHECK(
            new MixinBuilder("Checks saved TileEntity coordinates earlier to provide a more descriptive error message")
                    .setPhase(Phase.EARLY).setSide(Side.BOTH).addTargetedMod(TargetedMod.VANILLA)
                    .addMixinClasses("minecraft.MixinChunk")
                    .setApplyIf(() -> FixesConfig.earlyChunkTileCoordinateCheck)),

    FIX_DUPLICATE_SOUNDS(new MixinBuilder("Fix duplicate sounds being played when you close a gui while one is playing")
            .setPhase(Phase.EARLY).setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .addMixinClasses("minecraft.MixinMinecraft_FixDuplicateSounds")
            .setApplyIf(() -> FixesConfig.fixDuplicateSounds)),

    ADD_MOD_ITEM_STATS(new MixinBuilder("Add stats for modded items")
            .addMixinClasses("fml.MixinGameRegistry", "minecraft.MixinStats").addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> TweaksConfig.addModItemStats).setPhase(Phase.EARLY).setSide(Side.BOTH)),

    ADD_MOD_ENTITY_STATS(new MixinBuilder("Add stats for modded entities").addMixinClasses("minecraft.MixinStatList")
            .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> TweaksConfig.addModEntityStats).setPhase(Phase.EARLY)
            .setSide(Side.BOTH)),

    ADD_MOD_ENTITY_STATS_CLIENT(new MixinBuilder("Add stats for modded entities (client side)")
            .addMixinClasses("minecraft.MixinStatsMobsList", "minecraft.MixinStatsBlock", "minecraft.MixinStatsItem")
            .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> TweaksConfig.addModEntityStats).setPhase(Phase.EARLY)
            .setSide(Side.CLIENT)),

    FIX_SLASH_COMMAND(new MixinBuilder(
            "Fix forge command handler not checking for a / and also not running commands with any case")
                    .setPhase(Phase.EARLY).setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
                    .addMixinClasses("minecraft.MixinClientCommandHandler_CommandFix")
                    .setApplyIf(() -> FixesConfig.fixSlashCommands)),

    FIX_CASE_COMMAND(new MixinBuilder("Fix the command handler not allowing you to run commands typed in any case")
            .setPhase(Phase.EARLY).setSide(Side.BOTH).addTargetedMod(TargetedMod.VANILLA)
            .addMixinClasses("minecraft.MixinCommandHandler_CommandFix").setApplyIf(() -> FixesConfig.fixCaseCommands)),
    FIX_GAMESETTINGS_OUTOFBOUNDS(new MixinBuilder("Fix array out of bound error in GameSettings menu")
            .setPhase(Phase.EARLY).setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .addMixinClasses("minecraft.MixinGameSettings_FixArrayOutOfBounds")
            .setApplyIf(() -> FixesConfig.fixGameSettingsArrayOutOfBounds).addExcludedMod(TargetedMod.LWJGL3IFY)),

    LIMIT_RECURSIVE_BLOCK_UPDATE_DEPTH(new MixinBuilder(
            "Limit the number of recursive cascading block updates during world generation to prevent stack overflow crashes")
                    .setPhase(Phase.EARLY).setSide(Side.BOTH).addTargetedMod(TargetedMod.VANILLA)
                    .addMixinClasses("minecraft.MixinWorldServer_LimitUpdateRecursion")
                    .setApplyIf(() -> FixesConfig.limitRecursiveBlockUpdateDepth >= 0)),

    ADD_MOD_CONFIG_SEARCHBAR(new MixinBuilder("Adds a search bar to the mod config GUI").setPhase(Phase.EARLY)
            .setSide(Side.CLIENT).addMixinClasses("fml.MixinGuiConfig")
            .setApplyIf(() -> TweaksConfig.addModConfigSearchBar).addTargetedMod(TargetedMod.VANILLA)),

    FIX_BUTTON_POS_GUIOPENLINK(new MixinBuilder("Fix the buttons not being centered in the GuiConfirmOpenLink")
            .setPhase(Phase.EARLY).setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .addMixinClasses("minecraft.MixinGuiConfirmOpenLink")
            .setApplyIf(() -> FixesConfig.fixButtonsGuiConfirmOpenLink)),

    FIX_CHAT_OPEN_LINK(new MixinBuilder("Fix the vanilla method to open chat links not working for every OS")
            .setPhase(Phase.EARLY).setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .addMixinClasses("minecraft.MixinGuiChat_OpenLinks").setApplyIf(() -> FixesConfig.fixChatOpenLink)),

    FIX_NAMETAG_BRIGHTNESS(new MixinBuilder("Fix nametag brightness").setPhase(Phase.EARLY).setSide(Side.CLIENT)
            .addMixinClasses("minecraft.MixinRendererLivingEntity_NametagBrightness")
            .setApplyIf(() -> FixesConfig.fixNametagBrightness).addTargetedMod(TargetedMod.VANILLA)),

    FIX_HIT_EFFECT_BRIGHTNESS(new MixinBuilder("Fix hit effect brightness").setPhase(Phase.EARLY).setSide(Side.CLIENT)
            .addMixinClasses("minecraft.MixinRendererLivingEntity_HitEffectBrightness")
            .setApplyIf(() -> FixesConfig.fixHitEffectBrightness).addTargetedMod(TargetedMod.VANILLA)),

    FIX_BUKKIT_PLAYER_CONTAINER(new MixinBuilder("Fix Bukkit BetterQuesting crash").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addTargetedMod(TargetedMod.BUKKIT).addMixinClasses("minecraft.MixinContainerPlayer")
            .setApplyIf(() -> FixesConfig.fixBukkitBetterQuestingCrash)),

    MEMORY_FIXES_CLIENT(new MixinBuilder("Memory fixes").setPhase(Phase.EARLY).setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA).addMixinClasses("memory.MixinFMLClientHandler")
            .setApplyIf(() -> FixesConfig.enableMemoryFixes)),

    FAST_RANDOM(new MixinBuilder("Replaces uses of stdlib Random with a faster one").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addTargetedMod(TargetedMod.VANILLA)
            .addMixinClasses(
                    "minecraft.fastload.rand.MixinChunkProviderGenerate",
                    "minecraft.fastload.rand.MixinMapGenBase",
                    "minecraft.fastload.rand.MixinMapGenCaves")
            .setApplyIf(() -> SpeedupsConfig.fastRandom)),

    FAST_INT_CACHE(new MixinBuilder("Rewrite internal caching methods to be safer and faster").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addTargetedMod(TargetedMod.VANILLA)
            .addMixinClasses(
                    "minecraft.fastload.intcache.MixinCollectOneCache",
                    "minecraft.fastload.intcache.MixinCollectTwoCaches",
                    "minecraft.fastload.intcache.MixinGenLayerEdge",
                    "minecraft.fastload.intcache.MixinIntCache",
                    "minecraft.fastload.intcache.MixinWorldChunkManager")
            .setApplyIf(() -> SpeedupsConfig.fastIntCache)),

    NUKE_LONG_BOXING(new MixinBuilder("Remove Long boxing in MapGenStructure").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addTargetedMod(TargetedMod.VANILLA).addMixinClasses("minecraft.fastload.MixinMapGenStructure")
            .setApplyIf(() -> SpeedupsConfig.unboxMapGen)),

    EMBED_BLOCKIDS(new MixinBuilder("Embed IDs directly in the objects, to accelerate lookups").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addTargetedMod(TargetedMod.VANILLA).addExcludedMod(FASTCRAFT).addExcludedMod(BUKKIT)
            .addMixinClasses(
                    "minecraft.fastload.embedid.MixinEmbedIDs",
                    "minecraft.fastload.embedid.MixinFMLControlledNamespacedRegistry",
                    "minecraft.fastload.embedid.MixinObjectIntIdentityMap")
            .setApplyIf(() -> ASMConfig.embedID_experimental)),

    FAST_CHUNK_LOADING(new MixinBuilder("Invasively accelerates chunk handling").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addTargetedMod(TargetedMod.VANILLA)
            .addMixinClasses("minecraft.fastload.MixinEntityPlayerMP", "minecraft.fastload.MixinChunkProviderServer")
            .setApplyIf(() -> SpeedupsConfig.fastChunkHandling)),

    CANCEL_NONE_SOUNDS(new MixinBuilder("Skips playing 'none' and '' sounds").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addTargetedMod(TargetedMod.VANILLA).addMixinClasses("minecraft.shutup.MixinWorld")
            .setApplyIf(() -> FixesConfig.skipEmptySounds)),

    MEMORY_FIXES_IC2(new MixinBuilder("Removes allocation spam from the Direction.applyTo method").setPhase(Phase.LATE)
            .setSide(Side.BOTH).addMixinClasses("ic2.MixinDirection_Memory")
            .setApplyIf(() -> FixesConfig.enableMemoryFixes).addTargetedMod(TargetedMod.IC2)),

    FIX_PLAYER_BLOCK_PLACEMENT_DISTANCE_CHECK(new MixinBuilder("Fix wrong block placement distance check")
            .setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinNetHandlePlayServer_FixWrongBlockPlacementCheck")
            .setApplyIf(() -> FixesConfig.fixWrongBlockPlacementDistanceCheck).addTargetedMod(TargetedMod.VANILLA)),

    PREVENT_LAVA_CHUNK_LOADING(new MixinBuilder("Prevent lava blocks from loading chunks").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("minecraft.MixinBlockStaticLiquid")
            .setApplyIf(() -> SpeedupsConfig.lavaChunkLoading).addTargetedMod(TargetedMod.VANILLA)),

    FIX_GLASS_BOTTLE_NON_WATER_BLOCKS(
            new MixinBuilder("Fix Glass Bottles filling with Water from some other Fluid blocks").setPhase(Phase.EARLY)
                    .setSide(Side.BOTH).addMixinClasses("minecraft.MixinItemGlassBottle")
                    .setApplyIf(() -> FixesConfig.fixGlassBottleWaterFilling).addTargetedMod(TargetedMod.VANILLA)),

    FIX_IOOBE_RENDER_DISTANCE(
            new MixinBuilder("Fix out of bounds render distance when Optifine/Angelica is uninstalled")
                    .setPhase(Phase.EARLY).setSide(Side.CLIENT).addExcludedMod(OPTIFINE).addExcludedMod(ANGELICA)
                    .addExcludedMod(FALSETWEAKS).addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> true)
                    .addMixinClasses("minecraft.MixinGameSettings_ReduceRenderDistance")),

    BETTER_MOD_LIST(new MixinBuilder("Better Mod List").setPhase(Phase.EARLY).setSide(Side.CLIENT)
            .addMixinClasses("fml.MixinGuiModList", "fml.MixinGuiSlotModList", "fml.MixinGuiScrollingList")
            .setApplyIf(
                    () -> TweaksConfig.betterModList
                            && !doesResourceExist("com/enderio/core/common/transform/EnderCoreTransformerClient.class"))
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_EGG_PARTICLE(new MixinBuilder("Use correct egg particles instead of snowball ones (MC-7807)")
            .setPhase(Phase.EARLY).setSide(Side.CLIENT).addMixinClasses("minecraft.MixinEntityEgg")
            .setApplyIf(() -> FixesConfig.fixEggParticles).addTargetedMod(TargetedMod.VANILLA)),
    FIX_EVENTBUS_MEMORY_LEAK(
            new MixinBuilder("Fix EventBus keeping object references after unregistering event handlers.")
                    .setPhase(Phase.EARLY).setSide(Side.BOTH)
                    .addMixinClasses("fml.MixinListenerListInst", "fml.MixinEventBus")
                    .setApplyIf(() -> FixesConfig.fixEventBusMemoryLeak).addTargetedMod(TargetedMod.VANILLA)),

    // Ic2 adjustments
    IC2_UNPROTECTED_GET_BLOCK_FIX(new MixinBuilder("IC2 Kinetic Fix").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("ic2.MixinIc2WaterKinetic").setApplyIf(() -> FixesConfig.fixIc2UnprotectedGetBlock)
            .addTargetedMod(TargetedMod.IC2)),
    IC2_DIRECT_INV_ACCESS(new MixinBuilder("IC2 Direct Inventory Access Fix").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("ic2.MixinItemCropSeed", "ic2.MixinTileEntityCrop")
            .setApplyIf(() -> FixesConfig.fixIc2DirectInventoryAccess).addTargetedMod(TargetedMod.IC2)),
    IC2_NIGHT_VISION_NANO(new MixinBuilder("IC2 Nightvision Fix").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses(
                    "ic2.MixinIc2NanoSuitNightVision",
                    "ic2.MixinIc2QuantumSuitNightVision",
                    "ic2.MixinIc2NightVisionGoggles")
            .setApplyIf(() -> FixesConfig.fixIc2Nightvision).addTargetedMod(TargetedMod.IC2)),
    IC2_REACTOR_DUPE(new MixinBuilder("IC2 Reactor Dupe Fix").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("ic2.MixinTileEntityReactorChamberElectricNoDupe")
            .setApplyIf(() -> FixesConfig.fixIc2ReactorDupe).addTargetedMod(TargetedMod.IC2)),
    IC2_REACTOR_INVENTORY_SPEEDUP(new MixinBuilder("IC2 Reactor Inventory Speedup Fix").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("ic2.MixinTileEntityReactorChamberElectricInvSpeedup")
            .setApplyIf(() -> FixesConfig.optimizeIc2ReactorInventoryAccess).addTargetedMod(TargetedMod.IC2)),
    HIDE_IC2_REACTOR_COOLANT_SLOTS(new MixinBuilder("IC2 Reactory Fix").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("ic2.MixinTileEntityNuclearReactorElectric")
            .setApplyIf(() -> TweaksConfig.hideIc2ReactorSlots).addTargetedMod(TargetedMod.IC2)),
    IC2_FLUID_CONTAINER_TOOLTIP(new MixinBuilder("IC2 Fluid Container Tooltip Fix").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("ic2.MixinItemIC2FluidContainer")
            .setApplyIf(() -> TweaksConfig.displayIc2FluidLocalizedName).addTargetedMod(TargetedMod.IC2)),
    IC2_HOVER_MODE_FIX(new MixinBuilder("IC2 Hover Mode Fix").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("ic2.MixinIc2QuantumSuitHoverMode").setApplyIf(() -> FixesConfig.fixIc2HoverMode)
            .addTargetedMod(TargetedMod.IC2)),
    IC2_ARMOR_LAG_FIX(new MixinBuilder("IC2 Armor Lag Fix").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses(
                    "ic2.MixinElectricItemManager",
                    "ic2.MixinIC2ArmorHazmat",
                    "ic2.MixinIC2ArmorJetpack",
                    "ic2.MixinIC2ArmorNanoSuit",
                    "ic2.MixinIC2ArmorNightvisionGoggles",
                    "ic2.MixinIC2ArmorQuantumSuit",
                    "ic2.MixinIC2ArmorSolarHelmet",
                    "ic2.MixinIC2ArmorStaticBoots")
            .setApplyIf(() -> FixesConfig.fixIc2ArmorLag).addTargetedMod(TargetedMod.IC2)),
    IC2_RESOURCE_PACK_TRANSLATION_FIX(
            new MixinBuilder("IC2 Resource Pack Translation Fix").setPhase(Phase.EARLY).setSide(Side.CLIENT)
                    .addMixinClasses("fml.MixinLanguageRegistry", "fml.MixinFMLClientHandler", "ic2.MixinLocalization")
                    .setApplyIf(() -> FixesConfig.fixIc2ResourcePackTranslation).addTargetedMod(TargetedMod.IC2)),
    IC2_CROP_TRAMPLING_FIX(new MixinBuilder("IC2 Crop Trampling Fix").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("ic2.MixinIC2TileEntityCrop").setApplyIf(() -> FixesConfig.fixIc2CropTrampling)
            .addTargetedMod(TargetedMod.IC2)),
    IC2_SYNC_REACTORS(new MixinBuilder("Synchronize IC2 reactors for more consistent operation").setPhase(Phase.LATE)
            .setSide(Side.BOTH).addMixinClasses("ic2.sync.MixinTEReactorChamber", "ic2.sync.MixinTEReactor")
            .setApplyIf(() -> TweaksConfig.synchronizeIC2Reactors).addTargetedMod(TargetedMod.IC2)),

    // Disable update checkers
    BIBLIOCRAFT_UPDATE_CHECK(new MixinBuilder("Yeet Bibliocraft Update Check").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .addMixinClasses("bibliocraft.MixinVersionCheck").setApplyIf(() -> FixesConfig.removeUpdateChecks)
            .addTargetedMod(TargetedMod.BIBLIOCRAFT)),
    COFH_CORE_UPDATE_CHECK(new MixinBuilder("Yeet COFH Core Update Check").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("cofhcore.MixinCoFHCoreUpdateCheck").setApplyIf(() -> FixesConfig.removeUpdateChecks)
            .addTargetedMod(TargetedMod.COFH_CORE)),
    DAMAGE_INDICATORS_UPDATE_CHECK(new MixinBuilder("Yeet Damage Indicators Update Check").setPhase(Phase.LATE)
            .setSide(Side.CLIENT).addMixinClasses("damageindicators.MixinDIClientProxy")
            .setApplyIf(() -> FixesConfig.removeUpdateChecks).addTargetedMod(TargetedMod.DAMAGE_INDICATORS)),

    // Railcraft Anchors
    WAKE_ANCHORS_ON_LOGIN_PASSIVE(new MixinBuilder("Wake passive anchors on login").setPhase(Phase.LATE)
            .setSide(Side.BOTH).addMixinClasses("railcraft.MixinTileAnchorPassive")
            .setApplyIf(() -> TweaksConfig.installAnchorAlarm).addTargetedMod(TargetedMod.RAILCRAFT)),
    WAKE_ANCHORS_ON_LOGIN_PERSONAL(new MixinBuilder("Wake person anchors on login").setPhase(Phase.LATE)
            .setSide(Side.BOTH).addMixinClasses("railcraft.MixinTileAnchorPersonal")
            .setApplyIf(() -> TweaksConfig.installAnchorAlarm).addTargetedMod(TargetedMod.RAILCRAFT)),

    // Hunger overhaul
    HUNGER_OVERHAUL_LOW_STAT_EFFECT(new MixinBuilder("Patch unintended low stat effects").setPhase(Phase.LATE)
            .setSide(Side.BOTH).addMixinClasses("hungeroverhaul.MixinHungerOverhaulLowStatEffect")
            .setApplyIf(() -> FixesConfig.fixHungerOverhaul).addTargetedMod(TargetedMod.HUNGER_OVERHAUL)),
    HUNGER_OVERHAUL_REGEN(new MixinBuilder("Patch Regen").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("hungeroverhaul.MixinHungerOverhaulHealthRegen")
            .setApplyIf(() -> FixesConfig.fixHungerOverhaul).addTargetedMod(TargetedMod.HUNGER_OVERHAUL)),
    HUNGER_OVERHAUL_0_HUNGER(new MixinBuilder("Fix some items restore 0 hunger").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("hungeroverhaul.MixinHungerOverhaulModuleHarvestCraft")
            .setApplyIf(() -> FixesConfig.fixHungerOverhaulRestore0Hunger).addTargetedMod(TargetedMod.HUNGER_OVERHAUL)
            .addTargetedMod(TargetedMod.HARVESTCRAFT)),

    // Thaumcraft
    THREADED_THAUMCRAFT_MAZE_SAVING(new MixinBuilder("Threaded Thaumcraft Maze Saving").setPhase(Phase.LATE)
            .setSide(Side.BOTH).addMixinClasses("thaumcraft.MixinMazeHandler_threadedIO")
            .setApplyIf(() -> TweaksConfig.threadedWorldDataSaving).addTargetedMod(TargetedMod.THAUMCRAFT)),
    ADD_CV_SUPPORT_TO_WAND_PEDESTAL(new MixinBuilder("CV Support for Wand Pedestal").setPhase(Phase.LATE)
            .setSide(Side.BOTH).addMixinClasses("thaumcraft.MixinTileWandPedestal")
            .setApplyIf(() -> TweaksConfig.addCVSupportToWandPedestal).addTargetedMod(TargetedMod.THAUMCRAFT)),
    FIX_ASPECT_SORTING(new MixinBuilder("Fix Thaumcraft Aspects not being sorted by name")
            .addMixinClasses(
                    "thaumcraft.MixinGuiResearchRecipe",
                    "thaumcraft.MixinGuiResearchTable",
                    "thaumcraft.MixinGuiThaumatorium",
                    "thaumcraft.MixinItem_SortAspectsByName")
            .setPhase(Phase.LATE).setSide(Side.CLIENT).setApplyIf(() -> FixesConfig.fixThaumcraftAspectSorting)
            .addTargetedMod(TargetedMod.THAUMCRAFT)),
    FIX_GOLEM_MARKER_LOADING(new MixinBuilder("Fix golem marker loading failure when dimensionId larger than MAX_BYTE")
            .setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("thaumcraft.MixinEntityGolemBase", "thaumcraft.MixinItemGolemBell")
            .setApplyIf(() -> FixesConfig.fixThaumcraftGolemMarkerLoading).addTargetedMod(TargetedMod.THAUMCRAFT)),

    FIX_WORLD_COORDINATE_HASHING_METHOD(new MixinBuilder("Implement a proper hashing method for WorldCoordinates")
            .addMixinClasses("thaumcraft.MixinWorldCoordinates").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixThaumcraftWorldCoordinatesHashingMethod)
            .addTargetedMod(TargetedMod.THAUMCRAFT)),

    FIX_MAGICAL_LEAVES_LAG(new MixinBuilder("Fix Thaumcraft leaves frequent ticking")
            .addMixinClasses("thaumcraft.MixinBlockMagicalLeaves", "thaumcraft.MixinBlockMagicalLog")
            .setPhase(Phase.LATE).setSide(Side.BOTH).setApplyIf(() -> FixesConfig.fixThaumcraftLeavesLag)
            .addTargetedMod(TargetedMod.THAUMCRAFT)),
    FIX_THAUMCRAFT_VIS_DUPLICATION(new MixinBuilder("Fix Thaumcraft Vis Duplication")
            .addMixinClasses("thaumcraft.MixinTileWandPedestal_VisDuplication").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixWandPedestalVisDuplication).addTargetedMod(TargetedMod.THAUMCRAFT)),
    FIX_NULL_HANDLING_ITEMWISPESSENCE_CLIENT(new MixinBuilder("Fix handling of null stacks in ItemWispEssence")
            .addMixinClasses("thaumcraft.MixinItemWispEssence_Client").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixNullHandlingItemWispEssence).addTargetedMod(TargetedMod.THAUMCRAFT)),
    FIX_NULL_HANDLING_ITEMWISPESSENCE_BOTH(new MixinBuilder("Fix handling of null stacks in ItemWispEssence")
            .addMixinClasses("thaumcraft.MixinItemWispEssence_Both").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixNullHandlingItemWispEssence).addTargetedMod(TargetedMod.THAUMCRAFT)),

    // BOP
    FIX_QUICKSAND_XRAY(new MixinBuilder("Fix Xray through block without collision boundingBox").setPhase(Phase.LATE)
            .setSide(Side.BOTH).addMixinClasses("biomesoplenty.MixinBlockMud_FixXray")
            .setApplyIf(() -> FixesConfig.fixPerspectiveCamera).addTargetedMod(TargetedMod.BOP)),
    DEDUPLICATE_FORESTRY_COMPAT_IN_BOP(new MixinBuilder("BOP Forestry Compat").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("biomesoplenty.MixinForestryIntegration")
            .setApplyIf(() -> FixesConfig.deduplicateForestryCompatInBOP).addTargetedMod(TargetedMod.BOP)),
    SPEEDUP_BOP_BIOME_FOG(new MixinBuilder("BOP Biome Fog").addMixinClasses("biomesoplenty.MixinFogHandler")
            .setPhase(Phase.LATE).setSide(Side.CLIENT).setApplyIf(() -> SpeedupsConfig.speedupBOPFogHandling)
            .addTargetedMod(TargetedMod.BOP)),
    SPEEDUP_BOP_BIOME_FOG_ACCESSOR(new MixinBuilder("BOP Biome Fog Accessor")
            .addMixinClasses("biomesoplenty.AccessorFogHandler").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .setApplyIf(() -> SpeedupsConfig.speedupBOPFogHandling).addTargetedMod(TargetedMod.BOP)),
    BIG_FIR_TREES(new MixinBuilder("BOP Fir Trees").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("biomesoplenty.MixinBlockBOPSapling").setApplyIf(() -> TweaksConfig.makeBigFirsPlantable)
            .addTargetedMod(TargetedMod.BOP)),
    JAVA12_BOP(new MixinBuilder("BOP Java12-safe reflection").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("biomesoplenty.MixinBOPBiomes").addMixinClasses("biomesoplenty.MixinBOPReflectionHelper")
            .setApplyIf(() -> FixesConfig.java12BopCompat).addTargetedMod(TargetedMod.BOP)),
    DISABLE_QUICKSAND_GENERATION(new MixinBuilder("Disable BOP quicksand").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("biomesoplenty.MixinDisableQuicksandGeneration")
            .setApplyIf(() -> TweaksConfig.removeBOPQuicksandGeneration).addTargetedMod(TargetedMod.BOP)),
    // COFH
    COFH_REMOVE_TE_CACHE(
            new MixinBuilder("Remove CoFH tile entity cache").addMixinClasses("minecraft.MixinWorld_CoFH_TE_Cache")
                    .setSide(Side.BOTH).setApplyIf(() -> ASMConfig.cofhWorldTransformer)
                    .addTargetedMod(TargetedMod.COFH_CORE).setPhase(Phase.EARLY)),
    MFR_FIX_COFH_VALIDATE(new MixinBuilder("Remove CoFH TE cache usage from MFR")
            .addMixinClasses(
                    "minefactoryreloaded.MixinTileEntityBase",
                    "minefactoryreloaded.MixinTileEntityRedNetCable")
            .addTargetedMod(TargetedMod.MINEFACTORY_RELOADED).setApplyIf(() -> ASMConfig.cofhWorldTransformer)
            .setPhase(Phase.LATE).setSide(Side.BOTH)),
    TE_FIX_COFH_VALIDATE(new MixinBuilder("Remove CoFH TE cache usage from TE")
            .addMixinClasses("thermalexpansion.MixinTileInventoryTileLightFalse")
            .addTargetedMod(TargetedMod.THERMALEXPANSION).setApplyIf(() -> ASMConfig.cofhWorldTransformer)
            .setPhase(Phase.LATE).setSide(Side.BOTH)),
    FIX_ORE_DICT_NPE(new MixinBuilder("Fix NPE in OreDictionaryArbiter")
            .addMixinClasses("cofhcore.MixinOreDictionaryArbiter").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addTargetedMod(TargetedMod.COFH_CORE).setApplyIf(() -> FixesConfig.fixCofhOreDictNPE)),
    FIX_ORE_DICT_CME(new MixinBuilder("Fix race condition in CoFH oredict")
            .addMixinClasses("cofhcore.MixinFMLEventHandler").setPhase(Phase.EARLY).setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.COFH_CORE).setApplyIf(() -> FixesConfig.fixCofhOreDictCME)),
    COFH_IMPROVE_BREAKBLOCK(new MixinBuilder("Improve CoFH breakBlock method to support mods")
            .addMixinClasses("cofhcore.MixinBlockHelper").setPhase(Phase.EARLY).setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.COFH_CORE).setApplyIf(() -> TweaksConfig.improveCofhBreakBlock)),

    // Minefactory Reloaded
    DISARM_SACRED_TREE(new MixinBuilder("Prevents Sacred Rubber Tree Generation")
            .addMixinClasses("minefactoryreloaded.MixinBlockRubberSapling").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addTargetedMod(TargetedMod.MINEFACTORY_RELOADED)
            .setApplyIf(() -> FixesConfig.disableMassiveSacredTreeGeneration)),
    MFR_IMPROVE_BLOCKSMASHER(new MixinBuilder("Improve MFR block smasher")
            .addMixinClasses("minefactoryreloaded.MixinTileEntityBlockSmasher").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addTargetedMod(TargetedMod.MINEFACTORY_RELOADED).setApplyIf(() -> TweaksConfig.improveMfrBlockSmasher)),
    MFR_IMPROVE_BLOCKBREAKER(new MixinBuilder("Improve MFR block breaker")
            .addMixinClasses("minefactoryreloaded.MixinTileEntityBlockBreaker").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addTargetedMod(TargetedMod.MINEFACTORY_RELOADED).setApplyIf(() -> TweaksConfig.improveMfrBlockBreaker)),

    // Immersive engineering
    JAVA12_IMMERSIVE_ENGINERRING(new MixinBuilder("Immersive Engineering Java-12 safe potion array resizing")
            .setPhase(Phase.LATE).setSide(Side.BOTH).addMixinClasses("immersiveengineering.MixinIEPotions")
            .setApplyIf(() -> FixesConfig.java12ImmersiveEngineeringCompat)
            .addTargetedMod(TargetedMod.IMMERSIVE_ENGINENEERING)),
    JAVA12_MINE_CHEM(new MixinBuilder("Minechem Java-12 safe potion array resizing").setPhase(Phase.LATE)
            .setSide(Side.BOTH).addMixinClasses("minechem.MixinPotionInjector")
            .setApplyIf(() -> FixesConfig.java12MineChemCompat).addTargetedMod(TargetedMod.MINECHEM)),

    // Modular Powersuits
    MPS_PREVENT_RF_ENERGY_SYPHON(new MixinBuilder("Prevent MPS from charging and draining RF from Inventory")
            .setPhase(Phase.LATE).setSide(Side.BOTH).addMixinClasses("mps.MixinElectricAdapterRF")
            .setApplyIf(() -> TweaksConfig.preventMPSEnergyTransferRF).addTargetedMod(TargetedMod.MODULARPOWERSUITS)),
    MPS_PREVENT_EU_ENERGY_SYPHON(new MixinBuilder("Prevent MPS from charging and draining EU from Inventory")
            .setPhase(Phase.LATE).setSide(Side.BOTH).addMixinClasses("mps.MixinElectricAdapterEU")
            .setApplyIf(() -> TweaksConfig.preventMPSEnergyTransferEU).addTargetedMod(TargetedMod.MODULARPOWERSUITS)),
    MPS_PREVENT_ME_ENERGY_SYPHON(new MixinBuilder("Prevent MPS from charging and draining ME from Inventory")
            .setPhase(Phase.LATE).setSide(Side.BOTH).addMixinClasses("mps.MixinElectricAdapterME")
            .setApplyIf(() -> TweaksConfig.preventMPSEnergyTransferME).addTargetedMod(TargetedMod.MODULARPOWERSUITS)),

    // MrTJPCore (Project Red)
    FIX_HUD_LIGHTING_GLITCH(new MixinBuilder("HUD Lighting glitch").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("mrtjpcore.MixinFXEngine").setApplyIf(() -> TweaksConfig.fixHudLightingGlitch)
            .addTargetedMod(TargetedMod.MRTJPCORE)),
    FIX_POPPING_OFF(new MixinBuilder("Fix Popping Off").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("mrtjpcore.MixinPlacementLib").setApplyIf(() -> TweaksConfig.fixComponentsPoppingOff)
            .addTargetedMod(TargetedMod.MRTJPCORE)),

    // Automagy
    IMPLEMENTS_CONTAINER_FOR_THIRSTY_TANK(new MixinBuilder("Thirsty Tank Container").setPhase(Phase.LATE)
            .setSide(Side.BOTH).addMixinClasses("automagy.MixinItemBlockThirstyTank")
            .setApplyIf(() -> TweaksConfig.thirstyTankContainer).addTargetedMod(TargetedMod.AUTOMAGY)),

    // Better HUD
    FIX_BETTERHUD_ARMOR_BAR(new MixinBuilder("Fix better HUD armor display breaking with skulls").setPhase(Phase.LATE)
            .setSide(Side.BOTH).addMixinClasses("betterhud.MixinSkullDurabilityDisplay")
            .setApplyIf(() -> FixesConfig.fixBetterHUDArmorDisplay).addTargetedMod(TargetedMod.BETTERHUD)),

    FIX_BETTERHUD_HEARTS_FREEZE(
            new MixinBuilder("Fix better HUD freezing the game when trying to render high amounts of hp")
                    .setPhase(Phase.LATE).setSide(Side.BOTH).addMixinClasses("betterhud.MixinHealthRender")
                    .setApplyIf(() -> FixesConfig.fixBetterHUDHPDisplay).addTargetedMod(TargetedMod.BETTERHUD)),

    // ProjectE
    FIX_FURNACE_ITERATION(new MixinBuilder("Speedup Furnaces").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("projecte.MixinObjHandler").setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace)
            .addTargetedMod(TargetedMod.PROJECTE)),

    // LOTR
    FIX_LOTR_FURNACE_ERROR(new MixinBuilder("Patches lotr to work with the vanilla furnace speedup")
            .setPhase(Phase.LATE).setSide(Side.BOTH).addMixinClasses("lotr.MixinLOTRRecipes")
            .setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace).addTargetedMod(TargetedMod.VANILLA)
            .addTargetedMod(TargetedMod.GTNHLIB).addTargetedMod(TargetedMod.LOTR)),

    FIX_LOTR_JAVA12(new MixinBuilder("Fix lotr java 12+ compat").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses(
                    "lotr.MixinLOTRLogReflection",
                    "lotr.MixinRedirectHuornAI",
                    "lotr.MixinRemoveUnlockFinalField")
            .setApplyIf(() -> FixesConfig.java12LotrCompat).addTargetedMod(TargetedMod.LOTR)),

    // Journeymap
    FIX_JOURNEYMAP_KEYBINDS(new MixinBuilder("Fix Journeymap Keybinds").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .addMixinClasses("journeymap.MixinConstants").setApplyIf(() -> FixesConfig.fixJourneymapKeybinds)
            .addTargetedMod(TargetedMod.JOURNEYMAP)),
    FIX_JOURNEYMAP_ILLEGAL_FILE_PATH_CHARACTER(new MixinBuilder("Fix Journeymap Illegal File Path Character")
            .setPhase(Phase.LATE).setSide(Side.CLIENT).addMixinClasses("journeymap.MixinWorldData")
            .setApplyIf(() -> FixesConfig.fixJourneymapFilePath).addTargetedMod(TargetedMod.JOURNEYMAP)),

    FIX_JOURNEYMAP_JUMPY_SCROLLING(new MixinBuilder("Fix Journeymap jumpy scrolling in the waypoint manager")
            .setPhase(Phase.LATE).setSide(Side.CLIENT).addMixinClasses("journeymap.MixinWaypointManager")
            .setApplyIf(() -> FixesConfig.fixJourneymapJumpyScrolling).addTargetedMod(TargetedMod.JOURNEYMAP)),

    // Xaero's World Map
    FIX_XAEROS_WORLDMAP_SCROLL(
            new MixinBuilder("Fix Xaero's World Map map screen scrolling").addMixinClasses("xaeroworldmap.MixinGuiMap")
                    .setPhase(Phase.LATE).setSide(Side.CLIENT).setApplyIf(() -> FixesConfig.fixXaerosWorldMapScroll)
                    .addTargetedMod(TargetedMod.XAEROWORLDMAP).addTargetedMod(TargetedMod.LWJGL3IFY)),

    // Xaero's Minimap
    FIX_XAEROS_MINIMAP_ENTITYDOT(
            new MixinBuilder("Fix Xaero's Minimap player entity dot rendering when arrow is chosen")
                    .addMixinClasses("xaerominimap.MixinMinimapRenderer").setPhase(Phase.LATE).setSide(Side.CLIENT)
                    .setApplyIf(() -> FixesConfig.fixXaerosMinimapEntityDot).addTargetedMod(TargetedMod.XAEROMINIMAP)),

    // Pam's Harvest the Nether
    FIX_IGNIS_FRUIT_AABB(new MixinBuilder("Ignis Fruit").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("harvestthenether.MixinBlockPamFruit").setApplyIf(() -> FixesConfig.fixIgnisFruitAABB)
            .addTargetedMod(TargetedMod.HARVESTTHENETHER)),
    FIX_NETHER_LEAVES_FACE_RENDERING(new MixinBuilder("Nether Leaves")
            .addMixinClasses("harvestthenether.MixinBlockNetherLeaves").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixIgnisFruitAABB).addTargetedMod(TargetedMod.HARVESTTHENETHER)),

    // Potion Render Offset Fixes - Various Mods
    FIX_BAUBLES_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder("Baubles Inventory with Potions")
            .addMixinClasses("baubles.MixinGuiEvents").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.fixPotionRenderOffset).addTargetedMod(TargetedMod.BAUBLES)),
    FIX_GALACTICRAFT_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder("Galacticraft Inventory with Potions")
            .addMixinClasses("galacticraftcore.MixinGuiExtendedInventory").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.fixPotionRenderOffset).addTargetedMod(TargetedMod.GALACTICRAFT_CORE)),
    FIX_TRAVELLERSGEAR_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder("Travelers Gear with Potions")
            .addMixinClasses("travellersgear.MixinClientProxy").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.fixPotionRenderOffset).addTargetedMod(TargetedMod.TRAVELLERSGEAR)),
    FIX_TINKER_POTION_EFFECT_OFFSET(
            new MixinBuilder("Prevents the inventory from shifting when the player has active potion effects")
                    .setSide(Side.CLIENT).setPhase(Phase.LATE).addTargetedMod(TargetedMod.TINKERSCONSTRUCT)
                    .setApplyIf(() -> TweaksConfig.fixPotionRenderOffset)
                    .addMixinClasses("tconstruct.MixinTabRegistry")),

    // Extra Tinkers
    Fix_EXTRATIC_TECONFLICT(new MixinBuilder(
            "Disable ExtraTic's Integration with Metallurgy 3 Precious Materials Module: [Brass, Silver, Electrum & Platinum]")
                    .addMixinClasses("extratic.MixinPartsHandler", "extratic.MixinRecipeHandler").setPhase(Phase.LATE)
                    .setSide(Side.BOTH).setApplyIf(() -> FixesConfig.fixExtraTiCTEConflict)
                    .addTargetedMod(TargetedMod.EXTRATIC)),
    // Extra Utilities
    FIX_EXTRA_UTILITIES_UNENCHANTING(new MixinBuilder("Fix Exu Unenchanting")
            .addMixinClasses("extrautilities.MixinRecipeUnEnchanting").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesUnEnchanting).addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    DISABLE_AID_SPAWN_XU_SPIKES(
            new MixinBuilder("Fixes the vanilla zombie aid spawn triggering when killed by Extra Utilities Spikes")
                    .addMixinClasses("extrautilities.MixinBlockSpike").setPhase(Phase.LATE).setSide(Side.BOTH)
                    .setApplyIf(() -> TweaksConfig.disableAidSpawnByXUSpikes)
                    .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_EXTRA_UTILITIES_TRANSPARENT_ITEM_RENDER(
            new MixinBuilder("Fix extra utilities item rendering for transparent items")
                    .addMixinClasses("extrautilities.MixinTransparentItemRender").setPhase(Phase.LATE)
                    .setSide(Side.CLIENT).setApplyIf(() -> FixesConfig.fixExtraUtilitiesItemRendering)
                    .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_DRUM_EATING_CELLS(new MixinBuilder("Fix extra utilities drums eating ic2 cells and forestry capsules")
            .addMixinClasses("extrautilities.MixinBlockDrum").setSide(Side.BOTH).setPhase(Phase.LATE)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesDrumEatingCells)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_GREENSCREEN_MICROBLOCKS(new MixinBuilder("Fix extra utilities Lapis Caelestis microblocks")
            .addMixinClasses("extrautilities.MixinFullBrightMicroMaterial").setSide(Side.CLIENT).setPhase(Phase.LATE)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesGreenscreenMicroblocks)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_LAST_MILLENIUM_RAIN(new MixinBuilder("Remove rain from the Last Millenium (Extra Utilities)")
            .addMixinClasses("extrautilities.MixinChunkProviderEndOfTime").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesLastMilleniumRain)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_LAST_MILLENIUM_CREATURES(new MixinBuilder("Remove creatures from the Last Millenium (Extra Utilities)")
            .addMixinClasses("extrautilities.MixinWorldProviderEndOfTime").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesLastMilleniumCreatures)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_FLUID_RETRIEVAL_NODE(new MixinBuilder("Prevent fluid retrieval node from voiding (Extra Utilities)")
            .addMixinClasses("extrautilities.MixinFluidBufferRetrieval").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesFluidRetrievalNode)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_FILING_CABINET_DUPE(new MixinBuilder("Caps hotkey'd stacks to their maximum stack size in filing cabinets")
            .addMixinClasses("extrautilities.MixinContainerFilingCabinet").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesFilingCabinetDupe)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_FILTER_DUPE(new MixinBuilder("Prevent hotkeying other items onto item filters while they are open")
            .addMixinClasses("extrautilities.MixinContainerFilter").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesFilterDupe).addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    CONFIGURABLE_ENDERQUARRY_ENERGY(new MixinBuilder("Ender Quarry energy storage override")
            .addMixinClasses("extrautilities.MixinTileEntityEnderQuarry").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> TweaksConfig.extraUtilitiesEnderQuarryOverride > 0)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_ENDERQUARRY_FREEZE(new MixinBuilder("Fix Ender Quarry freezes randomly")
            .addMixinClasses("extrautilities.MixinTileEntityEnderQuarry_FixFreeze").setPhase(Phase.LATE)
            .setSide(Side.BOTH).setApplyIf(() -> FixesConfig.fixExtraUtilitiesEnderQuarryFreeze)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_HEALING_AXE_HEAL(new MixinBuilder("Fix the healing axe not healing entities when attacking them")
            .addMixinClasses("extrautilities.MixinItemHealingAxe").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesHealingAxeHeal).addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_CHEST_COMPARATOR_UPDATE(new MixinBuilder(
            "Fix Extra Utilities chests not updating comparator redstone signals when their inventories change")
                    .addMixinClasses("extrautilities.MixinExtraUtilsChest").setPhase(Phase.LATE).setSide(Side.BOTH)
                    .setApplyIf(() -> FixesConfig.fixExtraUtilitiesChestComparatorUpdate)
                    .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_ETHERIC_SWORD_UNBREAKABLE(new MixinBuilder("Make Etheric Sword truly unbreakable")
            .addMixinClasses("extrautilities.MixinItemEthericSword").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesEthericSwordUnbreakable)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_ENDER_COLLECTOR_CRASH(new MixinBuilder(
            "Prevent Extra Utilities Ender Collector from inserting into auto-dropping Blocks that create a crash-loop")
                    .addMixinClasses("extrautilities.MixinTileEnderCollector").setPhase(Phase.LATE).setSide(Side.BOTH)
                    .setApplyIf(() -> FixesConfig.fixExtraUtilitiesEnderCollectorCrash)
                    .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),

    // Gliby's Voice Chat
    FIX_GLIBYS_VC_THREAD_SHUTDOWN_CLIENT(
            new MixinBuilder("Fix Gliby's voice chat not shutting down its client thread cleanly")
                    .addMixinClasses("glibysvoicechat.MixinClientNetwork").setPhase(Phase.LATE).setSide(Side.CLIENT)
                    .setApplyIf(() -> FixesConfig.fixGlibysVoiceChatThreadStop)
                    .addTargetedMod(TargetedMod.GLIBYS_VOICE_CHAT)),
    FIX_GLIBYS_VC_THREAD_SHUTDOWN_SERVER(
            new MixinBuilder("Fix Gliby's voice chat not shutting down its server thread cleanly")
                    .addMixinClasses("glibysvoicechat.MixinVoiceChatServer").setPhase(Phase.LATE).setSide(Side.BOTH)
                    .setApplyIf(() -> FixesConfig.fixGlibysVoiceChatThreadStop)
                    .addTargetedMod(TargetedMod.GLIBYS_VOICE_CHAT)),

    // PortalGun
    PORTALGUN_FIX_URLS(new MixinBuilder("Fix URLs used to download the sound pack")
            .addMixinClasses("portalgun.MixinThreadDownloadResources").addTargetedMod(TargetedMod.PORTAL_GUN)
            .setApplyIf(() -> FixesConfig.fixPortalGunURLs).setPhase(Phase.LATE).setSide(Side.CLIENT)),

    // VoxelMap
    REPLACE_VOXELMAP_REFLECTION(new MixinBuilder("Replace VoxelMap Reflection")
            .addMixinClasses(
                    "voxelmap.reflection.MixinAddonResourcePack",
                    "voxelmap.reflection.MixinColorManager",
                    "voxelmap.reflection.MixinMap",
                    "voxelmap.reflection.MixinRadar",
                    "voxelmap.reflection.MixinVoxelMap",
                    "voxelmap.reflection.MixinWaypointManager$1")
            .addTargetedMod(TargetedMod.VOXELMAP).setApplyIf(() -> SpeedupsConfig.replaceVoxelMapReflection)
            .setPhase(Phase.LATE).setSide(Side.CLIENT)),
    VOXELMAP_Y_FIX(new MixinBuilder("Fix off by one Y coord").addMixinClasses("voxelmap.MixinMap")
            .addTargetedMod(TargetedMod.VOXELMAP).setApplyIf(() -> FixesConfig.fixVoxelMapYCoord).setPhase(Phase.LATE)
            .setSide(Side.CLIENT)),
    VOXELMAP_NPE_FIX(new MixinBuilder("Fix VoxelMap NPEs with Chunks")
            .addMixinClasses("voxelmap.chunk.MixinCachedRegion", "voxelmap.chunk.MixinComparisonCachedRegion")
            .addTargetedMod(TargetedMod.VOXELMAP).setApplyIf(() -> FixesConfig.fixVoxelMapChunkNPE).setPhase(Phase.LATE)
            .setSide(Side.CLIENT)),
    VOXELMAP_FILE_EXT(new MixinBuilder("Change VoxelMap cache file extension")
            .addMixinClasses(
                    "voxelmap.cache.MixinCachedRegion",
                    "voxelmap.cache.MixinCachedRegion$1",
                    "voxelmap.cache.MixinComparisonCachedRegion")
            .addTargetedMod(TargetedMod.VOXELMAP).setApplyIf(() -> TweaksConfig.changeCacheFileExtension)
            .setPhase(Phase.LATE).setSide(Side.CLIENT)),

    // Witchery
    DISABLE_POTION_ARRAY_EXTENDER(new MixinBuilder("Disable Witchery potion array extender")
            .addMixinClasses("witchery.MixinPotionArrayExtender").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.disableWitcheryPotionExtender).addTargetedMod(TargetedMod.WITCHERY)),

    FIX_WITCHERY_REFLECTION_SKIN(new MixinBuilder("Fixes Witchery player skins reflections")
            .addMixinClasses("witchery.MixinExtendedPlayer", "witchery.MixinEntityReflection").setSide(Side.CLIENT)
            .setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixWitcheryReflections)
            .addTargetedMod(TargetedMod.WITCHERY)),

    FIX_WITCHERY_THUNDERING_DETECTION(new MixinBuilder(
            "Fixes Witchery Thunder Detection for rituals and Witch Hunters breaking with mods modifying thunder frequency")
                    .addMixinClasses(
                            "witchery.MixinBlockCircle",
                            "witchery.MixinEntityWitchHunter",
                            "witchery.MixinRiteClimateChange")
                    .setSide(Side.BOTH).setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixWitcheryThunderDetection)
                    .addTargetedMod(TargetedMod.WITCHERY)),

    FIX_WITCHERY_RENDERING(new MixinBuilder("Fixes Witchery Rendering errors")
            .addMixinClasses("witchery.MixinBlockCircleGlyph").setSide(Side.CLIENT).setPhase(Phase.LATE)
            .setApplyIf(() -> FixesConfig.fixWitcheryRendering).addTargetedMod(TargetedMod.WITCHERY)),

    // Various Exploits/Fixes
    GC_TIME_COMMAND_FIX(new MixinBuilder("GC Time Fix").addMixinClasses("minecraft.MixinTimeCommandGalacticraftFix")
            .setPhase(Phase.EARLY).setSide(Side.BOTH).setApplyIf(() -> FixesConfig.fixTimeCommandWithGC)
            .addTargetedMod(TargetedMod.VANILLA).addTargetedMod(TargetedMod.GALACTICRAFT_CORE)),
    BIBLIOCRAFT_PACKET_FIX(
            new MixinBuilder("Packet Fix").addMixinClasses("bibliocraft.MixinBibliocraftPatchPacketExploits")
                    .setPhase(Phase.LATE).setSide((Side.BOTH)).setApplyIf(() -> FixesConfig.fixBibliocraftPackets)
                    .addTargetedMod(TargetedMod.BIBLIOCRAFT)),
    BIBLIOCRAFT_PATH_SANITIZATION_FIX(new MixinBuilder("Path sanitization fix")
            .addMixinClasses("bibliocraft.MixinPathSanitization").setPhase(Phase.LATE).setSide((Side.BOTH))
            .setApplyIf(() -> FixesConfig.fixBibliocraftPackets).addTargetedMod(TargetedMod.BIBLIOCRAFT)),
    ZTONES_PACKET_FIX(new MixinBuilder("Packet Fix").addMixinClasses("ztones.MixinZtonesPatchPacketExploits")
            .setPhase(Phase.LATE).setSide((Side.BOTH)).setApplyIf(() -> FixesConfig.fixZTonesPackets)
            .addTargetedMod(TargetedMod.ZTONES)),
    ASP_RECIPE_FIX(new MixinBuilder("MT Core recipe fix").addMixinClasses("advancedsolarpanels.MixinAdvancedSolarPanel")
            .addTargetedMod(TargetedMod.ADVANCED_SOLAR_PANELS).addExcludedMod(TargetedMod.DREAMCRAFT)
            .setApplyIf(() -> FixesConfig.fixMTCoreRecipe).setPhase(Phase.LATE).setSide(Side.BOTH)),
    TD_NASE_PREVENTION(new MixinBuilder("Prevent NegativeArraySizeException on itemduct transfers")
            .addMixinClasses("thermaldynamics.MixinSimulatedInv").setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.preventThermalDynamicsNASE).addTargetedMod(TargetedMod.THERMALDYNAMICS)
            .setPhase(Phase.LATE)),
    TD_FLUID_GRID_CCE(new MixinBuilder("Prevent ClassCastException on forming invalid Thermal Dynamic fluid grid")
            .addMixinClasses("thermaldynamics.MixinTileFluidDuctSuper").setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.preventFluidGridCrash).addTargetedMod(TargetedMod.THERMALDYNAMICS)
            .setPhase(Phase.LATE)),

    // Unbind Keybinds by default
    UNBIND_KEYS_TRAVELLERSGEAR(new MixinBuilder("Unbind Traveller's Gear keybinds")
            .addMixinClasses("travellersgear.MixinKeyHandler").setPhase(Phase.LATE).setSide((Side.CLIENT))
            .setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addTargetedMod(TargetedMod.TRAVELLERSGEAR)),
    UNBIND_KEYS_INDUSTRIALCRAFT(new MixinBuilder("Unbind Industrial craft keybinds")
            .addMixinClasses("ic2.MixinKeyboardClient").setPhase(Phase.LATE).setSide((Side.CLIENT))
            .setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addTargetedMod(TargetedMod.IC2)),
    UNBIND_KEYS_THAUMCRAFT(new MixinBuilder("Unbind Thaumcraft keybinds")
            .addMixinClasses("thaumcraft.MixinKeyHandlerThaumcraft").setPhase(Phase.LATE).setSide((Side.CLIENT))
            .setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addTargetedMod(TargetedMod.THAUMCRAFT)),
    UNBIND_KEYS_COFH(new MixinBuilder("Unbind COFH Core keybinds").addMixinClasses("cofhcore.MixinProxyClient")
            .setSide((Side.CLIENT)).setPhase(Phase.EARLY).setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault)
            .addTargetedMod(TargetedMod.COFH_CORE)),
    CHANGE_KEYBIND_CATEGORY_AUTOMAGY(new MixinBuilder("Change keybind category of Automagy")
            .addMixinClasses("automagy.MixinAutomagyKeyHandler").setPhase(Phase.LATE).setSide((Side.CLIENT))
            .setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addTargetedMod(TargetedMod.AUTOMAGY)),

    IC2_CELL(new MixinBuilder("No IC2 Cell Consumption in tanks").addMixinClasses("ic2.MixinIC2ItemCell")
            .setPhase(Phase.LATE).setSide(Side.BOTH).setApplyIf(() -> TweaksConfig.ic2CellWithContainer)
            .addTargetedMod(TargetedMod.IC2)),

    // Chunk generation/population
    DISABLE_CHUNK_TERRAIN_GENERATION(new MixinBuilder("Disable chunk terrain generation").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinChunkProviderServer_DisableTerrain").addTargetedMod(TargetedMod.VANILLA)
            .setSide(Side.BOTH).setApplyIf(() -> TweaksConfig.disableChunkTerrainGeneration)),
    DISABLE_WORLD_TYPE_CHUNK_POPULATION(
            new MixinBuilder("Disable chunk population tied to chunk generation (ores/structure)").setPhase(Phase.EARLY)
                    .addMixinClasses("minecraft.MixinChunkProviderServer_DisablePopulation")
                    .addTargetedMod(TargetedMod.VANILLA).setSide(Side.BOTH)
                    .setApplyIf(() -> TweaksConfig.disableWorldTypeChunkPopulation)),
    DISABLE_MODDED_CHUNK_POPULATION(new MixinBuilder("Disable all other mod chunk population (e.g. Natura clouds")
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinChunkProviderServer_DisableModGeneration")
            .addTargetedMod(TargetedMod.VANILLA).setSide(Side.BOTH)
            .setApplyIf(() -> TweaksConfig.disableModdedChunkPopulation)),

    // Candycraft
    FIX_SUGARBLOCK_NPE(new MixinBuilder("Fix NPE when interacting with sugar block")
            .addMixinClasses("candycraft.MixinBlockSugar").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixCandycraftBlockSugarNPE).addTargetedMod(TargetedMod.CANDYCRAFT)),

    // Morpheus
    FIX_NOT_WAKING_PLAYERS(new MixinBuilder("Fix players not being woken properly when not everyone is sleeping")
            .addMixinClasses("morpheus.MixinMorpheusWakePlayers").setPhase(Phase.LATE).setSide(Side.SERVER)
            .setApplyIf(() -> FixesConfig.fixMorpheusWaking).addTargetedMod(TargetedMod.MORPHEUS));

    private final List<String> mixinClasses;
    private final List<ITargetedMod> targetedMods;
    private final List<ITargetedMod> excludedMods;
    private final Supplier<Boolean> applyIf;
    private final Phase phase;
    private final Side side;

    Mixins(MixinBuilder builder) {
        this.mixinClasses = builder.mixinClasses;
        this.targetedMods = builder.targetedMods;
        this.excludedMods = builder.excludedMods;
        this.applyIf = builder.applyIf;
        this.phase = builder.phase;
        this.side = builder.side;
        if (this.mixinClasses.isEmpty()) {
            throw new RuntimeException("No mixin class specified for Mixin : " + this.name());
        }
        if (this.targetedMods.isEmpty()) {
            throw new RuntimeException("No targeted mods specified for Mixin : " + this.name());
        }
        if (this.applyIf == null) {
            throw new RuntimeException("No ApplyIf function specified for Mixin : " + this.name());
        }
        if (this.phase == null) {
            throw new RuntimeException("No Phase specified for Mixin : " + this.name());
        }
        if (this.side == null) {
            throw new RuntimeException("No Side function specified for Mixin : " + this.name());
        }
    }

    @Override
    public List<String> getMixinClasses() {
        return mixinClasses;
    }

    @Override
    public Supplier<Boolean> getApplyIf() {
        return applyIf;
    }

    @Override
    public Phase getPhase() {
        return phase;
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public List<ITargetedMod> getTargetedMods() {
        return targetedMods;
    }

    @Override
    public List<ITargetedMod> getExcludedMods() {
        return excludedMods;
    }

    /**
     * Checks if a resource is present.
     *
     * @param name The name of a resource is a '{@code /}'-separated path name that identifies the resource. For example
     *             "net/minecraft/client/Minecraft.class"
     */
    private static boolean doesResourceExist(String name) {
        try {
            return ClassLoader.getSystemClassLoader().getResource(name) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
