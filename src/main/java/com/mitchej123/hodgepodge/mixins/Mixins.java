package com.mitchej123.hodgepodge.mixins;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;
import com.mitchej123.hodgepodge.config.ASMConfig;
import com.mitchej123.hodgepodge.config.DebugConfig;
import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;

public enum Mixins implements IMixins {

    // spotless:off
    // Vanilla Fixes
    FIX_MINECRAFT_SERVER_LEAK(new MixinBuilder()
            .addCommonMixins("minecraft.server.MixinMinecraftServer_ClearServerRef")
            .setApplyIf(() -> FixesConfig.fixMinecraftServerLeak)
            .setPhase(Phase.EARLY)),
    ONLY_LOAD_LANGUAGES_ONCE_PER_FILE(new MixinBuilder()
            .addCommonMixins("minecraft.MixinLanguageRegistry")
            .setApplyIf(() -> FixesConfig.onlyLoadLanguagesOnce)
            .setPhase(Phase.EARLY)),
    CHANGE_CATEGORY_SPRINT_KEY(new MixinBuilder()
            .addClientMixins("minecraft.MixinGameSettings_SprintKey")
            .setApplyIf(() -> TweaksConfig.changeSprintCategory)
            .setPhase(Phase.EARLY)),
    FIX_TOO_MANY_ALLOCATIONS_CHUNK_POSITION_INT_PAIR(new MixinBuilder("Stops MC from allocating too many ChunkPositionIntPair objects")
            .addCommonMixins(
                    "minecraft.MixinChunkCoordIntPair_FixAllocations",
                    "minecraft.MixinWorld_FixAllocations",
                    "minecraft.MixinAnvilChunkLoader_FixAllocations")
            .addClientMixins("minecraft.MixinWorldClient_FixAllocations")
            .setApplyIf(() -> FixesConfig.fixTooManyAllocationsChunkPositionIntPair)
            .setPhase(Phase.EARLY)),
    FIX_TOO_MANY_ALLOCATIONS_CHUNK_POSITION_INT_PAIR_OPTIFINE_INCOMPAT(new MixinBuilder("Stops MC from allocating too many ChunkPositionIntPair objects")
            .addExcludedMod(TargetedMod.OPTIFINE)
            .addCommonMixins("minecraft.MixinWorldServer_FixAllocations")
            .setApplyIf(() -> FixesConfig.fixTooManyAllocationsChunkPositionIntPair)
            .setPhase(Phase.EARLY)),
    ADD_SIMULATION_DISTANCE_OPTION(new MixinBuilder("Add option to separate simulation distance from render distance")
            .addExcludedMod(TargetedMod.OPTIFINE)
            .addExcludedMod(TargetedMod.ULTRAMINE)
            .addCommonMixins(
                    "minecraft.MixinWorld_SimulationDistance",
                    "minecraft.MixinWorldServer_SimulationDistance",
                    "minecraft.MixinChunk_SimulationDistance")
            .setApplyIf(() -> FixesConfig.addSimulationDistance_WIP)
            .setPhase(Phase.EARLY)),
    FIX_RCON_THREADING(new MixinBuilder("Fix RCON Threading by forcing it to run on the main thread")
            .addServerMixins("minecraft.MixinMinecraftServer_RconThreadingFix")
            .setApplyIf(() -> FixesConfig.fixRconThreading)
            .setPhase(Phase.EARLY)),
    ADD_SIMULATION_DISTANCE_OPTION_THERMOS_FIX(new MixinBuilder("Add option to separate simulation distance from render distance (Thermos fix)")
            .addRequiredMod(TargetedMod.BUKKIT)
            .addExcludedMod(TargetedMod.OPTIFINE)
            .addExcludedMod(TargetedMod.ULTRAMINE)
            .addCommonMixins("minecraft.MixinWorldServer_SimulationDistanceThermosFix")
            .setApplyIf(() -> FixesConfig.addSimulationDistance_WIP)
            .setPhase(Phase.EARLY)),
    FIX_RESOURCEPACK_FOLDER_OPENING(new MixinBuilder("Fix resource pack folder sometimes not opening on windows")
            .addClientMixins("minecraft.MixinGuiScreenResourcePacks")
            .setApplyIf(() -> FixesConfig.fixResourcePackOpening)
            .setPhase(Phase.EARLY)),
    FIX_ENCHANTMENT_LEVEL_NUMERALS(new MixinBuilder("Fix enchantment levels not displaying properly above a certain value")
            .addCommonMixins("minecraft.MixinEnchantment_FixRomanNumerals")
            .setApplyIf(() -> FixesConfig.fixEnchantmentNumerals || TweaksConfig.arabicNumbersForEnchantsPotions)
            .setPhase(Phase.EARLY)),
    FIX_CONTAINER_PUT_STACKS_IN_SLOTS(new MixinBuilder("Prevents crash if server sends container with wrong itemStack size")
            .addClientMixins("minecraft.MixinContainer")
            .setApplyIf(() -> FixesConfig.fixContainerPutStacksInSlots)
            .setPhase(Phase.EARLY)),
    FIX_CONTAINER_SHIFT_CLICK_RECURSION(new MixinBuilder("Backports 1.12 logic for shift clicking slots to prevent recursion")
            .addCommonMixins("minecraft.MixinContainer_FixShiftRecursion")
            .setApplyIf(() -> FixesConfig.fixContainerShiftClickRecursion)
            .setPhase(Phase.EARLY)),
    FIX_NETHANDLERPLAYCLIENT_HANDLE_SET_SLOT(new MixinBuilder("Prevents crash if server sends itemStack with index larger than client's container")
            .addClientMixins("minecraft.MixinNetHandlerPlayClient_FixHandleSetSlot")
            .setApplyIf(() -> FixesConfig.fixNetHandlerPlayClientHandleSetSlot)
            .setPhase(Phase.EARLY)),
    FIX_NETHANDLERLOGINSERVER_OFFLINEMODE(new MixinBuilder("Allows the server to assign the logged in UUID to the same username when online_mode is false")
            .addServerMixins("minecraft.MixinNetHandlerLoginServer_OfflineMode")
            .setApplyIf(() -> FixesConfig.fixNetHandlerLoginServerOfflineMode)
            .setPhase(Phase.EARLY)),
    FIX_INVENTORY_POTION_EFFECT_NUMERALS(new MixinBuilder("Fix potion effects level not displaying properly above a certain value")
            .addClientMixins(
                    "minecraft.MixinInventoryEffectRenderer_FixPotionEffectNumerals",
                    "minecraft.MixinItemPotion_FixRomanNumerals")
            .setApplyIf(() -> FixesConfig.fixPotionEffectNumerals || TweaksConfig.arabicNumbersForEnchantsPotions)
            .setPhase(Phase.EARLY)),
    FIX_HASTE_ARM_SWING_ANIMATION(new MixinBuilder("Fix arm not swinging when having too much haste")
            .addCommonMixins("minecraft.MixinEntityLivingBase_FixHasteArmSwing")
            .setApplyIf(() -> FixesConfig.fixHasteArmSwing)
            .setPhase(Phase.EARLY)),
    DISABLE_REALMS_BUTTON(new MixinBuilder("Disable Realms button in main menu")
            .addClientMixins("minecraft.MixinGuiMainMenu_DisableRealmsButton")
            .setApplyIf(() -> TweaksConfig.disableRealmsButton)
            .setPhase(Phase.EARLY)),
    ADD_TIME_GET(new MixinBuilder("Add /time get command")
            .addCommonMixins("minecraft.MixinCommandTime")
            .setApplyIf(() -> TweaksConfig.addTimeGet)
            .setPhase(Phase.EARLY)),
    OPTIMIZE_WORLD_UPDATE_LIGHT(new MixinBuilder("Optimize world updateLightByType method")
            .addCommonMixins("minecraft.MixinWorld_FixLightUpdateLag")
            .addExcludedMod(TargetedMod.ARCHAICFIX)
            .addExcludedMod(TargetedMod.ANGELICA)
            .setApplyIf(() -> FixesConfig.optimizeWorldUpdateLight)
            .setPhase(Phase.EARLY)),
    FIX_FRIENDLY_CREATURE_SOUNDS(new MixinBuilder()
            .addClientMixins("minecraft.MixinSoundHandler")
            .setApplyIf(() -> FixesConfig.fixFriendlyCreatureSounds)
            .setPhase(Phase.EARLY)),
    LOGARITHMIC_VOLUME_CONTROL(new MixinBuilder()
            .addClientMixins(
                    "minecraft.MixinSoundManager",
                    "minecraft.MixinSoundManagerLibraryLoader")
            .setApplyIf(() -> FixesConfig.logarithmicVolumeControl)
            .setPhase(Phase.EARLY)),
    THROTTLE_ITEMPICKUPEVENT(new MixinBuilder("Throttle Item Pickup Event")
            .addCommonMixins("minecraft.MixinEntityPlayer_ThrottlePickup")
            .setApplyIf(() -> FixesConfig.throttleItemPickupEvent)
            .setPhase(Phase.EARLY)),
    ADD_THROWER_TO_DROPPED_ITEM(new MixinBuilder("Adds the thrower tag to all dropped EntityItems")
            .addCommonMixins("minecraft.MixinEntityPlayer_ItemThrower")
            .setApplyIf(() -> FixesConfig.addThrowerTagToDroppedItems)
            .setPhase(Phase.EARLY)),
    SYNC_ITEM_THROWER(new MixinBuilder("Synchonize from server to client the thrower and pickup delay of an item entity")
            .addCommonMixins("minecraft.packets.MixinS0EPacketSpawnObject_ItemThrower")
            .addClientMixins("minecraft.MixinNetHandlerPlayClient_ItemThrower")
            .setApplyIf(() -> FixesConfig.syncItemThrower)
            .setPhase(Phase.EARLY)),
    FIX_PERSPECTIVE_CAMERA(new MixinBuilder("Camera Perspective Fix")
            .addClientMixins("minecraft.MixinEntityRenderer")
            .addExcludedMod(TargetedMod.ARCHAICFIX)
            .addExcludedMod(TargetedMod.ANGELICA)
            .setApplyIf(() -> FixesConfig.fixPerspectiveCamera)
            .setPhase(Phase.EARLY)),
    FIX_DEBUG_BOUNDING_BOX(new MixinBuilder("Fix Bounding Box")
            .addClientMixins("minecraft.MixinRenderManager")
            .setApplyIf(() -> FixesConfig.fixDebugBoundingBox)
            .setPhase(Phase.EARLY)),
    FENCE_CONNECTIONS_FIX(new MixinBuilder("Fix Fence Connections")
            .addCommonMixins("minecraft.MixinBlockFence")
            .setApplyIf(() -> FixesConfig.fixFenceConnections)
            .setPhase(Phase.EARLY)),
    FIX_BOTTOM_FACE_UV(new MixinBuilder()
            .addClientMixins("minecraft.MixinRenderBlocks_FaceYNegUV")
            .setApplyIf(() -> FixesConfig.fixBottomFaceUV)
            .setPhase(Phase.EARLY)),
    FIX_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder()
            .addClientMixins("minecraft.MixinInventoryEffectRenderer_PotionOffset")
            .setApplyIf(() -> TweaksConfig.fixPotionRenderOffset)
            .setPhase(Phase.EARLY)),
    FIX_POTION_EFFECT_RENDERING(new MixinBuilder()
            .addClientMixins("minecraft.MixinInventoryEffectRenderer_PotionEffectRendering")
            .setApplyIf(() -> TweaksConfig.fixPotionEffectRender)
            .setPhase(Phase.EARLY)),
    FIX_IMMOBILE_FIREBALLS(new MixinBuilder()
            .addCommonMixins("minecraft.MixinEntityFireball")
            .setApplyIf(() -> FixesConfig.fixImmobileFireballs)
            .setPhase(Phase.EARLY)),
    FIX_REED_PLACING(new MixinBuilder("Fix placement of Sugar Canes")
            .addCommonMixins("minecraft.MixinItemReed")
            .setApplyIf(() -> FixesConfig.fixSugarCanePlacement)
            .setPhase(Phase.EARLY)),
    LONGER_CHAT(new MixinBuilder()
            .addClientMixins("minecraft.MixinGuiNewChat_LongerChat")
            .setApplyIf(() -> TweaksConfig.longerChat)
            .setPhase(Phase.EARLY)),
    TRANSPARENT_CHAT(new MixinBuilder()
            .addClientMixins("minecraft.MixinGuiNewChat_TransparentChat")
            .setApplyIf(() -> TweaksConfig.transparentChat)
            .setPhase(Phase.EARLY)),
    FIX_ENTITY_ATTRIBUTES_RANGE(new MixinBuilder()
            .addClientMixins("minecraft.MixinNetHandlerPlayClient_FixEntityAttributesRange")
            .setApplyIf(() -> FixesConfig.fixEntityAttributesRange)
            .setPhase(Phase.EARLY)),
    ENDERMAN_BLOCK_GRAB_DISABLE(new MixinBuilder("Disable Endermen Grabbing Blocks")
            .addCommonMixins("minecraft.MixinEntityEndermanGrab")
            .setApplyIf(() -> TweaksConfig.endermanBlockGrabDisable)
            .setPhase(Phase.EARLY)),
    ENDERMAN_BLOCK_PLACE_DISABLE(new MixinBuilder("Disable Endermen Placing Held Blocks")
            .addCommonMixins("minecraft.MixinEntityEndermanPlace")
            .setApplyIf(() -> TweaksConfig.endermanBlockPlaceDisable)
            .setPhase(Phase.EARLY)),
    ENDERMAN_BLOCK_PLACE_BLACKLIST(new MixinBuilder("Disable Endermen Placing Held Blocks on Configured Blocks")
            .addCommonMixins("minecraft.MixinEntityEndermanPlaceBlacklist")
            .setApplyIf(() -> TweaksConfig.endermanBlockPlaceBlacklist)
            .setPhase(Phase.EARLY)),
    WITCH_POTION_METADATA(new MixinBuilder("Fix Metadata of Witch Potions")
            .addCommonMixins("minecraft.MixinEntityWitch")
            .setApplyIf(() -> TweaksConfig.witchPotionMetadata)
            .setPhase(Phase.EARLY)),
    // config handled in mixin due to server->client config sync
    LONGER_MESSAGES(new MixinBuilder()
            .addClientMixins("minecraft.MixinGuiChat_LongerMessages")
            .addCommonMixins("minecraft.packets.MixinC01PacketChatMessage_LongerMessages")
            .setPhase(Phase.EARLY)),
    SPEEDUP_REMOVE_FORMATTING_CODES(new MixinBuilder("Speed up the vanilla method to remove formatting codes")
            .addClientMixins("minecraft.MixinEnumChatFormatting_FastFormat")
            .setApplyIf(() -> SpeedupsConfig.speedupRemoveFormatting)
            .setPhase(Phase.EARLY)),
    SPEEDUP_GRASS_BLOCK_RANDOM_TICKING(new MixinBuilder("Speed up grass block random ticking")
            .addCommonMixins("minecraft.MixinBlockGrass")
            .setApplyIf(() -> SpeedupsConfig.speedupGrassBlockRandomTicking)
            .setPhase(Phase.EARLY)),
    SPEEDUP_CHUNK_PROVIDER_CLIENT(new MixinBuilder("Speed up ChunkProviderClient")
            .addClientMixins("minecraft.MixinChunkProviderClient_RemoveChunkListing")
            .addExcludedMod(TargetedMod.FASTCRAFT)
            .setApplyIf(() -> SpeedupsConfig.speedupChunkProviderClient && ASMConfig.speedupLongIntHashMap)
            .setPhase(Phase.EARLY)),
    BETTER_HASHCODES(new MixinBuilder("Optimize various Hashcode")
            .addCommonMixins(
                    "minecraft.MixinChunkCoordinates_BetterHash",
                    "minecraft.MixinChunkCoordIntPair_BetterHash")
            .setApplyIf(() -> SpeedupsConfig.speedupChunkCoordinatesHashCode)
            .setPhase(Phase.EARLY)),
    TCP_NODELAY(new MixinBuilder("Set TCP NODELAY")
            .addCommonMixins("minecraft.MixinTcpNoDelay")
            .setApplyIf(() -> SpeedupsConfig.tcpNoDelay)
            .setPhase(Phase.EARLY)),
    WORLD_UNPROTECTED_GET_BLOCK(new MixinBuilder("Fix world unprotected getBlock")
            .addCommonMixins("minecraft.MixinWorldGetBlock")
            .setApplyIf(() -> FixesConfig.fixVanillaUnprotectedGetBlock)
            .setPhase(Phase.EARLY)),
    WORLD_UNPROTECTED_LIGHT_VALUE(new MixinBuilder("Fix world unprotected light value")
            .addCommonMixins("minecraft.MixinWorldLightValue")
            .setApplyIf(() -> FixesConfig.fixGetBlockLightValue)
            .setPhase(Phase.EARLY)),
    VILLAGE_UNCHECKED_GET_BLOCK(new MixinBuilder("Fix Village unchecked getBlock")
            .addCommonMixins(
                    "minecraft.MixinVillage",
                    "minecraft.MixinVillageCollection")
            .setApplyIf(() -> FixesConfig.fixVillageUncheckedGetBlock)
            .setPhase(Phase.EARLY)),
    FORGE_HOOKS_URL_FIX(new MixinBuilder("Fix forge URL hooks")
            .addCommonMixins("minecraft.MixinForgeHooks")
            .setApplyIf(() -> FixesConfig.fixUrlDetection)
            .setPhase(Phase.EARLY)),
    FORGE_UPDATE_CHECK_FIX(new MixinBuilder("Fix the forge update checker")
            .addCommonMixins("forge.MixinForgeVersion_FixUpdateCheck")
            .setApplyIf(() -> FixesConfig.fixForgeUpdateChecker)
            .setPhase(Phase.EARLY)),
    FORGE_FIX_CLASS_TYPO(new MixinBuilder("Fix a class name typo in MinecraftForge's initialize method")
            .addCommonMixins("forge.MixinMinecraftForge")
            .setApplyIf(() -> FixesConfig.fixEffectRendererClassTypo)
            .setPhase(Phase.EARLY)),
    NORTHWEST_BIAS_FIX(new MixinBuilder("Fix Northwest Bias")
            .addCommonMixins("minecraft.MixinRandomPositionGenerator")
            .setApplyIf(() -> FixesConfig.fixNorthWestBias)
            .setPhase(Phase.EARLY)),
    SPEEDUP_VANILLA_FURNACE(new MixinBuilder()
            .addCommonMixins("minecraft.MixinFurnaceRecipes")
            .setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace)
            .addRequiredMod(TargetedMod.GTNHLIB)
            .setPhase(Phase.EARLY)),
    GAMEOVER_GUI_LOCKED_DISABLED(new MixinBuilder("Fix Gameover GUI")
            .addClientMixins("minecraft.MixinGuiGameOver")
            .setApplyIf(() -> FixesConfig.fixGuiGameOver)
            .setPhase(Phase.EARLY)),
    PREVENT_PICKUP_LOOT(new MixinBuilder("Prevent monsters from picking up loot")
            .addCommonMixins("minecraft.MixinEntityLivingPickup")
            .setApplyIf(() -> TweaksConfig.preventPickupLoot)
            .setPhase(Phase.EARLY)),
    DROP_PICKED_LOOT_ON_DESPAWN(new MixinBuilder("Drop picked up loot on despawn")
            .addCommonMixins("minecraft.MixinEntityLivingDrop")
            .setApplyIf(() -> TweaksConfig.dropPickedLootOnDespawn)
            .setPhase(Phase.EARLY)),
    FIX_HOPPER_HIT_BOX(new MixinBuilder("Fix Vanilla Hopper hit box")
            .addCommonMixins("minecraft.MixinBlockHopper")
            .setApplyIf(() -> FixesConfig.fixHopperHitBox)
            .setPhase(Phase.EARLY)),
    TILE_ENTITY_RENDERER_PROFILER(new MixinBuilder()
            .addClientMixins("minecraft.profiler.TileEntityRendererDispatcherMixin")
            .addClientMixins("minecraft.profiler.MinecraftMixin")
            .setApplyIf(() -> TweaksConfig.enableTileRendererProfiler)
            .setPhase(Phase.EARLY)),
    DIMENSION_CHANGE_FIX(new MixinBuilder("Dimension Change Heart Fix")
            .addCommonMixins(
                    "minecraft.MixinServerConfigurationManager",
                    "minecraft.MixinEntityPlayerMP")
            .setApplyIf(() -> FixesConfig.fixDimensionChangeAttributes)
            .setPhase(Phase.EARLY)),
    CONFIGURABLE_PORTAL_RATIO(new MixinBuilder("Make Nether portal travel ratio configurable")
            .addCommonMixins("minecraft.MixinWorldProviderHell")
            .setPhase(Phase.EARLY)),
    FIX_EATING_STACKED_STEW(new MixinBuilder("Stacked Mushroom Stew Eating Fix")
            .addCommonMixins("minecraft.MixinItemSoup")
            .setApplyIf(() -> FixesConfig.fixEatingStackedStew)
            .setPhase(Phase.EARLY)),
    INCREASE_PARTICLE_LIMIT(new MixinBuilder()
            .addClientMixins("minecraft.MixinEffectRenderer")
            .setApplyIf(() -> TweaksConfig.increaseParticleLimit)
            .setPhase(Phase.EARLY)),
    ENLARGE_POTION_ARRAY(new MixinBuilder("Make the Potion array larger")
            .addCommonMixins("minecraft.MixinPotion")
            .setApplyIf(() -> FixesConfig.enlargePotionArray)
            .setPhase(Phase.EARLY)),
    FIX_POTION_LIMIT(new MixinBuilder()
            .addCommonMixins("minecraft.MixinPotionEffect")
            .setApplyIf(() -> FixesConfig.fixPotionLimit)
            .setPhase(Phase.EARLY)),
    FIX_HOPPER_VOIDING_ITEMS(new MixinBuilder()
            .addCommonMixins("minecraft.MixinTileEntityHopper")
            .setApplyIf(() -> FixesConfig.fixHopperVoidingItems)
            .setPhase(Phase.EARLY)),
    FIX_HUGE_CHAT_KICK(new MixinBuilder()
            .addCommonMixins("minecraft.packets.MixinS02PacketChat_FixHugeChatKick")
            .setApplyIf(() -> FixesConfig.fixHugeChatKick)
            .setPhase(Phase.EARLY)),
    FIX_BOGUS_INTEGRATED_SERVER_NPE(new MixinBuilder("Fix bogus FMLProxyPacket NPEs on integrated server crashes")
            .addCommonMixins(
                    "fml.MixinFMLProxyPacket",
                    "fml.MixinNetworkDispatcher",
                    "minecraft.NetworkManagerAccessor")
            .setApplyIf(() -> FixesConfig.fixBogusIntegratedServerNPEs)
            .setPhase(Phase.EARLY)),
    FIX_LAG_ON_INVENTORY_SYNC(new MixinBuilder("Fix inventory sync lag")
            .addCommonMixins("minecraft.MixinInventoryCrafting")
            .setApplyIf(() -> FixesConfig.fixInventorySyncLag)
            .setPhase(Phase.EARLY)),
    FIX_LOGIN_DIMENSION_ID_OVERFLOW(new MixinBuilder("Fix dimension id overflowing when a player first logins on a server")
            .addCommonMixins("minecraft.packets.MixinS01PacketJoinGame_FixDimensionID")
            .setApplyIf(() -> FixesConfig.fixLoginDimensionIDOverflow)
            .setPhase(Phase.EARLY)),
    FIX_WORLD_SERVER_LEAKING_UNLOADED_ENTITIES(new MixinBuilder()
            .addCommonMixins("minecraft.MixinWorldServerUpdateEntities")
            .setApplyIf(() -> FixesConfig.fixWorldServerLeakingUnloadedEntities)
            .setPhase(Phase.EARLY)),
    FIX_SKIN_MANAGER_CLIENT_WORLD_LEAK(new MixinBuilder()
            .addClientMixins("minecraft.MixinSkinManager$2")
            .setApplyIf(() -> FixesConfig.fixSkinManagerLeakingClientWorld)
            .setPhase(Phase.EARLY)),
    FIX_REDSTONE_TORCH_WORLD_LEAK(new MixinBuilder("Fix world leak in redstone torch")
            .addCommonMixins("minecraft.MixinBlockRedstoneTorch")
            .setApplyIf(() -> FixesConfig.fixRedstoneTorchWorldLeak)
            .addExcludedMod(TargetedMod.BUGTORCH)
            .setPhase(Phase.EARLY)),
    FIX_ARROW_WRONG_LIGHTING(new MixinBuilder()
            .addClientMixins("minecraft.MixinRendererLivingEntity")
            .setApplyIf(() -> FixesConfig.fixGlStateBugs)
            .setPhase(Phase.EARLY)),
    FIX_RESIZABLE_FULLSCREEN(new MixinBuilder()
            .addClientMixins("minecraft.MixinMinecraft_ResizableFullscreen")
            .setApplyIf(() -> FixesConfig.fixResizableFullscreen)
            .setPhase(Phase.EARLY)),
    FIX_UNFOCUSED_FULLSCREEN(new MixinBuilder()
            .addClientMixins("minecraft.MixinMinecraft_UnfocusedFullscreen")
            .setApplyIf(() -> FixesConfig.fixUnfocusedFullscreen)
            .addExcludedMod(TargetedMod.ARCHAICFIX)
            .setPhase(Phase.EARLY)),
    FIX_RENDERERS_WORLD_LEAK(new MixinBuilder()
            .addClientMixins(
                    "minecraft.MixinMinecraft_ClearRenderersWorldLeak",
                    "minecraft.MixinRenderGlobal_FixWordLeak")
            .setApplyIf(() -> FixesConfig.fixRenderersWorldLeak)
            .setPhase(Phase.EARLY)),
    FIX_OPTIFINE_CHUNKLOADING_CRASH(new MixinBuilder()
            .setApplyIf(() -> FixesConfig.fixOptifineChunkLoadingCrash)
            .addRequiredMod(TargetedMod.OPTIFINE)
            .addClientMixins(
                    "minecraft.MixinGameSettings_FixOFChunkLoading")
            .setPhase(Phase.EARLY)),
    ADD_TOGGLE_DEBUG_MESSAGE(new MixinBuilder("Toggle Debug Message")
            .addClientMixins("minecraft.MixinMinecraft_ToggleDebugMessage")
            .setApplyIf(() -> TweaksConfig.addToggleDebugMessage)
            .setPhase(Phase.EARLY)),
    OPTIMIZE_TEXTURE_LOADING(new MixinBuilder()
            .addClientMixins("minecraft.textures.client.MixinTextureUtil")
            .addExcludedMod(TargetedMod.ANGELICA)
            .setApplyIf(() -> SpeedupsConfig.optimizeTextureLoading)
            .setPhase(Phase.EARLY)),
    HIDE_POTION_PARTICLES(new MixinBuilder()
            .addClientMixins("minecraft.MixinEntityLivingBase_HidePotionParticles")
            .setApplyIf(() -> TweaksConfig.hidePotionParticlesFromSelf)
            .setPhase(Phase.EARLY)),
    DIMENSION_MANAGER_DEBUG(new MixinBuilder()
            .addCommonMixins("minecraft.MixinDimensionManager")
            .setApplyIf(() -> DebugConfig.dimensionManagerDebug)
            .setPhase(Phase.EARLY)),
    OPTIMIZE_TILEENTITY_REMOVAL(new MixinBuilder()
            .addCommonMixins("minecraft.MixinWorldUpdateEntities")
            .setApplyIf(() -> SpeedupsConfig.optimizeTileentityRemoval)
            .addExcludedMod(TargetedMod.BUKKIT)
            .setPhase(Phase.EARLY)),
    FIX_POTION_ITERATING(new MixinBuilder()
            .addCommonMixins("minecraft.MixinEntityLivingBase_FixPotionException")
            .setApplyIf(() -> FixesConfig.fixPotionIterating)
            .setPhase(Phase.EARLY)),
    ENHANCE_NIGHT_VISION(new MixinBuilder("Remove the blueish sky tint from night vision")
            .addClientMixins("minecraft.MixinEntityRenderer_EnhanceNightVision")
            .setApplyIf(() -> TweaksConfig.enhanceNightVision)
            .setPhase(Phase.EARLY)),
    NIGHT_VISION_FADE(new MixinBuilder()
            .addClientMixins("minecraft.MixinEntityRenderer_NightVisionFade")
            .setApplyIf(() -> TweaksConfig.fadeNightVision)
            .setPhase(Phase.EARLY)),
    OPTIMIZE_ASMDATATABLE_INDEX(new MixinBuilder("Optimize ASM DataTable Index")
            .addCommonMixins("fml.MixinASMDataTable")
            .setApplyIf(() -> SpeedupsConfig.optimizeASMDataTable)
            .setPhase(Phase.EARLY)),
    SQUASH_BED_ERROR_MESSAGE(new MixinBuilder()
            .addClientMixins("minecraft.MixinNetHandlerPlayClient_SquashBedMessages")
            .setApplyIf(() -> FixesConfig.squashBedErrorMessage)
            .setPhase(Phase.EARLY)),
    CHUNK_SAVE_CME_DEBUG(new MixinBuilder("Add debugging code to Chunk Save CME")
            .addCommonMixins("minecraft.nbt.MixinNBTTagCompound_CheckCME")
            .setApplyIf(() -> DebugConfig.chunkSaveCMEDebug)
            .addExcludedMod(TargetedMod.DRAGONAPI)
            .setPhase(Phase.EARLY)),
    SPEEDUP_NBT_COPY(new MixinBuilder("Speed up NBT copy")
            .addCommonMixins("minecraft.nbt.MixinNBTTagList_FastCopy")
            .setApplyIf(() -> ASMConfig.speedupNBTTagCompoundCopy)
            .addExcludedMod(TargetedMod.BUKKIT)
            .setPhase(Phase.EARLY)),
    STRING_POOLER_NBT_TAG(new MixinBuilder("Pool NBT Strings")
            .addCommonMixins("minecraft.nbt.MixinNBTTagCompound_StringPooler")
            .setApplyIf(() -> TweaksConfig.enableTagCompoundStringPooling)
            .addExcludedMod(TargetedMod.DRAGONAPI)
            .setPhase(Phase.EARLY)),
    STRING_POOLER_NBT_STRING(new MixinBuilder("Pool NBT Strings")
            .addCommonMixins("minecraft.nbt.MixinNBTTagString_StringPooler")
            .setApplyIf(() -> TweaksConfig.enableNBTStringPooling)
            .setPhase(Phase.EARLY)),
    THREADED_WORLDDATA_SAVING(new MixinBuilder()
            .addCommonMixins(
                    "minecraft.MixinMapStorage_threadedIO",
                    "minecraft.MixinSaveHandler_threadedIO",
                    "minecraft.MixinScoreboardSaveData_threadedIO",
                    "minecraft.MixinVillageCollection_threadedIO",
                    "minecraft.MixinMapData_threadedIO",
                    "forge.MixinForgeChunkManager_threadedIO")
            .setApplyIf(() -> TweaksConfig.threadedWorldDataSaving)
            .setPhase(Phase.EARLY)),
    DONT_SLEEP_ON_THREADED_IO(new MixinBuilder("Don't sleep on threaded IO")
            .addCommonMixins("minecraft.MixinThreadedFileIOBase_noSleep")
            .setApplyIf(() -> TweaksConfig.dontSleepOnThreadedIO)
            .setPhase(Phase.EARLY)),
    OPTIMIZE_MOB_SPAWNING(new MixinBuilder()
            .addCommonMixins(
                    "minecraft.MixinSpawnerAnimals_optimizeSpawning",
                    "minecraft.MixinSpawnListEntry_optimizeSpawning")
            .setApplyIf(() -> SpeedupsConfig.optimizeMobSpawning)
            .addExcludedMod(TargetedMod.BUKKIT)
            .setPhase(Phase.EARLY)),
    RENDER_DEBUG(new MixinBuilder()
            .addClientMixins("minecraft.MixinRenderGlobal")
            .setApplyIf(() -> DebugConfig.renderDebug)
            .addExcludedMod(TargetedMod.BUKKIT)
            .setPhase(Phase.EARLY)),
    STATIC_LAN_PORT(new MixinBuilder()
            .addClientMixins("minecraft.server.MixinHttpUtil")
            .setApplyIf(() -> TweaksConfig.enableDefaultLanPort)
            .setPhase(Phase.EARLY)),
    CROSSHAIR_THIRDPERSON(new MixinBuilder("Crosshairs thirdperson")
            .addClientMixins("forge.MixinGuiIngameForge_CrosshairThirdPerson")
            .setApplyIf(() -> TweaksConfig.hideCrosshairInThirdPerson)
            .setPhase(Phase.EARLY)),
    DONT_INVERT_CROSSHAIR_COLORS(new MixinBuilder("Don't invert crosshair colors")
            .addClientMixins("forge.MixinGuiIngameForge_CrosshairInvertColors")
            .setApplyIf(() -> TweaksConfig.dontInvertCrosshairColor)
            .setPhase(Phase.EARLY)),
    FIX_OPENGUIHANDLER_WINDOWID(new MixinBuilder("Fix OpenGuiHandler")
            .addCommonMixins("fml.MixinOpenGuiHandler")
            .setApplyIf(() -> FixesConfig.fixForgeOpenGuiHandlerWindowId)
            .setPhase(Phase.EARLY)),
    FIX_KEYBIND_CONFLICTS(new MixinBuilder("Trigger all conflicting keybinds")
            .addClientMixins(
                    "minecraft.MixinKeyBinding",
                    "minecraft.MixinMinecraft_UpdateKeys")
            .setApplyIf(() -> FixesConfig.triggerAllConflictingKeybindings)
            .addExcludedMod(TargetedMod.MODERNKEYBINDING)
            .setPhase(Phase.EARLY)),
    REMOVE_SPAWN_MINECART_SOUND(new MixinBuilder("Remove sound when spawning a minecart")
            .addClientMixins("minecraft.MixinWorldClient")
            .setApplyIf(() -> TweaksConfig.removeSpawningMinecartSound)
            .setPhase(Phase.EARLY)),
    MACOS_KEYS_TEXTFIELD_SHORTCUTS(new MixinBuilder("Macos use CMD to copy/select/delete text")
            .addClientMixins("minecraft.MixinGuiTextField")
            .setApplyIf(() -> TweaksConfig.enableMacosCmdShortcuts && System.getProperty("os.name").toLowerCase().contains("mac"))
            .setPhase(Phase.EARLY)),
    FIX_FONT_RENDERER_LINEWRAP_RECURSION(new MixinBuilder("Replace recursion with iteration in FontRenderer line wrapping code")
            .addClientMixins("minecraft.MixinFontRenderer")
            .setApplyIf(() -> FixesConfig.fixFontRendererLinewrapRecursion)
            .setPhase(Phase.EARLY)),
    BED_MESSAGE_ABOVE_HOTBAR(new MixinBuilder()
            .addCommonMixins("minecraft.MixinBlockBed")
            .setApplyIf(() -> TweaksConfig.bedMessageAboveHotbar)
            .setPhase(Phase.EARLY)),
    // BED_ALWAYS_SETS_SPAWN.addExcludedMod(TargetedMod.ETFURUMREQUIEM) // uncomment when EFR adds this feature
    BED_ALWAYS_SETS_SPAWN(new MixinBuilder("Clicking a bed in a valid dim will always set your spawn immediately")
            .setApplyIf(() -> TweaksConfig.bedAlwaysSetsSpawn)
            .addCommonMixins(
                    "minecraft.MixinBlockBed_AlwaysSetsSpawn")
            .setPhase(Phase.EARLY)),
    FIX_PLAYER_SKIN_FETCHING(new MixinBuilder()
            .addClientMixins(
                    "minecraft.MixinAbstractClientPlayer",
                    "minecraft.MixinThreadDownloadImageData")
            .setApplyIf(() -> FixesConfig.fixPlayerSkinFetching)
            .setPhase(Phase.EARLY)),
    VALIDATE_PACKET_ENCODING_BEFORE_SENDING(new MixinBuilder()
            .addCommonMixins(
                    "minecraft.packets.MixinDataWatcher",
                    "minecraft.packets.MixinS3FPacketCustomPayload_Validation")
            .setApplyIf(() -> FixesConfig.validatePacketEncodingBeforeSending)
            .setPhase(Phase.EARLY)),
    FIX_FLUID_CONTAINER_REGISTRY_KEY(new MixinBuilder("Fix Forge fluid container registry key")
            .addCommonMixins(
                    "forge.FluidContainerRegistryAccessor",
                    "forge.MixinFluidRegistry")
            .setApplyIf(() -> FixesConfig.fixFluidContainerRegistryKey)
            .setPhase(Phase.EARLY)),
    CHANGE_MAX_NETWORK_NBT_SIZE_LIMIT(new MixinBuilder("Modify the maximum NBT size limit as received from network packets")
            .addCommonMixins("minecraft.MixinPacketBuffer")
            .setApplyIf(() -> FixesConfig.changeMaxNetworkNbtSizeLimit)
            .setPhase(Phase.EARLY)),
    INCREASE_PACKET_SIZE_LIMIT(new MixinBuilder("Increase the packet size limit from 2MiB to a theoretical maximum of 4GiB")
            .addCommonMixins(
                    "minecraft.MixinMessageSerializer2",
                    "minecraft.MixinMessageDeserializer2",
                    "minecraft.packets.MixinS3FPacketCustomPayload_LengthLimit")
            .setApplyIf(() -> FixesConfig.increasePacketSizeLimit)
            .setPhase(Phase.EARLY)),
    FIX_XRAY_BLOCK_WITHOUT_COLLISION_AABB(new MixinBuilder("Fix Xray through block without collision boundingBox")
            .addCommonMixins(
                    "minecraft.MixinBlock_FixXray",
                    "minecraft.MixinWorld_FixXray")
            .setApplyIf(() -> FixesConfig.fixPerspectiveCamera)
            .setPhase(Phase.EARLY)),
    DISABLE_CREATIVE_TAB_ALL_SEARCH(new MixinBuilder("Disable the creative tab with search bar")
            .addClientMixins("minecraft.MixinGuiContainerCreative")
            .setApplyIf(() -> FixesConfig.removeCreativeSearchTab)
            .addRequiredMod(TargetedMod.NOTENOUGHITEMS)
            .addExcludedMod(TargetedMod.ARCHAICFIX)
            .setPhase(Phase.EARLY)),
    FIX_CHAT_COLOR_WRAPPING(new MixinBuilder("Fix wrapped chat lines missing colors")
            .addClientMixins("minecraft.MixinGuiNewChat_FixColorWrapping")
            .setApplyIf(() -> FixesConfig.fixChatWrappedColors)
            .setPhase(Phase.EARLY)),
    COMPACT_CHAT(new MixinBuilder()
            .addClientMixins("minecraft.MixinGuiNewChat_CompactChat")
            .setApplyIf(() -> TweaksConfig.compactChat)
            .setPhase(Phase.EARLY)),
    NETTY_PATCH(new MixinBuilder("Fix NPE in Netty's Bootstrap class")
            .addClientMixins("netty.MixinBootstrap")
            .setApplyIf(() -> FixesConfig.fixNettyNPE)
            .setPhase(Phase.EARLY)),
    MODERN_PICK_BLOCK(new MixinBuilder("Allows pick block to pull items from your inventory")
            .addClientMixins("forge.MixinForgeHooks_ModernPickBlock")
            .setApplyIf(() -> TweaksConfig.modernPickBlock)
            .setPhase(Phase.EARLY)),
    TESSELATOR_PRESERVE_QUAD_ORDER(new MixinBuilder("Preserve the rendering order of layered quads on terrain pass 1")
            .addClientMixins("minecraft.MixinTessellator")
            .setApplyIf(() -> FixesConfig.fixPreserveQuadOrder)
            .setPhase(Phase.EARLY)),
    // Always apply, config handled in mixin
    FAST_BLOCK_PLACING(new MixinBuilder("Allows blocks to be placed faster")
            .addClientMixins("minecraft.MixinMinecraft_FastBlockPlacing")
            .setPhase(Phase.EARLY)),
    FIX_NEGATIVE_KELVIN(new MixinBuilder("Fix the local temperature can go below absolute zero")
            .addCommonMixins("minecraft.MixinBiomeGenBase")
            .setApplyIf(() -> FixesConfig.fixNegativeKelvin)
            .setPhase(Phase.EARLY)),
    SPIGOT_EXTENDED_CHUNKS(new MixinBuilder("Spigot-style extended chunk format to remove the 2MB chunk size limit")
            .addExcludedMod(TargetedMod.BUKKIT)
            .addCommonMixins("minecraft.MixinRegionFile")
            .setApplyIf(() -> FixesConfig.remove2MBChunkLimit)
            .setPhase(Phase.EARLY)),
    AUTOSAVE_INTERVAL(new MixinBuilder("Sets the auto save interval in ticks")
            .addCommonMixins("minecraft.server.MixinMinecraftServer_AutoSaveInterval")
            .setApplyIf(() -> TweaksConfig.autoSaveInterval != 900)
            .setPhase(Phase.EARLY)),
    LIGHTER_WATER(new MixinBuilder("Decreases water opacity from 3 to 1, like in modern")
            .addCommonMixins("minecraft.MixinBlock_LighterWater")
            .setApplyIf(() -> TweaksConfig.useLighterWater)
            .setPhase(Phase.EARLY)),
    EARLY_CHUNK_TILE_COORDINATE_CHECK(new MixinBuilder("Checks saved TileEntity coordinates earlier to provide a more descriptive error message")
            .addCommonMixins("minecraft.MixinChunk")
            .setApplyIf(() -> FixesConfig.earlyChunkTileCoordinateCheck)
            .setPhase(Phase.EARLY)),
    FIX_DUPLICATE_SOUNDS(new MixinBuilder("Fix duplicate sounds being played when you close a gui while one is playing")
            .addClientMixins("minecraft.MixinMinecraft_FixDuplicateSounds")
            .setApplyIf(() -> FixesConfig.fixDuplicateSounds)
            .setPhase(Phase.EARLY)),
    ADD_MOD_ITEM_STATS(new MixinBuilder("Add stats for modded items")
            .addCommonMixins("fml.MixinGameRegistry")
            .addClientMixins("minecraft.MixinStats")
            .setApplyIf(() -> TweaksConfig.addModItemStats)
            .setPhase(Phase.EARLY)),
    ADD_MOD_ENTITY_STATS(new MixinBuilder("Add stats for modded entities")
            .addCommonMixins("minecraft.MixinStatList")
            .addClientMixins(
                    "minecraft.MixinStatsMobsList",
                    "minecraft.MixinStatsBlock",
                    "minecraft.MixinStatsItem")
            .setApplyIf(() -> TweaksConfig.addModEntityStats)
            .setPhase(Phase.EARLY)),
    FIX_SLASH_COMMAND(new MixinBuilder("Fix forge command handler not checking for a / and also not running commands with any case")
            .addClientMixins("minecraft.MixinClientCommandHandler_CommandFix")
            .setApplyIf(() -> FixesConfig.fixSlashCommands)
            .setPhase(Phase.EARLY)),
    FIX_CASE_COMMAND(new MixinBuilder("Fix the command handler not allowing you to run commands typed in any case")
            .addCommonMixins("minecraft.MixinCommandHandler_CommandFix")
            .setApplyIf(() -> FixesConfig.fixCaseCommands)
            .setPhase(Phase.EARLY)),
    FIX_GAMESETTINGS_OUTOFBOUNDS(new MixinBuilder("Fix array out of bound error in GameSettings menu")
            .addClientMixins("minecraft.MixinGameSettings_FixArrayOutOfBounds")
            .setApplyIf(() -> FixesConfig.fixGameSettingsArrayOutOfBounds)
            .addExcludedMod(TargetedMod.LWJGL3IFY)
            .setPhase(Phase.EARLY)),
    LIMIT_RECURSIVE_BLOCK_UPDATE_DEPTH(new MixinBuilder("Limit the number of recursive cascading block updates during world generation to prevent stack overflow crashes")
            .addCommonMixins("minecraft.MixinWorldServer_LimitUpdateRecursion")
            .setApplyIf(() -> FixesConfig.limitRecursiveBlockUpdateDepth >= 0)
            .setPhase(Phase.EARLY)),
    ADD_MOD_CONFIG_SEARCHBAR(new MixinBuilder("Adds a search bar to the mod config GUI")
            .addClientMixins("fml.MixinGuiConfig")
            .setApplyIf(() -> TweaksConfig.addModConfigSearchBar)
            .setPhase(Phase.EARLY)),
    FIX_BUTTON_POS_GUIOPENLINK(new MixinBuilder("Fix the buttons not being centered in the GuiConfirmOpenLink")
            .addClientMixins("minecraft.MixinGuiConfirmOpenLink")
            .setApplyIf(() -> FixesConfig.fixButtonsGuiConfirmOpenLink)
            .setPhase(Phase.EARLY)),
    FIX_CHAT_OPEN_LINK(new MixinBuilder("Fix the vanilla method to open chat links not working for every OS")
            .addClientMixins("minecraft.MixinGuiChat_OpenLinks")
            .setApplyIf(() -> FixesConfig.fixChatOpenLink)
            .setPhase(Phase.EARLY)),
    FIX_NAMETAG_BRIGHTNESS(new MixinBuilder()
            .addClientMixins("minecraft.MixinRendererLivingEntity_NametagBrightness")
            .setApplyIf(() -> FixesConfig.fixNametagBrightness)
            .setPhase(Phase.EARLY)),
    FIX_HIT_EFFECT_BRIGHTNESS(new MixinBuilder()
            .addClientMixins("minecraft.MixinRendererLivingEntity_HitEffectBrightness")
            .setApplyIf(() -> FixesConfig.fixHitEffectBrightness)
            .setPhase(Phase.EARLY)),
    FIX_BUKKIT_PLAYER_CONTAINER(new MixinBuilder("Fix Bukkit BetterQuesting crash")
            .addRequiredMod(TargetedMod.BUKKIT)
            .addCommonMixins("minecraft.MixinContainerPlayer")
            .setApplyIf(() -> FixesConfig.fixBukkitBetterQuestingCrash)
            .setPhase(Phase.EARLY)),
    FIX_BUKKIT_FIRE_SPREAD_NPE(new MixinBuilder("Fix vanilla fire spread sometimes causing NPE on thermos")
            .addRequiredMod(TargetedMod.BUKKIT)
            .addCommonMixins("minecraft.MixinBlockFireSpread")
            .setApplyIf(() -> FixesConfig.fixFireSpread)
            .setPhase(Phase.EARLY)),
    MEMORY_FIXES_CLIENT(new MixinBuilder("Memory fixes")
            .addClientMixins("memory.MixinFMLClientHandler")
            .setApplyIf(() -> FixesConfig.enableMemoryFixes)
            .setPhase(Phase.EARLY)),
    FAST_RANDOM(new MixinBuilder("Replaces uses of stdlib Random with a faster one")
            .addCommonMixins(
                    "minecraft.fastload.rand.MixinChunkProviderGenerate",
                    "minecraft.fastload.rand.MixinMapGenBase",
                    "minecraft.fastload.rand.MixinMapGenCaves")
            .setApplyIf(() -> SpeedupsConfig.fastRandom)
            .setPhase(Phase.EARLY)),
    FAST_INT_CACHE(new MixinBuilder("Rewrite internal caching methods to be safer and faster")
            .addCommonMixins(
                    "minecraft.fastload.intcache.MixinCollectOneCache",
                    "minecraft.fastload.intcache.MixinCollectTwoCaches",
                    "minecraft.fastload.intcache.MixinGenLayerEdge",
                    "minecraft.fastload.intcache.MixinIntCache",
                    "minecraft.fastload.intcache.MixinWorldChunkManager")
            .setApplyIf(() -> SpeedupsConfig.fastIntCache)
            .setPhase(Phase.EARLY)),
    NUKE_LONG_BOXING(new MixinBuilder("Remove Long boxing in MapGenStructure")
            .addCommonMixins("minecraft.fastload.MixinMapGenStructure")
            .setApplyIf(() -> SpeedupsConfig.unboxMapGen)
            .setPhase(Phase.EARLY)),
    EMBED_BLOCKIDS(new MixinBuilder("Embed IDs directly in the objects, to accelerate lookups")
            .addExcludedMod(TargetedMod.BUKKIT)
            .addCommonMixins(
                    "minecraft.fastload.embedid.MixinEmbedIDs",
                    "minecraft.fastload.embedid.MixinFMLControlledNamespacedRegistry",
                    "minecraft.fastload.embedid.MixinObjectIntIdentityMap")
            .setApplyIf(() -> ASMConfig.embedID_experimental)
            .setPhase(Phase.EARLY)),
    FAST_CHUNK_LOADING(new MixinBuilder("Invasively accelerates chunk handling")
            .addCommonMixins(
                    "minecraft.fastload.MixinEntityPlayerMP",
                    "minecraft.fastload.MixinChunkProviderServer")
            .setApplyIf(() -> SpeedupsConfig.fastChunkHandling)
            .setPhase(Phase.EARLY)),
    CANCEL_NONE_SOUNDS(new MixinBuilder("Skips playing empty sounds.")
            .addCommonMixins("minecraft.shutup.MixinWorld")
            .setApplyIf(() -> TweaksConfig.skipEmptySounds)
            .setPhase(Phase.EARLY)),
    HIDE_TEXTURE_ERRORS(new MixinBuilder()
            .addClientMixins("minecraft.shutup.MixinFMLClientHandler")
            .setApplyIf(() -> TweaksConfig.hideTextureErrors)
            .setPhase(Phase.EARLY)),
    FIX_PLAYER_BLOCK_PLACEMENT_DISTANCE_CHECK(new MixinBuilder("Fix wrong block placement distance check")
            .addCommonMixins("minecraft.MixinNetHandlePlayServer_FixWrongBlockPlacementCheck")
            .setApplyIf(() -> FixesConfig.fixWrongBlockPlacementDistanceCheck)
            .setPhase(Phase.EARLY)),
    PREVENT_LAVA_CHUNK_LOADING(new MixinBuilder("Prevent lava blocks from loading chunks")
            .addCommonMixins("minecraft.MixinBlockStaticLiquid")
            .setApplyIf(() -> SpeedupsConfig.lavaChunkLoading)
            .setPhase(Phase.EARLY)),
    FIX_GLASS_BOTTLE_NON_WATER_BLOCKS(new MixinBuilder("Fix Glass Bottles filling with Water from some other Fluid blocks")
            .addCommonMixins("minecraft.MixinItemGlassBottle")
            .setApplyIf(() -> FixesConfig.fixGlassBottleWaterFilling)
            .setPhase(Phase.EARLY)),
    FIX_IOOBE_RENDER_DISTANCE(new MixinBuilder("Fix out of bounds render distance when Optifine/Angelica is uninstalled")
            .addExcludedMod(TargetedMod.OPTIFINE)
            .addExcludedMod(TargetedMod.ANGELICA)
            .addExcludedMod(TargetedMod.FALSETWEAKS)
            .addExcludedMod(TargetedMod.ARCHAICFIX)
            .addClientMixins(
                    "minecraft.MixinGameSettings_ReduceRenderDistance")
            .setApplyIf(() -> FixesConfig.fixVanillaIOOBERenderDistance)
            .setPhase(Phase.EARLY)),
    FIX_ITEM_PHYSICS_LAG(new MixinBuilder("Fix item physics lag when many items are dropped")
            .addCommonMixins("minecraft.MixinWorld_FastItemPhysics")
            .setApplyIf(() -> SpeedupsConfig.fastItemEntityPhysics)
            .addExcludedMod(TargetedMod.FALSETWEAKS)
            .setPhase(Phase.EARLY)),
    BETTER_MOD_LIST(new MixinBuilder()
            .addClientMixins(
                    "fml.MixinGuiModList",
                    "fml.MixinGuiSlotModList",
                    "fml.MixinGuiScrollingList")
            .setApplyIf(() -> TweaksConfig.betterModList)
            .addExcludedMod(TargetedMod.ENDERCORE_WITH_MODLIST)
            .setPhase(Phase.EARLY)),
    FIX_EGG_PARTICLE(new MixinBuilder("Use correct egg particles instead of snowball ones (MC-7807)")
            .addClientMixins("minecraft.MixinEntityEgg")
            .setApplyIf(() -> FixesConfig.fixEggParticles)
            .setPhase(Phase.EARLY)),
    FIX_EVENTBUS_MEMORY_LEAK(new MixinBuilder("Fix EventBus keeping object references after unregistering event handlers.")
            .addCommonMixins(
                    "fml.MixinListenerListInst",
                    "fml.MixinEventBus")
            .setApplyIf(() -> FixesConfig.fixEventBusMemoryLeak)
            .setPhase(Phase.EARLY)),
    ADD_HUNGER_GAMERULE(new MixinBuilder()
            .addCommonMixins(
                    "minecraft.MixinEntityPlayer_HungerRule",
                    "minecraft.MixinFoodStats_HungerRule",
                    "minecraft.MixinGameRules_HungerRule")
            .setApplyIf(() -> TweaksConfig.hungerGameRule)
            .setPhase(Phase.EARLY)),
    DEBUG_EVENT_REGISTRATION(new MixinBuilder()
            .addCommonMixins("fml.MixinEventBus_DebugRegistration")
            .setApplyIf(() -> Boolean.getBoolean("hodgepodge.logEventTimes"))
            .setPhase(Phase.EARLY)),
    DEBUG_DUMP_TEXTURES_SIZES(new MixinBuilder()
            .addClientMixins("minecraft.debug.MixinDynamicTexture", "minecraft.debug.MixinTextureAtlasSprite")
            .setApplyIf(() -> Boolean.getBoolean("hodgepodge.debugtextures"))
            .setPhase(Phase.EARLY)),
    FIX_HOUSE_CHAR_RENDERING(new MixinBuilder()
            .addClientMixins("minecraft.MixinFontRenderer_House")
            .setApplyIf(() -> FixesConfig.fixHouseCharRendering)
            .setPhase(Phase.EARLY)),
    CACHE_LAST_MATCHING_RECIPES(new MixinBuilder()
            .addCommonMixins("minecraft.MixinCraftingManager")
            .setApplyIf(() -> SpeedupsConfig.cacheLastMatchingRecipe)
            .setPhase(Phase.EARLY)),
    FIX_CHUNK_LOADING_FROM_BLOCK_UPDATES(new MixinBuilder("Prevent block and entity updates from loading unloaded chunks")
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> FixesConfig.preventChunkLoadingFromBlockUpdates)
            .addCommonMixins(
                    "minecraft.chunkloading.MixinWorldServer_PreventChunkLoading",
                    "minecraft.chunkloading.MixinWorld_PreventChunkLoading")),
    REMOVE_INVALID_ENTITES(new MixinBuilder()
            .addCommonMixins("minecraft.MixinChunk_FixInvalidEntity")
            .setApplyIf(() -> FixesConfig.removeInvalidChunkEntites)
            .setPhase(Phase.EARLY)),
    SPEEDUP_TILE_DESCRIPTION_PACKETS(new MixinBuilder("Batch S35PacketUpdateTileEntity Packets")
            .addCommonMixins(
                    "minecraft.tiledescriptions.MixinEntityPlayerMP",
                    "minecraft.tiledescriptions.MixinPlayerInstance",
                    "forge.tiledescriptions.MixinForgeHooks")
            .setApplyIf(() -> SpeedupsConfig.batchDescriptionPacketsMixins)
            .setPhase(Phase.EARLY)),
    HIDE_DEPRECATED_ID_NOTICE(new MixinBuilder()
            .addClientMixins("minecraft.MixinHideDeprecatedIdNotice")
            .setApplyIf(() -> TweaksConfig.hideDeprecatedIdNotice)
            .setPhase(Phase.EARLY)),
    FIX_PISTON_OUT_OF_BOUNDS(new MixinBuilder()
            .addCommonMixins("minecraft.MixinTileEntityPiston", "minecraft.MixinBlockPistonBase")
            .setApplyIf(() -> FixesConfig.fixInvalidPistonCrashes)
            .setPhase(Phase.EARLY)),
    FIX_INSTANT_HAND_ITEM_TEXTURE_SWITCH(new MixinBuilder()
            .addClientMixins("minecraft.MixinItemRenderer_FixInstantItemSwitch")
            .setApplyIf(() -> FixesConfig.fixInstantHandItemTextureSwitch)
            .setPhase(Phase.EARLY)),
    SEND_DIFFICULTY_CHANGE_TO_CLIENT(new MixinBuilder("When difficulty updates on the server, inform all clients")
            .addCommonMixins("minecraft.MixinMinecraftServer_UpdateClientDifficulty")
            .setApplyIf(() -> FixesConfig.updateClientDifficultyOnServer)
            .setPhase(Phase.EARLY)),
    MAINTAIN_SLIME_HEALTH(new MixinBuilder("Prevent slimes from resetting to max health when loaded from NBT")
            .setApplyIf(() -> FixesConfig.maintainSlimeHealth)
            .addCommonMixins("minecraft.MixinEntitySlime_MaintainHealth")
            .setPhase(Phase.EARLY)),
    EAT_FOOD_IN_CREATIVE(new MixinBuilder("Allow players to eat food in Creative")
            .addCommonMixins("minecraft.MixinEntityPlayer_EatInCreative", "minecraft.MixinItemFood_DontConsumeCreative")
            .setApplyIf(() -> TweaksConfig.allowEatingFoodInCreative)
            .setPhase(Phase.EARLY)),
    HIDE_VOID_FOG(new MixinBuilder()
            .addClientMixins("minecraft.MixinWorldType_VoidParticles")
            .setApplyIf(() -> TweaksConfig.disableVoidFog != 0)
            .setPhase(Phase.EARLY)),
    ANVIL_MAX_LEVE(new MixinBuilder()
            .addCommonMixins("minecraft.MixinContainerRepair_MaxAnvilCost")
            .addClientMixins("minecraft.MixinGuiRepair_MaxAnvilCost")
            .setApplyIf(() -> TweaksConfig.anvilMaxLevel != 40)
            .setPhase(Phase.EARLY)),

    // Ic2 adjustments
    IC2_UNPROTECTED_GET_BLOCK_FIX(new MixinBuilder("IC2 Kinetic Fix")
            .addCommonMixins("ic2.MixinIc2WaterKinetic")
            .setApplyIf(() -> FixesConfig.fixIc2UnprotectedGetBlock)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.EARLY)),
    IC2_DIRECT_INV_ACCESS(new MixinBuilder("IC2 Direct Inventory Access Fix")
            .addCommonMixins(
                    "ic2.MixinItemCropSeed",
                    "ic2.MixinTileEntityCrop")
            .setApplyIf(() -> FixesConfig.fixIc2DirectInventoryAccess)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.EARLY)),
    IC2_NIGHT_VISION_NANO(new MixinBuilder("IC2 Nightvision Fix")
            .addCommonMixins(
                    "ic2.MixinIc2NanoSuitNightVision",
                    "ic2.MixinIc2QuantumSuitNightVision",
                    "ic2.MixinIc2NightVisionGoggles")
            .setApplyIf(() -> FixesConfig.fixIc2Nightvision)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.EARLY)),
    IC2_REACTOR_DUPE(new MixinBuilder("IC2 Reactor Dupe Fix")
            .addCommonMixins("ic2.MixinTileEntityReactorChamberElectricNoDupe")
            .setApplyIf(() -> FixesConfig.fixIc2ReactorDupe)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.EARLY)),
    IC2_REACTOR_INVENTORY_SPEEDUP(new MixinBuilder("IC2 Reactor Inventory Speedup Fix")
            .addCommonMixins("ic2.MixinTileEntityReactorChamberElectricInvSpeedup")
            .setApplyIf(() -> FixesConfig.optimizeIc2ReactorInventoryAccess)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.EARLY)),
    HIDE_IC2_REACTOR_COOLANT_SLOTS(new MixinBuilder("IC2 Reactory Fix")
            .addCommonMixins("ic2.MixinTileEntityNuclearReactorElectric")
            .setApplyIf(() -> TweaksConfig.hideIc2ReactorSlots)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.EARLY)),
    IC2_FLUID_CONTAINER_TOOLTIP(new MixinBuilder("IC2 Fluid Container Tooltip Fix")
            .addCommonMixins("ic2.MixinItemIC2FluidContainer")
            .setApplyIf(() -> TweaksConfig.displayIc2FluidLocalizedName)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.EARLY)),
    IC2_RESOURCE_PACK_TRANSLATION_FIX(new MixinBuilder()
            .addClientMixins(
                    "fml.MixinLanguageRegistry",
                    "fml.MixinFMLClientHandler",
                    "ic2.MixinLocalization")
            .setApplyIf(() -> FixesConfig.fixIc2ResourcePackTranslation)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.EARLY)),
    IC2_HOVER_MODE_FIX(new MixinBuilder()
            .addCommonMixins("ic2.MixinIc2QuantumSuitHoverMode")
            .setApplyIf(() -> FixesConfig.fixIc2HoverMode)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.LATE)),
    IC2_ARMOR_LAG_FIX(new MixinBuilder()
            .addCommonMixins(
                    "ic2.MixinElectricItemManager",
                    "ic2.MixinIC2ArmorHazmat",
                    "ic2.MixinIC2ArmorJetpack",
                    "ic2.MixinIC2ArmorNanoSuit",
                    "ic2.MixinIC2ArmorNightvisionGoggles",
                    "ic2.MixinIC2ArmorQuantumSuit",
                    "ic2.MixinIC2ArmorSolarHelmet",
                    "ic2.MixinIC2ArmorStaticBoots")
            .setApplyIf(() -> FixesConfig.fixIc2ArmorLag)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.LATE)),
    IC2_CROP_TRAMPLING_FIX(new MixinBuilder()
            .addCommonMixins("ic2.MixinIC2TileEntityCrop")
            .setApplyIf(() -> FixesConfig.fixIc2CropTrampling)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.LATE)),
    IC2_CROP_BREEDING_QOL(new MixinBuilder()
            .addCommonMixins("ic2.MixinIC2TileEntityCropCrossing")
            .setApplyIf(() -> TweaksConfig.ic2CropBreedingQol)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.LATE)),
    IC2_SYNC_REACTORS(new MixinBuilder("Synchronize IC2 reactors for more consistent operation")
            .addCommonMixins(
                    "ic2.sync.MixinTEReactorChamber",
                    "ic2.sync.MixinTEReactor")
            .setApplyIf(() -> TweaksConfig.synchronizeIC2Reactors)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.LATE)),
    IC2_CELL(new MixinBuilder("No IC2 Cell Consumption in tanks")
            .addCommonMixins("ic2.MixinIC2ItemCell")
            .setApplyIf(() -> TweaksConfig.ic2CellWithContainer)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.LATE)),
    IC2_SPEEDUP_REACTOR_SIZE_COMPUTATION(new MixinBuilder()
            .addCommonMixins(
                    "ic2.MixinTEReactorCacheReactorSize",
                    "ic2.MixinDirection_Memory")
            .setApplyIf(() -> SpeedupsConfig.speedupIC2ReactorSize)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.LATE)),
    IC2_KEYBINDS_ACTIVATE_IN_GUI_FIX(new MixinBuilder("Prevent Industrialcraft keybinds from activating in GUIs")
            .addClientMixins("ic2.MixinKeyboardClient_sendKeyUpdate")
            .setApplyIf(() -> FixesConfig.fixIc2KeybindsInGuis)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.LATE)),

    // Disable update checkers
    COFH_CORE_UPDATE_CHECK(new MixinBuilder("Yeet COFH Core Update Check")
            .addCommonMixins("cofhcore.MixinCoFHCoreUpdateCheck")
            .setApplyIf(() -> FixesConfig.removeUpdateChecks)
            .addRequiredMod(TargetedMod.COFH_CORE)
            .setPhase(Phase.EARLY)),
    BIBLIOCRAFT_UPDATE_CHECK(new MixinBuilder("Yeet Bibliocraft Update Check")
            .addClientMixins("bibliocraft.MixinVersionCheck")
            .setApplyIf(() -> FixesConfig.removeUpdateChecks)
            .addRequiredMod(TargetedMod.BIBLIOCRAFT)
            .setPhase(Phase.LATE)),
    DAMAGE_INDICATORS_UPDATE_CHECK(new MixinBuilder("Yeet Damage Indicators Update Check")
            .addClientMixins("damageindicators.MixinDIClientProxy")
            .setApplyIf(() -> FixesConfig.removeUpdateChecks)
            .addRequiredMod(TargetedMod.DAMAGE_INDICATORS)
            .setPhase(Phase.LATE)),

    // COFH
    COFH_REMOVE_TE_CACHE(new MixinBuilder("Remove CoFH tile entity cache")
            .addCommonMixins("minecraft.MixinWorld_CoFH_TE_Cache")
            .setApplyIf(() -> ASMConfig.cofhWorldTransformer)
            .addRequiredMod(TargetedMod.COFH_CORE)
            .setPhase(Phase.EARLY)),
    FIX_ORE_DICT_NPE(new MixinBuilder("Fix NPE in OreDictionaryArbiter")
            .addCommonMixins("cofhcore.MixinOreDictionaryArbiter")
            .addRequiredMod(TargetedMod.COFH_CORE)
            .setApplyIf(() -> FixesConfig.fixCofhOreDictNPE)
            .setPhase(Phase.EARLY)),
    FIX_ORE_DICT_CME(new MixinBuilder("Fix race condition in CoFH oredict")
            .addClientMixins("cofhcore.MixinFMLEventHandler")
            .addRequiredMod(TargetedMod.COFH_CORE)
            .setApplyIf(() -> FixesConfig.fixCofhOreDictCME)
            .setPhase(Phase.EARLY)),
    COFH_IMPROVE_BREAKBLOCK(new MixinBuilder("Improve CoFH breakBlock method to support mods")
            .addClientMixins("cofhcore.MixinBlockHelper")
            .addRequiredMod(TargetedMod.COFH_CORE)
            .setApplyIf(() -> TweaksConfig.improveCofhBreakBlock)
            .setPhase(Phase.EARLY)),
    MFR_FIX_COFH_VALIDATE(new MixinBuilder("Remove CoFH TE cache usage from MFR")
            .addCommonMixins(
                    "minefactoryreloaded.MixinTileEntityBase",
                    "minefactoryreloaded.MixinTileEntityRedNetCable")
            .addRequiredMod(TargetedMod.MINEFACTORY_RELOADED)
            .setApplyIf(() -> ASMConfig.cofhWorldTransformer)
            .setPhase(Phase.LATE)),
    TE_FIX_COFH_VALIDATE(new MixinBuilder("Remove CoFH TE cache usage from TE")
            .addCommonMixins("thermalexpansion.MixinTileInventoryTileLightFalse")
            .addRequiredMod(TargetedMod.THERMALEXPANSION)
            .setApplyIf(() -> ASMConfig.cofhWorldTransformer)
            .setPhase(Phase.LATE)),

    COFH_COMMAND_TPX_FIX(new MixinBuilder("Fix logic of /cofh tpx")
            .addCommonMixins("cofhcore.MixinCoFHCommandTpxFix")
            .setApplyIf(() -> FixesConfig.fixCofhTpxCommand)
            .addRequiredMod(TargetedMod.COFH_CORE)
            .setPhase(Phase.EARLY)),

    // Various Exploits/Fixes
    GC_TIME_COMMAND_FIX(new MixinBuilder("GC Time Fix")
            .addCommonMixins("minecraft.MixinTimeCommandGalacticraftFix")
            .setApplyIf(() -> FixesConfig.fixTimeCommandWithGC)
            .addRequiredMod(TargetedMod.GALACTICRAFT_CORE)
            .setPhase(Phase.EARLY)),

    // Unbind Keybinds by default
    UNBIND_KEYS_COFH(new MixinBuilder("Unbind COFH Core keybinds")
            .addClientMixins("cofhcore.MixinProxyClient")
            .setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault)
            .addRequiredMod(TargetedMod.COFH_CORE)
            .setPhase(Phase.EARLY)),

    // Chunk generation/population
    DISABLE_CHUNK_TERRAIN_GENERATION(new MixinBuilder()
            .addCommonMixins("minecraft.MixinChunkProviderServer_DisableTerrain")
            .setApplyIf(() -> TweaksConfig.disableChunkTerrainGeneration)
            .setPhase(Phase.EARLY)),
    DISABLE_WORLD_TYPE_CHUNK_POPULATION(new MixinBuilder("Disable chunk population tied to chunk generation (ores/structure)")
            .addCommonMixins("minecraft.MixinChunkProviderServer_DisablePopulation")
            .setApplyIf(() -> TweaksConfig.disableWorldTypeChunkPopulation)
            .setPhase(Phase.EARLY)),
    DISABLE_MODDED_CHUNK_POPULATION(new MixinBuilder("Disable all other mod chunk population (e.g. Natura clouds")
            .addCommonMixins("minecraft.MixinChunkProviderServer_DisableModGeneration")
            .setApplyIf(() -> TweaksConfig.disableModdedChunkPopulation)
            .setPhase(Phase.EARLY)),

    // Railcraft Anchors
    WAKE_ANCHORS_ON_LOGIN(new MixinBuilder("Wake passive anchors on login")
            .addCommonMixins(
                    "railcraft.MixinTileAnchorPassive",
                    "railcraft.MixinTileAnchorPersonal")
            .setApplyIf(() -> TweaksConfig.installAnchorAlarm)
            .addRequiredMod(TargetedMod.RAILCRAFT)
            .setPhase(Phase.LATE)),

    // Hunger overhaul
    HUNGER_OVERHAUL_LOW_STAT_EFFECT(new MixinBuilder("Patch unintended low stat effects")
            .addCommonMixins("hungeroverhaul.MixinHungerOverhaulLowStatEffect")
            .setApplyIf(() -> FixesConfig.fixHungerOverhaul)
            .addRequiredMod(TargetedMod.HUNGER_OVERHAUL)
            .setPhase(Phase.LATE)),
    HUNGER_OVERHAUL_REGEN(new MixinBuilder("Patch Regen")
            .addCommonMixins("hungeroverhaul.MixinHungerOverhaulHealthRegen")
            .setApplyIf(() -> FixesConfig.fixHungerOverhaul)
            .addRequiredMod(TargetedMod.HUNGER_OVERHAUL)
            .setPhase(Phase.LATE)),
    HUNGER_OVERHAUL_0_HUNGER(new MixinBuilder("Fix some items restore 0 hunger")
            .addCommonMixins("hungeroverhaul.MixinHungerOverhaulModuleHarvestCraft")
            .setApplyIf(() -> FixesConfig.fixHungerOverhaulRestore0Hunger)
            .addRequiredMod(TargetedMod.HUNGER_OVERHAUL)
            .addRequiredMod(TargetedMod.HARVESTCRAFT)
            .setPhase(Phase.LATE)),

    // Thaumcraft
    SPEEDUP_GET_INFUSION_RECIPES(new MixinBuilder()
            .addCommonMixins("thaumcraft.MixinThaumcraftApi_SpeedupGetInfusionRecipe")
            .setApplyIf(() -> SpeedupsConfig.speedupThaumGetInfusionRecipes)
            .addRequiredMod(TargetedMod.THAUMCRAFT)
            .setPhase(Phase.LATE)),
    THREADED_THAUMCRAFT_MAZE_SAVING(new MixinBuilder()
            .addCommonMixins("thaumcraft.MixinMazeHandler_threadedIO")
            .setApplyIf(() -> TweaksConfig.threadedWorldDataSaving)
            .addRequiredMod(TargetedMod.THAUMCRAFT)
            .setPhase(Phase.LATE)),
    ADD_CV_SUPPORT_TO_WAND_PEDESTAL(new MixinBuilder("CV Support for Wand Pedestal")
            .addCommonMixins("thaumcraft.MixinTileWandPedestal")
            .setApplyIf(() -> TweaksConfig.addCVSupportToWandPedestal)
            .addRequiredMod(TargetedMod.THAUMCRAFT)
            .setPhase(Phase.LATE)),
    FIX_ASPECT_SORTING(new MixinBuilder("Fix Thaumcraft Aspects not being sorted by name")
            .addClientMixins(
                    "thaumcraft.MixinGuiResearchRecipe",
                    "thaumcraft.MixinGuiResearchTable",
                    "thaumcraft.MixinGuiThaumatorium",
                    "thaumcraft.MixinItem_SortAspectsByName")
            .setApplyIf(() -> FixesConfig.fixThaumcraftAspectSorting)
            .addRequiredMod(TargetedMod.THAUMCRAFT)
            .setPhase(Phase.LATE)),
    FIX_GOLEM_MARKER_LOADING(new MixinBuilder("Fix golem marker loading failure when dimensionId larger than MAX_BYTE")
            .addCommonMixins(
                    "thaumcraft.MixinEntityGolemBase",
                    "thaumcraft.MixinItemGolemBell")
            .setApplyIf(() -> FixesConfig.fixThaumcraftGolemMarkerLoading)
            .addRequiredMod(TargetedMod.THAUMCRAFT)
            .setPhase(Phase.LATE)),
    FIX_WORLD_COORDINATE_HASHING_METHOD(new MixinBuilder("Implement a proper hashing method for WorldCoordinates")
            .addCommonMixins("thaumcraft.MixinWorldCoordinates")
            .setApplyIf(() -> FixesConfig.fixThaumcraftWorldCoordinatesHashingMethod)
            .addRequiredMod(TargetedMod.THAUMCRAFT)
            .setPhase(Phase.LATE)),
    FIX_MAGICAL_LEAVES_LAG(new MixinBuilder("Fix Thaumcraft leaves frequent ticking")
            .addCommonMixins(
                    "thaumcraft.MixinBlockMagicalLeaves",
                    "thaumcraft.MixinBlockMagicalLog")
            .setApplyIf(() -> FixesConfig.fixThaumcraftLeavesLag)
            .addRequiredMod(TargetedMod.THAUMCRAFT)
            .setPhase(Phase.LATE)),
    FIX_THAUMCRAFT_VIS_DUPLICATION(new MixinBuilder()
            .addCommonMixins("thaumcraft.MixinTileWandPedestal_VisDuplication")
            .setApplyIf(() -> FixesConfig.fixWandPedestalVisDuplication)
            .addRequiredMod(TargetedMod.THAUMCRAFT)
            .setPhase(Phase.LATE)),
    FIX_NULL_HANDLING_ITEMWISPESSENCE(new MixinBuilder("Fix handling of null stacks in ItemWispEssence")
            .addCommonMixins("thaumcraft.MixinItemWispEssence_Both")
            .addClientMixins("thaumcraft.MixinItemWispEssence_Client")
            .setApplyIf(() -> FixesConfig.fixNullHandlingItemWispEssence)
            .addRequiredMod(TargetedMod.THAUMCRAFT)
            .setPhase(Phase.LATE)),
    FIX_THAUMCRAFT_CHECK_FOR_EE3_ITEM(new MixinBuilder()
            .addCommonMixins("thaumcraft.MixinUtils")
            .setApplyIf(() -> FixesConfig.fixThaumcraftEE3Check)
            .addRequiredMod(TargetedMod.THAUMCRAFT)
            .setPhase(Phase.LATE)),

    // BOP
    FIX_QUICKSAND_XRAY(new MixinBuilder()
            .addCommonMixins("biomesoplenty.MixinBlockMud_FixXray")
            .setApplyIf(() -> FixesConfig.fixPerspectiveCamera)
            .addRequiredMod(TargetedMod.BOP)
            .setPhase(Phase.LATE)),
    DEDUPLICATE_FORESTRY_COMPAT_IN_BOP(new MixinBuilder()
            .addCommonMixins("biomesoplenty.MixinForestryIntegration")
            .setApplyIf(() -> FixesConfig.deduplicateForestryCompatInBOP)
            .addRequiredMod(TargetedMod.BOP)
            .setPhase(Phase.LATE)),
    SPEEDUP_BOP_BIOME_FOG(new MixinBuilder()
            .addClientMixins(
                    "biomesoplenty.MixinFogHandler",
                    "biomesoplenty.AccessorFogHandler")
            .setApplyIf(() -> SpeedupsConfig.speedupBOPFogHandling)
            .addRequiredMod(TargetedMod.BOP)
            .setPhase(Phase.LATE)),
    BIG_FIR_TREES(new MixinBuilder()
            .addCommonMixins("biomesoplenty.MixinBlockBOPSapling")
            .setApplyIf(() -> TweaksConfig.makeBigFirsPlantable)
            .addRequiredMod(TargetedMod.BOP)
            .setPhase(Phase.LATE)),
    JAVA12_BOP(new MixinBuilder("BOP Java12-safe reflection")
            .addCommonMixins("biomesoplenty.MixinBOPBiomes")
            .addCommonMixins("biomesoplenty.MixinBOPReflectionHelper")
            .setApplyIf(() -> FixesConfig.java12BopCompat)
            .addRequiredMod(TargetedMod.BOP)
            .setPhase(Phase.LATE)),
    DISABLE_QUICKSAND_GENERATION(new MixinBuilder()
            .addCommonMixins("biomesoplenty.MixinDisableQuicksandGeneration")
            .setApplyIf(() -> TweaksConfig.removeBOPQuicksandGeneration)
            .addRequiredMod(TargetedMod.BOP)
            .setPhase(Phase.LATE)),
    DISABLE_DONATOR_EFFECTS(new MixinBuilder()
            .addCommonMixins(
                    "biomesoplenty.MixinBOPEventHandler",
                    "biomesoplenty.MixinTrailManager")
            .setApplyIf(() -> TweaksConfig.removeBOPDonatorEffect)
            .addRequiredMod(TargetedMod.BOP)
            .setPhase(Phase.LATE)),
    ADD_BOP_FENCES(new MixinBuilder()
        .addCommonMixins("biomesoplenty.MixinBOPBlocks")
        .addCommonMixins("biomesoplenty.MixinBOPCrafting")
        .setApplyIf(() -> TweaksConfig.addBOPFences)
        .addRequiredMod(TargetedMod.BOP)
        .setPhase(Phase.LATE)),


    // Bibliowood Recipe Fix
    BIBLIOWOODS_RECIPE_FIX(new MixinBuilder("Fixes Bibliowoods Forestry recipes")
            .addCommonMixins("bibliowood.forestry.MixinTabRegistry")
            .setApplyIf(() -> FixesConfig.fixBibliowoodsForestryRecipes)
            .addRequiredMod(TargetedMod.BIBLIOWOODSFORESTRY)
            .setPhase(Phase.LATE)),

    // Minefactory Reloaded
    DISARM_SACRED_TREE(new MixinBuilder("Prevents Sacred Rubber Tree Generation")
            .addCommonMixins("minefactoryreloaded.MixinBlockRubberSapling")
            .addRequiredMod(TargetedMod.MINEFACTORY_RELOADED)
            .setApplyIf(() -> FixesConfig.disableMassiveSacredTreeGeneration)
            .setPhase(Phase.LATE)),
    MFR_IMPROVE_BLOCKSMASHER(new MixinBuilder("Improve MFR block smasher")
            .addCommonMixins("minefactoryreloaded.MixinTileEntityBlockSmasher")
            .addRequiredMod(TargetedMod.MINEFACTORY_RELOADED)
            .setApplyIf(() -> TweaksConfig.improveMfrBlockSmasher)
            .setPhase(Phase.LATE)),
    MFR_IMPROVE_BLOCKBREAKER(new MixinBuilder("Improve MFR block breaker")
            .addCommonMixins("minefactoryreloaded.MixinTileEntityBlockBreaker")
            .addRequiredMod(TargetedMod.MINEFACTORY_RELOADED)
            .setApplyIf(() -> TweaksConfig.improveMfrBlockBreaker)
            .setPhase(Phase.LATE)),

    // Immersive engineering
    JAVA12_IMMERSIVE_ENGINERRING(new MixinBuilder("Immersive Engineering Java-12 safe potion array resizing")
            .addCommonMixins("immersiveengineering.MixinIEPotions")
            .setApplyIf(() -> FixesConfig.java12ImmersiveEngineeringCompat)
            .addRequiredMod(TargetedMod.IMMERSIVE_ENGINENEERING)
            .setPhase(Phase.LATE)),
    JAVA12_MINE_CHEM(new MixinBuilder("Minechem Java-12 safe potion array resizing")
            .addCommonMixins("minechem.MixinPotionInjector")
            .setApplyIf(() -> FixesConfig.java12MineChemCompat)
            .addRequiredMod(TargetedMod.MINECHEM)
            .setPhase(Phase.LATE)),

    // Modular Powersuits
    MPS_PREVENT_RF_ENERGY_SYPHON(new MixinBuilder("Prevent MPS from charging and draining RF from Inventory")
            .addCommonMixins("mps.MixinElectricAdapterRF")
            .setApplyIf(() -> TweaksConfig.preventMPSEnergyTransferRF)
            .addRequiredMod(TargetedMod.MODULARPOWERSUITS)
            .setPhase(Phase.LATE)),
    MPS_PREVENT_EU_ENERGY_SYPHON(new MixinBuilder("Prevent MPS from charging and draining EU from Inventory")
            .addCommonMixins("mps.MixinElectricAdapterEU")
            .setApplyIf(() -> TweaksConfig.preventMPSEnergyTransferEU)
            .addRequiredMod(TargetedMod.MODULARPOWERSUITS)
            .setPhase(Phase.LATE)),
    MPS_PREVENT_ME_ENERGY_SYPHON(new MixinBuilder("Prevent MPS from charging and draining ME from Inventory")
            .addCommonMixins("mps.MixinElectricAdapterME")
            .setApplyIf(() -> TweaksConfig.preventMPSEnergyTransferME)
            .addRequiredMod(TargetedMod.MODULARPOWERSUITS)
            .setPhase(Phase.LATE)),

    // MrTJPCore (Project Red)
    FIX_HUD_LIGHTING_GLITCH(new MixinBuilder("HUD Lighting glitch")
            .addCommonMixins("mrtjpcore.MixinFXEngine")
            .setApplyIf(() -> TweaksConfig.fixHudLightingGlitch)
            .addRequiredMod(TargetedMod.MRTJPCORE)
            .setPhase(Phase.LATE)),
    FIX_POPPING_OFF(new MixinBuilder()
            .addCommonMixins("mrtjpcore.MixinPlacementLib")
            .setApplyIf(() -> TweaksConfig.fixComponentsPoppingOff)
            .addRequiredMod(TargetedMod.MRTJPCORE)
            .setPhase(Phase.LATE)),

    // Automagy
    IMPLEMENTS_CONTAINER_FOR_THIRSTY_TANK(new MixinBuilder("Thirsty Tank Container")
            .addCommonMixins("automagy.MixinItemBlockThirstyTank")
            .setApplyIf(() -> TweaksConfig.thirstyTankContainer)
            .addRequiredMod(TargetedMod.AUTOMAGY)
            .setPhase(Phase.LATE)),

    // Better HUD
    FIX_BETTERHUD_ARMOR_BAR(new MixinBuilder("Fix better HUD armor display breaking with skulls")
            .addCommonMixins("betterhud.MixinSkullDurabilityDisplay")
            .setApplyIf(() -> FixesConfig.fixBetterHUDArmorDisplay)
            .addRequiredMod(TargetedMod.BETTERHUD)
            .setPhase(Phase.LATE)),
    FIX_BETTERHUD_HEARTS_FREEZE(new MixinBuilder("Fix better HUD freezing the game when trying to render high amounts of hp")
            .addCommonMixins("betterhud.MixinHealthRender")
            .setApplyIf(() -> FixesConfig.fixBetterHUDHPDisplay)
            .addRequiredMod(TargetedMod.BETTERHUD)
            .setPhase(Phase.LATE)),

    // ProjectE
    FIX_FURNACE_ITERATION(new MixinBuilder("Speedup Furnaces")
            .addCommonMixins("projecte.MixinObjHandler")
            .setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace)
            .addRequiredMod(TargetedMod.PROJECTE)
            .setPhase(Phase.LATE)),

    // LOTR
    FIX_LOTR_FURNACE_ERROR(new MixinBuilder("Patches lotr to work with the vanilla furnace speedup")
            .addCommonMixins("lotr.MixinLOTRRecipes")
            .setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace)
            .addRequiredMod(TargetedMod.GTNHLIB)
            .addRequiredMod(TargetedMod.LOTR)
            .setPhase(Phase.LATE)),
    FIX_LOTR_JAVA12(new MixinBuilder("Fix lotr java 12+ compat")
            .addCommonMixins(
                    "lotr.MixinLOTRLogReflection",
                    "lotr.MixinRedirectHuornAI",
                    "lotr.MixinRemoveUnlockFinalField")
            .setApplyIf(() -> FixesConfig.java12LotrCompat)
            .addRequiredMod(TargetedMod.LOTR)
            .setPhase(Phase.LATE)),

    // Journeymap
    FIX_JOURNEYMAP_KEYBINDS(new MixinBuilder()
            .addClientMixins("journeymap.MixinConstants")
            .setApplyIf(() -> FixesConfig.fixJourneymapKeybinds)
            .addRequiredMod(TargetedMod.JOURNEYMAP)
            .setPhase(Phase.LATE)),
    FIX_JOURNEYMAP_ILLEGAL_FILE_PATH_CHARACTER(new MixinBuilder()
            .addClientMixins("journeymap.MixinWorldData")
            .setApplyIf(() -> FixesConfig.fixJourneymapFilePath)
            .addRequiredMod(TargetedMod.JOURNEYMAP)
            .setPhase(Phase.LATE)),
    FIX_JOURNEYMAP_JUMPY_SCROLLING(new MixinBuilder("Fix Journeymap jumpy scrolling in the waypoint manager")
            .addClientMixins("journeymap.MixinWaypointManager")
            .setApplyIf(() -> FixesConfig.fixJourneymapJumpyScrolling)
            .addRequiredMod(TargetedMod.JOURNEYMAP)
            .setPhase(Phase.LATE)),

    // Xaero's World Map
    FIX_XAEROS_WORLDMAP_SCROLL(new MixinBuilder("Fix Xaero's World Map map screen scrolling")
            .addClientMixins("xaeroworldmap.MixinGuiMap")
            .setApplyIf(() -> FixesConfig.fixXaerosWorldMapScroll)
            .addRequiredMod(TargetedMod.XAEROWORLDMAP)
            .addRequiredMod(TargetedMod.LWJGL3IFY)
            .setPhase(Phase.LATE)),

    // Xaero's Minimap
    FIX_XAEROS_MINIMAP_ENTITYDOT(new MixinBuilder("Fix Xaero's Minimap player entity dot rendering when arrow is chosen")
            .addClientMixins("xaerominimap.MixinMinimapRenderer")
            .setApplyIf(() -> FixesConfig.fixXaerosMinimapEntityDot)
            .addRequiredMod(TargetedMod.XAEROMINIMAP)
            .setPhase(Phase.LATE)),

    // Pam's Harvest the Nether
    FIX_IGNIS_FRUIT_AABB(new MixinBuilder("Ignis Fruit")
            .addCommonMixins("harvestthenether.MixinBlockPamFruit")
            .setApplyIf(() -> FixesConfig.fixIgnisFruitAABB)
            .addRequiredMod(TargetedMod.HARVESTTHENETHER)
            .setPhase(Phase.LATE)),
    FIX_NETHER_LEAVES_FACE_RENDERING(new MixinBuilder("Nether Leaves")
            .addClientMixins("harvestthenether.MixinBlockNetherLeaves")
            .setApplyIf(() -> FixesConfig.fixNetherLeavesFaceRendering)
            .addRequiredMod(TargetedMod.HARVESTTHENETHER)
            .setPhase(Phase.LATE)),
    FIX_NETHER_SEED_PLANT_BLOCK_NULL(
            new MixinBuilder("Nether Seeds")
                    .addCommonMixins("harvestthenether.MixinItemNetherSeed")
                    .setApplyIf(() -> FixesConfig.fixNetherSeedPlantBlockNull)
                    .addRequiredMod(TargetedMod.HARVESTTHENETHER)
                    .setPhase(Phase.LATE)),

    // Potion Render Offset Fixes - Various Mods
    FIX_BAUBLES_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder("Baubles Inventory with Potions")
            .addClientMixins("baubles.MixinGuiEvents")
            .setApplyIf(() -> TweaksConfig.fixPotionRenderOffset)
            .addRequiredMod(TargetedMod.BAUBLES)
            .setPhase(Phase.LATE)),
    FIX_GALACTICRAFT_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder("Galacticraft Inventory with Potions")
            .addClientMixins("galacticraftcore.MixinGuiExtendedInventory")
            .setApplyIf(() -> TweaksConfig.fixPotionRenderOffset)
            .addRequiredMod(TargetedMod.GALACTICRAFT_CORE)
            .setPhase(Phase.LATE)),
    FIX_TRAVELLERSGEAR_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder("Travelers Gear with Potions")
            .addClientMixins("travellersgear.MixinClientProxy")
            .setApplyIf(() -> TweaksConfig.fixPotionRenderOffset)
            .addRequiredMod(TargetedMod.TRAVELLERSGEAR)
            .setPhase(Phase.LATE)),
    FIX_TINKER_POTION_EFFECT_OFFSET(new MixinBuilder("Prevents the inventory from shifting when the player has active potion effects")
            .addRequiredMod(TargetedMod.TINKERSCONSTRUCT)
            .setApplyIf(() -> TweaksConfig.fixPotionRenderOffset)
            .addClientMixins("tconstruct.MixinTabRegistry")
            .setPhase(Phase.LATE)),

    // Extra Tinkers
    Fix_EXTRATIC_TECONFLICT(new MixinBuilder("Disable ExtraTic's Integration with Metallurgy 3 Precious Materials Module: [Brass, Silver, Electrum & Platinum]")
            .addCommonMixins(
                    "extratic.MixinPartsHandler",
                    "extratic.MixinRecipeHandler")
            .setApplyIf(() -> FixesConfig.fixExtraTiCTEConflict)
            .addRequiredMod(TargetedMod.EXTRATIC)
            .setPhase(Phase.LATE)),
    // Extra Utilities
    FIX_EXTRA_UTILITIES_UNENCHANTING(new MixinBuilder("Fix Exu Unenchanting")
            .addCommonMixins("extrautilities.MixinRecipeUnEnchanting")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesUnEnchanting)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    FIX_EXTRA_UTILITIES_REPAIR_COST(new MixinBuilder("Fix Exu spikes losing NBT tags (other than enchantments) when being placed on the ground")
            .addCommonMixins(
                    "extrautilities.MixinBlockSpike_PreserveNBT",
                    "extrautilities.MixinTileEntityEnchantedSpike_PreserveNBT")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesPreserveSpikeNBT)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    DISABLE_AID_SPAWN_XU_SPIKES(new MixinBuilder("Fixes the vanilla zombie aid spawn triggering when killed by Extra Utilities Spikes")
            .addCommonMixins("extrautilities.MixinBlockSpike")
            .setApplyIf(() -> TweaksConfig.disableAidSpawnByXUSpikes)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    FIX_EXTRA_UTILITIES_TRANSPARENT_ITEM_RENDER(new MixinBuilder("Fix extra utilities item rendering for transparent items")
            .addClientMixins("extrautilities.MixinTransparentItemRender")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesItemRendering)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    FIX_DRUM_EATING_CELLS(new MixinBuilder("Fix extra utilities drums eating ic2 cells and forestry capsules")
            .addCommonMixins("extrautilities.MixinBlockDrum")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesDrumEatingCells)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    FIX_GREENSCREEN_MICROBLOCKS(new MixinBuilder("Fix extra utilities Lapis Caelestis microblocks")
            .addClientMixins("extrautilities.MixinFullBrightMicroMaterial")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesGreenscreenMicroblocks)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    FIX_LAST_MILLENIUM_RAIN(new MixinBuilder("Remove rain from the Last Millenium (Extra Utilities)")
            .addCommonMixins("extrautilities.MixinWorldProviderEndOfTime")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesLastMilleniumRain)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    FIX_LAST_MILLENIUM_CREATURES(new MixinBuilder("Remove creatures from the Last Millenium (Extra Utilities)")
            .addCommonMixins("extrautilities.MixinChunkProviderEndOfTime")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesLastMilleniumCreatures)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    FIX_FLUID_RETRIEVAL_NODE(new MixinBuilder("Prevent fluid retrieval node from voiding (Extra Utilities)")
            .addCommonMixins("extrautilities.MixinFluidBufferRetrieval")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesFluidRetrievalNode)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    FIX_FILING_CABINET_DUPE(new MixinBuilder("Caps hotkey'd stacks to their maximum stack size in filing cabinets")
            .addCommonMixins("extrautilities.MixinContainerFilingCabinet")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesFilingCabinetDupe)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    FIX_FILTER_DUPE(new MixinBuilder("Prevent hotkeying other items onto item filters while they are open")
            .addCommonMixins("extrautilities.MixinContainerFilter")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesFilterDupe)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    CONFIGURABLE_ENDERQUARRY_ENERGY(new MixinBuilder("Ender Quarry energy storage override")
            .addCommonMixins("extrautilities.MixinTileEntityEnderQuarry")
            .setApplyIf(() -> TweaksConfig.extraUtilitiesEnderQuarryOverride > 0)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    FIX_ENDERQUARRY_FREEZE(new MixinBuilder("Fix Ender Quarry freezes randomly")
            .addCommonMixins("extrautilities.MixinTileEntityEnderQuarry_FixFreeze")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesEnderQuarryFreeze)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    FIX_HEALING_AXE_HEAL(new MixinBuilder("Fix the healing axe not healing entities when attacking them")
            .addCommonMixins("extrautilities.MixinItemHealingAxe")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesHealingAxeHeal)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    FIX_HEALING_AXE_UNBREAKABLE(new MixinBuilder("Fix the healing axe to be truely unbreakable for damage calls.")
            .addCommonMixins("extrautilities.MixinItemHealingAxeUnbreakable")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesHealingAxeUnbreakable)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)
    ),
    FIX_EROSION_SHOVEL_UNBREAKABLE(new MixinBuilder("Fix thet erosion shovel to be truely unbreakable for damage calls.")
            .addCommonMixins("extrautilities.MixinItemErosionShovelUnbreakable")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesErosionShovelUnbreakable)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)
    ),
    FIX_CHEST_COMPARATOR_UPDATE(new MixinBuilder("Fix Extra Utilities chests not updating comparator redstone signals when their inventories change")
            .addCommonMixins("extrautilities.MixinExtraUtilsChest")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesChestComparatorUpdate)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    FIX_ETHERIC_SWORD_UNBREAKABLE(new MixinBuilder("Make Etheric Sword truly unbreakable")
            .addCommonMixins("extrautilities.MixinItemEthericSword")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesEthericSwordUnbreakable)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    FIX_ENDER_COLLECTOR_CRASH(new MixinBuilder("Prevent Extra Utilities Ender Collector from inserting into auto-dropping Blocks that create a crash-loop")
            .addCommonMixins("extrautilities.MixinTileEnderCollector")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesEnderCollectorCrash)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),

    // Gliby's Voice Chat
    FIX_GLIBYS_VC_THREAD_SHUTDOWN(new MixinBuilder("Fix Gliby's voice chat not shutting down its threads cleanly")
            .addCommonMixins("glibysvoicechat.MixinVoiceChatServer")
            .addClientMixins("glibysvoicechat.MixinClientNetwork")
            .setApplyIf(() -> FixesConfig.fixGlibysVoiceChatThreadStop)
            .addRequiredMod(TargetedMod.GLIBYS_VOICE_CHAT)
            .setPhase(Phase.LATE)),

    // PortalGun
    PORTALGUN_FIX_URLS(new MixinBuilder("Fix URLs used to download the sound pack")
            .addClientMixins("portalgun.MixinThreadDownloadResources")
            .addRequiredMod(TargetedMod.PORTAL_GUN)
            .setApplyIf(() -> FixesConfig.fixPortalGunURLs)
            .setPhase(Phase.LATE)),

    // VoxelMap
    REPLACE_VOXELMAP_REFLECTION(new MixinBuilder()
            .addClientMixins(
                    "voxelmap.reflection.MixinAddonResourcePack",
                    "voxelmap.reflection.MixinColorManager",
                    "voxelmap.reflection.MixinMap",
                    "voxelmap.reflection.MixinRadar",
                    "voxelmap.reflection.MixinVoxelMap",
                    "voxelmap.reflection.MixinWaypointManager$1")
            .addRequiredMod(TargetedMod.VOXELMAP)
            .setApplyIf(() -> SpeedupsConfig.replaceVoxelMapReflection)
            .setPhase(Phase.LATE)),
    VOXELMAP_Y_FIX(new MixinBuilder("Fix off by one Y coord")
            .addClientMixins("voxelmap.MixinMap")
            .addRequiredMod(TargetedMod.VOXELMAP)
            .setApplyIf(() -> FixesConfig.fixVoxelMapYCoord)
            .setPhase(Phase.LATE)),
    VOXELMAP_NPE_FIX(new MixinBuilder("Fix VoxelMap NPEs with Chunks")
            .addClientMixins(
                    "voxelmap.chunk.MixinCachedRegion",
                    "voxelmap.chunk.MixinComparisonCachedRegion")
            .addRequiredMod(TargetedMod.VOXELMAP)
            .setApplyIf(() -> FixesConfig.fixVoxelMapChunkNPE)
            .setPhase(Phase.LATE)),
    VOXELMAP_FILE_EXT(new MixinBuilder("Change VoxelMap cache file extension")
            .addClientMixins(
                    "voxelmap.cache.MixinCachedRegion",
                    "voxelmap.cache.MixinCachedRegion$1",
                    "voxelmap.cache.MixinComparisonCachedRegion")
            .addRequiredMod(TargetedMod.VOXELMAP)
            .setApplyIf(() -> TweaksConfig.changeCacheFileExtension)
            .setPhase(Phase.LATE)),

    // Witchery
    DISABLE_POTION_ARRAY_EXTENDER(new MixinBuilder("Disable Witchery potion array extender")
            .addCommonMixins("witchery.MixinPotionArrayExtender")
            .setApplyIf(() -> FixesConfig.disableWitcheryPotionExtender)
            .addRequiredMod(TargetedMod.WITCHERY)
            .setPhase(Phase.LATE)),
    FIX_WITCHERY_REFLECTION_SKIN(new MixinBuilder("Fixes Witchery player skins reflections")
            .addClientMixins(
                    "witchery.MixinExtendedPlayer",
                    "witchery.MixinEntityReflection")
            .setApplyIf(() -> FixesConfig.fixWitcheryReflections)
            .addRequiredMod(TargetedMod.WITCHERY)
            .setPhase(Phase.LATE)),
    FIX_WITCHERY_THUNDERING_DETECTION(new MixinBuilder("Fixes Witchery Thunder Detection for rituals and Witch Hunters breaking with mods modifying thunder frequency")
            .addCommonMixins(
                    "witchery.MixinBlockCircle",
                    "witchery.MixinEntityWitchHunter",
                    "witchery.MixinRiteClimateChange")
            .setApplyIf(() -> FixesConfig.fixWitcheryThunderDetection)
            .addRequiredMod(TargetedMod.WITCHERY)
            .setPhase(Phase.LATE)),
    FIX_WITCHERY_RENDERING(new MixinBuilder("Fixes Witchery Rendering errors")
            .addClientMixins("witchery.MixinBlockCircleGlyph")
            .setApplyIf(() -> FixesConfig.fixWitcheryRendering)
            .addRequiredMod(TargetedMod.WITCHERY)
            .setPhase(Phase.LATE)),
    FIX_WITCHERY_DEMON_SHIFT_CLICK(new MixinBuilder("Prevent the Witchery Demon's trading menu from opening when shift-clicking")
            .addCommonMixins("witchery.MixinEntityDemon")
            .setApplyIf(() -> FixesConfig.fixWitcheryDemonShiftClick)
            .addRequiredMod(TargetedMod.WITCHERY)
            .setPhase(Phase.LATE)),

    // Various Exploits/Fixes
    BIBLIOCRAFT_PACKET_FIX(new MixinBuilder("Packet Fix")
            .addCommonMixins("bibliocraft.MixinBibliocraftPatchPacketExploits")
            .setApplyIf(() -> FixesConfig.fixBibliocraftPackets)
            .addRequiredMod(TargetedMod.BIBLIOCRAFT)
            .setPhase(Phase.LATE)),
    BIBLIOCRAFT_PATH_SANITIZATION_FIX(new MixinBuilder("Path sanitization fix")
            .addCommonMixins("bibliocraft.MixinPathSanitization")
            .setApplyIf(() -> FixesConfig.fixBibliocraftPathSanitization)
            .addRequiredMod(TargetedMod.BIBLIOCRAFT)
            .setPhase(Phase.LATE)),
    ZTONES_PACKET_FIX(new MixinBuilder("Packet Fix")
            .addCommonMixins("ztones.MixinZtonesPatchPacketExploits")
            .setApplyIf(() -> FixesConfig.fixZTonesPackets)
            .addRequiredMod(TargetedMod.ZTONES)
            .setPhase(Phase.LATE)),
    ASP_RECIPE_FIX(new MixinBuilder("MT Core recipe fix")
            .addCommonMixins("advancedsolarpanels.MixinAdvancedSolarPanel")
            .addRequiredMod(TargetedMod.ADVANCED_SOLAR_PANELS)
            .addExcludedMod(TargetedMod.DREAMCRAFT)
            .setApplyIf(() -> FixesConfig.fixMTCoreRecipe)
            .setPhase(Phase.LATE)),
    TD_NASE_PREVENTION(new MixinBuilder("Prevent NegativeArraySizeException on itemduct transfers")
            .addCommonMixins("thermaldynamics.MixinSimulatedInv")
            .setApplyIf(() -> FixesConfig.preventThermalDynamicsNASE)
            .addRequiredMod(TargetedMod.THERMALDYNAMICS)
            .setPhase(Phase.LATE)),
    TD_FLUID_GRID_CCE(new MixinBuilder("Prevent ClassCastException on forming invalid Thermal Dynamic fluid grid")
            .addCommonMixins("thermaldynamics.MixinTileFluidDuctSuper")
            .setApplyIf(() -> FixesConfig.preventFluidGridCrash)
            .addRequiredMod(TargetedMod.THERMALDYNAMICS)
            .setPhase(Phase.LATE)),

    // Unbind Keybinds by default
    UNBIND_KEYS_TRAVELLERSGEAR(new MixinBuilder("Unbind Traveller's Gear keybinds")
            .addClientMixins("travellersgear.MixinKeyHandler")
            .setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault)
            .addRequiredMod(TargetedMod.TRAVELLERSGEAR)
            .setPhase(Phase.LATE)),
    UNBIND_KEYS_INDUSTRIALCRAFT(new MixinBuilder("Unbind Industrial craft keybinds")
            .addClientMixins("ic2.MixinKeyboardClient")
            .setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.LATE)),
    UNBIND_KEYS_THAUMCRAFT(new MixinBuilder("Unbind Thaumcraft keybinds")
            .addClientMixins("thaumcraft.MixinKeyHandlerThaumcraft")
            .setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault)
            .addRequiredMod(TargetedMod.THAUMCRAFT)
            .setPhase(Phase.LATE)),
    CHANGE_KEYBIND_CATEGORY_AUTOMAGY(new MixinBuilder("Change keybind category of Automagy")
            .addClientMixins("automagy.MixinAutomagyKeyHandler")
            .setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault)
            .addRequiredMod(TargetedMod.AUTOMAGY)
            .setPhase(Phase.LATE)),

    // Candycraft
    FIX_SUGARBLOCK_NPE(new MixinBuilder("Fix NPE when interacting with sugar block")
            .addCommonMixins("candycraft.MixinBlockSugar")
            .setApplyIf(() -> FixesConfig.fixCandycraftBlockSugarNPE)
            .addRequiredMod(TargetedMod.CANDYCRAFT)
            .setPhase(Phase.LATE)),

    // Morpheus
    FIX_NOT_WAKING_PLAYERS(new MixinBuilder("Fix players not being woken properly when not everyone is sleeping")
            .addServerMixins("morpheus.MixinMorpheusWakePlayers")
            .setApplyIf(() -> FixesConfig.fixMorpheusWaking)
            .addRequiredMod(TargetedMod.MORPHEUS)
            .setPhase(Phase.LATE));

    // spotless:on
    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @Nonnull
    @Override
    public MixinBuilder getBuilder() {
        return builder;
    }
}
