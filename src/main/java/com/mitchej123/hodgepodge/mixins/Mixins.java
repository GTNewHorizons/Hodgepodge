package com.mitchej123.hodgepodge.mixins;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhlib.mixin.IMixins;
import com.gtnewhorizon.gtnhlib.mixin.MixinBuilder;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.ASMConfig;
import com.mitchej123.hodgepodge.config.DebugConfig;
import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;

public enum Mixins implements IMixins {

    // spotless:off
    // Vanilla Fixes
    ONLY_LOAD_LANGUAGES_ONCE_PER_FILE(new MixinBuilder("Only load languages once per file").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinLanguageRegistry").setApplyIf(() -> FixesConfig.onlyLoadLanguagesOnce)),
    CHANGE_CATEGORY_SPRINT_KEY(new MixinBuilder("Moves the sprint keybind to the movement category").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinGameSettings_SprintKey").setApplyIf(() -> TweaksConfig.changeSprintCategory)),
    FIX_TOO_MANY_ALLOCATIONS_CHUNK_POSITION_INT_PAIR(new MixinBuilder("Stops MC from allocating too many ChunkPositionIntPair objects").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinChunkCoordIntPair_FixAllocations", "minecraft.MixinWorld_FixAllocations", "minecraft.MixinAnvilChunkLoader_FixAllocations").setApplyIf(() -> FixesConfig.fixTooManyAllocationsChunkPositionIntPair)),
    FIX_TOO_MANY_ALLOCATIONS_CHUNK_POSITION_INT_PAIR_CLIENT(new MixinBuilder("Stops MC from allocating too many ChunkPositionIntPair objects").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinWorldClient_FixAllocations").setApplyIf(() -> FixesConfig.fixTooManyAllocationsChunkPositionIntPair)),
    FIX_TOO_MANY_ALLOCATIONS_CHUNK_POSITION_INT_PAIR_OPTIFINE_INCOMPAT(new MixinBuilder("Stops MC from allocating too many ChunkPositionIntPair objects").addExcludedMod(TargetedMod.OPTIFINE).setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinWorldServer_FixAllocations").setApplyIf(() -> FixesConfig.fixTooManyAllocationsChunkPositionIntPair)),
    ADD_SIMULATION_DISTANCE_OPTION(new MixinBuilder("Add option to separate simulation distance from render distance").setPhase(Phase.EARLY).addExcludedMod(TargetedMod.OPTIFINE).addExcludedMod(TargetedMod.ULTRAMINE).addCommonMixins("minecraft.MixinWorld_SimulationDistance", "minecraft.MixinWorldServer_SimulationDistance", "minecraft.MixinChunk_SimulationDistance").setApplyIf(() -> FixesConfig.addSimulationDistance)),
    ADD_SIMULATION_DISTANCE_OPTION_THERMOS_FIX(new MixinBuilder("Add option to separate simulation distance from render distance (Thermos fix)").setPhase(Phase.EARLY).addExcludedMod(TargetedMod.OPTIFINE).addExcludedMod(TargetedMod.ULTRAMINE).addCommonMixins("minecraft.MixinWorldServer_SimulationDistanceThermosFix").setApplyIf(() -> FixesConfig.addSimulationDistance && Common.thermosTainted)),
    FIX_RESOURCEPACK_FOLDER_OPENING(new MixinBuilder("Fix resource pack folder sometimes not opening on windows").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinGuiScreenResourcePacks").setApplyIf(() -> FixesConfig.fixResourcePackOpening)),
    FIX_ENCHANTMENT_LEVEL_NUMERALS(new MixinBuilder("Fix enchantment levels not displaying properly above a certain value").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinEnchantment_FixRomanNumerals").setApplyIf(() -> FixesConfig.fixEnchantmentNumerals || TweaksConfig.arabicNumbersForEnchantsPotions)),
    FIX_CONTAINER_PUT_STACKS_IN_SLOTS(new MixinBuilder("Prevents crash if server sends container with wrong itemStack size").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinContainer").setApplyIf(() -> FixesConfig.fixContainerPutStacksInSlots)),
    FIX_CONTAINER_SHIFT_CLICK_RECURSION(new MixinBuilder("Backports 1.12 logic for shift clicking slots to prevent recursion").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinContainer_FixShiftRecursion").setApplyIf(() -> FixesConfig.fixContainerShiftClickRecursion)),
    FIX_NETHANDLERPLAYCLIENT_HANDLE_SET_SLOT(new MixinBuilder("Prevents crash if server sends itemStack with index larger than client's container").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinNetHandlerPlayClient_FixHandleSetSlot").setApplyIf(() -> FixesConfig.fixNetHandlerPlayClientHandleSetSlot)),
    FIX_NETHANDLERLOGINSERVER_OFFLINEMODE(new MixinBuilder("Allows the server to assign the logged in UUID to the same username when online_mode is false").setPhase(Phase.EARLY).addServerMixins("minecraft.MixinNetHandlerLoginServer_OfflineMode").setApplyIf(() -> FixesConfig.fixNetHandlerLoginServerOfflineMode)),
    FIX_INVENTORY_POTION_EFFECT_NUMERALS(new MixinBuilder("Fix potion effects level not displaying properly above a certain value").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinInventoryEffectRenderer_FixPotionEffectNumerals", "minecraft.MixinItemPotion_FixRomanNumerals").setApplyIf(() -> FixesConfig.fixPotionEffectNumerals || TweaksConfig.arabicNumbersForEnchantsPotions)),
    FIX_HASTE_ARM_SWING_ANIMATION(new MixinBuilder("Fix arm not swinging when having too much haste").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinEntityLivingBase_FixHasteArmSwing").setApplyIf(() -> FixesConfig.fixHasteArmSwing)),
    DISABLE_REALMS_BUTTON(new MixinBuilder("Disable Realms button in main menu").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinGuiMainMenu_DisableRealmsButton").setApplyIf(() -> TweaksConfig.disableRealmsButton)),
    ADD_TIME_GET(new MixinBuilder("Add /time get command").addCommonMixins("minecraft.MixinCommandTime").setApplyIf(() -> TweaksConfig.addTimeGet).setPhase(Phase.EARLY)),
    OPTIMIZE_WORLD_UPDATE_LIGHT(new MixinBuilder("Optimize world updateLightByType method").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinWorld_FixLightUpdateLag").addExcludedMod(TargetedMod.ARCHAICFIX).addExcludedMod(TargetedMod.ANGELICA).setApplyIf(() -> FixesConfig.optimizeWorldUpdateLight)),
    FIX_FRIENDLY_CREATURE_SOUNDS(new MixinBuilder("Fix Friendly Creature Sounds").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinSoundHandler").setApplyIf(() -> FixesConfig.fixFriendlyCreatureSounds)),
    LOGARITHMIC_VOLUME_CONTROL(new MixinBuilder("Logarithmic Volume Control").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinSoundManager", "minecraft.MixinSoundManagerLibraryLoader").setApplyIf(() -> FixesConfig.logarithmicVolumeControl)),
    THROTTLE_ITEMPICKUPEVENT(new MixinBuilder("Throttle Item Pickup Event").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinEntityPlayer_ThrottlePickup").setApplyIf(() -> FixesConfig.throttleItemPickupEvent)),
    ADD_THROWER_TO_DROPPED_ITEM(new MixinBuilder("Adds the thrower tag to all dropped EntityItems").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinEntityPlayer_ItemThrower").setApplyIf(() -> FixesConfig.addThrowerTagToDroppedItems)),
    SYNC_ITEM_THROWER_COMMON(new MixinBuilder("Synchonize from server to client the thrower and pickup delay of an item entity").setPhase(Phase.EARLY).addCommonMixins("minecraft.packets.MixinS0EPacketSpawnObject_ItemThrower").setApplyIf(() -> FixesConfig.syncItemThrower)),
    SYNC_ITEM_THROWER_CLIENT(new MixinBuilder("Synchonize from server to client the thrower and pickup delay of an item entity").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinNetHandlerPlayClient_ItemThrower").setApplyIf(() -> FixesConfig.syncItemThrower)),
    FIX_PERSPECTIVE_CAMERA(new MixinBuilder("Camera Perspective Fix").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinEntityRenderer").addExcludedMod(TargetedMod.ARCHAICFIX).addExcludedMod(TargetedMod.ANGELICA).setApplyIf(() -> FixesConfig.fixPerspectiveCamera)),
    FIX_DEBUG_BOUNDING_BOX(new MixinBuilder("Fix Bounding Box").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinRenderManager").setApplyIf(() -> FixesConfig.fixDebugBoundingBox)),
    FENCE_CONNECTIONS_FIX(new MixinBuilder("Fix Fence Connections").addCommonMixins("minecraft.MixinBlockFence").setPhase(Phase.EARLY).setApplyIf(() -> FixesConfig.fixFenceConnections)),
    FIX_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder("Fix Inventory Offset with Potions").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinInventoryEffectRenderer_PotionOffset").setApplyIf(() -> TweaksConfig.fixPotionRenderOffset)),
    FIX_POTION_EFFECT_RENDERING(new MixinBuilder("Fix Potion Effect Rendering").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinInventoryEffectRenderer_PotionEffectRendering").setApplyIf(() -> TweaksConfig.fixPotionEffectRender)),
    FIX_IMMOBILE_FIREBALLS(new MixinBuilder("Fix Immobile Fireballs").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinEntityFireball").setApplyIf(() -> FixesConfig.fixImmobileFireballs)),
    FIX_REED_PLACING(new MixinBuilder("Fix placement of Sugar Canes").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinItemReed").setApplyIf(() -> FixesConfig.fixSugarCanePlacement)),
    LONGER_CHAT(new MixinBuilder("Longer Chat").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinGuiNewChat_LongerChat").setApplyIf(() -> TweaksConfig.longerChat)),
    TRANSPARENT_CHAT(new MixinBuilder("Transparent Chat").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinGuiNewChat_TransparentChat").setApplyIf(() -> TweaksConfig.transparentChat)),
    FIX_ENTITY_ATTRIBUTES_RANGE(new MixinBuilder("Fix Entity Attributes Range").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinNetHandlerPlayClient_FixEntityAttributesRange").setApplyIf(() -> FixesConfig.fixEntityAttributesRange)),
    ENDERMAN_BLOCK_GRAB_DISABLE(new MixinBuilder("Disable Endermen Grabbing Blocks").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinEntityEndermanGrab").setApplyIf(() -> TweaksConfig.endermanBlockGrabDisable)),
    ENDERMAN_BLOCK_PLACE_DISABLE(new MixinBuilder("Disable Endermen Placing Held Blocks").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinEntityEndermanPlace").setApplyIf(() -> TweaksConfig.endermanBlockPlaceDisable)),
    ENDERMAN_BLOCK_PLACE_BLACKLIST(new MixinBuilder("Disable Endermen Placing Held Blocks on Configured Blocks").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinEntityEndermanPlaceBlacklist").setApplyIf(() -> TweaksConfig.endermanBlockPlaceBlacklist)),
    WITCH_POTION_METADATA(new MixinBuilder("Fix Metadata of Witch Potions").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinEntityWitch").setApplyIf(() -> TweaksConfig.witchPotionMetadata)),
    // config handled in mixin due to server->client config sync
    LONGER_MESSAGES_CLIENT(new MixinBuilder("Longer Messages Client Side").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinGuiChat_LongerMessages").setApplyIf(() -> true)),
    // config handled in mixin due to server->client config sync
    LONGER_MESSAGES_SERVER(new MixinBuilder("Longer Messages Server Side").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinC01PacketChatMessage_LongerMessages").setApplyIf(() -> true)),
    SPEEDUP_REMOVE_FORMATTING_CODES(new MixinBuilder("Speed up the vanilla method to remove formatting codes").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinEnumChatFormatting_FastFormat").setApplyIf(() -> SpeedupsConfig.speedupRemoveFormatting)),
    SPEEDUP_GRASS_BLOCK_RANDOM_TICKING(new MixinBuilder("Speed up grass block random ticking").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinBlockGrass").setApplyIf(() -> SpeedupsConfig.speedupGrassBlockRandomTicking)),
    SPEEDUP_CHUNK_PROVIDER_CLIENT(new MixinBuilder("Speed up ChunkProviderClient").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinChunkProviderClient_RemoveChunkListing").addExcludedMod(TargetedMod.FASTCRAFT).setApplyIf(() -> SpeedupsConfig.speedupChunkProviderClient && ASMConfig.speedupLongIntHashMap)),
    BETTER_HASHCODES(new MixinBuilder("Optimize various Hashcode").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinChunkCoordinates_BetterHash", "minecraft.MixinChunkCoordIntPair_BetterHash").setApplyIf(() -> SpeedupsConfig.speedupChunkCoordinatesHashCode)),
    TCP_NODELAY(new MixinBuilder("Set TCP NODELAY").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinTcpNoDelay").setApplyIf(() -> SpeedupsConfig.tcpNoDelay)),
    WORLD_UNPROTECTED_GET_BLOCK(new MixinBuilder("Fix world unprotected getBlock").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinWorldGetBlock").setApplyIf(() -> FixesConfig.fixVanillaUnprotectedGetBlock)),
    WORLD_UNPROTECTED_LIGHT_VALUE(new MixinBuilder("Fix world unprotected light value").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinWorldLightValue").setApplyIf(() -> FixesConfig.fixGetBlockLightValue)),
    VILLAGE_UNCHECKED_GET_BLOCK(new MixinBuilder("Fix Village unchecked getBlock").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinVillage", "minecraft.MixinVillageCollection").setApplyIf(() -> FixesConfig.fixVillageUncheckedGetBlock)),
    FORGE_HOOKS_URL_FIX(new MixinBuilder("Fix forge URL hooks").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinForgeHooks").setApplyIf(() -> FixesConfig.fixUrlDetection)),
    FORGE_UPDATE_CHECK_FIX(new MixinBuilder("Fix the forge update checker").setPhase(Phase.EARLY).addCommonMixins("forge.MixinForgeVersion_FixUpdateCheck").setApplyIf(() -> FixesConfig.fixForgeUpdateChecker)),
    FORGE_FIX_CLASS_TYPO(new MixinBuilder("Fix a class name typo in MinecraftForge's initialize method").setPhase(Phase.EARLY).addCommonMixins("forge.MixinMinecraftForge").setApplyIf(() -> FixesConfig.fixEffectRendererClassTypo)),
    NORTHWEST_BIAS_FIX(new MixinBuilder("Fix Northwest Bias").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinRandomPositionGenerator").setApplyIf(() -> FixesConfig.fixNorthWestBias)),
    SPEEDUP_VANILLA_FURNACE(new MixinBuilder("Speedup Vanilla Furnace").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinFurnaceRecipes").setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace).addRequiredMod(TargetedMod.GTNHLIB)),
    GAMEOVER_GUI_LOCKED_DISABLED(new MixinBuilder("Fix Gameover GUI").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinGuiGameOver").setApplyIf(() -> FixesConfig.fixGuiGameOver)),
    PREVENT_PICKUP_LOOT(new MixinBuilder("Prevent monsters from picking up loot").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinEntityLivingPickup").setApplyIf(() -> TweaksConfig.preventPickupLoot)),
    DROP_PICKED_LOOT_ON_DESPAWN(new MixinBuilder("Drop picked up loot on despawn").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinEntityLivingDrop").setApplyIf(() -> TweaksConfig.dropPickedLootOnDespawn)),
    FIX_HOPPER_HIT_BOX(new MixinBuilder("Fix Vanilla Hopper hit box").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinBlockHopper").setApplyIf(() -> FixesConfig.fixHopperHitBox)),
    TILE_RENDERER_PROFILER_DISPATCHER(new MixinBuilder("TileEntity Render Dispatcher Fix").setPhase(Phase.EARLY).addClientMixins("minecraft.profiler.TileEntityRendererDispatcherMixin").setApplyIf(() -> TweaksConfig.enableTileRendererProfiler)),
    TILE_RENDERER_PROFILER_MINECRAFT(new MixinBuilder("Tile Entity Render Profiler").setPhase(Phase.EARLY).addClientMixins("minecraft.profiler.MinecraftMixin").setApplyIf(() -> TweaksConfig.enableTileRendererProfiler)),
    DIMENSION_CHANGE_FIX(new MixinBuilder("Dimension Change Heart Fix").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinServerConfigurationManager", "minecraft.MixinEntityPlayerMP").setApplyIf(() -> FixesConfig.fixDimensionChangeAttributes)),
    FIX_EATING_STACKED_STEW(new MixinBuilder("Stacked Mushroom Stew Eating Fix").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinItemSoup").setApplyIf(() -> FixesConfig.fixEatingStackedStew)),
    INCREASE_PARTICLE_LIMIT(new MixinBuilder("Increase Particle Limit").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinEffectRenderer").setApplyIf(() -> TweaksConfig.increaseParticleLimit)),
    ENLARGE_POTION_ARRAY(new MixinBuilder("Make the Potion array larger").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinPotion").setApplyIf(() -> FixesConfig.enlargePotionArray)),
    FIX_POTION_LIMIT(new MixinBuilder("Fix Potion Limit").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinPotionEffect").setApplyIf(() -> FixesConfig.fixPotionLimit)),
    FIX_HOPPER_VOIDING_ITEMS(new MixinBuilder("Fix Hopper Voiding Items").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinTileEntityHopper").setApplyIf(() -> FixesConfig.fixHopperVoidingItems)),
    FIX_HUGE_CHAT_KICK(new MixinBuilder("Fix huge chat kick").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinS02PacketChat").setApplyIf(() -> FixesConfig.fixHugeChatKick)),
    FIX_BOGUS_INTEGRATED_SERVER_NPE(new MixinBuilder("Fix bogus FMLProxyPacket NPEs on integrated server crashes").setPhase(Phase.EARLY).addCommonMixins("fml.MixinFMLProxyPacket", "fml.MixinNetworkDispatcher", "minecraft.NetworkManagerAccessor").setApplyIf(() -> FixesConfig.fixBogusIntegratedServerNPEs)),
    FIX_LAG_ON_INVENTORY_SYNC(new MixinBuilder("Fix inventory sync lag").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinInventoryCrafting").setApplyIf(() -> FixesConfig.fixInventorySyncLag)),
    FIX_LOGIN_DIMENSION_ID_OVERFLOW(new MixinBuilder("Fix dimension id overflowing when a player first logins on a server").setPhase(Phase.EARLY).addCommonMixins("minecraft.packets.MixinS01PacketJoinGame_FixDimensionID").setApplyIf(() -> FixesConfig.fixLoginDimensionIDOverflow)),
    FIX_WORLD_SERVER_LEAKING_UNLOADED_ENTITIES(new MixinBuilder("Fix world server leaking unloaded entities").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinWorldServerUpdateEntities").setApplyIf(() -> FixesConfig.fixWorldServerLeakingUnloadedEntities)),
    FIX_SKIN_MANAGER_CLIENT_WORLD_LEAK(new MixinBuilder("Fix skin manager client world leak").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinSkinManager$2").setApplyIf(() -> FixesConfig.fixSkinManagerLeakingClientWorld)),
    FIX_REDSTONE_TORCH_WORLD_LEAK(new MixinBuilder("Fix world leak in redstone torch").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinBlockRedstoneTorch").setApplyIf(() -> FixesConfig.fixRedstoneTorchWorldLeak).addExcludedMod(TargetedMod.BUGTORCH)),
    FIX_ARROW_WRONG_LIGHTING(new MixinBuilder("Fix arrow wrong lighting").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinRendererLivingEntity").setApplyIf(() -> FixesConfig.fixGlStateBugs)),
    FIX_RESIZABLE_FULLSCREEN(new MixinBuilder("Fix Resizable Fullscreen").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinMinecraft_ResizableFullscreen").setApplyIf(() -> FixesConfig.fixResizableFullscreen)),
    FIX_UNFOCUSED_FULLSCREEN(new MixinBuilder("Fix Unfocused Fullscreen").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinMinecraft_UnfocusedFullscreen").setApplyIf(() -> FixesConfig.fixUnfocusedFullscreen).addExcludedMod(TargetedMod.ARCHAICFIX)),
    FIX_RENDERERS_WORLD_LEAK(new MixinBuilder("Fix Renderers World Leak").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinMinecraft_ClearRenderersWorldLeak", "minecraft.MixinRenderGlobal_FixWordLeak").setApplyIf(() -> FixesConfig.fixRenderersWorldLeak)),
    FIX_OPTIFINE_CHUNKLOADING_CRASH(new MixinBuilder("Fix Optifine Chunkloading Crash").setPhase(Phase.EARLY).setApplyIf(() -> FixesConfig.fixOptifineChunkLoadingCrash).addRequiredMod(TargetedMod.OPTIFINE).addClientMixins("minecraft.MixinGameSettings_FixOFChunkLoading")),
    ADD_TOGGLE_DEBUG_MESSAGE(new MixinBuilder("Toggle Debug Message").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinMinecraft_ToggleDebugMessage").setApplyIf(() -> TweaksConfig.addToggleDebugMessage)),
    OPTIMIZE_TEXTURE_LOADING(new MixinBuilder("Optimize Texture Loading").setPhase(Phase.EARLY).addClientMixins("minecraft.textures.client.MixinTextureUtil").addExcludedMod(TargetedMod.ANGELICA).setApplyIf(() -> SpeedupsConfig.optimizeTextureLoading)),
    HIDE_POTION_PARTICLES(new MixinBuilder("Hide Potion Particles").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinEntityLivingBase_HidePotionParticles").setApplyIf(() -> TweaksConfig.hidePotionParticlesFromSelf)),
    DIMENSION_MANAGER_DEBUG(new MixinBuilder("Dimension Manager Debug").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinDimensionManager").setApplyIf(() -> DebugConfig.dimensionManagerDebug)),
    OPTIMIZE_TILEENTITY_REMOVAL(new MixinBuilder("Optimize TileEntity Removal").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinWorldUpdateEntities").setApplyIf(() -> SpeedupsConfig.optimizeTileentityRemoval).addExcludedMod(TargetedMod.FASTCRAFT).addExcludedMod(TargetedMod.BUKKIT)),
    FIX_POTION_ITERATING(new MixinBuilder("Fix Potion Iterating").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinEntityLivingBase_FixPotionException").setApplyIf(() -> FixesConfig.fixPotionIterating)),
    ENHANCE_NIGHT_VISION(new MixinBuilder("Remove the blueish sky tint from night vision").setPhase(Phase.EARLY).setApplyIf(() -> TweaksConfig.enhanceNightVision).addClientMixins("minecraft.MixinEntityRenderer_EnhanceNightVision")),
    OPTIMIZE_ASMDATATABLE_INDEX(new MixinBuilder("Optimize ASM DataTable Index").setPhase(Phase.EARLY).addCommonMixins("fml.MixinASMDataTable").setApplyIf(() -> SpeedupsConfig.optimizeASMDataTable)),
    SQUASH_BED_ERROR_MESSAGE(new MixinBuilder("Stop \"You can only sleep at night\" message filling the chat").addClientMixins("minecraft.MixinNetHandlerPlayClient").setApplyIf(() -> FixesConfig.squashBedErrorMessage).setPhase(Phase.EARLY)),
    CHUNK_SAVE_CME_DEBUG(new MixinBuilder("Add debugging code to Chunk Save CME").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinNBTTagCompound").setApplyIf(() -> DebugConfig.chunkSaveCMEDebug)),
    SPEEDUP_NBT_COPY(new MixinBuilder("Speed up NBT copy").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinNBTTagCompound_speedup", "minecraft.MixinNBTTagList_speedup").setApplyIf(() -> ASMConfig.speedupNBTTagCompoundCopy).addExcludedMod(TargetedMod.BUKKIT)),
    STRING_POOLER_NBT_TAG(new MixinBuilder("Pool NBT Strings").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinNBTTagCompound_stringPooler").setApplyIf(() -> TweaksConfig.enableTagCompoundStringPooling)),
    STRING_POOLER_NBT_STRING(new MixinBuilder("Pool NBT Strings").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinNBTTagString_stringPooler").setApplyIf(() -> TweaksConfig.enableNBTStringPooling)),
    THREADED_WORLDDATA_SAVING(new MixinBuilder("Threaded WorldData Saving").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinMapStorage_threadedIO", "minecraft.MixinSaveHandler_threadedIO", "minecraft.MixinScoreboardSaveData_threadedIO", "minecraft.MixinVillageCollection_threadedIO", "minecraft.MixinMapData_threadedIO", "forge.MixinForgeChunkManager_threadedIO").setApplyIf(() -> TweaksConfig.threadedWorldDataSaving)),
    DONT_SLEEP_ON_THREADED_IO(new MixinBuilder("Don't sleep on threaded IO").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinThreadedFileIOBase_noSleep").setApplyIf(() -> TweaksConfig.dontSleepOnThreadedIO)),
    OPTIMIZE_MOB_SPAWNING(new MixinBuilder("Optimize Mob Spawning").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinSpawnerAnimals_optimizeSpawning", "minecraft.MixinSpawnListEntry_optimizeSpawning").setApplyIf(() -> SpeedupsConfig.optimizeMobSpawning).addExcludedMod(TargetedMod.BUKKIT)),
    RENDER_DEBUG(new MixinBuilder("Render Debug").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinRenderGlobal").setApplyIf(() -> DebugConfig.renderDebug).addExcludedMod(TargetedMod.BUKKIT)),
    STATIC_LAN_PORT(new MixinBuilder("Static Lan Port").setPhase(Phase.EARLY).addClientMixins("minecraft.server.MixinHttpUtil").setApplyIf(() -> TweaksConfig.enableDefaultLanPort)),
    CROSSHAIR_THIRDPERSON(new MixinBuilder("Crosshairs thirdperson").setPhase(Phase.EARLY).addClientMixins("forge.MixinGuiIngameForge_CrosshairThirdPerson").setApplyIf(() -> TweaksConfig.hideCrosshairInThirdPerson)),
    DONT_INVERT_CROSSHAIR_COLORS(new MixinBuilder("Don't invert crosshair colors").setPhase(Phase.EARLY).addClientMixins("forge.MixinGuiIngameForge_CrosshairInvertColors").setApplyIf(() -> TweaksConfig.dontInvertCrosshairColor)),
    FIX_OPENGUIHANDLER_WINDOWID(new MixinBuilder("Fix OpenGuiHandler").setPhase(Phase.EARLY).addCommonMixins("fml.MixinOpenGuiHandler").setApplyIf(() -> FixesConfig.fixForgeOpenGuiHandlerWindowId)),
    FIX_KEYBIND_CONFLICTS(new MixinBuilder("Trigger all conflicting keybinds").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinKeyBinding", "minecraft.MixinMinecraft_UpdateKeys").setApplyIf(() -> FixesConfig.triggerAllConflictingKeybindings).addExcludedMod(TargetedMod.MODERNKEYBINDING)),
    REMOVE_SPAWN_MINECART_SOUND(new MixinBuilder("Remove sound when spawning a minecart").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinWorldClient").setApplyIf(() -> TweaksConfig.removeSpawningMinecartSound)),
    MACOS_KEYS_TEXTFIELD_SHORTCUTS(new MixinBuilder("Macos use CMD to copy/select/delete text").addClientMixins("minecraft.MixinGuiTextField").setApplyIf(() -> System.getProperty("os.name").toLowerCase().contains("mac") && TweaksConfig.enableMacosCmdShortcuts).setPhase(Phase.EARLY)),
    FIX_FONT_RENDERER_LINEWRAP_RECURSION(new MixinBuilder("Replace recursion with iteration in FontRenderer line wrapping code").addClientMixins("minecraft.MixinFontRenderer").setApplyIf(() -> FixesConfig.fixFontRendererLinewrapRecursion).setPhase(Phase.EARLY)),
    BED_MESSAGE_ABOVE_HOTBAR(new MixinBuilder("Bed Message Above Hotbar").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinBlockBed").setApplyIf(() -> TweaksConfig.bedMessageAboveHotbar)),
    // BED_ALWAYS_SETS_SPAWN.addExcludedMod(TargetedMod.ETFURUMREQUIEM) // uncomment when EFR adds this feature
    BED_ALWAYS_SETS_SPAWN(new MixinBuilder("Clicking a bed in a valid dim will always set your spawn immediately").setPhase(Phase.EARLY).setApplyIf(() -> TweaksConfig.bedAlwaysSetsSpawn).addCommonMixins("minecraft.MixinBlockBed_AlwaysSetsSpawn")),
    FIX_PLAYER_SKIN_FETCHING(new MixinBuilder("Fix player skin fetching").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinAbstractClientPlayer", "minecraft.MixinThreadDownloadImageData").setApplyIf(() -> FixesConfig.fixPlayerSkinFetching)),
    VALIDATE_PACKET_ENCODING_BEFORE_SENDING(new MixinBuilder("Validate packet encoding before sending").setPhase(Phase.EARLY).addCommonMixins("minecraft.packets.MixinDataWatcher", "minecraft.packets.MixinS3FPacketCustomPayload_Validation").setApplyIf(() -> FixesConfig.validatePacketEncodingBeforeSending)),
    FIX_FLUID_CONTAINER_REGISTRY_KEY(new MixinBuilder("Fix Forge fluid container registry key").setPhase(Phase.EARLY).addCommonMixins("forge.FluidContainerRegistryAccessor", "forge.MixinFluidRegistry").setApplyIf(() -> FixesConfig.fixFluidContainerRegistryKey)),
    CHANGE_MAX_NETWORK_NBT_SIZE_LIMIT(new MixinBuilder("Modify the maximum NBT size limit as received from network packets").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinPacketBuffer").setApplyIf(() -> FixesConfig.changeMaxNetworkNbtSizeLimit)),
    INCREASE_PACKET_SIZE_LIMIT(new MixinBuilder("Increase the packet size limit from 2MiB to a theoretical maximum of 4GiB").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinMessageSerializer2", "minecraft.MixinMessageDeserializer2", "minecraft.packets.MixinS3FPacketCustomPayload_LengthLimit").setApplyIf(() -> FixesConfig.increasePacketSizeLimit)),
    FIX_XRAY_BLOCK_WITHOUT_COLLISION_AABB(new MixinBuilder("Fix Xray through block without collision boundingBox").addCommonMixins("minecraft.MixinBlock_FixXray", "minecraft.MixinWorld_FixXray").setApplyIf(() -> FixesConfig.fixPerspectiveCamera).setPhase(Phase.EARLY)),
    DISABLE_CREATIVE_TAB_ALL_SEARCH(new MixinBuilder("Disable the creative tab with search bar").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinGuiContainerCreative").setApplyIf(() -> FixesConfig.removeCreativeSearchTab).addRequiredMod(TargetedMod.NOTENOUGHITEMS)),
    FIX_CHAT_COLOR_WRAPPING(new MixinBuilder("Fix wrapped chat lines missing colors").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinGuiNewChat_FixColorWrapping").setApplyIf(() -> FixesConfig.fixChatWrappedColors)),
    COMPACT_CHAT(new MixinBuilder("Compact chat").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinGuiNewChat_CompactChat").setApplyIf(() -> TweaksConfig.compactChat)),
    NETTY_PATCH(new MixinBuilder("Fix NPE in Netty's Bootstrap class").addClientMixins("netty.MixinBootstrap").setPhase(Phase.EARLY).setApplyIf(() -> FixesConfig.fixNettyNPE)),
    MODERN_PICK_BLOCK(new MixinBuilder("Allows pick block to pull items from your inventory").setPhase(Phase.EARLY).addClientMixins("forge.MixinForgeHooks_ModernPickBlock").setApplyIf(() -> TweaksConfig.modernPickBlock)),
    TESSELATOR_PRESERVE_QUAD_ORDER(new MixinBuilder("Preserve the rendering order of layered quads on terrain pass 1").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinTessellator").setApplyIf(() -> FixesConfig.fixPreserveQuadOrder)),
    FAST_BLOCK_PLACING(new MixinBuilder("Allows blocks to be placed faster").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinMinecraft_FastBlockPlacing").setApplyIf(() -> true)), // Always apply, config handled in mixin
    FIX_NEGATIVE_KELVIN(new MixinBuilder("Fix the local temperature can go below absolute zero").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinBiomeGenBase").setApplyIf(() -> FixesConfig.fixNegativeKelvin)),
    SPIGOT_EXTENDED_CHUNKS(new MixinBuilder("Spigot-style extended chunk format to remove the 2MB chunk size limit").addExcludedMod(TargetedMod.BUKKIT).setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinRegionFile").setApplyIf(() -> FixesConfig.remove2MBChunkLimit)),
    AUTOSAVE_INTERVAL(new MixinBuilder("Sets the auto save interval in ticks").setPhase(Phase.EARLY).addCommonMixins("minecraft.server.MixinMinecraftServer_AutoSaveInterval").setApplyIf(() -> TweaksConfig.autoSaveInterval != 900)),
    LIGHTER_WATER(new MixinBuilder("Decreases water opacity from 3 to 1, like in modern").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinBlock_LighterWater").setApplyIf(() -> TweaksConfig.useLighterWater)),
    EARLY_CHUNK_TILE_COORDINATE_CHECK(new MixinBuilder("Checks saved TileEntity coordinates earlier to provide a more descriptive error message").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinChunk").setApplyIf(() -> FixesConfig.earlyChunkTileCoordinateCheck)),
    FIX_DUPLICATE_SOUNDS(new MixinBuilder("Fix duplicate sounds being played when you close a gui while one is playing").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinMinecraft_FixDuplicateSounds").setApplyIf(() -> FixesConfig.fixDuplicateSounds)),
    ADD_MOD_ITEM_STATS(new MixinBuilder("Add stats for modded items").addCommonMixins("fml.MixinGameRegistry").setApplyIf(() -> TweaksConfig.addModItemStats).setPhase(Phase.EARLY)),
    ADD_MOD_ITEM_STATS_CLIENT(new MixinBuilder("Add stats for modded items").addClientMixins("minecraft.MixinStats").setApplyIf(() -> TweaksConfig.addModItemStats).setPhase(Phase.EARLY)),
    ADD_MOD_ENTITY_STATS(new MixinBuilder("Add stats for modded entities").addCommonMixins("minecraft.MixinStatList").setApplyIf(() -> TweaksConfig.addModEntityStats).setPhase(Phase.EARLY)),
    ADD_MOD_ENTITY_STATS_CLIENT(new MixinBuilder("Add stats for modded entities (client side)").addClientMixins("minecraft.MixinStatsMobsList", "minecraft.MixinStatsBlock", "minecraft.MixinStatsItem").setApplyIf(() -> TweaksConfig.addModEntityStats).setPhase(Phase.EARLY)),
    FIX_SLASH_COMMAND(new MixinBuilder("Fix forge command handler not checking for a / and also not running commands with any case").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinClientCommandHandler_CommandFix").setApplyIf(() -> FixesConfig.fixSlashCommands)),
    FIX_CASE_COMMAND(new MixinBuilder("Fix the command handler not allowing you to run commands typed in any case").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinCommandHandler_CommandFix").setApplyIf(() -> FixesConfig.fixCaseCommands)),
    FIX_GAMESETTINGS_OUTOFBOUNDS(new MixinBuilder("Fix array out of bound error in GameSettings menu").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinGameSettings_FixArrayOutOfBounds").setApplyIf(() -> FixesConfig.fixGameSettingsArrayOutOfBounds).addExcludedMod(TargetedMod.LWJGL3IFY)),
    LIMIT_RECURSIVE_BLOCK_UPDATE_DEPTH(new MixinBuilder("Limit the number of recursive cascading block updates during world generation to prevent stack overflow crashes").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinWorldServer_LimitUpdateRecursion").setApplyIf(() -> FixesConfig.limitRecursiveBlockUpdateDepth >= 0)),
    ADD_MOD_CONFIG_SEARCHBAR(new MixinBuilder("Adds a search bar to the mod config GUI").setPhase(Phase.EARLY).addClientMixins("fml.MixinGuiConfig").setApplyIf(() -> TweaksConfig.addModConfigSearchBar)),
    FIX_BUTTON_POS_GUIOPENLINK(new MixinBuilder("Fix the buttons not being centered in the GuiConfirmOpenLink").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinGuiConfirmOpenLink").setApplyIf(() -> FixesConfig.fixButtonsGuiConfirmOpenLink)),
    FIX_CHAT_OPEN_LINK(new MixinBuilder("Fix the vanilla method to open chat links not working for every OS").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinGuiChat_OpenLinks").setApplyIf(() -> FixesConfig.fixChatOpenLink)),
    FIX_NAMETAG_BRIGHTNESS(new MixinBuilder("Fix nametag brightness").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinRendererLivingEntity_NametagBrightness").setApplyIf(() -> FixesConfig.fixNametagBrightness)),
    FIX_HIT_EFFECT_BRIGHTNESS(new MixinBuilder("Fix hit effect brightness").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinRendererLivingEntity_HitEffectBrightness").setApplyIf(() -> FixesConfig.fixHitEffectBrightness)),
    FIX_BUKKIT_PLAYER_CONTAINER(new MixinBuilder("Fix Bukkit BetterQuesting crash").setPhase(Phase.EARLY).addRequiredMod(TargetedMod.BUKKIT).addCommonMixins("minecraft.MixinContainerPlayer").setApplyIf(() -> FixesConfig.fixBukkitBetterQuestingCrash)),
    FIX_BUKKIT_FIRE_SPREAD_NPE(new MixinBuilder("Fix vanilla fire spread sometimes causing NPE on thermos").addRequiredMod(TargetedMod.BUKKIT).setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinBlockFireSpread").setApplyIf(() -> FixesConfig.fixFireSpread)),
    MEMORY_FIXES_CLIENT(new MixinBuilder("Memory fixes").setPhase(Phase.EARLY).addClientMixins("memory.MixinFMLClientHandler").setApplyIf(() -> FixesConfig.enableMemoryFixes)),
    FAST_RANDOM(new MixinBuilder("Replaces uses of stdlib Random with a faster one").setPhase(Phase.EARLY).addCommonMixins("minecraft.fastload.rand.MixinChunkProviderGenerate", "minecraft.fastload.rand.MixinMapGenBase", "minecraft.fastload.rand.MixinMapGenCaves").setApplyIf(() -> SpeedupsConfig.fastRandom)),
    FAST_INT_CACHE(new MixinBuilder("Rewrite internal caching methods to be safer and faster").setPhase(Phase.EARLY).addCommonMixins("minecraft.fastload.intcache.MixinCollectOneCache", "minecraft.fastload.intcache.MixinCollectTwoCaches", "minecraft.fastload.intcache.MixinGenLayerEdge", "minecraft.fastload.intcache.MixinIntCache", "minecraft.fastload.intcache.MixinWorldChunkManager").setApplyIf(() -> SpeedupsConfig.fastIntCache)),
    NUKE_LONG_BOXING(new MixinBuilder("Remove Long boxing in MapGenStructure").setPhase(Phase.EARLY).addCommonMixins("minecraft.fastload.MixinMapGenStructure").setApplyIf(() -> SpeedupsConfig.unboxMapGen)),
    EMBED_BLOCKIDS(new MixinBuilder("Embed IDs directly in the objects, to accelerate lookups").setPhase(Phase.EARLY).addExcludedMod(TargetedMod.FASTCRAFT).addExcludedMod(TargetedMod.BUKKIT).addCommonMixins("minecraft.fastload.embedid.MixinEmbedIDs", "minecraft.fastload.embedid.MixinFMLControlledNamespacedRegistry", "minecraft.fastload.embedid.MixinObjectIntIdentityMap").setApplyIf(() -> ASMConfig.embedID_experimental)),
    FAST_CHUNK_LOADING(new MixinBuilder("Invasively accelerates chunk handling").setPhase(Phase.EARLY).addCommonMixins("minecraft.fastload.MixinEntityPlayerMP", "minecraft.fastload.MixinChunkProviderServer").setApplyIf(() -> SpeedupsConfig.fastChunkHandling)),
    CANCEL_NONE_SOUNDS(new MixinBuilder("Skips playing empty sounds.").setPhase(Phase.EARLY).addCommonMixins("minecraft.shutup.MixinWorld").setApplyIf(() -> FixesConfig.skipEmptySounds)),
    MEMORY_FIXES_IC2(new MixinBuilder("Removes allocation spam from the Direction.applyTo method").setPhase(Phase.LATE).addCommonMixins("ic2.MixinDirection_Memory").setApplyIf(() -> FixesConfig.enableMemoryFixes).addRequiredMod(TargetedMod.IC2)),
    FIX_PLAYER_BLOCK_PLACEMENT_DISTANCE_CHECK(new MixinBuilder("Fix wrong block placement distance check").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinNetHandlePlayServer_FixWrongBlockPlacementCheck").setApplyIf(() -> FixesConfig.fixWrongBlockPlacementDistanceCheck)),
    PREVENT_LAVA_CHUNK_LOADING(new MixinBuilder("Prevent lava blocks from loading chunks").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinBlockStaticLiquid").setApplyIf(() -> SpeedupsConfig.lavaChunkLoading)),
    FIX_GLASS_BOTTLE_NON_WATER_BLOCKS(new MixinBuilder("Fix Glass Bottles filling with Water from some other Fluid blocks").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinItemGlassBottle").setApplyIf(() -> FixesConfig.fixGlassBottleWaterFilling)),
    FIX_IOOBE_RENDER_DISTANCE(new MixinBuilder("Fix out of bounds render distance when Optifine/Angelica is uninstalled").setPhase(Phase.EARLY).addExcludedMod(TargetedMod.OPTIFINE).addExcludedMod(TargetedMod.ANGELICA).addExcludedMod(TargetedMod.FALSETWEAKS).setApplyIf(() -> true).addClientMixins("minecraft.MixinGameSettings_ReduceRenderDistance")),
    BETTER_MOD_LIST(new MixinBuilder("Better Mod List").setPhase(Phase.EARLY).addClientMixins("fml.MixinGuiModList", "fml.MixinGuiSlotModList", "fml.MixinGuiScrollingList").setApplyIf(() -> TweaksConfig.betterModList ).addExcludedMod(TargetedMod.ENDERIO)),
    FIX_EGG_PARTICLE(new MixinBuilder("Use correct egg particles instead of snowball ones (MC-7807)").setPhase(Phase.EARLY).addClientMixins("minecraft.MixinEntityEgg").setApplyIf(() -> FixesConfig.fixEggParticles)),
    FIX_EVENTBUS_MEMORY_LEAK(new MixinBuilder("Fix EventBus keeping object references after unregistering event handlers.").setPhase(Phase.EARLY).addCommonMixins("fml.MixinListenerListInst", "fml.MixinEventBus").setApplyIf(() -> FixesConfig.fixEventBusMemoryLeak)),

    // Ic2 adjustments
    IC2_UNPROTECTED_GET_BLOCK_FIX(new MixinBuilder("IC2 Kinetic Fix").setPhase(Phase.EARLY).addCommonMixins("ic2.MixinIc2WaterKinetic").setApplyIf(() -> FixesConfig.fixIc2UnprotectedGetBlock).addRequiredMod(TargetedMod.IC2)),
    IC2_DIRECT_INV_ACCESS(new MixinBuilder("IC2 Direct Inventory Access Fix").setPhase(Phase.EARLY).addCommonMixins("ic2.MixinItemCropSeed", "ic2.MixinTileEntityCrop").setApplyIf(() -> FixesConfig.fixIc2DirectInventoryAccess).addRequiredMod(TargetedMod.IC2)),
    IC2_NIGHT_VISION_NANO(new MixinBuilder("IC2 Nightvision Fix").setPhase(Phase.EARLY).addCommonMixins("ic2.MixinIc2NanoSuitNightVision", "ic2.MixinIc2QuantumSuitNightVision", "ic2.MixinIc2NightVisionGoggles").setApplyIf(() -> FixesConfig.fixIc2Nightvision).addRequiredMod(TargetedMod.IC2)),
    IC2_REACTOR_DUPE(new MixinBuilder("IC2 Reactor Dupe Fix").setPhase(Phase.EARLY).addCommonMixins("ic2.MixinTileEntityReactorChamberElectricNoDupe").setApplyIf(() -> FixesConfig.fixIc2ReactorDupe).addRequiredMod(TargetedMod.IC2)),
    IC2_REACTOR_INVENTORY_SPEEDUP(new MixinBuilder("IC2 Reactor Inventory Speedup Fix").setPhase(Phase.EARLY).addCommonMixins("ic2.MixinTileEntityReactorChamberElectricInvSpeedup").setApplyIf(() -> FixesConfig.optimizeIc2ReactorInventoryAccess).addRequiredMod(TargetedMod.IC2)),
    HIDE_IC2_REACTOR_COOLANT_SLOTS(new MixinBuilder("IC2 Reactory Fix").setPhase(Phase.EARLY).addCommonMixins("ic2.MixinTileEntityNuclearReactorElectric").setApplyIf(() -> TweaksConfig.hideIc2ReactorSlots).addRequiredMod(TargetedMod.IC2)),
    IC2_FLUID_CONTAINER_TOOLTIP(new MixinBuilder("IC2 Fluid Container Tooltip Fix").setPhase(Phase.EARLY).addCommonMixins("ic2.MixinItemIC2FluidContainer").setApplyIf(() -> TweaksConfig.displayIc2FluidLocalizedName).addRequiredMod(TargetedMod.IC2)),
    IC2_HOVER_MODE_FIX(new MixinBuilder("IC2 Hover Mode Fix").setPhase(Phase.LATE).addCommonMixins("ic2.MixinIc2QuantumSuitHoverMode").setApplyIf(() -> FixesConfig.fixIc2HoverMode).addRequiredMod(TargetedMod.IC2)),
    IC2_ARMOR_LAG_FIX(new MixinBuilder("IC2 Armor Lag Fix").setPhase(Phase.LATE).addCommonMixins("ic2.MixinElectricItemManager", "ic2.MixinIC2ArmorHazmat", "ic2.MixinIC2ArmorJetpack", "ic2.MixinIC2ArmorNanoSuit", "ic2.MixinIC2ArmorNightvisionGoggles", "ic2.MixinIC2ArmorQuantumSuit", "ic2.MixinIC2ArmorSolarHelmet", "ic2.MixinIC2ArmorStaticBoots").setApplyIf(() -> FixesConfig.fixIc2ArmorLag).addRequiredMod(TargetedMod.IC2)),
    IC2_RESOURCE_PACK_TRANSLATION_FIX(new MixinBuilder("IC2 Resource Pack Translation Fix").setPhase(Phase.EARLY).addClientMixins("fml.MixinLanguageRegistry", "fml.MixinFMLClientHandler", "ic2.MixinLocalization").setApplyIf(() -> FixesConfig.fixIc2ResourcePackTranslation).addRequiredMod(TargetedMod.IC2)),
    IC2_CROP_TRAMPLING_FIX(new MixinBuilder("IC2 Crop Trampling Fix").setPhase(Phase.LATE).addCommonMixins("ic2.MixinIC2TileEntityCrop").setApplyIf(() -> FixesConfig.fixIc2CropTrampling).addRequiredMod(TargetedMod.IC2)),
    IC2_SYNC_REACTORS(new MixinBuilder("Synchronize IC2 reactors for more consistent operation").setPhase(Phase.LATE).addCommonMixins("ic2.sync.MixinTEReactorChamber", "ic2.sync.MixinTEReactor").setApplyIf(() -> TweaksConfig.synchronizeIC2Reactors).addRequiredMod(TargetedMod.IC2)),
    IC2_CELL(new MixinBuilder("No IC2 Cell Consumption in tanks").addCommonMixins("ic2.MixinIC2ItemCell").setPhase(Phase.LATE).setApplyIf(() -> TweaksConfig.ic2CellWithContainer).addRequiredMod(TargetedMod.IC2)),

    // Disable update checkers
    BIBLIOCRAFT_UPDATE_CHECK(new MixinBuilder("Yeet Bibliocraft Update Check").setPhase(Phase.LATE).addClientMixins("bibliocraft.MixinVersionCheck").setApplyIf(() -> FixesConfig.removeUpdateChecks).addRequiredMod(TargetedMod.BIBLIOCRAFT)),
    COFH_CORE_UPDATE_CHECK(new MixinBuilder("Yeet COFH Core Update Check").setPhase(Phase.EARLY).addCommonMixins("cofhcore.MixinCoFHCoreUpdateCheck").setApplyIf(() -> FixesConfig.removeUpdateChecks).addRequiredMod(TargetedMod.COFH_CORE)),
    DAMAGE_INDICATORS_UPDATE_CHECK(new MixinBuilder("Yeet Damage Indicators Update Check").setPhase(Phase.LATE).addClientMixins("damageindicators.MixinDIClientProxy").setApplyIf(() -> FixesConfig.removeUpdateChecks).addRequiredMod(TargetedMod.DAMAGE_INDICATORS)),

    // Railcraft Anchors
    WAKE_ANCHORS_ON_LOGIN_PASSIVE(new MixinBuilder("Wake passive anchors on login").setPhase(Phase.LATE).addCommonMixins("railcraft.MixinTileAnchorPassive").setApplyIf(() -> TweaksConfig.installAnchorAlarm).addRequiredMod(TargetedMod.RAILCRAFT)),
    WAKE_ANCHORS_ON_LOGIN_PERSONAL(new MixinBuilder("Wake person anchors on login").setPhase(Phase.LATE).addCommonMixins("railcraft.MixinTileAnchorPersonal").setApplyIf(() -> TweaksConfig.installAnchorAlarm).addRequiredMod(TargetedMod.RAILCRAFT)),

    // Hunger overhaul
    HUNGER_OVERHAUL_LOW_STAT_EFFECT(new MixinBuilder("Patch unintended low stat effects").setPhase(Phase.LATE).addCommonMixins("hungeroverhaul.MixinHungerOverhaulLowStatEffect").setApplyIf(() -> FixesConfig.fixHungerOverhaul).addRequiredMod(TargetedMod.HUNGER_OVERHAUL)),
    HUNGER_OVERHAUL_REGEN(new MixinBuilder("Patch Regen").setPhase(Phase.LATE).addCommonMixins("hungeroverhaul.MixinHungerOverhaulHealthRegen").setApplyIf(() -> FixesConfig.fixHungerOverhaul).addRequiredMod(TargetedMod.HUNGER_OVERHAUL)),
    HUNGER_OVERHAUL_0_HUNGER(new MixinBuilder("Fix some items restore 0 hunger").setPhase(Phase.LATE).addCommonMixins("hungeroverhaul.MixinHungerOverhaulModuleHarvestCraft").setApplyIf(() -> FixesConfig.fixHungerOverhaulRestore0Hunger).addRequiredMod(TargetedMod.HUNGER_OVERHAUL).addRequiredMod(TargetedMod.HARVESTCRAFT)),

    // Thaumcraft
    THREADED_THAUMCRAFT_MAZE_SAVING(new MixinBuilder("Threaded Thaumcraft Maze Saving").setPhase(Phase.LATE).addCommonMixins("thaumcraft.MixinMazeHandler_threadedIO").setApplyIf(() -> TweaksConfig.threadedWorldDataSaving).addRequiredMod(TargetedMod.THAUMCRAFT)),
    ADD_CV_SUPPORT_TO_WAND_PEDESTAL(new MixinBuilder("CV Support for Wand Pedestal").setPhase(Phase.LATE).addCommonMixins("thaumcraft.MixinTileWandPedestal").setApplyIf(() -> TweaksConfig.addCVSupportToWandPedestal).addRequiredMod(TargetedMod.THAUMCRAFT)),
    FIX_ASPECT_SORTING(new MixinBuilder("Fix Thaumcraft Aspects not being sorted by name").addClientMixins("thaumcraft.MixinGuiResearchRecipe", "thaumcraft.MixinGuiResearchTable", "thaumcraft.MixinGuiThaumatorium", "thaumcraft.MixinItem_SortAspectsByName").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixThaumcraftAspectSorting).addRequiredMod(TargetedMod.THAUMCRAFT)),
    FIX_GOLEM_MARKER_LOADING(new MixinBuilder("Fix golem marker loading failure when dimensionId larger than MAX_BYTE").setPhase(Phase.LATE).addCommonMixins("thaumcraft.MixinEntityGolemBase", "thaumcraft.MixinItemGolemBell").setApplyIf(() -> FixesConfig.fixThaumcraftGolemMarkerLoading).addRequiredMod(TargetedMod.THAUMCRAFT)),
    FIX_WORLD_COORDINATE_HASHING_METHOD(new MixinBuilder("Implement a proper hashing method for WorldCoordinates").addCommonMixins("thaumcraft.MixinWorldCoordinates").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixThaumcraftWorldCoordinatesHashingMethod).addRequiredMod(TargetedMod.THAUMCRAFT)),
    FIX_MAGICAL_LEAVES_LAG(new MixinBuilder("Fix Thaumcraft leaves frequent ticking").addCommonMixins("thaumcraft.MixinBlockMagicalLeaves", "thaumcraft.MixinBlockMagicalLog").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixThaumcraftLeavesLag).addRequiredMod(TargetedMod.THAUMCRAFT)),
    FIX_THAUMCRAFT_VIS_DUPLICATION(new MixinBuilder("Fix Thaumcraft Vis Duplication").addCommonMixins("thaumcraft.MixinTileWandPedestal_VisDuplication").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixWandPedestalVisDuplication).addRequiredMod(TargetedMod.THAUMCRAFT)),
    FIX_NULL_HANDLING_ITEMWISPESSENCE_CLIENT(new MixinBuilder("Fix handling of null stacks in ItemWispEssence").addClientMixins("thaumcraft.MixinItemWispEssence_Client").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixNullHandlingItemWispEssence).addRequiredMod(TargetedMod.THAUMCRAFT)),
    FIX_NULL_HANDLING_ITEMWISPESSENCE_BOTH(new MixinBuilder("Fix handling of null stacks in ItemWispEssence").addCommonMixins("thaumcraft.MixinItemWispEssence_Both").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixNullHandlingItemWispEssence).addRequiredMod(TargetedMod.THAUMCRAFT)),

    // BOP
    FIX_QUICKSAND_XRAY(new MixinBuilder("Fix Xray through block without collision boundingBox").setPhase(Phase.LATE).addCommonMixins("biomesoplenty.MixinBlockMud_FixXray").setApplyIf(() -> FixesConfig.fixPerspectiveCamera).addRequiredMod(TargetedMod.BOP)),
    DEDUPLICATE_FORESTRY_COMPAT_IN_BOP(new MixinBuilder("BOP Forestry Compat").setPhase(Phase.LATE).addCommonMixins("biomesoplenty.MixinForestryIntegration").setApplyIf(() -> FixesConfig.deduplicateForestryCompatInBOP).addRequiredMod(TargetedMod.BOP)),
    SPEEDUP_BOP_BIOME_FOG(new MixinBuilder("BOP Biome Fog").addClientMixins("biomesoplenty.MixinFogHandler").setPhase(Phase.LATE).setApplyIf(() -> SpeedupsConfig.speedupBOPFogHandling).addRequiredMod(TargetedMod.BOP)),
    SPEEDUP_BOP_BIOME_FOG_ACCESSOR(new MixinBuilder("BOP Biome Fog Accessor").addClientMixins("biomesoplenty.AccessorFogHandler").setPhase(Phase.LATE).setApplyIf(() -> SpeedupsConfig.speedupBOPFogHandling).addRequiredMod(TargetedMod.BOP)),
    BIG_FIR_TREES(new MixinBuilder("BOP Fir Trees").setPhase(Phase.LATE).addCommonMixins("biomesoplenty.MixinBlockBOPSapling").setApplyIf(() -> TweaksConfig.makeBigFirsPlantable).addRequiredMod(TargetedMod.BOP)),
    JAVA12_BOP(new MixinBuilder("BOP Java12-safe reflection").setPhase(Phase.LATE).addCommonMixins("biomesoplenty.MixinBOPBiomes").addCommonMixins("biomesoplenty.MixinBOPReflectionHelper").setApplyIf(() -> FixesConfig.java12BopCompat).addRequiredMod(TargetedMod.BOP)),
    DISABLE_QUICKSAND_GENERATION(new MixinBuilder("Disable BOP quicksand").setPhase(Phase.LATE).addCommonMixins("biomesoplenty.MixinDisableQuicksandGeneration").setApplyIf(() -> TweaksConfig.removeBOPQuicksandGeneration).addRequiredMod(TargetedMod.BOP)),
    // COFH
    COFH_REMOVE_TE_CACHE(new MixinBuilder("Remove CoFH tile entity cache").addCommonMixins("minecraft.MixinWorld_CoFH_TE_Cache").setApplyIf(() -> ASMConfig.cofhWorldTransformer).addRequiredMod(TargetedMod.COFH_CORE).setPhase(Phase.EARLY)),
    MFR_FIX_COFH_VALIDATE(new MixinBuilder("Remove CoFH TE cache usage from MFR").addCommonMixins("minefactoryreloaded.MixinTileEntityBase", "minefactoryreloaded.MixinTileEntityRedNetCable").addRequiredMod(TargetedMod.MINEFACTORY_RELOADED).setApplyIf(() -> ASMConfig.cofhWorldTransformer).setPhase(Phase.LATE)),
    TE_FIX_COFH_VALIDATE(new MixinBuilder("Remove CoFH TE cache usage from TE").addCommonMixins("thermalexpansion.MixinTileInventoryTileLightFalse").addRequiredMod(TargetedMod.THERMALEXPANSION).setApplyIf(() -> ASMConfig.cofhWorldTransformer).setPhase(Phase.LATE)),
    FIX_ORE_DICT_NPE(new MixinBuilder("Fix NPE in OreDictionaryArbiter").addCommonMixins("cofhcore.MixinOreDictionaryArbiter").setPhase(Phase.EARLY).addRequiredMod(TargetedMod.COFH_CORE).setApplyIf(() -> FixesConfig.fixCofhOreDictNPE)),
    FIX_ORE_DICT_CME(new MixinBuilder("Fix race condition in CoFH oredict").addClientMixins("cofhcore.MixinFMLEventHandler").setPhase(Phase.EARLY).addRequiredMod(TargetedMod.COFH_CORE).setApplyIf(() -> FixesConfig.fixCofhOreDictCME)),
    COFH_IMPROVE_BREAKBLOCK(new MixinBuilder("Improve CoFH breakBlock method to support mods").addClientMixins("cofhcore.MixinBlockHelper").setPhase(Phase.EARLY).addRequiredMod(TargetedMod.COFH_CORE).setApplyIf(() -> TweaksConfig.improveCofhBreakBlock)),

    // Minefactory Reloaded
    DISARM_SACRED_TREE(new MixinBuilder("Prevents Sacred Rubber Tree Generation").addCommonMixins("minefactoryreloaded.MixinBlockRubberSapling").setPhase(Phase.LATE).addRequiredMod(TargetedMod.MINEFACTORY_RELOADED).setApplyIf(() -> FixesConfig.disableMassiveSacredTreeGeneration)),
    MFR_IMPROVE_BLOCKSMASHER(new MixinBuilder("Improve MFR block smasher").addCommonMixins("minefactoryreloaded.MixinTileEntityBlockSmasher").setPhase(Phase.LATE).addRequiredMod(TargetedMod.MINEFACTORY_RELOADED).setApplyIf(() -> TweaksConfig.improveMfrBlockSmasher)),
    MFR_IMPROVE_BLOCKBREAKER(new MixinBuilder("Improve MFR block breaker").addCommonMixins("minefactoryreloaded.MixinTileEntityBlockBreaker").setPhase(Phase.LATE).addRequiredMod(TargetedMod.MINEFACTORY_RELOADED).setApplyIf(() -> TweaksConfig.improveMfrBlockBreaker)),

    // Immersive engineering
    JAVA12_IMMERSIVE_ENGINERRING(new MixinBuilder("Immersive Engineering Java-12 safe potion array resizing").setPhase(Phase.LATE).addCommonMixins("immersiveengineering.MixinIEPotions").setApplyIf(() -> FixesConfig.java12ImmersiveEngineeringCompat).addRequiredMod(TargetedMod.IMMERSIVE_ENGINENEERING)),
    JAVA12_MINE_CHEM(new MixinBuilder("Minechem Java-12 safe potion array resizing").setPhase(Phase.LATE).addCommonMixins("minechem.MixinPotionInjector").setApplyIf(() -> FixesConfig.java12MineChemCompat).addRequiredMod(TargetedMod.MINECHEM)),

    // Modular Powersuits
    MPS_PREVENT_RF_ENERGY_SYPHON(new MixinBuilder("Prevent MPS from charging and draining RF from Inventory").setPhase(Phase.LATE).addCommonMixins("mps.MixinElectricAdapterRF").setApplyIf(() -> TweaksConfig.preventMPSEnergyTransferRF).addRequiredMod(TargetedMod.MODULARPOWERSUITS)),
    MPS_PREVENT_EU_ENERGY_SYPHON(new MixinBuilder("Prevent MPS from charging and draining EU from Inventory").setPhase(Phase.LATE).addCommonMixins("mps.MixinElectricAdapterEU").setApplyIf(() -> TweaksConfig.preventMPSEnergyTransferEU).addRequiredMod(TargetedMod.MODULARPOWERSUITS)),
    MPS_PREVENT_ME_ENERGY_SYPHON(new MixinBuilder("Prevent MPS from charging and draining ME from Inventory").setPhase(Phase.LATE).addCommonMixins("mps.MixinElectricAdapterME").setApplyIf(() -> TweaksConfig.preventMPSEnergyTransferME).addRequiredMod(TargetedMod.MODULARPOWERSUITS)),

    // MrTJPCore (Project Red)
    FIX_HUD_LIGHTING_GLITCH(new MixinBuilder("HUD Lighting glitch").setPhase(Phase.LATE).addCommonMixins("mrtjpcore.MixinFXEngine").setApplyIf(() -> TweaksConfig.fixHudLightingGlitch).addRequiredMod(TargetedMod.MRTJPCORE)),
    FIX_POPPING_OFF(new MixinBuilder("Fix Popping Off").setPhase(Phase.LATE).addCommonMixins("mrtjpcore.MixinPlacementLib").setApplyIf(() -> TweaksConfig.fixComponentsPoppingOff).addRequiredMod(TargetedMod.MRTJPCORE)),

    // Automagy
    IMPLEMENTS_CONTAINER_FOR_THIRSTY_TANK(new MixinBuilder("Thirsty Tank Container").setPhase(Phase.LATE).addCommonMixins("automagy.MixinItemBlockThirstyTank").setApplyIf(() -> TweaksConfig.thirstyTankContainer).addRequiredMod(TargetedMod.AUTOMAGY)),

    // Better HUD
    FIX_BETTERHUD_ARMOR_BAR(new MixinBuilder("Fix better HUD armor display breaking with skulls").setPhase(Phase.LATE).addCommonMixins("betterhud.MixinSkullDurabilityDisplay").setApplyIf(() -> FixesConfig.fixBetterHUDArmorDisplay).addRequiredMod(TargetedMod.BETTERHUD)),
    FIX_BETTERHUD_HEARTS_FREEZE(new MixinBuilder("Fix better HUD freezing the game when trying to render high amounts of hp").setPhase(Phase.LATE).addCommonMixins("betterhud.MixinHealthRender").setApplyIf(() -> FixesConfig.fixBetterHUDHPDisplay).addRequiredMod(TargetedMod.BETTERHUD)),

    // ProjectE
    FIX_FURNACE_ITERATION(new MixinBuilder("Speedup Furnaces").setPhase(Phase.LATE).addCommonMixins("projecte.MixinObjHandler").setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace).addRequiredMod(TargetedMod.PROJECTE)),

    // LOTR
    FIX_LOTR_FURNACE_ERROR(new MixinBuilder("Patches lotr to work with the vanilla furnace speedup").setPhase(Phase.LATE).addCommonMixins("lotr.MixinLOTRRecipes").setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace).addRequiredMod(TargetedMod.GTNHLIB).addRequiredMod(TargetedMod.LOTR)),
    FIX_LOTR_JAVA12(new MixinBuilder("Fix lotr java 12+ compat").setPhase(Phase.LATE).addCommonMixins("lotr.MixinLOTRLogReflection", "lotr.MixinRedirectHuornAI", "lotr.MixinRemoveUnlockFinalField").setApplyIf(() -> FixesConfig.java12LotrCompat).addRequiredMod(TargetedMod.LOTR)),

    // Journeymap
    FIX_JOURNEYMAP_KEYBINDS(new MixinBuilder("Fix Journeymap Keybinds").setPhase(Phase.LATE).addClientMixins("journeymap.MixinConstants").setApplyIf(() -> FixesConfig.fixJourneymapKeybinds).addRequiredMod(TargetedMod.JOURNEYMAP)),
    FIX_JOURNEYMAP_ILLEGAL_FILE_PATH_CHARACTER(new MixinBuilder("Fix Journeymap Illegal File Path Character").setPhase(Phase.LATE).addClientMixins("journeymap.MixinWorldData").setApplyIf(() -> FixesConfig.fixJourneymapFilePath).addRequiredMod(TargetedMod.JOURNEYMAP)),
    FIX_JOURNEYMAP_JUMPY_SCROLLING(new MixinBuilder("Fix Journeymap jumpy scrolling in the waypoint manager").setPhase(Phase.LATE).addClientMixins("journeymap.MixinWaypointManager").setApplyIf(() -> FixesConfig.fixJourneymapJumpyScrolling).addRequiredMod(TargetedMod.JOURNEYMAP)),

    // Xaero's World Map
    FIX_XAEROS_WORLDMAP_SCROLL(new MixinBuilder("Fix Xaero's World Map map screen scrolling").addClientMixins("xaeroworldmap.MixinGuiMap").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixXaerosWorldMapScroll).addRequiredMod(TargetedMod.XAEROWORLDMAP).addRequiredMod(TargetedMod.LWJGL3IFY)),

    // Xaero's Minimap
    FIX_XAEROS_MINIMAP_ENTITYDOT(new MixinBuilder("Fix Xaero's Minimap player entity dot rendering when arrow is chosen").addClientMixins("xaerominimap.MixinMinimapRenderer").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixXaerosMinimapEntityDot).addRequiredMod(TargetedMod.XAEROMINIMAP)),

    // Pam's Harvest the Nether
    FIX_IGNIS_FRUIT_AABB(new MixinBuilder("Ignis Fruit").setPhase(Phase.LATE).addCommonMixins("harvestthenether.MixinBlockPamFruit").setApplyIf(() -> FixesConfig.fixIgnisFruitAABB).addRequiredMod(TargetedMod.HARVESTTHENETHER)),
    FIX_NETHER_LEAVES_FACE_RENDERING(new MixinBuilder("Nether Leaves").addClientMixins("harvestthenether.MixinBlockNetherLeaves").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixNetherLeavesFaceRendering).addRequiredMod(TargetedMod.HARVESTTHENETHER)),

    // Potion Render Offset Fixes - Various Mods
    FIX_BAUBLES_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder("Baubles Inventory with Potions").addClientMixins("baubles.MixinGuiEvents").setPhase(Phase.LATE).setApplyIf(() -> TweaksConfig.fixPotionRenderOffset).addRequiredMod(TargetedMod.BAUBLES)),
    FIX_GALACTICRAFT_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder("Galacticraft Inventory with Potions").addClientMixins("galacticraftcore.MixinGuiExtendedInventory").setPhase(Phase.LATE).setApplyIf(() -> TweaksConfig.fixPotionRenderOffset).addRequiredMod(TargetedMod.GALACTICRAFT_CORE)),
    FIX_TRAVELLERSGEAR_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder("Travelers Gear with Potions").addClientMixins("travellersgear.MixinClientProxy").setPhase(Phase.LATE).setApplyIf(() -> TweaksConfig.fixPotionRenderOffset).addRequiredMod(TargetedMod.TRAVELLERSGEAR)),
    FIX_TINKER_POTION_EFFECT_OFFSET(new MixinBuilder("Prevents the inventory from shifting when the player has active potion effects").setPhase(Phase.LATE).addRequiredMod(TargetedMod.TINKERSCONSTRUCT).setApplyIf(() -> TweaksConfig.fixPotionRenderOffset).addClientMixins("tconstruct.MixinTabRegistry")),

    // Extra Tinkers
    Fix_EXTRATIC_TECONFLICT(new MixinBuilder("Disable ExtraTic's Integration with Metallurgy 3 Precious Materials Module: [Brass, Silver, Electrum & Platinum]").addCommonMixins("extratic.MixinPartsHandler", "extratic.MixinRecipeHandler").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixExtraTiCTEConflict).addRequiredMod(TargetedMod.EXTRATIC)),
    // Extra Utilities
    FIX_EXTRA_UTILITIES_UNENCHANTING(new MixinBuilder("Fix Exu Unenchanting").addCommonMixins("extrautilities.MixinRecipeUnEnchanting").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixExtraUtilitiesUnEnchanting).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    DISABLE_AID_SPAWN_XU_SPIKES(new MixinBuilder("Fixes the vanilla zombie aid spawn triggering when killed by Extra Utilities Spikes").addCommonMixins("extrautilities.MixinBlockSpike").setPhase(Phase.LATE).setApplyIf(() -> TweaksConfig.disableAidSpawnByXUSpikes).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_EXTRA_UTILITIES_TRANSPARENT_ITEM_RENDER(new MixinBuilder("Fix extra utilities item rendering for transparent items").addClientMixins("extrautilities.MixinTransparentItemRender").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixExtraUtilitiesItemRendering).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_DRUM_EATING_CELLS(new MixinBuilder("Fix extra utilities drums eating ic2 cells and forestry capsules").addCommonMixins("extrautilities.MixinBlockDrum").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixExtraUtilitiesDrumEatingCells).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_GREENSCREEN_MICROBLOCKS(new MixinBuilder("Fix extra utilities Lapis Caelestis microblocks").addClientMixins("extrautilities.MixinFullBrightMicroMaterial").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixExtraUtilitiesGreenscreenMicroblocks).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_LAST_MILLENIUM_RAIN(new MixinBuilder("Remove rain from the Last Millenium (Extra Utilities)").addCommonMixins("extrautilities.MixinChunkProviderEndOfTime").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixExtraUtilitiesLastMilleniumRain).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_LAST_MILLENIUM_CREATURES(new MixinBuilder("Remove creatures from the Last Millenium (Extra Utilities)").addCommonMixins("extrautilities.MixinWorldProviderEndOfTime").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixExtraUtilitiesLastMilleniumCreatures).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_FLUID_RETRIEVAL_NODE(new MixinBuilder("Prevent fluid retrieval node from voiding (Extra Utilities)").addCommonMixins("extrautilities.MixinFluidBufferRetrieval").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixExtraUtilitiesFluidRetrievalNode).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_FILING_CABINET_DUPE(new MixinBuilder("Caps hotkey'd stacks to their maximum stack size in filing cabinets").addCommonMixins("extrautilities.MixinContainerFilingCabinet").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixExtraUtilitiesFilingCabinetDupe).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_FILTER_DUPE(new MixinBuilder("Prevent hotkeying other items onto item filters while they are open").addCommonMixins("extrautilities.MixinContainerFilter").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixExtraUtilitiesFilterDupe).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    CONFIGURABLE_ENDERQUARRY_ENERGY(new MixinBuilder("Ender Quarry energy storage override").addCommonMixins("extrautilities.MixinTileEntityEnderQuarry").setPhase(Phase.LATE).setApplyIf(() -> TweaksConfig.extraUtilitiesEnderQuarryOverride > 0).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_ENDERQUARRY_FREEZE(new MixinBuilder("Fix Ender Quarry freezes randomly").addCommonMixins("extrautilities.MixinTileEntityEnderQuarry_FixFreeze").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixExtraUtilitiesEnderQuarryFreeze).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_HEALING_AXE_HEAL(new MixinBuilder("Fix the healing axe not healing entities when attacking them").addCommonMixins("extrautilities.MixinItemHealingAxe").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixExtraUtilitiesHealingAxeHeal).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_CHEST_COMPARATOR_UPDATE(new MixinBuilder("Fix Extra Utilities chests not updating comparator redstone signals when their inventories change").addCommonMixins("extrautilities.MixinExtraUtilsChest").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixExtraUtilitiesChestComparatorUpdate).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_ETHERIC_SWORD_UNBREAKABLE(new MixinBuilder("Make Etheric Sword truly unbreakable").addCommonMixins("extrautilities.MixinItemEthericSword").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixExtraUtilitiesEthericSwordUnbreakable).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_ENDER_COLLECTOR_CRASH(new MixinBuilder("Prevent Extra Utilities Ender Collector from inserting into auto-dropping Blocks that create a crash-loop").addCommonMixins("extrautilities.MixinTileEnderCollector").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixExtraUtilitiesEnderCollectorCrash).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),

    // Gliby's Voice Chat
    FIX_GLIBYS_VC_THREAD_SHUTDOWN_CLIENT(new MixinBuilder("Fix Gliby's voice chat not shutting down its client thread cleanly").addClientMixins("glibysvoicechat.MixinClientNetwork").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixGlibysVoiceChatThreadStop).addRequiredMod(TargetedMod.GLIBYS_VOICE_CHAT)),
    FIX_GLIBYS_VC_THREAD_SHUTDOWN_SERVER(new MixinBuilder("Fix Gliby's voice chat not shutting down its server thread cleanly").addCommonMixins("glibysvoicechat.MixinVoiceChatServer").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixGlibysVoiceChatThreadStop).addRequiredMod(TargetedMod.GLIBYS_VOICE_CHAT)),

    // PortalGun
    PORTALGUN_FIX_URLS(new MixinBuilder("Fix URLs used to download the sound pack").addClientMixins("portalgun.MixinThreadDownloadResources").addRequiredMod(TargetedMod.PORTAL_GUN).setApplyIf(() -> FixesConfig.fixPortalGunURLs).setPhase(Phase.LATE)),

    // VoxelMap
    REPLACE_VOXELMAP_REFLECTION(new MixinBuilder("Replace VoxelMap Reflection").addClientMixins("voxelmap.reflection.MixinAddonResourcePack", "voxelmap.reflection.MixinColorManager", "voxelmap.reflection.MixinMap", "voxelmap.reflection.MixinRadar", "voxelmap.reflection.MixinVoxelMap", "voxelmap.reflection.MixinWaypointManager$1").addRequiredMod(TargetedMod.VOXELMAP).setApplyIf(() -> SpeedupsConfig.replaceVoxelMapReflection).setPhase(Phase.LATE)),
    VOXELMAP_Y_FIX(new MixinBuilder("Fix off by one Y coord").addClientMixins("voxelmap.MixinMap").addRequiredMod(TargetedMod.VOXELMAP).setApplyIf(() -> FixesConfig.fixVoxelMapYCoord).setPhase(Phase.LATE)),
    VOXELMAP_NPE_FIX(new MixinBuilder("Fix VoxelMap NPEs with Chunks").addClientMixins("voxelmap.chunk.MixinCachedRegion", "voxelmap.chunk.MixinComparisonCachedRegion").addRequiredMod(TargetedMod.VOXELMAP).setApplyIf(() -> FixesConfig.fixVoxelMapChunkNPE).setPhase(Phase.LATE)),
    VOXELMAP_FILE_EXT(new MixinBuilder("Change VoxelMap cache file extension").addClientMixins("voxelmap.cache.MixinCachedRegion", "voxelmap.cache.MixinCachedRegion$1", "voxelmap.cache.MixinComparisonCachedRegion").addRequiredMod(TargetedMod.VOXELMAP).setApplyIf(() -> TweaksConfig.changeCacheFileExtension).setPhase(Phase.LATE)),

    // Witchery
    DISABLE_POTION_ARRAY_EXTENDER(new MixinBuilder("Disable Witchery potion array extender").addCommonMixins("witchery.MixinPotionArrayExtender").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.disableWitcheryPotionExtender).addRequiredMod(TargetedMod.WITCHERY)),
    FIX_WITCHERY_REFLECTION_SKIN(new MixinBuilder("Fixes Witchery player skins reflections").addClientMixins("witchery.MixinExtendedPlayer", "witchery.MixinEntityReflection").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixWitcheryReflections).addRequiredMod(TargetedMod.WITCHERY)),
    FIX_WITCHERY_THUNDERING_DETECTION(new MixinBuilder("Fixes Witchery Thunder Detection for rituals and Witch Hunters breaking with mods modifying thunder frequency").addCommonMixins("witchery.MixinBlockCircle", "witchery.MixinEntityWitchHunter", "witchery.MixinRiteClimateChange").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixWitcheryThunderDetection).addRequiredMod(TargetedMod.WITCHERY)),
    FIX_WITCHERY_RENDERING(new MixinBuilder("Fixes Witchery Rendering errors").addClientMixins("witchery.MixinBlockCircleGlyph").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixWitcheryRendering).addRequiredMod(TargetedMod.WITCHERY)),

    // Various Exploits/Fixes
    GC_TIME_COMMAND_FIX(new MixinBuilder("GC Time Fix").addCommonMixins("minecraft.MixinTimeCommandGalacticraftFix").setPhase(Phase.EARLY).setApplyIf(() -> FixesConfig.fixTimeCommandWithGC).addRequiredMod(TargetedMod.GALACTICRAFT_CORE)),
    BIBLIOCRAFT_PACKET_FIX(new MixinBuilder("Packet Fix").addCommonMixins("bibliocraft.MixinBibliocraftPatchPacketExploits").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixBibliocraftPackets).addRequiredMod(TargetedMod.BIBLIOCRAFT)),
    BIBLIOCRAFT_PATH_SANITIZATION_FIX(new MixinBuilder("Path sanitization fix").addCommonMixins("bibliocraft.MixinPathSanitization").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixBibliocraftPathSanitization).addRequiredMod(TargetedMod.BIBLIOCRAFT)),
    ZTONES_PACKET_FIX(new MixinBuilder("Packet Fix").addCommonMixins("ztones.MixinZtonesPatchPacketExploits").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixZTonesPackets).addRequiredMod(TargetedMod.ZTONES)),
    ASP_RECIPE_FIX(new MixinBuilder("MT Core recipe fix").addCommonMixins("advancedsolarpanels.MixinAdvancedSolarPanel").addRequiredMod(TargetedMod.ADVANCED_SOLAR_PANELS).addExcludedMod(TargetedMod.DREAMCRAFT).setApplyIf(() -> FixesConfig.fixMTCoreRecipe).setPhase(Phase.LATE)),
    TD_NASE_PREVENTION(new MixinBuilder("Prevent NegativeArraySizeException on itemduct transfers").addCommonMixins("thermaldynamics.MixinSimulatedInv").setApplyIf(() -> FixesConfig.preventThermalDynamicsNASE).addRequiredMod(TargetedMod.THERMALDYNAMICS).setPhase(Phase.LATE)),
    TD_FLUID_GRID_CCE(new MixinBuilder("Prevent ClassCastException on forming invalid Thermal Dynamic fluid grid").addCommonMixins("thermaldynamics.MixinTileFluidDuctSuper").setApplyIf(() -> FixesConfig.preventFluidGridCrash).addRequiredMod(TargetedMod.THERMALDYNAMICS).setPhase(Phase.LATE)),

    // Unbind Keybinds by default
    UNBIND_KEYS_TRAVELLERSGEAR(new MixinBuilder("Unbind Traveller's Gear keybinds").addClientMixins("travellersgear.MixinKeyHandler").setPhase(Phase.LATE).setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addRequiredMod(TargetedMod.TRAVELLERSGEAR)),
    UNBIND_KEYS_INDUSTRIALCRAFT(new MixinBuilder("Unbind Industrial craft keybinds").addClientMixins("ic2.MixinKeyboardClient").setPhase(Phase.LATE).setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addRequiredMod(TargetedMod.IC2)),
    UNBIND_KEYS_THAUMCRAFT(new MixinBuilder("Unbind Thaumcraft keybinds").addClientMixins("thaumcraft.MixinKeyHandlerThaumcraft").setPhase(Phase.LATE).setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addRequiredMod(TargetedMod.THAUMCRAFT)),
    UNBIND_KEYS_COFH(new MixinBuilder("Unbind COFH Core keybinds").addClientMixins("cofhcore.MixinProxyClient").setPhase(Phase.EARLY).setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addRequiredMod(TargetedMod.COFH_CORE)),
    CHANGE_KEYBIND_CATEGORY_AUTOMAGY(new MixinBuilder("Change keybind category of Automagy").addClientMixins("automagy.MixinAutomagyKeyHandler").setPhase(Phase.LATE).setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addRequiredMod(TargetedMod.AUTOMAGY)),

    // Chunk generation/population
    DISABLE_CHUNK_TERRAIN_GENERATION(new MixinBuilder("Disable chunk terrain generation").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinChunkProviderServer_DisableTerrain").setApplyIf(() -> TweaksConfig.disableChunkTerrainGeneration)),
    DISABLE_WORLD_TYPE_CHUNK_POPULATION(new MixinBuilder("Disable chunk population tied to chunk generation (ores/structure)").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinChunkProviderServer_DisablePopulation").setApplyIf(() -> TweaksConfig.disableWorldTypeChunkPopulation)),
    DISABLE_MODDED_CHUNK_POPULATION(new MixinBuilder("Disable all other mod chunk population (e.g. Natura clouds").setPhase(Phase.EARLY).addCommonMixins("minecraft.MixinChunkProviderServer_DisableModGeneration").setApplyIf(() -> TweaksConfig.disableModdedChunkPopulation)),

    // Candycraft
    FIX_SUGARBLOCK_NPE(new MixinBuilder("Fix NPE when interacting with sugar block").addCommonMixins("candycraft.MixinBlockSugar").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixCandycraftBlockSugarNPE).addRequiredMod(TargetedMod.CANDYCRAFT)),

    // Morpheus
    FIX_NOT_WAKING_PLAYERS(new MixinBuilder("Fix players not being woken properly when not everyone is sleeping").addServerMixins("morpheus.MixinMorpheusWakePlayers").setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixMorpheusWaking).addRequiredMod(TargetedMod.MORPHEUS));

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
