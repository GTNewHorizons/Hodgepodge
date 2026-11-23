package com.mitchej123.hodgepodge.mixins;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;
import com.mitchej123.hodgepodge.config.ASMConfig;
import com.mitchej123.hodgepodge.config.DebugConfig;
import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.MemoryConfig;
import com.mitchej123.hodgepodge.config.SoundConfig;
import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.core.shared.EarlyConfig;

public enum Mixins implements IMixins {

    // spotless:off
    // region Vanilla Fixes
    HODGEPODGE_F3_INFO(new MixinBuilder()
            .addClientMixins("debug.MixinIntCache")
            .setApplyIf(() -> TweaksConfig.moreReadableIntCacheSize)
            .setPhase(Phase.EARLY)),
    FIX_DATAWATCHER_SHARING_OBJECTS_IN_SP(new MixinBuilder()
            .addClientMixins("minecraft.MixinDataWatcher_DeepCopyInSP")
            .setApplyIf(() -> FixesConfig.deepCopyDataWatcherInSP)
            .setPhase(Phase.EARLY)),
    ONLY_LOAD_LANGUAGES_ONCE_PER_FILE(new MixinBuilder()
            .addCommonMixins("minecraft.MixinLanguageRegistry")
            .setApplyIf(() -> FixesConfig.onlyLoadLanguagesOnce)
            .setPhase(Phase.EARLY)),
    CHANGE_CATEGORY_SPRINT_KEY(new MixinBuilder()
            .addClientMixins("minecraft.MixinGameSettings_SprintKey")
            .setApplyIf(() -> TweaksConfig.changeSprintCategory)
            .setPhase(Phase.EARLY)),
    CONFIGURABLE_MUSIC_DELAY(new MixinBuilder("Configurable delay between in-game music tracks")
            .addClientMixins("minecraft.MixinMusicTicker")
            .setApplyIf(() -> TweaksConfig.configurableMusicDelay)
            .setPhase(Phase.EARLY)),
    SIGN_INPUT_IGNORES_FORMAT_CODES(new MixinBuilder("Sign input counts visible chars only")
            .addClientMixins(
                    "minecraft.MixinGuiEditSign",
                    "minecraft.MixinTileEntitySignRenderer_CursorReset")
            .addCommonMixins(
                    "minecraft.MixinNetHandlerPlayServer_SignLimit",
                    "minecraft.MixinC12PacketUpdateSign_RaiseReadLimit",
                    "minecraft.MixinTileEntitySign_RaiseNbtReadLimit")
            .setApplyIf(() -> TweaksConfig.signInputIgnoresFormatCodes)
            .setPhase(Phase.EARLY)),
    ANVIL_INPUT_IGNORES_FORMAT_CODES(new MixinBuilder("Anvil rename counts visible chars only, format codes don't eat the 30-char limit")
            .addCommonMixins("minecraft.MixinNetHandlerPlayServer_AnvilColorCodes")
            .setApplyIf(() -> TweaksConfig.signInputIgnoresFormatCodes)
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
                    "minecraft.MixinChunk_SimulationDistance",
                    "forge.MixinForgeChunkManagerTicket_SimulationDistance")
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
    FIX_PLAYER_CLONING_ON_RECONNECT(new MixinBuilder("Wait for an earlier session for the same UUID to leave the world before accepting a login")
            // Thermos moves logout saving/removal to disconnect(), outside the vanilla hook targets.
            .addExcludedMod(TargetedMod.BUKKIT)
            .addCommonMixins(
                    "minecraft.MixinNetHandlerLoginServer_AwaitPreviousSession",
                    "minecraft.MixinNetHandlerPlayServer_PreWorldDisconnect",
                    "minecraft.MixinServerConfigurationManager_LoginSessionSave",
                    "fml.MixinNetworkDispatcher_LoginSessionState",
                    "minecraft.MixinNetworkSystem_LoginSessionIndex")
            .setApplyIf(() -> FixesConfig.fixPlayerCloningOnReconnect)
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
    LOCALIZE_FORGE_MOD_OPTIONS_BUTTON(new MixinBuilder("Allow translating Forge's Mod Options button in pause menu")
            .addClientMixins("minecraft.MixinGuiIngameMenu_LocalizeModOptionsButton")
            .setApplyIf(() -> TweaksConfig.localizeForgeModOptionsButton)
            .setPhase(Phase.EARLY)),
    ADD_TIME_GET(new MixinBuilder("Add /time get command")
            .addCommonMixins("minecraft.MixinCommandTime")
            .setApplyIf(() -> TweaksConfig.addTimeGet)
            .setPhase(Phase.EARLY)),
    OPTIMIZE_WORLD_UPDATE_LIGHT(new MixinBuilder("Optimize world updateLightByType method")
            .addCommonMixins("minecraft.MixinWorld_FixLightUpdateLag")
            .addExcludedMod(TargetedMod.ARCHAICFIX)
            .addExcludedMod(TargetedMod.ANGELICA)
            .addExcludedMod(TargetedMod.SUPERNOVA)
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
    KEEP_SOUND_ENGINE_AT_BOOT(new MixinBuilder("Keep the sound engine across the post-load resource reload")
            .addClientMixins(
                    "fml.MixinFMLClientHandler_BootSoundRefresh",
                    "minecraft.MixinSoundManager_KeepBootEngine",
                    "minecraft.MixinSoundSystemStarterThread_BootReload")
            .setApplyIf(() -> SpeedupsConfig.keepSoundEngineAtBoot)
            .setPhase(Phase.EARLY)),
    INITIAL_OPENAL_OUTPUT_DEVICE(new MixinBuilder()
            .addClientMixins("paulscode.MixinLibraryLWJGLOpenAL")
            .addRequiredMod(TargetedMod.LWJGL3IFY)
            .setApplyIf(() -> SoundConfig.manageOutputDevicesAtStartup)
            .setPhase(Phase.EARLY)),
    REPLACE_ARCHAICFIX_SOUND_DEVICE_RECOVERY(new MixinBuilder()
            .addClientMixins("archaicfix.MixinSoundDeviceThread")
            .addRequiredMod(TargetedMod.ARCHAICFIX)
            .addRequiredMod(TargetedMod.LWJGL3IFY)
            .setApplyIf(() -> SoundConfig.manageOutputDevicesAtStartup)
            .setPhase(Phase.LATE)),
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
    FIX_CAMERA_PARTICLE_ROTATION(new MixinBuilder("Orient particles from the render view entity (MC-46445)")
            .addClientMixins("minecraft.MixinActiveRenderInfo_CameraRotation")
            .setApplyIf(() -> FixesConfig.fixCameraParticleRotation)
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
            .addExcludedMod(TargetedMod.FALSETWEAKS_LT_433)
            .setApplyIf(() -> FixesConfig.fixBottomFaceUV)
            .setPhase(Phase.EARLY)),
    FIX_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder()
            .addClientMixins("minecraft.MixinInventoryEffectRenderer_PotionOffset")
            .setApplyIf(() -> TweaksConfig.fixPotionRenderOffset)
            .setPhase(Phase.EARLY)),
    TOGGLE_INVENTORY_EFFECT_ICONS(new MixinBuilder()
            .addClientMixins("minecraft.MixinInventoryEffectRenderer_TogglePotionIcons")
            .setApplyIf(() -> !TweaksConfig.showInventoryEffectIcons)
            .setPhase(Phase.EARLY)),
    FIX_POTION_EFFECT_RENDERING(new MixinBuilder()
            .addClientMixins(
                    "minecraft.MixinInventoryEffectRenderer_PotionEffectRendering",
                    "minecraft.MixinGuiContainer_PotionEffectRendering")
            .setApplyIf(() -> TweaksConfig.fixPotionEffectRender)
            .setPhase(Phase.EARLY)),
    FIX_POTION_EFFECT_ALPHA_TEST(new MixinBuilder("Fix potion effect panel visual glitch caused by Forge leaving GL_ALPHA_TEST disabled after item rendering")
            .addClientMixins("minecraft.MixinInventoryEffectRenderer_AlphaTestFix")
            .setApplyIf(() -> FixesConfig.fixPotionEffectAlphaTest)
            .setPhase(Phase.EARLY)),
    FIX_CREATIVE_TAB_ALPHA_TEST(new MixinBuilder("Fix creative tab background black corners caused by Forge leaving GL_ALPHA_TEST disabled after item rendering")
            .addClientMixins("minecraft.MixinGuiContainerCreative_AlphaTestFix")
            .setApplyIf(() -> FixesConfig.fixCreativeTabAlphaTest)
            .setPhase(Phase.EARLY)),
    FIX_XP_BAR_ALPHA_TEST(new MixinBuilder("Fix XP and jump bar black pixels caused by GL_ALPHA_TEST being left disabled")
            .addClientMixins("forge.MixinGuiIngameForge_AlphaTest")
            .setApplyIf(() -> FixesConfig.fixXpBarAlphaTest)
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
    TEXTURED_SCROLLBAR(new MixinBuilder("Replace vanilla scrollbar with a textured one")
            .addClientMixins(
                    "minecraft.MixinGuiSlot_TexturedScrollbar",
                    "fml.MixinGuiScrollingList_TexturedScrollbar")
            .setApplyIf(() -> TweaksConfig.texturedScrollbar)
            .setPhase(Phase.EARLY)),
    MODERN_ANVIL_LABELS(new MixinBuilder("Adds the Inventory label and modern cost text to the anvil GUI")
            .addClientMixins("minecraft.MixinGuiRepair_ModernLabels")
            .setApplyIf(() -> TweaksConfig.modernAnvilLabels)
            .setPhase(Phase.EARLY)),
    CLEAN_CHAT_LOGS(new MixinBuilder()
            .addClientMixins("minecraft.MixinGuiNewChat_CleanChatLogs")
            .setApplyIf(() -> TweaksConfig.cleanChatLogs)
            .setPhase(Phase.EARLY)),
    F1_SHOW_HAND(new MixinBuilder()
            .addClientMixins(
                    "minecraft.MixinEntityRenderer_F1ShowHand",
                    "minecraft.MixinMinecraft_F1ShowHand")
            .setApplyIf(() -> TweaksConfig.f1ShowHand)
            .setPhase(Phase.EARLY)),
    FIX_ENTITY_ATTRIBUTES_RANGE(new MixinBuilder()
            .addClientMixins("minecraft.MixinNetHandlerPlayClient_FixEntityAttributesRange")
            .setApplyIf(() -> FixesConfig.fixEntityAttributesRange)
            .setPhase(Phase.EARLY)),
    FIX_LONG_POTION_DURATION(new MixinBuilder("Fix Potion Durations Longer Than 27 Minutes")
            .addCommonMixins(
                    "minecraft.MixinS1DPacketEntityEffect_LongDuration",
                    "minecraft.MixinNetHandlerPlayServer_LongPotionDuration",
                    "minecraft.PotionEffectAccessor")
            .setApplyIf(() -> FixesConfig.fixLongPotionDuration)
            .setPhase(Phase.EARLY)),
    FIX_LONG_TIMER_FORMAT(new MixinBuilder("Show Timers Longer Than An Hour As h:mm:ss")
            .addClientMixins("minecraft.MixinStringUtils_LongTimer")
            .setApplyIf(() -> FixesConfig.fixLongTimerFormat)
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
    PREVENT_LOADING_CHUNKS_WHEN_TICKING_BLOCKS(new MixinBuilder()
            .addCommonMixins(
                    "minecraft.chunkloading.blocks.MixinBlockCocoa",
                    "minecraft.chunkloading.blocks.MixinBlockCrops",
                    "minecraft.chunkloading.blocks.MixinBlockFire",
                    "minecraft.chunkloading.blocks.MixinBlockGrass",
                    "minecraft.chunkloading.blocks.MixinBlockFarmland",
                    "minecraft.chunkloading.blocks.MixinBlockMushroom",
                    "minecraft.chunkloading.blocks.MixinBlockMycelium",
                    "minecraft.chunkloading.blocks.MixinBlockSapling",
                    "minecraft.chunkloading.blocks.MixinBlockStaticLiquid",
                    "minecraft.chunkloading.blocks.MixinBlockStem",
                    "minecraft.chunkloading.blocks.MixinBlockTorch",
                    "minecraft.chunkloading.blocks.MixinBlockVine")
            .setApplyIf(() -> SpeedupsConfig.preventLoadingChunksWhenTickingBlocks)
            .setPhase(Phase.EARLY)),
    PREVENT_LOADING_CHUNKS_WHEN_LIQUIDS_FLOW(new MixinBuilder()
            .addCommonMixins(
                    "minecraft.chunkloading.blocks.MixinBlockDynamicLiquid",
                    "minecraft.chunkloading.blocks.MixinBlockFluidClassic",
                    "minecraft.chunkloading.blocks.MixinBlockFluidFinite")
            .setApplyIf(() -> SpeedupsConfig.preventLoadingChunksWhenLiquidsFlow)
            .setPhase(Phase.EARLY)),
    PREVENT_LOADING_CHUNKS_WHEN_PATH_FINDING(new MixinBuilder()
            .addCommonMixins("minecraft.chunkloading.pathfinding.MixinPathNavigate")
            .setApplyIf(() -> SpeedupsConfig.preventLoadingChunksWhenPathfinding)
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
    FIX_FUEL_OVERFLOW(new MixinBuilder("Fix vanilla furnace fuel overflow")
            .addCommonMixins("minecraft.MixinTileEntityFurnace_FixFuelOverflow")
            .setApplyIf(() -> FixesConfig.fixFuelOverflow)
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
    FIX_UNLOCALIZED_DISCONNECT_MESSAGES(new MixinBuilder("Localize vanilla disconnect messages")
            .addCommonMixins(
                    "minecraft.MixinNetHandlerPlayServer_LocalizedKick",
                    "minecraft.MixinNetHandlerLoginServer_LocalizedKick")
            .setApplyIf(() -> FixesConfig.fixUnlocalizedDisconnectMessages)
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
    FIX_END_GHOST_PLAYER(new MixinBuilder("Fix stale player copy left in the old world when entering the End")
            .addCommonMixins("minecraft.MixinServerConfigurationManager_FixEndGhostPlayer")
            .setApplyIf(() -> FixesConfig.fixEndGhostPlayer)
            .setPhase(Phase.EARLY)),
    CONFIGURABLE_PORTAL_RATIO(new MixinBuilder("Make Nether portal travel ratio configurable")
            .addCommonMixins("minecraft.MixinWorldProviderHell")
            .setPhase(Phase.EARLY)),
    FIX_RESET_RAIN_AND_THUNDER(new MixinBuilder()
            .addCommonMixins("minecraft.MixinWorldProvider_FixResetRainAndThunder")
            .setApplyIf(() -> FixesConfig.fixResetRainAndThunder)
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
    FIX_HANDSHAKE_STARTING_KICK_TRANSLATABLE(new MixinBuilder("Send a translatable message instead of a hardcoded English one when rejecting logins while the server is still starting")
            .addCommonMixins("minecraft.MixinNetHandlerHandshakeTCP_TranslatableKick")
            .setApplyIf(() -> FixesConfig.fixHandshakeStartingKickTranslatable)
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
    FIX_BED_HEIGHT_OVERFLOW(new MixinBuilder("Fix bed height overflowing on multiplayer")
            .addCommonMixins("minecraft.packets.MixinS0APacketUseBed_FixHeightOverflow")
            .setApplyIf(() -> FixesConfig.fixBedHeightOverflow)
            .setPhase(Phase.EARLY)),
    ADD_MEMORY_CLEANING_SHUTDOWN_HOOKS(new MixinBuilder()
            .addCommonMixins("memory.MixinMinecraftServer_ShutdownHook")
            .addClientMixins("memory.MixinMinecraft_ShutdownHook")
            .setPhase(Phase.EARLY)),
    FIX_MINECRAFT_SERVER_LEAK(new MixinBuilder()
            .addCommonMixins("memory.MixinMinecraftServer_ClearServerRef")
            .setApplyIf(() -> MemoryConfig.leaks.fixMinecraftServerLeak)
            .setPhase(Phase.EARLY)),
    FIX_WORLD_SERVER_LEAKING_UNLOADED_ENTITIES(new MixinBuilder()
            .addCommonMixins("memory.MixinWorldServerUpdateEntities")
            .setApplyIf(() -> MemoryConfig.leaks.fixWorldServerLeakingUnloadedEntities)
            .setPhase(Phase.EARLY)),
    FIX_SKIN_MANAGER_CLIENT_WORLD_LEAK(new MixinBuilder()
            .addClientMixins("memory.MixinSkinManager$2")
            .setApplyIf(() -> MemoryConfig.leaks.fixSkinManagerLeakingClientWorld)
            .setPhase(Phase.EARLY)),
    FIX_ITEM_RENDERER_ITEM_LEAK(new MixinBuilder()
            .addClientMixins("memory.ItemRendererAccessor")
            .setApplyIf(() -> MemoryConfig.leaks.fixEntityRendererItemRendererLeak)
            .setPhase(Phase.EARLY)),
    FIX_REDSTONE_TORCH_WORLD_LEAK(new MixinBuilder("Fix world leak in redstone torch")
            .addCommonMixins("memory.MixinBlockRedstoneTorch")
            .setApplyIf(() -> MemoryConfig.leaks.fixRedstoneTorchWorldLeak)
            .setPhase(Phase.EARLY)),
    FIX_WORLD_MAP_STORAGE_LEAK(new MixinBuilder()
            .addCommonMixins("memory.WorldAccessor_ClearMapStorage")
            .setApplyIf(() -> MemoryConfig.leaks.fixWorldMapStorageLeak)
            .setPhase(Phase.EARLY)),
    FIX_RENDERERS_WORLD_LEAK(new MixinBuilder()
            .addClientMixins("memory.MixinRenderGlobal_FixWordLeak")
            .setApplyIf(() -> MemoryConfig.leaks.fixRenderersWorldLeak)
            .setPhase(Phase.EARLY)),
    FIX_NETHANDLER_MAPSTORAGE_LEAK(new MixinBuilder()
            .addClientMixins("memory.MixinNetHandlerPlayClient_ClearMapStorage")
            .setApplyIf(() -> MemoryConfig.leaks.fixNetHandlerClientWorldLeak)
            .setPhase(Phase.EARLY)),
    FIX_ENTITY_RENDERER_POINTED_ENTITY_LEAK(new MixinBuilder()
            .addClientMixins("memory.MixinEntityRenderer_ClearPointedEntity")
            .setApplyIf(() -> MemoryConfig.leaks.fixPointedEntityLeak)
            .setPhase(Phase.EARLY)),
    FIX_ENCHANTMENT_HELPER_WORLD_LEAK(new MixinBuilder()
            .addCommonMixins("memory.MixinEnchantmentHelper_FixLeak")
            .setApplyIf(() -> MemoryConfig.leaks.fixEnchantmentHelperLeak)
            .addExcludedMod(TargetedMod.ARCHAICFIX)
            .setPhase(Phase.EARLY)),
    FIX_RENDER_FALLING_BLOCK_WORLD_LEAK(new MixinBuilder()
            .addClientMixins("memory.MixinRenderFallingBlock")
            .setApplyIf(() -> MemoryConfig.leaks.fixRenderFallingBlockLeak)
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
            .addCommonMixins("debug.MixinDimensionManager")
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
    OPTIMIZE_JAR_DISCOVERER(new MixinBuilder("Reduce FML jar discovery regex overhead")
            .addCommonMixins("fml.MixinJarDiscoverer")
            .setApplyIf(() -> SpeedupsConfig.optimizeJarDiscovererRegexOverhead)
            .setPhase(Phase.EARLY)),
    SQUASH_BED_ERROR_MESSAGE(new MixinBuilder()
            .addClientMixins("minecraft.MixinNetHandlerPlayClient_SquashBedMessages")
            .setApplyIf(() -> FixesConfig.squashBedErrorMessage)
            .setPhase(Phase.EARLY)),
    CHUNK_SAVE_CME_DEBUG(new MixinBuilder("Add debugging code to Chunk Save CME")
            .addCommonMixins("debug.MixinNBTTagCompound_CheckCME")
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
    FIX_CHUNK_SAVE_QUEUE(new MixinBuilder("Prevent chunk saves from being stranded during IO task removal")
            .addCommonMixins("minecraft.MixinAnvilChunkLoader_SaveQueue")
            .setApplyIf(() -> FixesConfig.fixChunkSaveQueueRace)
            .setPhase(Phase.EARLY)),
    FIX_CHUNK_LOAD_DURING_SAVE(new MixinBuilder("Prevent loading an outdated chunk from disk while its newer save is being written")
            .addCommonMixins(
                    "minecraft.MixinAnvilChunkLoader_LoadDuringSave",
                    "minecraft.AccessorAnvilChunkLoaderPendingChunk")
            .setApplyIf(() -> FixesConfig.fixChunkLoadDuringSave)
            .setPhase(Phase.EARLY)),
    THREADED_WORLDDATA_SAVING(new MixinBuilder()
            .addCommonMixins(
                    "minecraft.MixinMapStorage_threadedIO",
                    "minecraft.MixinSaveHandler_threadedIO",
                    "minecraft.MixinMinecraftServer_WorldDataSave",
                    "minecraft.MixinCommandSaveAll_WorldDataSave",
                    "minecraft.MixinScoreboardSaveData_threadedIO",
                    "minecraft.MixinVillageCollection_threadedIO",
                    "minecraft.MixinMapData_threadedIO",
                    "forge.MixinForgeChunkManager_threadedIO")
            .setApplyIf(() -> TweaksConfig.threadedWorldDataSaving)
            .setPhase(Phase.EARLY)),
    DONT_SLEEP_ON_THREADED_IO(new MixinBuilder("Don't sleep on threaded IO")
            .addCommonMixins("minecraft.MixinThreadedFileIOBase_noSleep")
            .setApplyIf(() -> TweaksConfig.dontSleepOnThreadedIO)
            .addExcludedMod(TargetedMod.BUKKIT)
            .setPhase(Phase.EARLY)),
    OPTIMIZE_MOB_SPAWNING(new MixinBuilder()
            .addCommonMixins(
                    "minecraft.MixinSpawnerAnimals_optimizeSpawning",
                    "minecraft.MixinSpawnListEntry_optimizeSpawning")
            .setApplyIf(() -> SpeedupsConfig.optimizeMobSpawning)
            .setPhase(Phase.EARLY)),
    OPTIMIZE_MOB_SPAWNING_NULL_VANILLA_MAP(new MixinBuilder("Null out vanilla eligibleChunksForSpawning HashMap")
            .addCommonMixins("minecraft.MixinSpawnerAnimals_nullVanillaMap")
            .setApplyIf(() -> SpeedupsConfig.optimizeMobSpawning)
            .addExcludedMod(TargetedMod.BUKKIT)
            .setPhase(Phase.EARLY)),
    SPAWN_CONTEXT(new MixinBuilder("Adds spawn context field to World for spawn reason routing")
            .addCommonMixins("minecraft.MixinWorld_SpawnContext")
            .setApplyIf(() -> SpeedupsConfig.optimizeMobSpawning)
            .setPhase(Phase.EARLY)),
    BUKKIT_SPAWN_REASON(new MixinBuilder("Routes spawnEntityInWorld through Bukkit addEntity with spawn reason")
            .addCommonMixins("minecraft.MixinWorld_BukkitSpawnReason")
            .setApplyIf(() -> SpeedupsConfig.optimizeMobSpawning)
            .addRequiredMod(TargetedMod.BUKKIT)
            .setPhase(Phase.EARLY)),
    RENDER_DEBUG(new MixinBuilder()
            .addClientMixins("debug.MixinRenderGlobal")
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
    CROSSHAIR_GUI_OPEN(new MixinBuilder("Stops rendering the crosshair when a GUI screen is open")
            .addClientMixins("forge.MixinGuiIngameForge_CrosshairGuiOpen")
            .setApplyIf(() -> TweaksConfig.hideCrosshairInGui)
            .setPhase(Phase.EARLY)),
    DONT_INVERT_CROSSHAIR_COLORS(new MixinBuilder("Don't invert crosshair colors")
            .addClientMixins("forge.MixinGuiIngameForge_CrosshairInvertColors")
            .setApplyIf(() -> TweaksConfig.dontInvertCrosshairColor)
            .setPhase(Phase.EARLY)),
    FIX_HOTBAR_SELECTION_HEIGHT(new MixinBuilder("Draw the full hotbar selection frame")
            .addClientMixins("forge.MixinGuiIngameForge_HotbarSelectionHeight")
            .setApplyIf(() -> FixesConfig.fixHotbarSelectionHeight)
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
    FIX_KEYBIND_CATEGORY_SORTING(new MixinBuilder("Fix controls menu crash on colliding localized category names")
            .addClientMixins("minecraft.MixinKeyBinding_FixComparison")
            .setApplyIf(() -> FixesConfig.fixKeybindCategorySorting)
            .addExcludedMod(TargetedMod.MODERNKEYBINDING)
            .setPhase(Phase.EARLY)),
    REMOVE_SPAWN_MINECART_SOUND(new MixinBuilder("Remove sound when spawning a minecart")
            .addClientMixins("minecraft.MixinWorldClient")
            .setApplyIf(() -> TweaksConfig.removeSpawningMinecartSound)
            .setPhase(Phase.EARLY)),
    TEXTFIELD_CTRL_SHORTCUTS(new MixinBuilder("Use CTRL (CMD on MacOS) to copy/select/delete text")
            .addClientMixins("minecraft.MixinGuiTextField")
            .setApplyIf(() -> TweaksConfig.enableTextFieldCtrlShortcuts)
            .setPhase(Phase.EARLY)),
    FIX_FONT_RENDERER_LINEWRAP_RECURSION(new MixinBuilder("Replace recursion with iteration in FontRenderer line wrapping code")
            .addClientMixins("minecraft.MixinFontRenderer")
            .setApplyIf(() -> FixesConfig.fixFontRendererLinewrapRecursion)
            .setPhase(Phase.EARLY)),
    FONT_RENDERER_FALLBACK_PREPROCESS(new MixinBuilder("Preprocess text through GTNHLib fallback when Angelica is absent")
            .addClientMixins("minecraft.MixinFontRenderer_FallbackPreprocess")
            .setApplyIf(() -> true)
            .setPhase(Phase.EARLY)),
    FONT_RENDERER_FALLBACK_SHADOW(new MixinBuilder("Render drop shadow for text segments with shadow format code when Angelica is absent")
            .addClientMixins("minecraft.MixinFontRenderer_FallbackShadow")
            .setApplyIf(() -> true)
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
    RETURN_CURSOR_STACK(new MixinBuilder("Return cursor stacks and synchronize inventory on container close")
            .addCommonMixins("minecraft.MixinEntityPlayerMP_ReturnCursorStack")
            .addClientMixins("minecraft.MixinEntityClientPlayerMP_ReturnCursorStack")
            .setPhase(Phase.EARLY)),
    DISABLE_CREATIVE_TAB_ALL_SEARCH(new MixinBuilder("Disable the creative tab with search bar")
            .addClientMixins("minecraft.MixinGuiContainerCreative")
            .setApplyIf(() -> FixesConfig.removeCreativeSearchTab)
            .addRequiredMod(TargetedMod.NOTENOUGHITEMS)
            .addExcludedMod(TargetedMod.ARCHAICFIX)
            .setPhase(Phase.EARLY)),
    CREATIVE_TAB_TITLE_COLOR(new MixinBuilder("Allow creative tab gui title color via localization key")
            .addClientMixins("minecraft.MixinGuiContainerCreative_TabTitle")
            .setApplyIf(() -> TweaksConfig.creativeTabLocalizationOverrides)
            .setPhase(Phase.EARLY)),
    FIX_CHAT_COLOR_WRAPPING(new MixinBuilder("Fix wrapped chat lines missing colors")
            .addClientMixins(
                    "minecraft.MixinGuiNewChat_FixColorWrapping",
                    "minecraft.MixinGuiTextField_FixColorScroll")
            .setApplyIf(() -> FixesConfig.fixChatWrappedColors)
            .setPhase(Phase.EARLY)),
    FIX_COMMAND_FORMATTING_LOSS(new MixinBuilder("Fix /say, /tell, /me losing formatting after first word")
            .addCommonMixins("minecraft.MixinCommandBase_JoinArgs")
            .setApplyIf(() -> FixesConfig.fixCommandFormattingLoss)
            .setPhase(Phase.EARLY)),
    FIX_DISCONNECT_SCREEN_LAYOUT(new MixinBuilder("Fix disconnect screen button overlapping long kick messages")
            .addClientMixins("minecraft.MixinGuiDisconnected_FixLayout")
            .setApplyIf(() -> FixesConfig.fixDisconnectScreenLayout)
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
    FIX_FNFE_SERVER_BOOT(new MixinBuilder("Fix printed errors about json files when running a server for the first time")
            .addServerMixins("minecraft.server.MixinUserList")
            .setApplyIf(() -> FixesConfig.fixFileNotFoundExceptionsServerFirstBoot)
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
    SPEEDUP_CHUNK_COMPRESSION(new MixinBuilder("Pooled deflater and batched writes for chunk saving")
            .addCommonMixins(
                    "minecraft.MixinRegionFile_FastChunkCompression",
                    "minecraft.MixinAnvilChunkLoader_FastChunkWrite")
            .setApplyIf(() -> SpeedupsConfig.speedupChunkCompression)
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
    FIX_WORLDGEN_LIQUIDS_RECURSION(new MixinBuilder("Prevent recursive spring generation updates")
            .addCommonMixins("minecraft.MixinWorldGenLiquids_NoImmediateUpdates")
            .setApplyIf(() -> FixesConfig.fixWorldGenLiquidsRecursion)
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
    MEMORY_FIXES_CLIENT(new MixinBuilder()
            .addClientMixins("memory.MixinFMLClientHandler")
            .setApplyIf(() -> MemoryConfig.allocs.clearFMLTextureErrors)
            .setPhase(Phase.EARLY)),
    ASYNC_FML_FILE_LOG(new MixinBuilder("Async FML file log: drain before hard exit")
            .addCommonMixins("fml.MixinFMLCommonHandler_DrainAsyncLog")
            .setApplyIf(() -> !EarlyConfig.noAsyncFmlFileLog)
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
    FAST_BLOCK_LOOKUP(new MixinBuilder("Flat array for Block.getBlockById, bypassing registry dispatch chain")
            .addExcludedMod(TargetedMod.BUKKIT)
            .addCommonMixins("minecraft.fastload.MixinBlock_FastLookup")
            .setApplyIf(() -> SpeedupsConfig.fastBlockLookup)
            .setPhase(Phase.EARLY)),
    SPEEDUP_LEAF_DECAY(new MixinBuilder()
            .addCommonMixins("minecraft.MixinBlockLeaves_BFSDecay")
            .setApplyIf(() -> SpeedupsConfig.speedupLeafDecay)
            .setPhase(Phase.EARLY)),
    ASYNC_ICON_LOADING(new MixinBuilder()
            .addClientMixins("minecraft.fastload.MixinTextureMap_AsyncIcons")
            .setApplyIf(() -> SpeedupsConfig.asyncIconLoading)
            .addExcludedMod(TargetedMod.NOTFINE)
            .addExcludedMod(TargetedMod.ANGELICA_LT_2158)
            .setPhase(Phase.EARLY)),
    REMOVE_EXTRA_ICON_LOAD(new MixinBuilder()
            .addClientMixins("minecraft.fastload.MixinTextureMap_RemoveExtraIconLoad")
            .setApplyIf(() -> SpeedupsConfig.removeExtraIconLoad)
            .setPhase(Phase.EARLY)),
    SPEEDUP_BOP_LEAF_DECAY(new MixinBuilder()
            .addCommonMixins(
                    "bfsleafdecay.MixinBlockBOPAppleLeaves",
                    "bfsleafdecay.MixinBlockBOPColorizedLeaves",
                    "bfsleafdecay.MixinBlockBOPLeaves",
                    "bfsleafdecay.MixinBlockBOPPermsimmonLeaves")
            .setApplyIf(() -> SpeedupsConfig.speedupLeafDecay)
            .addRequiredMod(TargetedMod.BOP)
            .setPhase(Phase.LATE)),
    SPEEDUP_PAM_NETHER_LEAF_DECAY(new MixinBuilder()
            .addCommonMixins("bfsleafdecay.MixinBlockNetherLeaves")
            .setApplyIf(() -> SpeedupsConfig.speedupLeafDecay)
            .addRequiredMod(TargetedMod.HARVESTTHENETHER)
            .setPhase(Phase.LATE)),
    SPEEDUP_THAUMCRAFT_LEAF_DECAY(new MixinBuilder()
            .addCommonMixins("bfsleafdecay.MixinBlockMagicalLeaves")
            .setApplyIf(() -> SpeedupsConfig.speedupLeafDecay)
            .addRequiredMod(TargetedMod.THAUMCRAFT)
            .setPhase(Phase.LATE)),
    SPEEDUP_WITCHERY_LEAF_DECAY(new MixinBuilder()
            .addCommonMixins("bfsleafdecay.MixinBlockWitchLeaves")
            .setApplyIf(() -> SpeedupsConfig.speedupLeafDecay)
            .addRequiredMod(TargetedMod.WITCHERY)
            .setPhase(Phase.LATE)),
    SPEEDUP_FALLING_BLOCK_TICK(new MixinBuilder("Skip useless falling block tick scheduling")
            .addCommonMixins("minecraft.MixinBlockFalling_SkipUselessTick")
            .setApplyIf(() -> SpeedupsConfig.skipUselessFallingBlockTicks)
            .setPhase(Phase.EARLY)),
    SPEEDUP_PENDING_TICK_LOOKUP(new MixinBuilder("Spatial index for pending block updates")
            .addCommonMixins("minecraft.MixinWorldServer_PendingTickIndex")
            .setApplyIf(() -> SpeedupsConfig.speedupPendingTickLookup)
            .addExcludedMod(TargetedMod.BUKKIT)
            .setPhase(Phase.EARLY)),
    SPEEDUP_CHUNK_UNLOAD(new MixinBuilder("Optimized chunk unloading with fastutil collections")
            .addCommonMixins("minecraft.fastload.MixinChunkProviderServer_FastUnload")
            .setApplyIf(() -> SpeedupsConfig.speedupChunkUnload)
            .addExcludedMod(TargetedMod.BUKKIT)
            .addExcludedMod(TargetedMod.ULTRAMINE)
            .setPhase(Phase.EARLY)),
    ENTITY_CHUNK_LOAD_GUARD(new MixinBuilder("Prevent entity ticks from triggering chunk generation")
            .addCommonMixins(
                    "minecraft.fastload.MixinChunkProviderServer_EntityGuard",
                    "minecraft.fastload.ServerConfigurationManager_FixTeleporter")
            .setApplyIf(() -> SpeedupsConfig.preventEntityChunkLoading)
            .addExcludedMod(TargetedMod.BUKKIT)
            .setPhase(Phase.EARLY)),
    ENTITY_CHUNK_LOAD_GUARD_BUKKIT(new MixinBuilder("Prevent entity ticks from triggering chunk generation (Bukkit)")
            .addCommonMixins(
                    "minecraft.fastload.MixinChunkProviderServer_EntityGuard_Bukkit",
                    "minecraft.fastload.ServerConfigurationManager_FixTeleporter")
            .setApplyIf(() -> SpeedupsConfig.preventEntityChunkLoading)
            .addRequiredMod(TargetedMod.BUKKIT)
            .setPhase(Phase.EARLY)),
    THROTTLE_CHUNK_GENERATION(new MixinBuilder("Spread chunk generation across ticks to prevent lag spikes")
            .addCommonMixins(
                    "minecraft.fastload.MixinMinecraftServer_TickStart",
                    "minecraft.fastload.MixinChunkProviderServer_DeferPopulation",
                    "minecraft.fastload.MixinPlayerManager_ThrottleChunkGen",
                    "minecraft.fastload.MixinChunk_SendWithoutPopulation")
            .setApplyIf(() -> SpeedupsConfig.throttleChunkGeneration)
            .setPhase(Phase.EARLY)),
    FAST_CHUNK_SENDING(new MixinBuilder("Removes hard caps on chunk sending speed")
            .addCommonMixins("minecraft.fastload.MixinEntityPlayerMP")
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
    MOVED_TOO_QUICKLY_THRESHOLD(new MixinBuilder("Override the 'moved too quickly' server-side speed check threshold")
            .addCommonMixins("minecraft.MixinNetHandlerPlayServer_DisableMovedTooQuickly")
            .setApplyIf(() -> FixesConfig.movedTooQuicklyThreshold > 100.0D)
            .setPhase(Phase.EARLY)),
    FIX_ITEM_BOUNCING(new MixinBuilder("Fixes items bouncing on stairs and other blocks with odd hitboxes")
            .addCommonMixins("minecraft.MixinEntityItem_BouncingFix")
            .setApplyIf(() -> FixesConfig.fixEntityBouncing)
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
    CULL_DISTANT_ITEM_FRAME_CONTENTS(new MixinBuilder("Skip rendering distant item frames and their contents")
            .addClientMixins("minecraft.MixinRenderItemFrame_CullDistantContents")
            .setApplyIf(() -> SpeedupsConfig.cullDistantItemFrameContents)
            .setPhase(Phase.EARLY)),
    FIX_FENCE_RIGHT_CLICK(new MixinBuilder()
            .addCommonMixins("minecraft.MixinBlockFence_RightClick")
            .setApplyIf(() -> FixesConfig.fixFenceRightClick)
            .setPhase(Phase.EARLY)),
    FIX_ENTITY_GRAVITY(new MixinBuilder("Fixes entity having buggy gravity")
            .addClientMixins("minecraft.MixinEntityLivingBase_GravityFix")
            .setApplyIf(() -> FixesConfig.fixEntityGravity)
            .setPhase(Phase.EARLY)),
    BETTER_MOD_LIST(new MixinBuilder()
            .addClientMixins(
                    "fml.MixinGuiModList",
                    "fml.MixinGuiScrollingList",
                    "fml.AccessorGuiScrollingList",
                    "fml.AccessorGuiSlotModList")
            .setApplyIf(() -> TweaksConfig.betterModList)
            .addExcludedMod(TargetedMod.ENDERCORE_WITH_MODLIST)
            .setPhase(Phase.EARLY)),
    SCROLL_LIST_API(new MixinBuilder("Expose GuiScrollingList scroll state through IScrollList")
            .addClientMixins("fml.MixinGuiScrollingList_ScrollList")
            .setApplyIf(() -> TweaksConfig.betterModList || TweaksConfig.fixScrollingLists)
            .setPhase(Phase.EARLY)),
    FIX_SCROLLING_LISTS(new MixinBuilder("Handle scrolling list input in mouse events and anchor scroll bar drag to the pressed point")
            .addClientMixins(
                    "minecraft.MixinGuiScreen_ScrollInput",
                    "minecraft.MixinGuiSlot_ScrollInput",
                    "fml.MixinGuiScrollingList_ScrollInput")
            .setApplyIf(() -> TweaksConfig.fixScrollingLists)
            .setPhase(Phase.EARLY)),
    FIX_ARROW_OWNER(new MixinBuilder("Fix arrow forgetting its owner upon owner reloading (MC-59626) to allow enchantments and HEE charms to work")
            .addCommonMixins("minecraft.MixinEntityArrow")
            .setApplyIf(() -> FixesConfig.fixArrowOwner)
            .setPhase(Phase.EARLY)),
    FIX_EGG_PARTICLE(new MixinBuilder("Use correct egg particles instead of snowball ones (MC-7807)")
            .addClientMixins("minecraft.MixinEntityEgg")
            .setApplyIf(() -> FixesConfig.fixEggParticles)
            .setPhase(Phase.EARLY)),
    FIX_EVENTBUS_MEMORY_LEAK(new MixinBuilder("Fix EventBus keeping object references after unregistering event handlers.")
            .addCommonMixins(
                    "memory.MixinListenerListInst",
                    "memory.MixinEventBus")
            .setApplyIf(() -> MemoryConfig.leaks.fixEventBusMemoryLeak)
            .setPhase(Phase.EARLY)),
    ADD_HUNGER_GAMERULE(new MixinBuilder()
            .addCommonMixins(
                    "minecraft.MixinEntityPlayer_HungerRule",
                    "minecraft.MixinFoodStats_HungerRule",
                    "minecraft.MixinGameRules_HungerRule")
            .setApplyIf(() -> TweaksConfig.hungerGameRule)
            .setPhase(Phase.EARLY)),
    DEBUG_EVENT_REGISTRATION(new MixinBuilder()
            .addCommonMixins("debug.MixinEventBus_DebugRegistration")
            .setApplyIf(() -> Boolean.getBoolean("hodgepodge.logEventTimes"))
            .setPhase(Phase.EARLY)),
    DEBUG_DUMP_TEXTURES_SIZES(new MixinBuilder()
            .addClientMixins("debug.MixinDynamicTexture", "debug.MixinTextureAtlasSprite")
            .setApplyIf(() -> Boolean.getBoolean("hodgepodge.debugtextures"))
            .setPhase(Phase.EARLY)),
    FIX_HOUSE_CHAR_RENDERING(new MixinBuilder()
            .addClientMixins("minecraft.MixinFontRenderer_House")
            .setApplyIf(() -> FixesConfig.fixHouseCharRendering)
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
    SPEEDUP_TILE_DESCRIPTION_PACKETS_NBT(new MixinBuilder("Optimize S35PacketUpdateTileEntity Array Packets")
            .addCommonMixins("minecraft.packets.MixinS35PacketUpdateTileEntity_ByteArray")
            .setApplyIf(() -> SpeedupsConfig.directTileEntityArraySerialization)
            .setPhase(Phase.EARLY)),
    HIDE_DEPRECATED_ID_NOTICE(new MixinBuilder()
            .addClientMixins("minecraft.MixinHideDeprecatedIdNotice")
            .setApplyIf(() -> TweaksConfig.hideDeprecatedIdNotice)
            .setPhase(Phase.EARLY)),
    FIX_PISTON_OUT_OF_BOUNDS(new MixinBuilder()
            .addCommonMixins("minecraft.MixinTileEntityPiston", "minecraft.MixinBlockPistonBase", "minecraft.MixinItemPiston")
            .setApplyIf(() -> FixesConfig.fixInvalidPistonCrashes)
            .setPhase(Phase.EARLY)),
    IMPROVE_REDSTONE_HITBOX(new MixinBuilder("Add a accurate hitbox to the redstone wire")
            .addClientMixins("minecraft.MixinBlockRedstoneWire_Hitbox")
            .setApplyIf(() -> TweaksConfig.improvedRedstoneWireHitbox)
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
    ANVIL_MAX_LEVEL(new MixinBuilder()
            .addCommonMixins("minecraft.MixinContainerRepair_MaxAnvilCost")
            .addClientMixins("minecraft.MixinGuiRepair_MaxAnvilCost")
            .setApplyIf(() -> TweaksConfig.anvilMaxLevel != 40)
            .setPhase(Phase.EARLY)),
    PREVENT_MOUSE_CENTERING_ON_ESC_IN_GUIS(new MixinBuilder("Prevent moving mouse cursor to the center when pressing Esc in GUIs")
            .addClientMixins("minecraft.MixinGuiScreen_PreventDoubleMouseGrabbing")
            .setApplyIf(() -> FixesConfig.preventMouseCenteringOnEscInGUIs)
            .setPhase(Phase.EARLY)),
    CLEAR_STALE_LOADING_SCREEN_INPUT(new MixinBuilder("Clear stale clicks made on the loading screen and prevent them from firing in the main menu")
            .addClientMixins("minecraft.MixinMinecraft_ClearStaleLoadingScreenInput")
            .setApplyIf(() -> FixesConfig.clearStaleLoadingScreenInput).setPhase(Phase.EARLY)),
    FIX_BREAKING_SPECIAL_ARMOR_WITH_THORNS_ENCHANTMENT(new MixinBuilder("Fix breaking electric and other special armor when Thorns enchantment is applied")
            .addCommonMixins("minecraft.MixinEnchantmentThorns_FixBreakingSpecialArmor")
            .setApplyIf(() -> FixesConfig.fixBreakingSpecialArmorWithThornsEnchantment)
            .setPhase(Phase.EARLY)),
    FIX_BREAKING_SPECIAL_ARMOR_ON_BLOCK_FALL(new MixinBuilder("Fix breaking electric and other special armor helmet when a block falls on your head")
            .addCommonMixins("minecraft.MixinEntityLivingBase_FixBreakingSpecialArmor")
            .setApplyIf(() -> FixesConfig.fixBreakingSpecialArmorHelmetOnBlockFall)
            .setPhase(Phase.EARLY)),
    FIX_SAVE_FILE_WRITTEN_TO_EXIST_DIRECTORY(new MixinBuilder()
            .addClientMixins("minecraft.MixinGuiCreateWorld_NotWriteToExistDir")
            .setApplyIf(() -> FixesConfig.fixSaveFileWrittenToExistingDirectory)
            .setPhase(Phase.EARLY)),
    FIX_FLAT_WORLD_LAYER_MISSING_ITEM_BLOCK_CRASH(new MixinBuilder()
            .addClientMixins("minecraft.MixinGuiCreateFlatWorld_FixItemCrash")
            .setApplyIf(() -> FixesConfig.fixFlatWorldLayerMissingItemBlockCrash)
            .setPhase(Phase.EARLY)),
    FIX_FAKE_PLAYER_CHAT_CRASH(new MixinBuilder()
            .addCommonMixins("forge.MixinFakePlayer")
            .setApplyIf(() -> FixesConfig.fixFakePlayerChatCrash)
            .setPhase(Phase.EARLY)),
    OPTIMIZE_WAVEFRONT_OBJECT_MODEL_LOADING(new MixinBuilder("Reduce regex overhead when loading object models")
            .addClientMixins("forge.MixinWavefrontObject_OptimizeModelLoading")
            .setApplyIf(() -> SpeedupsConfig.optimizeWavefrontObjectModelLoading)
            .setPhase(Phase.EARLY)),
    CACHE_ADVANCED_MODEL_LOADER(new MixinBuilder()
            .addClientMixins("forge.MixinAdvancedModelLoader_CacheModels")
            .setApplyIf(() -> MemoryConfig.allocs.cacheAdvancedModels)
            .setPhase(Phase.EARLY)),
    FIX_FORGE_PLAYER_LEAK(new MixinBuilder()
            .addCommonMixins("memory.MixinFakePlayerFactory_FixLeak")
            .setApplyIf(() -> MemoryConfig.leaks.fixForgePlayerFactoryLeak)
            .setPhase(Phase.EARLY)),
    INTERN_ASMDATATABLE_STRINGS(new MixinBuilder()
            .addCommonMixins(
                    "fml.MixinEnumHolder_Intern",
                    "fml.MixinModAnnotation_Intern",
                    "fml.MixinASMData_Intern")
            .setApplyIf(() -> MemoryConfig.allocs.deduplicateASMDataTableStrings)
            .setPhase(Phase.EARLY)),
    INTERN_RESOURCE_LOCATION_DOMAIN(new MixinBuilder()
            .addClientMixins("memory.MixinResourceLocation_Intern")
            .setApplyIf(() -> MemoryConfig.allocs.internResourceLocationDomain)
            .setPhase(Phase.EARLY)),
    INTERN_UNIQUE_IDENTIFIER_MODID(new MixinBuilder()
            .addClientMixins("fml.MixinUniqueIdentifier_Intern")
            .setApplyIf(() -> MemoryConfig.allocs.internUniqueIdentifierModid)
            .setPhase(Phase.EARLY)),
    FIX_MULTIPLE_ENCHANT_GLINT(new MixinBuilder()
            .addClientMixins("minecraft.MixinItem_EnchantGlint")
            .setApplyIf(() -> FixesConfig.fixMultipleEnchantGlint)
            .setPhase(Phase.EARLY)),
    CLIP_PLAYER_IN_INVENTORY(new MixinBuilder()
            .addClientMixins("minecraft.MixinGuiInventory_ClipPlayer", "minecraft.MixinGuiContainerCreative_ClipPlayer", "minecraft.MixinGuiScreenHorseInventory_ClipPlayer")
            .setApplyIf(() -> FixesConfig.clipPlayerRenderInGuis)
            .setPhase(Phase.EARLY)),
    MOVE_INVENTORY_CRAFTING_GRID(new MixinBuilder("Moves the player inventory crafting grid and uses a new inventory texture")
            .addClientMixins("minecraft.MixinGuiInventory_MoveCraftingGrid", "minecraft.MixinContainerPlayer_MoveCraftingGrid")
            .setApplyIf(() -> TweaksConfig.moveInventoryCraftingGrid)
            .setPhase(Phase.EARLY)),
    FIX_NEGATIVE_LOOTING_CRASH(new MixinBuilder()
            .addCommonMixins("minecraft.crashfixes.MixinEnchantmentHelper")
            .setApplyIf(() -> FixesConfig.minLootingIsZero)
            .setPhase(Phase.EARLY)),
    FIX_VILLAGER_TRADING_DESYNC(new MixinBuilder("Fix Villagers only updating out-of-stock state after reopening GUI")
            .addCommonMixins(
                    "minecraft.villager.AccessorMerchantRecipe",
                    "minecraft.villager.MixinMerchantRecipeList_WritePacketData")
            .addClientMixins(
                    "minecraft.villager.MixinMerchantRecipeList_ReadPacketData",
                    "minecraft.villager.MixinNpcMerchant")
            .setApplyIf(() -> FixesConfig.fixVillagerTradingDesync)
            .setPhase(Phase.EARLY)),
    FIX_UNDEAD_FIRE_FLICKER(new MixinBuilder("Fix undead entities flickering with fire when exposed to the sun while immune to fire")
            .addCommonMixins("minecraft.MixinEntityUndead_SunFireImmunity")
            .setApplyIf(() -> FixesConfig.preventFireImmuneUndeadFlicker)
            .setPhase(Phase.EARLY)),
    FIX_ITEM_FRAME_DUPE(new MixinBuilder("Fix vanilla item frame duplication.")
            .addCommonMixins("minecraft.MixinEntityItemFrame_FixDupe")
            .setApplyIf(() -> FixesConfig.fixItemFrameDupe)
            .setPhase(Phase.EARLY)),
    FIX_ENTITY_NAME_LOCALIZATION(new MixinBuilder()
            .addCommonMixins(
                    "minecraft.MixinEntity_TranslateNameComponent",
                    "minecraft.MixinEntityHorse_ChatComponentName",
                    "minecraft.MixinEntityItem_ChatComponentName",
                    "minecraft.MixinEntityLiving_ChatComponentName",
                    "minecraft.MixinEntityMinecart_ChatComponentName",
                    "minecraft.MixinEntityOcelot_ChatComponentName")
            .setApplyIf(() -> FixesConfig.entityNameLocalization)
            .setPhase(Phase.EARLY)),
    WITHER_SKELETON_CUSTOM_NAME(new MixinBuilder()
            .addCommonMixins("minecraft.MixinEntitySkeleton_CustomWitherName")
            .setApplyIf(() -> FixesConfig.witherSkeletonSpecialName)
            .setPhase(Phase.EARLY)),
    FML_QUERY_SCREEN_FPS(new MixinBuilder()
            .addCommonMixins("minecraft.MixinMinecraft_FMLQueryFPS")
            .setApplyIf(() -> FixesConfig.raiseMissingItemsFPS)
            .setPhase(Phase.EARLY)),
    FIX_BLOCK_HIT_DELAY(new MixinBuilder()
            .addClientMixins("minecraft.MixinPlayerControllerMP_BlockHitDelay")
            .setApplyIf(() -> FixesConfig.fixBlockHitDelay)
            .setPhase(Phase.EARLY)),
    OPTIMIZE_RESOURCE_PACK_PATH(new MixinBuilder("Avoid String.format overhead when resolving resource pack paths")
            .addClientMixins("minecraft.MixinAbstractResourcePack")
            .setApplyIf(() -> SpeedupsConfig.optimizeResourcePackPath)
            .setPhase(Phase.EARLY)),
    FURNACE_SAVE_ITEM_BURN_TIME_TO_NBT(new MixinBuilder()
            .addCommonMixins("minecraft.MixinFurnaceSaveItemBurnTimeToNBT")
            .setApplyIf(() -> FixesConfig.furnaceSaveItemBurnTimeToNBT)
            .setPhase(Phase.EARLY)),
    TRACK_INCOMING_PACKETS(new MixinBuilder("Track incoming packets for /packetstats")
            .addClientMixins("debug.MixinNetworkManager_TrackIncomingPackets")
            .setApplyIf(() -> DebugConfig.trackIncomingPackets)
            .setPhase(Phase.EARLY)),
    CHANGE_RECEIVED_PACKET_COUNT_LIMIT_TO_TIME_LIMIT(new MixinBuilder("Change received packet count limit to time limit")
            .addClientMixins("minecraft.packets.MixinNetworkManager_ReceivedProcessingLimit")
            .setApplyIf(() -> FixesConfig.changeReceivedPacketCountLimitToTimeLimit)
            .setPhase(Phase.EARLY)),
    // endregion

    // region Ic2 adjustments
    FIX_TESR_LEAK(new MixinBuilder()
            .addClientMixins("ic2.leaks.MixinOverlayTesr")
            .setApplyIf(() -> MemoryConfig.leaks.fixIC2TESRleak)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.LATE)),
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
            .addCommonMixins(
                    "ic2.MixinTileEntityReactorChamberElectricNoDupe",
                    "ic2.MixinTileEntityNuclearReactorElectric_RefreshChambers")
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
    LOCALIZE_IC2_NEI_TITLES(new MixinBuilder("Localize IC2 NEI recipe handler titles")
            .addClientMixins("ic2.MixinIc2NeiHandlerTitles").addRequiredMod(TargetedMod.IC2).setPhase(Phase.LATE)),
    LOCALIZE_EXTRA_UTILITIES_NEI_TITLES(new MixinBuilder("Localize Extra Utilities NEI recipe handler titles")
            .addClientMixins("extrautilities.MixinExuNeiHandlerTitles").addRequiredMod(TargetedMod.EXTRA_UTILITIES)
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
    FIX_DIVISION_SIGIL_ACTIVATION(new MixinBuilder("Fix Division Sigil not activating with enchantment tables and beacons from other mods")
            .addCommonMixins(
                    "extrautilities.MixinEventHandlerServer",
                    "extrautilities.MixinEventHandlerSiege",
                    "extrautilities.MixinItemDivisionSigil")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesDivisionSigilActivation)
            .addExcludedMod(TargetedMod.ARCHAICFIX)
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
    FIX_WITCHERY_ENUM_VALUES_SPAM(new MixinBuilder()
            .addCommonMixins("witchery.MixinExtendedPlayer_EnumValuesSpam")
            .setApplyIf(() -> MemoryConfig.allocs.fixWitcheryEnumValuesSpam)
            .addRequiredMod(TargetedMod.WITCHERY)
            .setPhase(Phase.LATE)),
    WITCHERY_EID_COMPAT(new MixinBuilder()
            .addCommonMixins("witchery.MixinRiteClimateChange_EIDCompat")
            .setApplyIf(() -> FixesConfig.fixWitcheryEid)
            .addRequiredMod(TargetedMod.WITCHERY)
            .addRequiredMod(TargetedMod.ENDLESSIDS)
            .setPhase(Phase.LATE)),
    FIX_WITCHERY_HOTBAR_SELECTION_HEIGHT(new MixinBuilder("Draw the full selection frame on Witchery's vampire hotbar")
            .addClientMixins("witchery.MixinClientEvents_HotbarSelectionHeight")
            .setApplyIf(() -> FixesConfig.fixHotbarSelectionHeight)
            .addRequiredMod(TargetedMod.WITCHERY)
            .setPhase(Phase.LATE)),

    // Various Exploits/Fixes
    BIBLIOCRAFT_PACKET_FIX(new MixinBuilder("Packet Fix")
            .addCommonMixins(
                    "bibliocraft.MixinBibliocraftPatchPacketExploits",
                    "bibliocraft.MixinContainerFancySign")
            .setApplyIf(() -> FixesConfig.fixBibliocraftPackets)
            .addRequiredMod(TargetedMod.BIBLIOCRAFT)
            .setPhase(Phase.LATE)),
    BIBLIOCRAFT_ARMOR_STAND_BREAK_FIX(new MixinBuilder("Bibliocraft Armor Stand on-break-block fix")
            .addCommonMixins("bibliocraft.MixinBlockArmorStand_CheckedBreak")
            .setApplyIf(() -> FixesConfig.fixBibliocraftArmorStandBreak)
            .addRequiredMod(TargetedMod.BIBLIOCRAFT)
            .setPhase(Phase.LATE)),
    BIBLIOCRAFT_INVENTORIES_MARK_DIRTY(new MixinBuilder("Mark Bibliocraft inventories to save when they are modified")
            .addCommonMixins("bibliocraft.MixinBibliocraftInventories_MarkDirty")
            .setApplyIf(() -> FixesConfig.bibliocraftInventoriesMarkDirty)
            .addRequiredMod(TargetedMod.BIBLIOCRAFT)
            .setPhase(Phase.LATE)),
    BIBLIOCRAFT_PATH_SANITIZATION_FIX(new MixinBuilder("Path sanitization fix")
            .addCommonMixins("bibliocraft.MixinPathSanitization")
            .setApplyIf(() -> FixesConfig.fixBibliocraftPathSanitization)
            .addRequiredMod(TargetedMod.BIBLIOCRAFT)
            .setPhase(Phase.LATE)),
    BIBLIOCRAFT_NO_PAUSE_GUI_CLIPBOARD(new MixinBuilder()
            .addCommonMixins("bibliocraft.MixinGuiClipboard_NoPause")
            .setApplyIf(() -> FixesConfig.noPauseGuiClipboard)
            .addRequiredMod(TargetedMod.BIBLIOCRAFT)
            .setPhase(Phase.LATE)),
    BIBLIOCRAFT_PAINTING_UTIL_FIX(new MixinBuilder("PaintingUtil jar Path Fix")
            .addCommonMixins("bibliocraft.MixinPaintingUtil")
            .setApplyIf(() -> FixesConfig.fixBibliocraftPaintingUtilPath)
            .addRequiredMod(TargetedMod.BIBLIOCRAFT)
            .setPhase(Phase.LATE)),
    BIBLIOCRAFT_FIX_TESR_WORLD_LEAK(new MixinBuilder()
            .addClientMixins(
                    "bibliocraft.leaks.MixinTileEntityArmorStandRenderer",
                    "bibliocraft.leaks.MixinTileEntityClipboardRenderer",
                    "bibliocraft.leaks.MixinTileEntityClockRenderer",
                    "bibliocraft.leaks.MixinTileEntityFancySignRenderer",
                    "bibliocraft.leaks.MixinTileEntityFancyWorkbenchRenderer",
                    "bibliocraft.leaks.MixinTileEntityFurniturePanelerRenderer",
                    "bibliocraft.leaks.MixinTileEntityLampRenderer",
                    "bibliocraft.leaks.MixinTileEntityLanternRenderer",
                    "bibliocraft.leaks.MixinTileEntityPaintingRenderer",
                    "bibliocraft.leaks.MixinTileEntityPaintPressRenderer",
                    "bibliocraft.leaks.MixinTileEntityPotionShelfRenderer",
                    "bibliocraft.leaks.MixinTileEntityPrintPressRenderer",
                    "bibliocraft.leaks.MixinTileEntitySwordPedestalRenderer",
                    "bibliocraft.leaks.MixinTileEntityTableRenderer",
                    "bibliocraft.leaks.MixinTileEntityTypeSetRenderer",
                    "bibliocraft.leaks.MixinTileEntityTypewriterRenderer")
            .setApplyIf(() -> MemoryConfig.leaks.fixBibliocraftTESRWorldLeak)
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
