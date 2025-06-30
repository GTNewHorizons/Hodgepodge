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

public enum EarlyMixins implements IMixins {

    // spotless:off
    // Vanilla Fixes
    ONLY_LOAD_LANGUAGES_ONCE_PER_FILE(new MixinBuilder("Only load languages once per file").addCommonMixins("minecraft.MixinLanguageRegistry").setApplyIf(() -> FixesConfig.onlyLoadLanguagesOnce)),
    CHANGE_CATEGORY_SPRINT_KEY(new MixinBuilder("Moves the sprint keybind to the movement category").addClientMixins("minecraft.MixinGameSettings_SprintKey").setApplyIf(() -> TweaksConfig.changeSprintCategory)),
    FIX_TOO_MANY_ALLOCATIONS_CHUNK_POSITION_INT_PAIR(new MixinBuilder("Stops MC from allocating too many ChunkPositionIntPair objects").addCommonMixins("minecraft.MixinChunkCoordIntPair_FixAllocations", "minecraft.MixinWorld_FixAllocations", "minecraft.MixinAnvilChunkLoader_FixAllocations").setApplyIf(() -> FixesConfig.fixTooManyAllocationsChunkPositionIntPair)),
    FIX_TOO_MANY_ALLOCATIONS_CHUNK_POSITION_INT_PAIR_CLIENT(new MixinBuilder("Stops MC from allocating too many ChunkPositionIntPair objects").addClientMixins("minecraft.MixinWorldClient_FixAllocations").setApplyIf(() -> FixesConfig.fixTooManyAllocationsChunkPositionIntPair)),
    FIX_TOO_MANY_ALLOCATIONS_CHUNK_POSITION_INT_PAIR_OPTIFINE_INCOMPAT(new MixinBuilder("Stops MC from allocating too many ChunkPositionIntPair objects").addExcludedMod(TargetedMod.OPTIFINE).addCommonMixins("minecraft.MixinWorldServer_FixAllocations").setApplyIf(() -> FixesConfig.fixTooManyAllocationsChunkPositionIntPair)),
    ADD_SIMULATION_DISTANCE_OPTION(new MixinBuilder("Add option to separate simulation distance from render distance").addExcludedMod(TargetedMod.OPTIFINE).addExcludedMod(TargetedMod.ULTRAMINE).addCommonMixins("minecraft.MixinWorld_SimulationDistance", "minecraft.MixinWorldServer_SimulationDistance", "minecraft.MixinChunk_SimulationDistance").setApplyIf(() -> FixesConfig.addSimulationDistance)),
    ADD_SIMULATION_DISTANCE_OPTION_THERMOS_FIX(new MixinBuilder("Add option to separate simulation distance from render distance (Thermos fix)").addExcludedMod(TargetedMod.OPTIFINE).addExcludedMod(TargetedMod.ULTRAMINE).addCommonMixins("minecraft.MixinWorldServer_SimulationDistanceThermosFix").setApplyIf(() -> FixesConfig.addSimulationDistance && Common.thermosTainted)),
    FIX_RESOURCEPACK_FOLDER_OPENING(new MixinBuilder("Fix resource pack folder sometimes not opening on windows").addClientMixins("minecraft.MixinGuiScreenResourcePacks").setApplyIf(() -> FixesConfig.fixResourcePackOpening)),
    FIX_ENCHANTMENT_LEVEL_NUMERALS(new MixinBuilder("Fix enchantment levels not displaying properly above a certain value").addCommonMixins("minecraft.MixinEnchantment_FixRomanNumerals").setApplyIf(() -> FixesConfig.fixEnchantmentNumerals || TweaksConfig.arabicNumbersForEnchantsPotions)),
    FIX_CONTAINER_PUT_STACKS_IN_SLOTS(new MixinBuilder("Prevents crash if server sends container with wrong itemStack size").addClientMixins("minecraft.MixinContainer").setApplyIf(() -> FixesConfig.fixContainerPutStacksInSlots)),
    FIX_CONTAINER_SHIFT_CLICK_RECURSION(new MixinBuilder("Backports 1.12 logic for shift clicking slots to prevent recursion").addCommonMixins("minecraft.MixinContainer_FixShiftRecursion").setApplyIf(() -> FixesConfig.fixContainerShiftClickRecursion)),
    FIX_NETHANDLERPLAYCLIENT_HANDLE_SET_SLOT(new MixinBuilder("Prevents crash if server sends itemStack with index larger than client's container").addClientMixins("minecraft.MixinNetHandlerPlayClient_FixHandleSetSlot").setApplyIf(() -> FixesConfig.fixNetHandlerPlayClientHandleSetSlot)),
    FIX_NETHANDLERLOGINSERVER_OFFLINEMODE(new MixinBuilder("Allows the server to assign the logged in UUID to the same username when online_mode is false").addServerMixins("minecraft.MixinNetHandlerLoginServer_OfflineMode").setApplyIf(() -> FixesConfig.fixNetHandlerLoginServerOfflineMode)),
    FIX_INVENTORY_POTION_EFFECT_NUMERALS(new MixinBuilder("Fix potion effects level not displaying properly above a certain value").addClientMixins("minecraft.MixinInventoryEffectRenderer_FixPotionEffectNumerals", "minecraft.MixinItemPotion_FixRomanNumerals").setApplyIf(() -> FixesConfig.fixPotionEffectNumerals || TweaksConfig.arabicNumbersForEnchantsPotions)),
    FIX_HASTE_ARM_SWING_ANIMATION(new MixinBuilder("Fix arm not swinging when having too much haste").addCommonMixins("minecraft.MixinEntityLivingBase_FixHasteArmSwing").setApplyIf(() -> FixesConfig.fixHasteArmSwing)),
    DISABLE_REALMS_BUTTON(new MixinBuilder("Disable Realms button in main menu").addClientMixins("minecraft.MixinGuiMainMenu_DisableRealmsButton").setApplyIf(() -> TweaksConfig.disableRealmsButton)),
    ADD_TIME_GET(new MixinBuilder("Add /time get command").addCommonMixins("minecraft.MixinCommandTime").setApplyIf(() -> TweaksConfig.addTimeGet)),
    OPTIMIZE_WORLD_UPDATE_LIGHT(new MixinBuilder("Optimize world updateLightByType method").addCommonMixins("minecraft.MixinWorld_FixLightUpdateLag").addExcludedMod(TargetedMod.ARCHAICFIX).addExcludedMod(TargetedMod.ANGELICA).setApplyIf(() -> FixesConfig.optimizeWorldUpdateLight)),
    FIX_FRIENDLY_CREATURE_SOUNDS(new MixinBuilder("Fix Friendly Creature Sounds").addClientMixins("minecraft.MixinSoundHandler").setApplyIf(() -> FixesConfig.fixFriendlyCreatureSounds)),
    LOGARITHMIC_VOLUME_CONTROL(new MixinBuilder("Logarithmic Volume Control").addClientMixins("minecraft.MixinSoundManager", "minecraft.MixinSoundManagerLibraryLoader").setApplyIf(() -> FixesConfig.logarithmicVolumeControl)),
    THROTTLE_ITEMPICKUPEVENT(new MixinBuilder("Throttle Item Pickup Event").addCommonMixins("minecraft.MixinEntityPlayer_ThrottlePickup").setApplyIf(() -> FixesConfig.throttleItemPickupEvent)),
    ADD_THROWER_TO_DROPPED_ITEM(new MixinBuilder("Adds the thrower tag to all dropped EntityItems").addCommonMixins("minecraft.MixinEntityPlayer_ItemThrower").setApplyIf(() -> FixesConfig.addThrowerTagToDroppedItems)),
    SYNC_ITEM_THROWER_COMMON(new MixinBuilder("Synchonize from server to client the thrower and pickup delay of an item entity").addCommonMixins("minecraft.packets.MixinS0EPacketSpawnObject_ItemThrower").setApplyIf(() -> FixesConfig.syncItemThrower)),
    SYNC_ITEM_THROWER_CLIENT(new MixinBuilder("Synchonize from server to client the thrower and pickup delay of an item entity").addClientMixins("minecraft.MixinNetHandlerPlayClient_ItemThrower").setApplyIf(() -> FixesConfig.syncItemThrower)),
    FIX_PERSPECTIVE_CAMERA(new MixinBuilder("Camera Perspective Fix").addClientMixins("minecraft.MixinEntityRenderer").addExcludedMod(TargetedMod.ARCHAICFIX).addExcludedMod(TargetedMod.ANGELICA).setApplyIf(() -> FixesConfig.fixPerspectiveCamera)),
    FIX_DEBUG_BOUNDING_BOX(new MixinBuilder("Fix Bounding Box").addClientMixins("minecraft.MixinRenderManager").setApplyIf(() -> FixesConfig.fixDebugBoundingBox)),
    FENCE_CONNECTIONS_FIX(new MixinBuilder("Fix Fence Connections").addCommonMixins("minecraft.MixinBlockFence").setApplyIf(() -> FixesConfig.fixFenceConnections)),
    FIX_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder("Fix Inventory Offset with Potions").addClientMixins("minecraft.MixinInventoryEffectRenderer_PotionOffset").setApplyIf(() -> TweaksConfig.fixPotionRenderOffset)),
    FIX_POTION_EFFECT_RENDERING(new MixinBuilder("Fix Potion Effect Rendering").addClientMixins("minecraft.MixinInventoryEffectRenderer_PotionEffectRendering").setApplyIf(() -> TweaksConfig.fixPotionEffectRender)),
    FIX_IMMOBILE_FIREBALLS(new MixinBuilder("Fix Immobile Fireballs").addCommonMixins("minecraft.MixinEntityFireball").setApplyIf(() -> FixesConfig.fixImmobileFireballs)),
    FIX_REED_PLACING(new MixinBuilder("Fix placement of Sugar Canes").addCommonMixins("minecraft.MixinItemReed").setApplyIf(() -> FixesConfig.fixSugarCanePlacement)),
    LONGER_CHAT(new MixinBuilder("Longer Chat").addClientMixins("minecraft.MixinGuiNewChat_LongerChat").setApplyIf(() -> TweaksConfig.longerChat)),
    TRANSPARENT_CHAT(new MixinBuilder("Transparent Chat").addClientMixins("minecraft.MixinGuiNewChat_TransparentChat").setApplyIf(() -> TweaksConfig.transparentChat)),
    FIX_ENTITY_ATTRIBUTES_RANGE(new MixinBuilder("Fix Entity Attributes Range").addClientMixins("minecraft.MixinNetHandlerPlayClient_FixEntityAttributesRange").setApplyIf(() -> FixesConfig.fixEntityAttributesRange)),
    ENDERMAN_BLOCK_GRAB_DISABLE(new MixinBuilder("Disable Endermen Grabbing Blocks").addCommonMixins("minecraft.MixinEntityEndermanGrab").setApplyIf(() -> TweaksConfig.endermanBlockGrabDisable)),
    ENDERMAN_BLOCK_PLACE_DISABLE(new MixinBuilder("Disable Endermen Placing Held Blocks").addCommonMixins("minecraft.MixinEntityEndermanPlace").setApplyIf(() -> TweaksConfig.endermanBlockPlaceDisable)),
    ENDERMAN_BLOCK_PLACE_BLACKLIST(new MixinBuilder("Disable Endermen Placing Held Blocks on Configured Blocks").addCommonMixins("minecraft.MixinEntityEndermanPlaceBlacklist").setApplyIf(() -> TweaksConfig.endermanBlockPlaceBlacklist)),
    WITCH_POTION_METADATA(new MixinBuilder("Fix Metadata of Witch Potions").addCommonMixins("minecraft.MixinEntityWitch").setApplyIf(() -> TweaksConfig.witchPotionMetadata)),
    // config handled in mixin due to server->client config sync
    LONGER_MESSAGES_CLIENT(new MixinBuilder("Longer Messages Client Side").addClientMixins("minecraft.MixinGuiChat_LongerMessages").setApplyIf(() -> true)),
    // config handled in mixin due to server->client config sync
    LONGER_MESSAGES_SERVER(new MixinBuilder("Longer Messages Server Side").addCommonMixins("minecraft.MixinC01PacketChatMessage_LongerMessages").setApplyIf(() -> true)),
    SPEEDUP_REMOVE_FORMATTING_CODES(new MixinBuilder("Speed up the vanilla method to remove formatting codes").addClientMixins("minecraft.MixinEnumChatFormatting_FastFormat").setApplyIf(() -> SpeedupsConfig.speedupRemoveFormatting)),
    SPEEDUP_GRASS_BLOCK_RANDOM_TICKING(new MixinBuilder("Speed up grass block random ticking").addCommonMixins("minecraft.MixinBlockGrass").setApplyIf(() -> SpeedupsConfig.speedupGrassBlockRandomTicking)),
    SPEEDUP_CHUNK_PROVIDER_CLIENT(new MixinBuilder("Speed up ChunkProviderClient").addClientMixins("minecraft.MixinChunkProviderClient_RemoveChunkListing").addExcludedMod(TargetedMod.FASTCRAFT).setApplyIf(() -> SpeedupsConfig.speedupChunkProviderClient && ASMConfig.speedupLongIntHashMap)),
    BETTER_HASHCODES(new MixinBuilder("Optimize various Hashcode").addCommonMixins("minecraft.MixinChunkCoordinates_BetterHash", "minecraft.MixinChunkCoordIntPair_BetterHash").setApplyIf(() -> SpeedupsConfig.speedupChunkCoordinatesHashCode)),
    TCP_NODELAY(new MixinBuilder("Set TCP NODELAY").addCommonMixins("minecraft.MixinTcpNoDelay").setApplyIf(() -> SpeedupsConfig.tcpNoDelay)),
    WORLD_UNPROTECTED_GET_BLOCK(new MixinBuilder("Fix world unprotected getBlock").addCommonMixins("minecraft.MixinWorldGetBlock").setApplyIf(() -> FixesConfig.fixVanillaUnprotectedGetBlock)),
    WORLD_UNPROTECTED_LIGHT_VALUE(new MixinBuilder("Fix world unprotected light value").addCommonMixins("minecraft.MixinWorldLightValue").setApplyIf(() -> FixesConfig.fixGetBlockLightValue)),
    VILLAGE_UNCHECKED_GET_BLOCK(new MixinBuilder("Fix Village unchecked getBlock").addCommonMixins("minecraft.MixinVillage", "minecraft.MixinVillageCollection").setApplyIf(() -> FixesConfig.fixVillageUncheckedGetBlock)),
    FORGE_HOOKS_URL_FIX(new MixinBuilder("Fix forge URL hooks").addCommonMixins("minecraft.MixinForgeHooks").setApplyIf(() -> FixesConfig.fixUrlDetection)),
    FORGE_UPDATE_CHECK_FIX(new MixinBuilder("Fix the forge update checker").addCommonMixins("forge.MixinForgeVersion_FixUpdateCheck").setApplyIf(() -> FixesConfig.fixForgeUpdateChecker)),
    FORGE_FIX_CLASS_TYPO(new MixinBuilder("Fix a class name typo in MinecraftForge's initialize method").addCommonMixins("forge.MixinMinecraftForge").setApplyIf(() -> FixesConfig.fixEffectRendererClassTypo)),
    NORTHWEST_BIAS_FIX(new MixinBuilder("Fix Northwest Bias").addCommonMixins("minecraft.MixinRandomPositionGenerator").setApplyIf(() -> FixesConfig.fixNorthWestBias)),
    SPEEDUP_VANILLA_FURNACE(new MixinBuilder("Speedup Vanilla Furnace").addCommonMixins("minecraft.MixinFurnaceRecipes").setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace).addRequiredMod(TargetedMod.GTNHLIB)),
    GAMEOVER_GUI_LOCKED_DISABLED(new MixinBuilder("Fix Gameover GUI").addClientMixins("minecraft.MixinGuiGameOver").setApplyIf(() -> FixesConfig.fixGuiGameOver)),
    PREVENT_PICKUP_LOOT(new MixinBuilder("Prevent monsters from picking up loot").addCommonMixins("minecraft.MixinEntityLivingPickup").setApplyIf(() -> TweaksConfig.preventPickupLoot)),
    DROP_PICKED_LOOT_ON_DESPAWN(new MixinBuilder("Drop picked up loot on despawn").addCommonMixins("minecraft.MixinEntityLivingDrop").setApplyIf(() -> TweaksConfig.dropPickedLootOnDespawn)),
    FIX_HOPPER_HIT_BOX(new MixinBuilder("Fix Vanilla Hopper hit box").addCommonMixins("minecraft.MixinBlockHopper").setApplyIf(() -> FixesConfig.fixHopperHitBox)),
    TILE_RENDERER_PROFILER_DISPATCHER(new MixinBuilder("TileEntity Render Dispatcher Fix").addClientMixins("minecraft.profiler.TileEntityRendererDispatcherMixin").setApplyIf(() -> TweaksConfig.enableTileRendererProfiler)),
    TILE_RENDERER_PROFILER_MINECRAFT(new MixinBuilder("Tile Entity Render Profiler").addClientMixins("minecraft.profiler.MinecraftMixin").setApplyIf(() -> TweaksConfig.enableTileRendererProfiler)),
    DIMENSION_CHANGE_FIX(new MixinBuilder("Dimension Change Heart Fix").addCommonMixins("minecraft.MixinServerConfigurationManager", "minecraft.MixinEntityPlayerMP").setApplyIf(() -> FixesConfig.fixDimensionChangeAttributes)),
    FIX_EATING_STACKED_STEW(new MixinBuilder("Stacked Mushroom Stew Eating Fix").addCommonMixins("minecraft.MixinItemSoup").setApplyIf(() -> FixesConfig.fixEatingStackedStew)),
    INCREASE_PARTICLE_LIMIT(new MixinBuilder("Increase Particle Limit").addClientMixins("minecraft.MixinEffectRenderer").setApplyIf(() -> TweaksConfig.increaseParticleLimit)),
    ENLARGE_POTION_ARRAY(new MixinBuilder("Make the Potion array larger").addCommonMixins("minecraft.MixinPotion").setApplyIf(() -> FixesConfig.enlargePotionArray)),
    FIX_POTION_LIMIT(new MixinBuilder("Fix Potion Limit").addCommonMixins("minecraft.MixinPotionEffect").setApplyIf(() -> FixesConfig.fixPotionLimit)),
    FIX_HOPPER_VOIDING_ITEMS(new MixinBuilder("Fix Hopper Voiding Items").addCommonMixins("minecraft.MixinTileEntityHopper").setApplyIf(() -> FixesConfig.fixHopperVoidingItems)),
    FIX_HUGE_CHAT_KICK(new MixinBuilder("Fix huge chat kick").addCommonMixins("minecraft.MixinS02PacketChat").setApplyIf(() -> FixesConfig.fixHugeChatKick)),
    FIX_BOGUS_INTEGRATED_SERVER_NPE(new MixinBuilder("Fix bogus FMLProxyPacket NPEs on integrated server crashes").addCommonMixins("fml.MixinFMLProxyPacket", "fml.MixinNetworkDispatcher", "minecraft.NetworkManagerAccessor").setApplyIf(() -> FixesConfig.fixBogusIntegratedServerNPEs)),
    FIX_LAG_ON_INVENTORY_SYNC(new MixinBuilder("Fix inventory sync lag").addCommonMixins("minecraft.MixinInventoryCrafting").setApplyIf(() -> FixesConfig.fixInventorySyncLag)),
    FIX_LOGIN_DIMENSION_ID_OVERFLOW(new MixinBuilder("Fix dimension id overflowing when a player first logins on a server").addCommonMixins("minecraft.packets.MixinS01PacketJoinGame_FixDimensionID").setApplyIf(() -> FixesConfig.fixLoginDimensionIDOverflow)),
    FIX_WORLD_SERVER_LEAKING_UNLOADED_ENTITIES(new MixinBuilder("Fix world server leaking unloaded entities").addCommonMixins("minecraft.MixinWorldServerUpdateEntities").setApplyIf(() -> FixesConfig.fixWorldServerLeakingUnloadedEntities)),
    FIX_SKIN_MANAGER_CLIENT_WORLD_LEAK(new MixinBuilder("Fix skin manager client world leak").addClientMixins("minecraft.MixinSkinManager$2").setApplyIf(() -> FixesConfig.fixSkinManagerLeakingClientWorld)),
    FIX_REDSTONE_TORCH_WORLD_LEAK(new MixinBuilder("Fix world leak in redstone torch").addCommonMixins("minecraft.MixinBlockRedstoneTorch").setApplyIf(() -> FixesConfig.fixRedstoneTorchWorldLeak).addExcludedMod(TargetedMod.BUGTORCH)),
    FIX_ARROW_WRONG_LIGHTING(new MixinBuilder("Fix arrow wrong lighting").addClientMixins("minecraft.MixinRendererLivingEntity").setApplyIf(() -> FixesConfig.fixGlStateBugs)),
    FIX_RESIZABLE_FULLSCREEN(new MixinBuilder("Fix Resizable Fullscreen").addClientMixins("minecraft.MixinMinecraft_ResizableFullscreen").setApplyIf(() -> FixesConfig.fixResizableFullscreen)),
    FIX_UNFOCUSED_FULLSCREEN(new MixinBuilder("Fix Unfocused Fullscreen").addClientMixins("minecraft.MixinMinecraft_UnfocusedFullscreen").setApplyIf(() -> FixesConfig.fixUnfocusedFullscreen).addExcludedMod(TargetedMod.ARCHAICFIX)),
    FIX_RENDERERS_WORLD_LEAK(new MixinBuilder("Fix Renderers World Leak").addClientMixins("minecraft.MixinMinecraft_ClearRenderersWorldLeak", "minecraft.MixinRenderGlobal_FixWordLeak").setApplyIf(() -> FixesConfig.fixRenderersWorldLeak)),
    FIX_OPTIFINE_CHUNKLOADING_CRASH(new MixinBuilder("Fix Optifine Chunkloading Crash").setApplyIf(() -> FixesConfig.fixOptifineChunkLoadingCrash).addRequiredMod(TargetedMod.OPTIFINE).addClientMixins("minecraft.MixinGameSettings_FixOFChunkLoading")),
    ADD_TOGGLE_DEBUG_MESSAGE(new MixinBuilder("Toggle Debug Message").addClientMixins("minecraft.MixinMinecraft_ToggleDebugMessage").setApplyIf(() -> TweaksConfig.addToggleDebugMessage)),
    OPTIMIZE_TEXTURE_LOADING(new MixinBuilder("Optimize Texture Loading").addClientMixins("minecraft.textures.client.MixinTextureUtil").addExcludedMod(TargetedMod.ANGELICA).setApplyIf(() -> SpeedupsConfig.optimizeTextureLoading)),
    HIDE_POTION_PARTICLES(new MixinBuilder("Hide Potion Particles").addClientMixins("minecraft.MixinEntityLivingBase_HidePotionParticles").setApplyIf(() -> TweaksConfig.hidePotionParticlesFromSelf)),
    DIMENSION_MANAGER_DEBUG(new MixinBuilder("Dimension Manager Debug").addCommonMixins("minecraft.MixinDimensionManager").setApplyIf(() -> DebugConfig.dimensionManagerDebug)),
    OPTIMIZE_TILEENTITY_REMOVAL(new MixinBuilder("Optimize TileEntity Removal").addCommonMixins("minecraft.MixinWorldUpdateEntities").setApplyIf(() -> SpeedupsConfig.optimizeTileentityRemoval).addExcludedMod(TargetedMod.FASTCRAFT).addExcludedMod(TargetedMod.BUKKIT)),
    FIX_POTION_ITERATING(new MixinBuilder("Fix Potion Iterating").addCommonMixins("minecraft.MixinEntityLivingBase_FixPotionException").setApplyIf(() -> FixesConfig.fixPotionIterating)),
    ENHANCE_NIGHT_VISION(new MixinBuilder("Remove the blueish sky tint from night vision").setApplyIf(() -> TweaksConfig.enhanceNightVision).addClientMixins("minecraft.MixinEntityRenderer_EnhanceNightVision")),
    OPTIMIZE_ASMDATATABLE_INDEX(new MixinBuilder("Optimize ASM DataTable Index").addCommonMixins("fml.MixinASMDataTable").setApplyIf(() -> SpeedupsConfig.optimizeASMDataTable)),
    SQUASH_BED_ERROR_MESSAGE(new MixinBuilder("Stop \"You can only sleep at night\" message filling the chat").addClientMixins("minecraft.MixinNetHandlerPlayClient").setApplyIf(() -> FixesConfig.squashBedErrorMessage)),
    CHUNK_SAVE_CME_DEBUG(new MixinBuilder("Add debugging code to Chunk Save CME").addCommonMixins("minecraft.MixinNBTTagCompound").setApplyIf(() -> DebugConfig.chunkSaveCMEDebug)),
    SPEEDUP_NBT_COPY(new MixinBuilder("Speed up NBT copy").addCommonMixins("minecraft.MixinNBTTagCompound_speedup", "minecraft.MixinNBTTagList_speedup").setApplyIf(() -> ASMConfig.speedupNBTTagCompoundCopy).addExcludedMod(TargetedMod.BUKKIT)),
    STRING_POOLER_NBT_TAG(new MixinBuilder("Pool NBT Strings").addCommonMixins("minecraft.MixinNBTTagCompound_stringPooler").setApplyIf(() -> TweaksConfig.enableTagCompoundStringPooling)),
    STRING_POOLER_NBT_STRING(new MixinBuilder("Pool NBT Strings").addCommonMixins("minecraft.MixinNBTTagString_stringPooler").setApplyIf(() -> TweaksConfig.enableNBTStringPooling)),
    THREADED_WORLDDATA_SAVING(new MixinBuilder("Threaded WorldData Saving").addCommonMixins("minecraft.MixinMapStorage_threadedIO", "minecraft.MixinSaveHandler_threadedIO", "minecraft.MixinScoreboardSaveData_threadedIO", "minecraft.MixinVillageCollection_threadedIO", "minecraft.MixinMapData_threadedIO", "forge.MixinForgeChunkManager_threadedIO").setApplyIf(() -> TweaksConfig.threadedWorldDataSaving)),
    DONT_SLEEP_ON_THREADED_IO(new MixinBuilder("Don't sleep on threaded IO").addCommonMixins("minecraft.MixinThreadedFileIOBase_noSleep").setApplyIf(() -> TweaksConfig.dontSleepOnThreadedIO)),
    OPTIMIZE_MOB_SPAWNING(new MixinBuilder("Optimize Mob Spawning").addCommonMixins("minecraft.MixinSpawnerAnimals_optimizeSpawning", "minecraft.MixinSpawnListEntry_optimizeSpawning").setApplyIf(() -> SpeedupsConfig.optimizeMobSpawning).addExcludedMod(TargetedMod.BUKKIT)),
    RENDER_DEBUG(new MixinBuilder("Render Debug").addClientMixins("minecraft.MixinRenderGlobal").setApplyIf(() -> DebugConfig.renderDebug).addExcludedMod(TargetedMod.BUKKIT)),
    STATIC_LAN_PORT(new MixinBuilder("Static Lan Port").addClientMixins("minecraft.server.MixinHttpUtil").setApplyIf(() -> TweaksConfig.enableDefaultLanPort)),
    CROSSHAIR_THIRDPERSON(new MixinBuilder("Crosshairs thirdperson").addClientMixins("forge.MixinGuiIngameForge_CrosshairThirdPerson").setApplyIf(() -> TweaksConfig.hideCrosshairInThirdPerson)),
    DONT_INVERT_CROSSHAIR_COLORS(new MixinBuilder("Don't invert crosshair colors").addClientMixins("forge.MixinGuiIngameForge_CrosshairInvertColors").setApplyIf(() -> TweaksConfig.dontInvertCrosshairColor)),
    FIX_OPENGUIHANDLER_WINDOWID(new MixinBuilder("Fix OpenGuiHandler").addCommonMixins("fml.MixinOpenGuiHandler").setApplyIf(() -> FixesConfig.fixForgeOpenGuiHandlerWindowId)),
    FIX_KEYBIND_CONFLICTS(new MixinBuilder("Trigger all conflicting keybinds").addClientMixins("minecraft.MixinKeyBinding", "minecraft.MixinMinecraft_UpdateKeys").setApplyIf(() -> FixesConfig.triggerAllConflictingKeybindings).addExcludedMod(TargetedMod.MODERNKEYBINDING)),
    REMOVE_SPAWN_MINECART_SOUND(new MixinBuilder("Remove sound when spawning a minecart").addClientMixins("minecraft.MixinWorldClient").setApplyIf(() -> TweaksConfig.removeSpawningMinecartSound)),
    MACOS_KEYS_TEXTFIELD_SHORTCUTS(new MixinBuilder("Macos use CMD to copy/select/delete text").addClientMixins("minecraft.MixinGuiTextField").setApplyIf(() -> System.getProperty("os.name").toLowerCase().contains("mac") && TweaksConfig.enableMacosCmdShortcuts)),
    FIX_FONT_RENDERER_LINEWRAP_RECURSION(new MixinBuilder("Replace recursion with iteration in FontRenderer line wrapping code").addClientMixins("minecraft.MixinFontRenderer").setApplyIf(() -> FixesConfig.fixFontRendererLinewrapRecursion)),
    BED_MESSAGE_ABOVE_HOTBAR(new MixinBuilder("Bed Message Above Hotbar").addCommonMixins("minecraft.MixinBlockBed").setApplyIf(() -> TweaksConfig.bedMessageAboveHotbar)),
    // BED_ALWAYS_SETS_SPAWN.addExcludedMod(TargetedMod.ETFURUMREQUIEM) // uncomment when EFR adds this feature
    BED_ALWAYS_SETS_SPAWN(new MixinBuilder("Clicking a bed in a valid dim will always set your spawn immediately").setApplyIf(() -> TweaksConfig.bedAlwaysSetsSpawn).addCommonMixins("minecraft.MixinBlockBed_AlwaysSetsSpawn")),
    FIX_PLAYER_SKIN_FETCHING(new MixinBuilder("Fix player skin fetching").addClientMixins("minecraft.MixinAbstractClientPlayer", "minecraft.MixinThreadDownloadImageData").setApplyIf(() -> FixesConfig.fixPlayerSkinFetching)),
    VALIDATE_PACKET_ENCODING_BEFORE_SENDING(new MixinBuilder("Validate packet encoding before sending").addCommonMixins("minecraft.packets.MixinDataWatcher", "minecraft.packets.MixinS3FPacketCustomPayload_Validation").setApplyIf(() -> FixesConfig.validatePacketEncodingBeforeSending)),
    FIX_FLUID_CONTAINER_REGISTRY_KEY(new MixinBuilder("Fix Forge fluid container registry key").addCommonMixins("forge.FluidContainerRegistryAccessor", "forge.MixinFluidRegistry").setApplyIf(() -> FixesConfig.fixFluidContainerRegistryKey)),
    CHANGE_MAX_NETWORK_NBT_SIZE_LIMIT(new MixinBuilder("Modify the maximum NBT size limit as received from network packets").addCommonMixins("minecraft.MixinPacketBuffer").setApplyIf(() -> FixesConfig.changeMaxNetworkNbtSizeLimit)),
    INCREASE_PACKET_SIZE_LIMIT(new MixinBuilder("Increase the packet size limit from 2MiB to a theoretical maximum of 4GiB").addCommonMixins("minecraft.MixinMessageSerializer2", "minecraft.MixinMessageDeserializer2", "minecraft.packets.MixinS3FPacketCustomPayload_LengthLimit").setApplyIf(() -> FixesConfig.increasePacketSizeLimit)),
    FIX_XRAY_BLOCK_WITHOUT_COLLISION_AABB(new MixinBuilder("Fix Xray through block without collision boundingBox").addCommonMixins("minecraft.MixinBlock_FixXray", "minecraft.MixinWorld_FixXray").setApplyIf(() -> FixesConfig.fixPerspectiveCamera)),
    DISABLE_CREATIVE_TAB_ALL_SEARCH(new MixinBuilder("Disable the creative tab with search bar").addClientMixins("minecraft.MixinGuiContainerCreative").setApplyIf(() -> FixesConfig.removeCreativeSearchTab).addRequiredMod(TargetedMod.NOTENOUGHITEMS)),
    FIX_CHAT_COLOR_WRAPPING(new MixinBuilder("Fix wrapped chat lines missing colors").addClientMixins("minecraft.MixinGuiNewChat_FixColorWrapping").setApplyIf(() -> FixesConfig.fixChatWrappedColors)),
    COMPACT_CHAT(new MixinBuilder("Compact chat").addClientMixins("minecraft.MixinGuiNewChat_CompactChat").setApplyIf(() -> TweaksConfig.compactChat)),
    NETTY_PATCH(new MixinBuilder("Fix NPE in Netty's Bootstrap class").addClientMixins("netty.MixinBootstrap").setApplyIf(() -> FixesConfig.fixNettyNPE)),
    MODERN_PICK_BLOCK(new MixinBuilder("Allows pick block to pull items from your inventory").addClientMixins("forge.MixinForgeHooks_ModernPickBlock").setApplyIf(() -> TweaksConfig.modernPickBlock)),
    TESSELATOR_PRESERVE_QUAD_ORDER(new MixinBuilder("Preserve the rendering order of layered quads on terrain pass 1").addClientMixins("minecraft.MixinTessellator").setApplyIf(() -> FixesConfig.fixPreserveQuadOrder)),
    FAST_BLOCK_PLACING(new MixinBuilder("Allows blocks to be placed faster").addClientMixins("minecraft.MixinMinecraft_FastBlockPlacing").setApplyIf(() -> true)), // Always apply, config handled in mixin
    FIX_NEGATIVE_KELVIN(new MixinBuilder("Fix the local temperature can go below absolute zero").addCommonMixins("minecraft.MixinBiomeGenBase").setApplyIf(() -> FixesConfig.fixNegativeKelvin)),
    SPIGOT_EXTENDED_CHUNKS(new MixinBuilder("Spigot-style extended chunk format to remove the 2MB chunk size limit").addExcludedMod(TargetedMod.BUKKIT).addCommonMixins("minecraft.MixinRegionFile").setApplyIf(() -> FixesConfig.remove2MBChunkLimit)),
    AUTOSAVE_INTERVAL(new MixinBuilder("Sets the auto save interval in ticks").addCommonMixins("minecraft.server.MixinMinecraftServer_AutoSaveInterval").setApplyIf(() -> TweaksConfig.autoSaveInterval != 900)),
    LIGHTER_WATER(new MixinBuilder("Decreases water opacity from 3 to 1, like in modern").addCommonMixins("minecraft.MixinBlock_LighterWater").setApplyIf(() -> TweaksConfig.useLighterWater)),
    EARLY_CHUNK_TILE_COORDINATE_CHECK(new MixinBuilder("Checks saved TileEntity coordinates earlier to provide a more descriptive error message").addCommonMixins("minecraft.MixinChunk").setApplyIf(() -> FixesConfig.earlyChunkTileCoordinateCheck)),
    FIX_DUPLICATE_SOUNDS(new MixinBuilder("Fix duplicate sounds being played when you close a gui while one is playing").addClientMixins("minecraft.MixinMinecraft_FixDuplicateSounds").setApplyIf(() -> FixesConfig.fixDuplicateSounds)),
    ADD_MOD_ITEM_STATS(new MixinBuilder("Add stats for modded items").addCommonMixins("fml.MixinGameRegistry").setApplyIf(() -> TweaksConfig.addModItemStats)),
    ADD_MOD_ITEM_STATS_CLIENT(new MixinBuilder("Add stats for modded items").addClientMixins("minecraft.MixinStats").setApplyIf(() -> TweaksConfig.addModItemStats)),
    ADD_MOD_ENTITY_STATS(new MixinBuilder("Add stats for modded entities").addCommonMixins("minecraft.MixinStatList").setApplyIf(() -> TweaksConfig.addModEntityStats)),
    ADD_MOD_ENTITY_STATS_CLIENT(new MixinBuilder("Add stats for modded entities (client side)").addClientMixins("minecraft.MixinStatsMobsList", "minecraft.MixinStatsBlock", "minecraft.MixinStatsItem").setApplyIf(() -> TweaksConfig.addModEntityStats)),
    FIX_SLASH_COMMAND(new MixinBuilder("Fix forge command handler not checking for a / and also not running commands with any case").addClientMixins("minecraft.MixinClientCommandHandler_CommandFix").setApplyIf(() -> FixesConfig.fixSlashCommands)),
    FIX_CASE_COMMAND(new MixinBuilder("Fix the command handler not allowing you to run commands typed in any case").addCommonMixins("minecraft.MixinCommandHandler_CommandFix").setApplyIf(() -> FixesConfig.fixCaseCommands)),
    FIX_GAMESETTINGS_OUTOFBOUNDS(new MixinBuilder("Fix array out of bound error in GameSettings menu").addClientMixins("minecraft.MixinGameSettings_FixArrayOutOfBounds").setApplyIf(() -> FixesConfig.fixGameSettingsArrayOutOfBounds).addExcludedMod(TargetedMod.LWJGL3IFY)),
    LIMIT_RECURSIVE_BLOCK_UPDATE_DEPTH(new MixinBuilder("Limit the number of recursive cascading block updates during world generation to prevent stack overflow crashes").addCommonMixins("minecraft.MixinWorldServer_LimitUpdateRecursion").setApplyIf(() -> FixesConfig.limitRecursiveBlockUpdateDepth >= 0)),
    ADD_MOD_CONFIG_SEARCHBAR(new MixinBuilder("Adds a search bar to the mod config GUI").addClientMixins("fml.MixinGuiConfig").setApplyIf(() -> TweaksConfig.addModConfigSearchBar)),
    FIX_BUTTON_POS_GUIOPENLINK(new MixinBuilder("Fix the buttons not being centered in the GuiConfirmOpenLink").addClientMixins("minecraft.MixinGuiConfirmOpenLink").setApplyIf(() -> FixesConfig.fixButtonsGuiConfirmOpenLink)),
    FIX_CHAT_OPEN_LINK(new MixinBuilder("Fix the vanilla method to open chat links not working for every OS").addClientMixins("minecraft.MixinGuiChat_OpenLinks").setApplyIf(() -> FixesConfig.fixChatOpenLink)),
    FIX_NAMETAG_BRIGHTNESS(new MixinBuilder("Fix nametag brightness").addClientMixins("minecraft.MixinRendererLivingEntity_NametagBrightness").setApplyIf(() -> FixesConfig.fixNametagBrightness)),
    FIX_HIT_EFFECT_BRIGHTNESS(new MixinBuilder("Fix hit effect brightness").addClientMixins("minecraft.MixinRendererLivingEntity_HitEffectBrightness").setApplyIf(() -> FixesConfig.fixHitEffectBrightness)),
    FIX_BUKKIT_PLAYER_CONTAINER(new MixinBuilder("Fix Bukkit BetterQuesting crash").addRequiredMod(TargetedMod.BUKKIT).addCommonMixins("minecraft.MixinContainerPlayer").setApplyIf(() -> FixesConfig.fixBukkitBetterQuestingCrash)),
    FIX_BUKKIT_FIRE_SPREAD_NPE(new MixinBuilder("Fix vanilla fire spread sometimes causing NPE on thermos").addRequiredMod(TargetedMod.BUKKIT).addCommonMixins("minecraft.MixinBlockFireSpread").setApplyIf(() -> FixesConfig.fixFireSpread)),
    MEMORY_FIXES_CLIENT(new MixinBuilder("Memory fixes").addClientMixins("memory.MixinFMLClientHandler").setApplyIf(() -> FixesConfig.enableMemoryFixes)),
    FAST_RANDOM(new MixinBuilder("Replaces uses of stdlib Random with a faster one").addCommonMixins("minecraft.fastload.rand.MixinChunkProviderGenerate", "minecraft.fastload.rand.MixinMapGenBase", "minecraft.fastload.rand.MixinMapGenCaves").setApplyIf(() -> SpeedupsConfig.fastRandom)),
    FAST_INT_CACHE(new MixinBuilder("Rewrite internal caching methods to be safer and faster").addCommonMixins("minecraft.fastload.intcache.MixinCollectOneCache", "minecraft.fastload.intcache.MixinCollectTwoCaches", "minecraft.fastload.intcache.MixinGenLayerEdge", "minecraft.fastload.intcache.MixinIntCache", "minecraft.fastload.intcache.MixinWorldChunkManager").setApplyIf(() -> SpeedupsConfig.fastIntCache)),
    NUKE_LONG_BOXING(new MixinBuilder("Remove Long boxing in MapGenStructure").addCommonMixins("minecraft.fastload.MixinMapGenStructure").setApplyIf(() -> SpeedupsConfig.unboxMapGen)),
    EMBED_BLOCKIDS(new MixinBuilder("Embed IDs directly in the objects, to accelerate lookups").addExcludedMod(TargetedMod.FASTCRAFT).addExcludedMod(TargetedMod.BUKKIT).addCommonMixins("minecraft.fastload.embedid.MixinEmbedIDs", "minecraft.fastload.embedid.MixinFMLControlledNamespacedRegistry", "minecraft.fastload.embedid.MixinObjectIntIdentityMap").setApplyIf(() -> ASMConfig.embedID_experimental)),
    FAST_CHUNK_LOADING(new MixinBuilder("Invasively accelerates chunk handling").addCommonMixins("minecraft.fastload.MixinEntityPlayerMP", "minecraft.fastload.MixinChunkProviderServer").setApplyIf(() -> SpeedupsConfig.fastChunkHandling)),
    CANCEL_NONE_SOUNDS(new MixinBuilder("Skips playing empty sounds.").addCommonMixins("minecraft.shutup.MixinWorld").setApplyIf(() -> FixesConfig.skipEmptySounds)),
    FIX_PLAYER_BLOCK_PLACEMENT_DISTANCE_CHECK(new MixinBuilder("Fix wrong block placement distance check").addCommonMixins("minecraft.MixinNetHandlePlayServer_FixWrongBlockPlacementCheck").setApplyIf(() -> FixesConfig.fixWrongBlockPlacementDistanceCheck)),
    PREVENT_LAVA_CHUNK_LOADING(new MixinBuilder("Prevent lava blocks from loading chunks").addCommonMixins("minecraft.MixinBlockStaticLiquid").setApplyIf(() -> SpeedupsConfig.lavaChunkLoading)),
    FIX_GLASS_BOTTLE_NON_WATER_BLOCKS(new MixinBuilder("Fix Glass Bottles filling with Water from some other Fluid blocks").addCommonMixins("minecraft.MixinItemGlassBottle").setApplyIf(() -> FixesConfig.fixGlassBottleWaterFilling)),
    FIX_IOOBE_RENDER_DISTANCE(new MixinBuilder("Fix out of bounds render distance when Optifine/Angelica is uninstalled").addExcludedMod(TargetedMod.OPTIFINE).addExcludedMod(TargetedMod.ANGELICA).addExcludedMod(TargetedMod.FALSETWEAKS).setApplyIf(() -> true).addClientMixins("minecraft.MixinGameSettings_ReduceRenderDistance")),
    BETTER_MOD_LIST(new MixinBuilder("Better Mod List").addClientMixins("fml.MixinGuiModList", "fml.MixinGuiSlotModList", "fml.MixinGuiScrollingList").setApplyIf(() -> TweaksConfig.betterModList).addExcludedMod(TargetedMod.ENDERIO)),
    FIX_EGG_PARTICLE(new MixinBuilder("Use correct egg particles instead of snowball ones (MC-7807)").addClientMixins("minecraft.MixinEntityEgg").setApplyIf(() -> FixesConfig.fixEggParticles)),
    FIX_EVENTBUS_MEMORY_LEAK(new MixinBuilder("Fix EventBus keeping object references after unregistering event handlers.").addCommonMixins("fml.MixinListenerListInst", "fml.MixinEventBus").setApplyIf(() -> FixesConfig.fixEventBusMemoryLeak)),

    // Ic2 adjustments
    IC2_UNPROTECTED_GET_BLOCK_FIX(new MixinBuilder("IC2 Kinetic Fix").addCommonMixins("ic2.MixinIc2WaterKinetic").setApplyIf(() -> FixesConfig.fixIc2UnprotectedGetBlock).addRequiredMod(TargetedMod.IC2)),
    IC2_DIRECT_INV_ACCESS(new MixinBuilder("IC2 Direct Inventory Access Fix").addCommonMixins("ic2.MixinItemCropSeed", "ic2.MixinTileEntityCrop").setApplyIf(() -> FixesConfig.fixIc2DirectInventoryAccess).addRequiredMod(TargetedMod.IC2)),
    IC2_NIGHT_VISION_NANO(new MixinBuilder("IC2 Nightvision Fix").addCommonMixins("ic2.MixinIc2NanoSuitNightVision", "ic2.MixinIc2QuantumSuitNightVision", "ic2.MixinIc2NightVisionGoggles").setApplyIf(() -> FixesConfig.fixIc2Nightvision).addRequiredMod(TargetedMod.IC2)),
    IC2_REACTOR_DUPE(new MixinBuilder("IC2 Reactor Dupe Fix").addCommonMixins("ic2.MixinTileEntityReactorChamberElectricNoDupe").setApplyIf(() -> FixesConfig.fixIc2ReactorDupe).addRequiredMod(TargetedMod.IC2)),
    IC2_REACTOR_INVENTORY_SPEEDUP(new MixinBuilder("IC2 Reactor Inventory Speedup Fix").addCommonMixins("ic2.MixinTileEntityReactorChamberElectricInvSpeedup").setApplyIf(() -> FixesConfig.optimizeIc2ReactorInventoryAccess).addRequiredMod(TargetedMod.IC2)),
    HIDE_IC2_REACTOR_COOLANT_SLOTS(new MixinBuilder("IC2 Reactory Fix").addCommonMixins("ic2.MixinTileEntityNuclearReactorElectric").setApplyIf(() -> TweaksConfig.hideIc2ReactorSlots).addRequiredMod(TargetedMod.IC2)),
    IC2_FLUID_CONTAINER_TOOLTIP(new MixinBuilder("IC2 Fluid Container Tooltip Fix").addCommonMixins("ic2.MixinItemIC2FluidContainer").setApplyIf(() -> TweaksConfig.displayIc2FluidLocalizedName).addRequiredMod(TargetedMod.IC2)),
    IC2_RESOURCE_PACK_TRANSLATION_FIX(new MixinBuilder("IC2 Resource Pack Translation Fix").addClientMixins("fml.MixinLanguageRegistry", "fml.MixinFMLClientHandler", "ic2.MixinLocalization").setApplyIf(() -> FixesConfig.fixIc2ResourcePackTranslation).addRequiredMod(TargetedMod.IC2)),

    // Disable update checkers
    COFH_CORE_UPDATE_CHECK(new MixinBuilder("Yeet COFH Core Update Check").addCommonMixins("cofhcore.MixinCoFHCoreUpdateCheck").setApplyIf(() -> FixesConfig.removeUpdateChecks).addRequiredMod(TargetedMod.COFH_CORE)),

    // COFH
    COFH_REMOVE_TE_CACHE(new MixinBuilder("Remove CoFH tile entity cache").addCommonMixins("minecraft.MixinWorld_CoFH_TE_Cache").setApplyIf(() -> ASMConfig.cofhWorldTransformer).addRequiredMod(TargetedMod.COFH_CORE)),
    FIX_ORE_DICT_NPE(new MixinBuilder("Fix NPE in OreDictionaryArbiter").addCommonMixins("cofhcore.MixinOreDictionaryArbiter").addRequiredMod(TargetedMod.COFH_CORE).setApplyIf(() -> FixesConfig.fixCofhOreDictNPE)),
    FIX_ORE_DICT_CME(new MixinBuilder("Fix race condition in CoFH oredict").addClientMixins("cofhcore.MixinFMLEventHandler").addRequiredMod(TargetedMod.COFH_CORE).setApplyIf(() -> FixesConfig.fixCofhOreDictCME)),
    COFH_IMPROVE_BREAKBLOCK(new MixinBuilder("Improve CoFH breakBlock method to support mods").addClientMixins("cofhcore.MixinBlockHelper").addRequiredMod(TargetedMod.COFH_CORE).setApplyIf(() -> TweaksConfig.improveCofhBreakBlock)),

    // Various Exploits/Fixes
    GC_TIME_COMMAND_FIX(new MixinBuilder("GC Time Fix").addCommonMixins("minecraft.MixinTimeCommandGalacticraftFix").setApplyIf(() -> FixesConfig.fixTimeCommandWithGC).addRequiredMod(TargetedMod.GALACTICRAFT_CORE)),

    // Unbind Keybinds by default
    UNBIND_KEYS_COFH(new MixinBuilder("Unbind COFH Core keybinds").addClientMixins("cofhcore.MixinProxyClient").setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addRequiredMod(TargetedMod.COFH_CORE)),

    // Chunk generation/population
    DISABLE_CHUNK_TERRAIN_GENERATION(new MixinBuilder("Disable chunk terrain generation").addCommonMixins("minecraft.MixinChunkProviderServer_DisableTerrain").setApplyIf(() -> TweaksConfig.disableChunkTerrainGeneration)),
    DISABLE_WORLD_TYPE_CHUNK_POPULATION(new MixinBuilder("Disable chunk population tied to chunk generation (ores/structure)").addCommonMixins("minecraft.MixinChunkProviderServer_DisablePopulation").setApplyIf(() -> TweaksConfig.disableWorldTypeChunkPopulation)),
    DISABLE_MODDED_CHUNK_POPULATION(new MixinBuilder("Disable all other mod chunk population (e.g. Natura clouds").addCommonMixins("minecraft.MixinChunkProviderServer_DisableModGeneration").setApplyIf(() -> TweaksConfig.disableModdedChunkPopulation));

    // spotless:on
    private final MixinBuilder builder;

    EarlyMixins(MixinBuilder builder) {
        this.builder = builder.setPhase(Phase.EARLY);
    }

    @Nonnull
    @Override
    public MixinBuilder getBuilder() {
        return builder;
    }
}
