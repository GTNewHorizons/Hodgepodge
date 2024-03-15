package com.mitchej123.hodgepodge.mixins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.ASMConfig;
import com.mitchej123.hodgepodge.config.DebugConfig;
import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.PollutionConfig;
import com.mitchej123.hodgepodge.config.PollutionRecolorConfig;
import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

public enum Mixins {

    // Vanilla Fixes
    CHANGE_CATEGORY_SPRINT_KEY(
            new Builder("Moves the sprint keybind to the movement category").addTargetedMod(TargetedMod.VANILLA)
                    .setSide(Side.CLIENT).setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinGameSettings_SprintKey")
                    .setApplyIf(() -> TweaksConfig.changeSprintCategory)),
    FIX_TOO_MANY_ALLOCATIONS_CHUNK_POSITION_INT_PAIR(
            new Builder("Stops MC from allocating too many ChunkPositionIntPair objects")
                    .addTargetedMod(TargetedMod.VANILLA).setSide(Side.BOTH).setPhase(Phase.EARLY)
                    .addMixinClasses(
                            "minecraft.MixinChunkCoordIntPair",
                            "minecraft.MixinWorld_FixAllocations",
                            "minecraft.MixinWorldClient_FixAllocations")
                    .setApplyIf(() -> FixesConfig.fixTooManyAllocationsChunkPositionIntPair)),
    FIX_TOO_MANY_ALLOCATIONS_CHUNK_POSITION_INT_PAIR_OPTIFINE_INCOMPAT(
            new Builder("Stops MC from allocating too many ChunkPositionIntPair objects")
                    .addTargetedMod(TargetedMod.VANILLA).addExcludedMod(TargetedMod.OPTIFINE).setSide(Side.BOTH)
                    .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinWorldServer_FixAllocations")
                    .setApplyIf(() -> FixesConfig.fixTooManyAllocationsChunkPositionIntPair)),
    FIX_RESOURCEPACK_FOLDER_OPENING(new Builder("Fix resource pack folder sometimes not opening on windows")
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinGuiScreenResourcePacks").setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixResourcePackOpening).addTargetedMod(TargetedMod.VANILLA)),
    FIX_ENCHANTMENT_LEVEL_NUMERALS(new Builder("Fix enchantment levels not displaying properly above a certain value")
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinEnchantment_FixRomanNumerals").setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixEnchantmentNumerals || TweaksConfig.arabicNumbersForEnchantsPotions)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_CONTAINER_PUT_STACKS_IN_SLOTS(new Builder("Prevents crash if server sends container with wrong itemStack size")
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinContainer").setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixContainerPutStacksInSlots).addTargetedMod(TargetedMod.VANILLA)),
    FIX_NETHANDLERPLAYCLIENT_HANDLE_SET_SLOT(
            new Builder("Prevents crash if server sends itemStack with index larger than client's container")
                    .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinNetHandlerPlayClient_FixHandleSetSlot")
                    .setSide(Side.CLIENT).setApplyIf(() -> FixesConfig.fixNetHandlerPlayClientHandleSetSlot)
                    .addTargetedMod(TargetedMod.VANILLA)),
    FIX_NETHANDLERLOGINSERVER_OFFLINEMODE(
            new Builder("Allows the server to assign the logged in UUID to the same username when online_mode is false")
                    .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinNetHandlerLoginServer_OfflineMode")
                    .setSide(Side.SERVER).setApplyIf(() -> FixesConfig.fixNetHandlerLoginServerOfflineMode)
                    .addTargetedMod(TargetedMod.VANILLA)),
    FIX_INVENTORY_POTION_EFFECT_NUMERALS(
            new Builder("Fix potion effects level not displaying properly above a certain value").setPhase(Phase.EARLY)
                    .addMixinClasses(
                            "minecraft.MixinInventoryEffectRenderer_FixPotionEffectNumerals",
                            "minecraft.MixinItemPotion_FixRomanNumerals")
                    .setSide(Side.CLIENT)
                    .setApplyIf(
                            () -> FixesConfig.fixPotionEffectNumerals || TweaksConfig.arabicNumbersForEnchantsPotions)
                    .addTargetedMod(TargetedMod.VANILLA)),
    FIX_HASTE_ARM_SWING_ANIMATION(new Builder("Fix arm not swinging when having too much haste").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityLivingBase_FixHasteArmSwing").setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixHasteArmSwing).addTargetedMod(TargetedMod.VANILLA)),

    DISABLE_REALMS_BUTTON(new Builder("Disable Realms button in main menu").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiMainMenu_DisableRealmsButton").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.disableRealmsButton).addTargetedMod(TargetedMod.VANILLA)),

    OPTIMIZE_WORLD_UPDATE_LIGHT(new Builder("Optimize world updateLightByType method").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinWorld_FixLightUpdateLag").setSide(Side.BOTH)
            .addExcludedMod(TargetedMod.ARCHAICFIX).addExcludedMod(TargetedMod.ANGELICA)
            .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> FixesConfig.optimizeWorldUpdateLight)),
    FIX_FRIENDLY_CREATURE_SOUNDS(new Builder("Fix Friendly Creature Sounds").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinSoundHandler").setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> FixesConfig.fixFriendlyCreatureSounds)),
    LOGARITHMIC_VOLUME_CONTROL(new Builder("Logarithmic Volume Control").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinSoundManager", "minecraft.MixinSoundManagerLibraryLoader")
            .setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> FixesConfig.logarithmicVolumeControl)),
    THROTTLE_ITEMPICKUPEVENT(new Builder("Throttle Item Pickup Event").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityPlayer").setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.throttleItemPickupEvent).addTargetedMod(TargetedMod.VANILLA)),
    FIX_PERSPECTIVE_CAMERA(
            new Builder("Camera Perspective Fix").setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinEntityRenderer")
                    .setSide(Side.CLIENT).addExcludedMod(TargetedMod.ARCHAICFIX).addExcludedMod(TargetedMod.ANGELICA)
                    .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> FixesConfig.fixPerspectiveCamera)),
    FIX_DEBUG_BOUNDING_BOX(new Builder("Fix Bounding Box").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinRenderManager").setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> FixesConfig.fixDebugBoundingBox)),
    FENCE_CONNECTIONS_FIX(new Builder("Fix Fence Connections").addMixinClasses("minecraft.MixinBlockFence")
            .setSide(Side.BOTH).setPhase(Phase.EARLY).addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> FixesConfig.fixFenceConnections)),
    FIX_INVENTORY_OFFSET_WITH_POTIONS(new Builder("Fix Inventory Offset with Potions").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinInventoryEffectRenderer_PotionOffset").setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> TweaksConfig.fixPotionRenderOffset)),
    FIX_POTION_EFFECT_RENDERING(new Builder("Fix Potion Effect Rendering").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinInventoryEffectRenderer_PotionEffectRendering").setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> TweaksConfig.fixPotionEffectRender)),
    FIX_IMMOBILE_FIREBALLS(new Builder("Fix Immobile Fireballs").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityFireball").addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> FixesConfig.fixImmobileFireballs).setSide(Side.BOTH)),
    LONGER_CHAT(new Builder("Longer Chat").setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinGuiNewChat_LongerChat")
            .setSide(Side.CLIENT).setApplyIf(() -> TweaksConfig.longerChat).addTargetedMod(TargetedMod.VANILLA)),
    TRANSPARENT_CHAT(new Builder("Transparent Chat").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiNewChat_TransparentChat").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.transparentChat).addTargetedMod(TargetedMod.VANILLA)),
    // config handled in mixin due to server->client config sync
    LONGER_MESSAGES_CLIENT(new Builder("Longer Messages Client Side").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiChat_LongerMessages").setApplyIf(() -> true)
            .addTargetedMod(TargetedMod.VANILLA).setSide(Side.CLIENT)),
    // config handled in mixin due to server->client config sync
    LONGER_MESSAGES_SERVER(new Builder("Longer Messages Server Side").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinC01PacketChatMessage_LongerMessages").setApplyIf(() -> true)
            .addTargetedMod(TargetedMod.VANILLA).setSide(Side.BOTH)),
    SPEEDUP_GRASS_BLOCK_RANDOM_TICKING(new Builder("Speed up grass block random ticking").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinBlockGrass").addTargetedMod(TargetedMod.VANILLA).setSide(Side.BOTH)
            .setApplyIf(() -> SpeedupsConfig.speedupGrassBlockRandomTicking)),
    SPEEDUP_CHUNK_PROVIDER_CLIENT(new Builder("Speed up ChunkProviderClient").setPhase(Phase.EARLY).setSide(Side.CLIENT)
            .addMixinClasses("minecraft.MixinChunkProviderClient_RemoveChunkListing")
            .addTargetedMod(TargetedMod.VANILLA).addExcludedMod(TargetedMod.FASTCRAFT)
            .setApplyIf(() -> SpeedupsConfig.speedupChunkProviderClient && ASMConfig.speedupLongIntHashMap)),
    CHUNK_COORDINATES_HASHCODE(new Builder("Optimize Chunk Coordinates Hashcode").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("minecraft.MixinChunkCoordinates").addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> SpeedupsConfig.speedupChunkCoordinatesHashCode)),
    TCP_NODELAY(new Builder("Set TCP NODELAY").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinTcpNoDelay").setApplyIf(() -> SpeedupsConfig.tcpNoDelay)
            .addTargetedMod(TargetedMod.VANILLA)),
    WORLD_UNPROTECTED_GET_BLOCK(new Builder("Fix world unprotected getBlock").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinWorldGetBlock").setApplyIf(() -> FixesConfig.fixVanillaUnprotectedGetBlock)
            .addTargetedMod(TargetedMod.VANILLA)),
    WORLD_UNPROTECTED_LIGHT_VALUE(new Builder("Fix world unprotected light value").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("minecraft.MixinWorldLightValue")
            .setApplyIf(() -> FixesConfig.fixGetBlockLightValue).addTargetedMod(TargetedMod.VANILLA)),
    VILLAGE_UNCHECKED_GET_BLOCK(new Builder("Fix Village unchecked getBlock").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinVillage", "minecraft.MixinVillageCollection")
            .setApplyIf(() -> FixesConfig.fixVillageUncheckedGetBlock).addTargetedMod(TargetedMod.VANILLA)),
    FORGE_HOOKS_URL_FIX(new Builder("Fix forge URL hooks").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinForgeHooks").setApplyIf(() -> FixesConfig.fixUrlDetection)
            .addTargetedMod(TargetedMod.VANILLA)),
    FORGE_UPDATE_CHECK_FIX(new Builder("Fix the forge update checker").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("forge.MixinForgeVersion_FixUpdateCheck")
            .setApplyIf(() -> FixesConfig.fixForgeUpdateChecker).addTargetedMod(TargetedMod.VANILLA)),
    FORGE_FIX_CLASS_TYPO(new Builder("Fix a class name typo in MinecraftForge's initialize method")
            .setPhase(Phase.EARLY).setSide(Side.BOTH).addMixinClasses("forge.MixinMinecraftForge")
            .setApplyIf(() -> FixesConfig.fixEffectRendererClassTypo).addTargetedMod(TargetedMod.VANILLA)),
    NORTHWEST_BIAS_FIX(new Builder("Fix Northwest Bias").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinRandomPositionGenerator").setApplyIf(() -> FixesConfig.fixNorthWestBias)
            .addTargetedMod(TargetedMod.VANILLA)),
    SPEEDUP_VANILLA_FURNACE(new Builder("Speedup Vanilla Furnace").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinFurnaceRecipes").setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace)
            .addTargetedMod(TargetedMod.VANILLA).addTargetedMod(TargetedMod.GTNHLIB)),
    GAMEOVER_GUI_LOCKED_DISABLED(new Builder("Fix Gameover GUI").setPhase(Phase.EARLY).setSide(Side.CLIENT)
            .addMixinClasses("minecraft.MixinGuiGameOver").setApplyIf(() -> FixesConfig.fixGuiGameOver)
            .addTargetedMod(TargetedMod.VANILLA)),
    PREVENT_PICKUP_LOOT(new Builder("Prevent monsters from picking up loot").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinEntityLivingPickup").setApplyIf(() -> TweaksConfig.preventPickupLoot)
            .addTargetedMod(TargetedMod.VANILLA)),
    DROP_PICKED_LOOT_ON_DESPAWN(new Builder("Drop picked up loot on despawn").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinEntityLivingDrop").setApplyIf(() -> TweaksConfig.dropPickedLootOnDespawn)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_HOPPER_HIT_BOX(new Builder("Fix Vanilla Hopper hit box").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinBlockHopper").setApplyIf(() -> FixesConfig.fixHopperHitBox)
            .addTargetedMod(TargetedMod.VANILLA)),
    TILE_RENDERER_PROFILER_DISPATCHER(new Builder("TileEntity Render Dispatcher Fix").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.profiler.TileEntityRendererDispatcherMixin").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.enableTileRendererProfiler).addTargetedMod(TargetedMod.VANILLA)),
    TILE_RENDERER_PROFILER_MINECRAFT(new Builder("Tile Entity Render Profiler").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.profiler.MinecraftMixin").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.enableTileRendererProfiler).addTargetedMod(TargetedMod.VANILLA)),
    DIMENSION_CHANGE_FIX(new Builder("Dimension Change Heart Fix").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinServerConfigurationManager", "minecraft.MixinEntityPlayerMP")
            .setApplyIf(() -> FixesConfig.fixDimensionChangeHearts).addTargetedMod(TargetedMod.VANILLA)),
    FIX_EATING_STACKED_STEW(new Builder("Stacked Mushroom Stew Eating Fix").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinItemSoup").setApplyIf(() -> FixesConfig.fixEatingStackedStew)
            .addTargetedMod(TargetedMod.VANILLA)),
    INCREASE_PARTICLE_LIMIT(new Builder("Increase Particle Limit").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEffectRenderer").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.increaseParticleLimit).addTargetedMod(TargetedMod.VANILLA)),
    ENLARGE_POTION_ARRAY(new Builder("Make the Potion array larger").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinPotion").setApplyIf(() -> FixesConfig.enlargePotionArray)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_POTION_LIMIT(new Builder("Fix Potion Limit").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinPotionEffect").setApplyIf(() -> FixesConfig.fixPotionLimit)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_HOPPER_VOIDING_ITEMS(new Builder("Fix Hopper Voiding Items").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinTileEntityHopper").setApplyIf(() -> FixesConfig.fixHopperVoidingItems)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_HUGE_CHAT_KICK(new Builder("Fix huge chat kick").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinS02PacketChat").setApplyIf(() -> FixesConfig.fixHugeChatKick)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_BOGUS_INTEGRATED_SERVER_NPE(new Builder("Fix bogus FMLProxyPacket NPEs on integrated server crashes")
            .setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses(
                    "fml.MixinFMLProxyPacket",
                    "fml.MixinNetworkDispatcher",
                    "minecraft.NetworkManagerAccessor")
            .setApplyIf(() -> FixesConfig.fixBogusIntegratedServerNPEs).addTargetedMod(TargetedMod.VANILLA)),

    FIX_LOGIN_DIMENSION_ID_OVERFLOW(
            new Builder("Fix dimension id overflowing when a player first logins on a server").setPhase(Phase.EARLY)
                    .setSide(Side.BOTH).addMixinClasses("minecraft.packets.MixinS01PacketJoinGame_FixDimensionID")
                    .setApplyIf(() -> FixesConfig.fixLoginDimensionIDOverflow).addTargetedMod(TargetedMod.VANILLA)),

    FIX_WORLD_SERVER_LEAKING_UNLOADED_ENTITIES(new Builder("Fix world server leaking unloaded entities")
            .setPhase(Phase.EARLY).setSide(Side.BOTH).addMixinClasses("minecraft.MixinWorldServerUpdateEntities")
            .setApplyIf(() -> FixesConfig.fixWorldServerLeakingUnloadedEntities).addTargetedMod(TargetedMod.VANILLA)),

    FIX_REDSTONE_TORCH_WORLD_LEAK(new Builder("Fix world leak in redstone torch").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("minecraft.MixinBlockRedstoneTorch")
            .setApplyIf(() -> FixesConfig.fixRedstoneTorchWorldLeak).addTargetedMod(TargetedMod.VANILLA)
            .addExcludedMod(TargetedMod.BUGTORCH)),
    FIX_ARROW_WRONG_LIGHTING(new Builder("Fix arrow wrong lighting").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinRendererLivingEntity").setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixGlStateBugs).addTargetedMod(TargetedMod.VANILLA)),
    FIX_RESIZABLE_FULLSCREEN(new Builder("Fix Resizable Fullscreen").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinMinecraft_ResizableFullscreen").setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixResizableFullscreen).addTargetedMod(TargetedMod.VANILLA)),
    FIX_UNFOCUSED_FULLSCREEN(new Builder("Fix Unfocused Fullscreen").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinMinecraft_UnfocusedFullscreen").setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixUnfocusedFullscreen).addTargetedMod(TargetedMod.VANILLA)),
    FIX_RENDERERS_WORLD_LEAK(new Builder("Fix Renderers World Leak").setPhase(Phase.EARLY)
            .addMixinClasses(
                    "minecraft.MixinMinecraft_ClearRenderersWorldLeak",
                    "minecraft.MixinRenderGlobal_FixWordLeak")
            .setSide(Side.CLIENT).setApplyIf(() -> FixesConfig.fixRenderersWorldLeak)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_OPTIFINE_CHUNKLOADING_CRASH(new Builder("Fix Optifine Chunkloading Crash").setPhase(Phase.EARLY)
            .setApplyIf(() -> FixesConfig.fixOptifineChunkLoadingCrash).setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA).addTargetedMod(TargetedMod.OPTIFINE)
            .addMixinClasses("minecraft.MixinGameSettings_FixOFChunkLoading")),
    ADD_TOGGLE_DEBUG_MESSAGE(new Builder("Toggle Debug Message").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinMinecraft_ToggleDebugMessage").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.addToggleDebugMessage).addTargetedMod(TargetedMod.VANILLA)),
    SPEEDUP_VANILLA_ANIMATIONS(new Builder("Speedup Vanilla Animations").setPhase(Phase.EARLY)
            .setApplyIf(() -> FixesConfig.speedupAnimations).setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .addExcludedMod(TargetedMod.ANGELICA).addMixinClasses(
                    "minecraft.textures.client.MixinTextureAtlasSprite",
                    "minecraft.textures.client.MixinTextureMap",
                    "minecraft.textures.client.MixinBlockFire",
                    "minecraft.textures.client.MixinMinecraftForgeClient",
                    "minecraft.textures.client.MixinChunkCache",
                    "minecraft.textures.client.MixinRenderBlocks",
                    "minecraft.textures.client.MixinRenderBlockFluid",
                    "minecraft.textures.client.MixinWorldRenderer",
                    "minecraft.textures.client.MixinRenderItem")),
    SPEEDUP_VANILLA_ANIMATIONS_FC(new Builder("Speedup Vanilla Animations - Fastcraft").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.textures.client.fastcraft.MixinTextureMap").setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.speedupAnimations).addTargetedMod(TargetedMod.VANILLA)
            .addTargetedMod(TargetedMod.FASTCRAFT).addExcludedMod(TargetedMod.OPTIFINE)),
    OPTIMIZE_TEXTURE_LOADING(new Builder("Optimize Texture Loading").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.textures.client.MixinTextureUtil").addTargetedMod(TargetedMod.VANILLA)
            .addExcludedMod(TargetedMod.ANGELICA).setApplyIf(() -> SpeedupsConfig.optimizeTextureLoading)
            .setSide(Side.CLIENT)),
    HIDE_POTION_PARTICLES(new Builder("Hide Potion Particles").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityLivingBase_HidePotionParticles").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.hidePotionParticlesFromSelf).addTargetedMod(TargetedMod.VANILLA)),
    DIMENSION_MANAGER_DEBUG(new Builder("Dimension Manager Debug").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinDimensionManager").setApplyIf(() -> DebugConfig.dimensionManagerDebug)
            .addTargetedMod(TargetedMod.VANILLA)),
    OPTIMIZE_TILEENTITY_REMOVAL(new Builder("Optimize TileEntity Removal").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinWorldUpdateEntities")
            .setApplyIf(() -> SpeedupsConfig.optimizeTileentityRemoval).addTargetedMod(TargetedMod.VANILLA)
            .addExcludedMod(TargetedMod.FASTCRAFT).addExcludedMod(TargetedMod.BUKKIT)),
    FIX_POTION_ITERATING(new Builder("Fix Potion Iterating").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinEntityLivingBase_FixPotionException")
            .setApplyIf(() -> FixesConfig.fixPotionIterating).addTargetedMod(TargetedMod.VANILLA)),
    ENHANCE_NIGHT_VISION(new Builder("Remove the blueish sky tint from night vision").setSide(Side.CLIENT)
            .setPhase(Phase.EARLY).addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> TweaksConfig.enhanceNightVision)
            .addMixinClasses("minecraft.MixinEntityRenderer_EnhanceNightVision")),
    OPTIMIZE_ASMDATATABLE_INDEX(new Builder("Optimize ASM DataTable Index").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("fml.MixinASMDataTable").setApplyIf(() -> SpeedupsConfig.optimizeASMDataTable)
            .addTargetedMod(TargetedMod.VANILLA)),
    SQUASH_BED_ERROR_MESSAGE(new Builder("Stop \"You can only sleep at night\" message filling the chat")
            .addMixinClasses("minecraft.MixinNetHandlerPlayClient").addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> FixesConfig.squashBedErrorMessage).setPhase(Phase.EARLY).setSide(Side.CLIENT)),
    CHUNK_SAVE_CME_DEBUG(new Builder("Add debugging code to Chunk Save CME").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinNBTTagCompound").setApplyIf(() -> DebugConfig.chunkSaveCMEDebug)
            .addTargetedMod(TargetedMod.VANILLA)),
    RENDER_DEBUG(new Builder("Render Debug").setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinRenderGlobal")
            .setSide(Side.CLIENT).setApplyIf(() -> DebugConfig.renderDebug).addTargetedMod(TargetedMod.VANILLA)),
    STATIC_LAN_PORT(new Builder("Static Lan Port").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.server.MixinHttpUtil").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.enableDefaultLanPort).addTargetedMod(TargetedMod.VANILLA)),
    CROSSHAIR_THIRDPERSON(new Builder("Crosshairs thirdperson").setPhase(Phase.EARLY)
            .addMixinClasses("forge.MixinGuiIngameForge_CrosshairThirdPerson").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.hideCrosshairInThirdPerson).addTargetedMod(TargetedMod.VANILLA)),
    DONT_INVERT_CROSSHAIR_COLORS(new Builder("Don't invert crosshair colors").setPhase(Phase.EARLY)
            .addMixinClasses("forge.MixinGuiIngameForge_CrosshairInvertColors").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.dontInvertCrosshairColor).addTargetedMod(TargetedMod.VANILLA)),
    FIX_OPENGUIHANDLER_WINDOWID(new Builder("Fix OpenGuiHandler").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("fml.MixinOpenGuiHandler").setApplyIf(() -> FixesConfig.fixForgeOpenGuiHandlerWindowId)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_KEYBIND_CONFLICTS(new Builder("Trigger all conflicting keybinds").setPhase(Phase.EARLY).setSide(Side.CLIENT)
            .addMixinClasses("minecraft.MixinKeyBinding", "minecraft.MixinMinecraft_UpdateKeys")
            .setApplyIf(() -> FixesConfig.triggerAllConflictingKeybindings).addTargetedMod(TargetedMod.VANILLA)),
    REMOVE_SPAWN_MINECART_SOUND(new Builder("Remove sound when spawning a minecart").setPhase(Phase.EARLY)
            .setSide(Side.CLIENT).addMixinClasses("minecraft.MixinWorldClient").addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> TweaksConfig.removeSpawningMinecartSound)),
    MACOS_KEYS_TEXTFIELD_SHORTCUTS(new Builder("Macos use CMD to copy/select/delete text")
            .addMixinClasses("minecraft.MixinGuiTextField").addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(
                    () -> System.getProperty("os.name").toLowerCase().contains("mac")
                            && TweaksConfig.enableMacosCmdShortcuts)
            .setPhase(Phase.EARLY).setSide(Side.CLIENT)),
    FIX_FONT_RENDERER_LINEWRAP_RECURSION(
            new Builder("Replace recursion with iteration in FontRenderer line wrapping code")
                    .addMixinClasses("minecraft.MixinFontRenderer").addTargetedMod(TargetedMod.VANILLA)
                    .setApplyIf(() -> FixesConfig.fixFontRendererLinewrapRecursion).setPhase(Phase.EARLY)
                    .setSide(Side.CLIENT)),
    BED_MESSAGE_ABOVE_HOTBAR(new Builder("Bed Message Above Hotbar").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinBlockBed").setSide(Side.BOTH)
            .setApplyIf(() -> TweaksConfig.bedMessageAboveHotbar).addTargetedMod(TargetedMod.VANILLA)),
    FIX_PLAYER_SKIN_FETCHING(new Builder("Fix player skin fetching").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinAbstractClientPlayer", "minecraft.MixinThreadDownloadImageData")
            .setSide(Side.CLIENT).setApplyIf(() -> FixesConfig.fixPlayerSkinFetching)
            .addTargetedMod(TargetedMod.VANILLA)),
    VALIDATE_PACKET_ENCODING_BEFORE_SENDING(new Builder("Validate packet encoding before sending").setPhase(Phase.EARLY)
            .addMixinClasses(
                    "minecraft.packets.MixinDataWatcher",
                    "minecraft.packets.MixinS3FPacketCustomPayload_Validation")
            .setSide(Side.BOTH).setApplyIf(() -> FixesConfig.validatePacketEncodingBeforeSending)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_FLUID_CONTAINER_REGISTRY_KEY(new Builder("Fix Forge fluid container registry key").setPhase(Phase.EARLY)
            .addMixinClasses("forge.FluidContainerRegistryAccessor", "forge.MixinFluidRegistry").setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixFluidContainerRegistryKey).addTargetedMod(TargetedMod.VANILLA)),
    CHANGE_MAX_NETWORK_NBT_SIZE_LIMIT(new Builder("Modify the maximum NBT size limit as received from network packets")
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinPacketBuffer").setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.changeMaxNetworkNbtSizeLimit).addTargetedMod(TargetedMod.VANILLA)),

    INCREASE_PACKET_SIZE_LIMIT(new Builder("Increase the packet size limit from 2MiB to a theoretical maximum of 4GiB")
            .setPhase(Phase.EARLY)
            .addMixinClasses(
                    "minecraft.MixinMessageSerializer2",
                    "minecraft.MixinMessageDeserializer2",
                    "minecraft.packets.MixinS3FPacketCustomPayload_LengthLimit")
            .setSide(Side.BOTH).setApplyIf(() -> FixesConfig.increasePacketSizeLimit)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_XRAY_BLOCK_WITHOUT_COLLISION_AABB(new Builder("Fix Xray through block without collision boundingBox")
            .addMixinClasses("minecraft.MixinBlock_FixXray", "minecraft.MixinWorld_FixXray")
            .setApplyIf(() -> FixesConfig.fixPerspectiveCamera).addTargetedMod(TargetedMod.VANILLA)
            .setPhase(Phase.EARLY).setSide(Side.BOTH)),
    DISABLE_CREATIVE_TAB_ALL_SEARCH(new Builder("Disable the creative tab with search bar").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiContainerCreative").setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.removeCreativeSearchTab).addTargetedMod(TargetedMod.NOTENOUGHITEMS)),
    FIX_CHAT_COLOR_WRAPPING(new Builder("Fix wrapped chat lines missing colors").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiNewChat_FixColorWrapping").setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixChatWrappedColors).addTargetedMod(TargetedMod.VANILLA)),
    COMPACT_CHAT(new Builder("Compact chat").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiNewChat_CompactChat").setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.compactChat).addTargetedMod(TargetedMod.VANILLA)),
    NETTY_PATCH(new Builder("Fix NPE in Netty's Bootstrap class").addMixinClasses("netty.MixinBootstrap")
            .setPhase(Phase.EARLY).setSide(Side.CLIENT).setApplyIf(() -> FixesConfig.fixNettyNPE)
            .addTargetedMod(TargetedMod.VANILLA)),
    MODERN_PICK_BLOCK(new Builder("Allows pick block to pull items from your inventory")
            .addTargetedMod(TargetedMod.VANILLA).setSide(Side.CLIENT).setPhase(Phase.EARLY)
            .addMixinClasses("forge.MixinForgeHooks_ModernPickBlock").setApplyIf(() -> TweaksConfig.modernPickBlock)),
    TESSELATOR_PRESERVE_QUAD_ORDER(new Builder("Preserve the rendering order of layered quads on terrain pass 1")
            .addTargetedMod(TargetedMod.VANILLA).setSide(Side.CLIENT).setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinTessellator").setApplyIf(() -> FixesConfig.fixPreserveQuadOrder)),
    FAST_BLOCK_PLACING(new Builder("Allows blocks to be placed faster").addTargetedMod(TargetedMod.VANILLA)
            .setSide(Side.CLIENT).setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinMinecraft_FastBlockPlacing")
            .setApplyIf(() -> true)), // Always apply, config handled in mixin
    FIX_NEGATIVE_KELVIN(new Builder("Fix the local temperature can go below absolute zero")
            .addTargetedMod(TargetedMod.VANILLA).setSide(Side.BOTH).setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinBiomeGenBase").setApplyIf(() -> FixesConfig.fixNegativeKelvin)),

    SPIGOT_EXTENDED_CHUNKS(new Builder("Spigot-style extended chunk format to remove the 2MB chunk size limit")
            .addTargetedMod(TargetedMod.VANILLA).addExcludedMod(TargetedMod.BUKKIT).setSide(Side.BOTH)
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinRegionFile")
            .setApplyIf(() -> FixesConfig.remove2MBChunkLimit)),

    AUTOSAVE_INTERVAL(new Builder("Sets the auto save interval in ticks").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addTargetedMod(TargetedMod.VANILLA)
            .addMixinClasses("minecraft.server.MixinMinecraftServer_AutoSaveInterval")
            .setApplyIf(() -> TweaksConfig.autoSaveInterval != 900)),

    LIGHTER_WATER(new Builder("Decreases water opacity from 3 to 1, like in modern").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addTargetedMod(TargetedMod.VANILLA).addMixinClasses("minecraft.MixinBlock_LighterWater")
            .setApplyIf(() -> TweaksConfig.useLighterWater)),

    EARLY_CHUNK_TILE_COORDINATE_CHECK(
            new Builder("Checks saved TileEntity coordinates earlier to provide a more descriptive error message")
                    .setPhase(Phase.EARLY).setSide(Side.BOTH).addTargetedMod(TargetedMod.VANILLA)
                    .addMixinClasses("minecraft.MixinChunk")
                    .setApplyIf(() -> FixesConfig.earlyChunkTileCoordinateCheck)),

    FIX_DUPLICATE_SOUNDS(new Builder("Fix duplicate sounds being played when you close a gui while one is playing")
            .setPhase(Phase.EARLY).setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .addMixinClasses("minecraft.MixinMinecraft_FixDuplicateSounds")
            .setApplyIf(() -> FixesConfig.fixDuplicateSounds)),

    ADD_MOD_ITEM_STATS(new Builder("Add stats for modded items").addMixinClasses("fml.MixinGameRegistry")
            .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> TweaksConfig.addModItemStats).setPhase(Phase.EARLY)
            .setSide(Side.BOTH)),

    ADD_MOD_ENTITY_STATS(new Builder("Add stats for modded entities").addMixinClasses("minecraft.MixinStatList")
            .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> TweaksConfig.addModEntityStats).setPhase(Phase.EARLY)
            .setSide(Side.BOTH)),

    ADD_MOD_ENTITY_STATS_CLIENT(new Builder("Add stats for modded entities (client side)")
            .addMixinClasses("minecraft.MixinStatsMobsList", "minecraft.MixinStatsBlock", "minecraft.MixinStatsItem")
            .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> TweaksConfig.addModEntityStats).setPhase(Phase.EARLY)
            .setSide(Side.CLIENT)),

    FIX_SLASH_COMMAND(
            new Builder("Fix forge command handler not checking for a / and also not running commands with any case")
                    .setPhase(Phase.EARLY).setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
                    .addMixinClasses("minecraft.MixinClientCommandHandler_CommandFix")
                    .setApplyIf(() -> FixesConfig.fixSlashCommands)),

    FIX_CASE_COMMAND(new Builder("Fix the command handler not allowing you to run commands typed in any case")
            .setPhase(Phase.EARLY).setSide(Side.BOTH).addTargetedMod(TargetedMod.VANILLA)
            .addMixinClasses("minecraft.MixinCommandHandler_CommandFix").setApplyIf(() -> FixesConfig.fixCaseCommands)),
    FIX_GAMESETTINGS_OUTOFBOUNDS(new Builder("Fix array out of bound error in GameSettings menu").setPhase(Phase.EARLY)
            .setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .addMixinClasses("minecraft.MixinGameSettings_FixArrayOutOfBounds")
            .setApplyIf(() -> FixesConfig.fixGameSettingsArrayOutOfBounds).addExcludedMod(TargetedMod.LWJGL3IFY)),

    LIMIT_RECURSIVE_BLOCK_UPDATE_DEPTH(new Builder(
            "Limit the number of recursive cascading block updates during world generation to prevent stack overflow crashes")
                    .setPhase(Phase.EARLY).setSide(Side.BOTH).addTargetedMod(TargetedMod.VANILLA)
                    .addMixinClasses("minecraft.MixinWorldServer_LimitUpdateRecursion")
                    .setApplyIf(() -> FixesConfig.limitRecursiveBlockUpdateDepth >= 0)),

    FAST_CHUNK_LOADING(new Builder("Lightly threads chunk generation and loading").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addTargetedMod(TargetedMod.VANILLA)
            .addMixinClasses(
                    "minecraft.fastload.MixinIntCache",
                    "minecraft.fastload.MixinWorldChunkManager",
                    "minecraft.fastload.MixinWorldServer")
            .setApplyIf(() -> SpeedupsConfig.fastChunkHandling)),

    // Ic2 adjustments
    IC2_UNPROTECTED_GET_BLOCK_FIX(new Builder("IC2 Kinetic Fix").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("ic2.MixinIc2WaterKinetic").setApplyIf(() -> FixesConfig.fixIc2UnprotectedGetBlock)
            .addTargetedMod(TargetedMod.IC2)),
    IC2_DIRECT_INV_ACCESS(new Builder("IC2 Direct Inventory Access Fix").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("ic2.MixinItemCropSeed", "ic2.MixinTileEntityCrop")
            .setApplyIf(() -> FixesConfig.fixIc2DirectInventoryAccess).addTargetedMod(TargetedMod.IC2)),
    IC2_NIGHT_VISION_NANO(new Builder("IC2 Nightvision Fix").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses(
                    "ic2.MixinIc2NanoSuitNightVision",
                    "ic2.MixinIc2QuantumSuitNightVision",
                    "ic2.MixinIc2NightVisionGoggles")
            .setApplyIf(() -> FixesConfig.fixIc2Nightvision).addTargetedMod(TargetedMod.IC2)),
    IC2_REACTOR_DUPE(new Builder("IC2 Reactor Dupe Fix").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("ic2.MixinTileEntityReactorChamberElectricNoDupe")
            .setApplyIf(() -> FixesConfig.fixIc2ReactorDupe).addTargetedMod(TargetedMod.IC2)),
    IC2_REACTOR_INVENTORY_SPEEDUP(new Builder("IC2 Reactor Inventory Speedup Fix").setPhase(Phase.EARLY)
            .setSide(Side.BOTH).addMixinClasses("ic2.MixinTileEntityReactorChamberElectricInvSpeedup")
            .setApplyIf(() -> FixesConfig.optimizeIc2ReactorInventoryAccess).addTargetedMod(TargetedMod.IC2)),
    HIDE_IC2_REACTOR_COOLANT_SLOTS(new Builder("IC2 Reactory Fix").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("ic2.MixinTileEntityNuclearReactorElectric")
            .setApplyIf(() -> TweaksConfig.hideIc2ReactorSlots).addTargetedMod(TargetedMod.IC2)),
    IC2_HAZMAT(new Builder("Hazmat").setPhase(Phase.LATE).setSide(Side.BOTH).addMixinClasses("ic2.MixinIc2Hazmat")
            .setApplyIf(() -> FixesConfig.fixIc2Hazmat).addTargetedMod(TargetedMod.IC2).addTargetedMod(TargetedMod.GT5U)
            .addExcludedMod(TargetedMod.GT6)),
    IC2_FLUID_CONTAINER_TOOLTIP(new Builder("IC2 Fluid Container Tooltip Fix").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("ic2.MixinItemIC2FluidContainer")
            .setApplyIf(() -> TweaksConfig.displayIc2FluidLocalizedName).addTargetedMod(TargetedMod.IC2)),
    IC2_FLUID_RENDER_FIX(new Builder("IC2 Fluid Render Fix").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("ic2.textures.MixinRenderLiquidCell").setApplyIf(() -> FixesConfig.speedupAnimations)
            .addTargetedMod(TargetedMod.IC2).addExcludedMod(TargetedMod.ANGELICA)),
    IC2_HOVER_MODE_FIX(new Builder("IC2 Hover Mode Fix").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("ic2.MixinIc2QuantumSuitHoverMode").setApplyIf(() -> FixesConfig.fixIc2HoverMode)
            .addTargetedMod(TargetedMod.IC2)),
    IC2_ARMOR_LAG_FIX(new Builder("IC2 Armor Lag Fix").setPhase(Phase.LATE).setSide(Side.BOTH)
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
            new Builder("IC2 Resource Pack Translation Fix").setPhase(Phase.EARLY).setSide(Side.CLIENT)
                    .addMixinClasses("fml.MixinLanguageRegistry", "fml.MixinFMLClientHandler", "ic2.MixinLocalization")
                    .setApplyIf(() -> FixesConfig.fixIc2ResourcePackTranslation).addTargetedMod(TargetedMod.IC2)),
    IC2_CROP_TRAMPLING_FIX(new Builder("IC2 Crop Trampling Fix").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("ic2.MixinIC2TileEntityCrop").setApplyIf(() -> FixesConfig.fixIc2CropTrampling)
            .addTargetedMod(TargetedMod.IC2)),

    // Disable update checkers
    BIBLIOCRAFT_UPDATE_CHECK(new Builder("Yeet Bibliocraft Update Check").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .addMixinClasses("bibliocraft.MixinVersionCheck").setApplyIf(() -> FixesConfig.removeUpdateChecks)
            .addTargetedMod(TargetedMod.BIBLIOCRAFT)),
    COFH_CORE_UPDATE_CHECK(new Builder("Yeet COFH Core Update Check").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("cofhcore.MixinCoFHCoreUpdateCheck").setApplyIf(() -> FixesConfig.removeUpdateChecks)
            .addTargetedMod(TargetedMod.COFH_CORE)),
    DAMAGE_INDICATORS_UPDATE_CHECK(new Builder("Yeet Damage Indicators Update Check").setPhase(Phase.LATE)
            .setSide(Side.CLIENT).addMixinClasses("damageindicators.MixinDIClientProxy")
            .setApplyIf(() -> FixesConfig.removeUpdateChecks).addTargetedMod(TargetedMod.DAMAGE_INDICATORS)),

    // Railcraft Anchors
    WAKE_ANCHORS_ON_LOGIN_PASSIVE(new Builder("Wake passive anchors on login").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("railcraft.MixinTileAnchorPassive").setApplyIf(() -> TweaksConfig.installAnchorAlarm)
            .addTargetedMod(TargetedMod.RAILCRAFT)),
    WAKE_ANCHORS_ON_LOGIN_PERSONAL(new Builder("Wake person anchors on login").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("railcraft.MixinTileAnchorPersonal").setApplyIf(() -> TweaksConfig.installAnchorAlarm)
            .addTargetedMod(TargetedMod.RAILCRAFT)),

    // Hunger overhaul
    HUNGER_OVERHAUL_LOW_STAT_EFFECT(new Builder("Patch unintended low stat effects").setPhase(Phase.LATE)
            .setSide(Side.BOTH).addMixinClasses("hungeroverhaul.MixinHungerOverhaulLowStatEffect")
            .setApplyIf(() -> FixesConfig.fixHungerOverhaul).addTargetedMod(TargetedMod.HUNGER_OVERHAUL)),
    HUNGER_OVERHAUL_REGEN(new Builder("Patch Regen").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("hungeroverhaul.MixinHungerOverhaulHealthRegen")
            .setApplyIf(() -> FixesConfig.fixHungerOverhaul).addTargetedMod(TargetedMod.HUNGER_OVERHAUL)),
    HUNGER_OVERHAUL_0_HUNGER(new Builder("Fix some items restore 0 hunger").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("hungeroverhaul.MixinHungerOverhaulModuleHarvestCraft")
            .setApplyIf(() -> FixesConfig.fixHungerOverhaulRestore0Hunger).addTargetedMod(TargetedMod.HUNGER_OVERHAUL)
            .addTargetedMod(TargetedMod.HARVESTCRAFT)),

    // Thaumcraft
    ADD_CV_SUPPORT_TO_WAND_PEDESTAL(new Builder("CV Support for Wand Pedestal").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("thaumcraft.MixinTileWandPedestal")
            .setApplyIf(() -> TweaksConfig.addCVSupportToWandPedestal).addTargetedMod(TargetedMod.THAUMCRAFT)),
    FIX_ASPECT_SORTING(new Builder("Fix Thaumcraft Aspects not being sorted by name")
            .addMixinClasses(
                    "thaumcraft.MixinGuiResearchRecipe",
                    "thaumcraft.MixinGuiResearchTable",
                    "thaumcraft.MixinGuiThaumatorium",
                    "thaumcraft.MixinItem_SortAspectsByName")
            .setPhase(Phase.LATE).setSide(Side.CLIENT).setApplyIf(() -> FixesConfig.fixThaumcraftAspectSorting)
            .addTargetedMod(TargetedMod.THAUMCRAFT)),
    FIX_GOLEM_MARKER_LOADING(new Builder("Fix golem marker loading failure when dimensionId larger than MAX_BYTE")
            .setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("thaumcraft.MixinEntityGolemBase", "thaumcraft.MixinItemGolemBell")
            .setApplyIf(() -> FixesConfig.fixThaumcraftGolemMarkerLoading).addTargetedMod(TargetedMod.THAUMCRAFT)),

    FIX_WORLD_COORDINATE_HASHING_METHOD(new Builder("Implement a proper hashing method for WorldCoordinates")
            .addMixinClasses("thaumcraft.MixinWorldCoordinates").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixThaumcraftWorldCoordinatesHashingMethod)
            .addTargetedMod(TargetedMod.THAUMCRAFT)),

    FIX_MAGICAL_LEAVES_LAG(new Builder("Fix Thaumcraft leaves frequent ticking")
            .addMixinClasses("thaumcraft.MixinBlockMagicalLeaves", "thaumcraft.MixinBlockMagicalLog")
            .setPhase(Phase.LATE).setSide(Side.BOTH).setApplyIf(() -> FixesConfig.fixThaumcraftLeavesLag)
            .addTargetedMod(TargetedMod.THAUMCRAFT)),
    FIX_THAUMCRAFT_VIS_DUPLICATION(new Builder("Fix Thaumcraft Vis Duplication")
            .addMixinClasses("thaumcraft.MixinTileWandPedestal_VisDuplication").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixWandPedestalVisDuplication).addTargetedMod(TargetedMod.THAUMCRAFT)),
    FIX_NULL_HANDLING_ITEMWISPESSENCE_CLIENT(new Builder("Fix handling of null stacks in ItemWispEssence")
            .addMixinClasses("thaumcraft.MixinItemWispEssence_Client").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixNullHandlingItemWispEssence).addTargetedMod(TargetedMod.THAUMCRAFT)),
    FIX_NULL_HANDLING_ITEMWISPESSENCE_BOTH(new Builder("Fix handling of null stacks in ItemWispEssence")
            .addMixinClasses("thaumcraft.MixinItemWispEssence_Both").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixNullHandlingItemWispEssence).addTargetedMod(TargetedMod.THAUMCRAFT)),

    // BOP
    FIX_QUICKSAND_XRAY(new Builder("Fix Xray through block without collision boundingBox").setPhase(Phase.LATE)
            .setSide(Side.BOTH).addMixinClasses("biomesoplenty.MixinBlockMud_FixXray")
            .setApplyIf(() -> FixesConfig.fixPerspectiveCamera).addTargetedMod(TargetedMod.BOP)),
    DEDUPLICATE_FORESTRY_COMPAT_IN_BOP(new Builder("BOP Forestry Compat").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("biomesoplenty.MixinForestryIntegration")
            .setApplyIf(() -> FixesConfig.deduplicateForestryCompatInBOP).addTargetedMod(TargetedMod.BOP)),
    SPEEDUP_BOP_BIOME_FOG(new Builder("BOP Biome Fog").addMixinClasses("biomesoplenty.MixinFogHandler")
            .setPhase(Phase.LATE).setSide(Side.CLIENT).setApplyIf(() -> SpeedupsConfig.speedupBOPFogHandling)
            .addTargetedMod(TargetedMod.BOP)),
    SPEEDUP_BOP_BIOME_FOG_ACCESSOR(new Builder("BOP Biome Fog Accessor")
            .addMixinClasses("biomesoplenty.AccessorFogHandler").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .setApplyIf(() -> SpeedupsConfig.speedupBOPFogHandling).addTargetedMod(TargetedMod.BOP)),
    BIG_FIR_TREES(new Builder("BOP Fir Trees").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("biomesoplenty.MixinBlockBOPSapling").setApplyIf(() -> TweaksConfig.makeBigFirsPlantable)
            .addTargetedMod(TargetedMod.BOP)),
    JAVA12_BOP(new Builder("BOP Java12-safe reflection").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("biomesoplenty.MixinBOPBiomes").addMixinClasses("biomesoplenty.MixinBOPReflectionHelper")
            .setApplyIf(() -> FixesConfig.java12BopCompat).addTargetedMod(TargetedMod.BOP)),
    DISABLE_QUICKSAND_GENERATION(new Builder("Disable BOP quicksand").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("biomesoplenty.MixinDisableQuicksandGeneration")
            .setApplyIf(() -> TweaksConfig.removeBOPQuicksandGeneration).addTargetedMod(TargetedMod.BOP)),
    // COFH
    COFH_REMOVE_TE_CACHE(
            new Builder("Remove CoFH tile entity cache").addMixinClasses("minecraft.MixinWorld_CoFH_TE_Cache")
                    .setSide(Side.BOTH).setApplyIf(() -> ASMConfig.cofhWorldTransformer)
                    .addTargetedMod(TargetedMod.COFH_CORE).setPhase(Phase.EARLY)),
    MFR_FIX_COFH_VALIDATE(new Builder("Remove CoFH TE cache usage from MFR")
            .addMixinClasses(
                    "minefactoryreloaded.MixinTileEntityBase",
                    "minefactoryreloaded.MixinTileEntityRedNetCable")
            .addTargetedMod(TargetedMod.MINEFACTORY_RELOADED).setApplyIf(() -> ASMConfig.cofhWorldTransformer)
            .setPhase(Phase.LATE).setSide(Side.BOTH)),
    TE_FIX_COFH_VALIDATE(new Builder("Remove CoFH TE cache usage from TE")
            .addMixinClasses("thermalexpansion.MixinTileInventoryTileLightFalse")
            .addTargetedMod(TargetedMod.THERMALEXPANSION).setApplyIf(() -> ASMConfig.cofhWorldTransformer)
            .setPhase(Phase.LATE).setSide(Side.BOTH)),
    FIX_ORE_DICT_NPE(new Builder("Fix NPE in OreDictionaryArbiter")
            .addMixinClasses("cofhcore.MixinOreDictionaryArbiter").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addTargetedMod(TargetedMod.COFH_CORE).setApplyIf(() -> FixesConfig.fixCofhOreDictNPE)),

    // Immersive engineering
    JAVA12_IMMERSIVE_ENGINERRING(new Builder("Immersive Engineering Java-12 safe potion array resizing")
            .setPhase(Phase.LATE).setSide(Side.BOTH).addMixinClasses("immersiveengineering.MixinIEPotions")
            .setApplyIf(() -> FixesConfig.java12ImmersiveEngineeringCompat)
            .addTargetedMod(TargetedMod.IMMERSIVE_ENGINENEERING)),
    JAVA12_MINE_CHEM(new Builder("Minechem Java-12 safe potion array resizing").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("minechem.MixinPotionInjector").setApplyIf(() -> FixesConfig.java12MineChemCompat)
            .addTargetedMod(TargetedMod.MINECHEM)),

    // MrTJPCore (Project Red)
    FIX_HUD_LIGHTING_GLITCH(new Builder("HUD Lighting glitch").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("mrtjpcore.MixinFXEngine").setApplyIf(() -> TweaksConfig.fixHudLightingGlitch)
            .addTargetedMod(TargetedMod.MRTJPCORE)),
    FIX_POPPING_OFF(new Builder("Fix Popping Off").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("mrtjpcore.MixinPlacementLib").setApplyIf(() -> TweaksConfig.fixComponentsPoppingOff)
            .addTargetedMod(TargetedMod.MRTJPCORE)),

    // Automagy
    IMPLEMENTS_CONTAINER_FOR_THIRSTY_TANK(new Builder("Thirsty Tank Container").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("automagy.MixinItemBlockThirstyTank").setApplyIf(() -> TweaksConfig.thirstyTankContainer)
            .addTargetedMod(TargetedMod.AUTOMAGY)),

    // Better HUD
    FIX_BETTERHUD_ARMOR_BAR(new Builder("Fix better HUD armor display breaking with skulls").setPhase(Phase.LATE)
            .setSide(Side.BOTH).addMixinClasses("betterhud.MixinSkullDurabilityDisplay")
            .setApplyIf(() -> FixesConfig.fixBetterHUDArmorDisplay).addTargetedMod(TargetedMod.BETTERHUD)),

    FIX_BETTERHUD_HEARTS_FREEZE(new Builder("Fix better HUD freezing the game when trying to render high amounts of hp")
            .setPhase(Phase.LATE).setSide(Side.BOTH).addMixinClasses("betterhud.MixinHealthRender")
            .setApplyIf(() -> FixesConfig.fixBetterHUDHPDisplay).addTargetedMod(TargetedMod.BETTERHUD)),

    // ProjectE
    FIX_FURNACE_ITERATION(new Builder("Speedup Furnaces").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("projecte.MixinObjHandler").setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace)
            .addTargetedMod(TargetedMod.PROJECTE)),

    // LOTR
    FIX_LOTR_FURNACE_ERROR(new Builder("Patches lotr to work with the vanilla furnace speedup").setPhase(Phase.LATE)
            .setSide(Side.BOTH).addMixinClasses("lotr.MixinLOTRRecipes")
            .setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace).addTargetedMod(TargetedMod.VANILLA)
            .addTargetedMod(TargetedMod.GTNHLIB).addTargetedMod(TargetedMod.LOTR)),

    FIX_LOTR_JAVA12(new Builder("Fix lotr java 12+ compat").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses(
                    "lotr.MixinLOTRLogReflection",
                    "lotr.MixinRedirectHuornAI",
                    "lotr.MixinRemoveUnlockFinalField")
            .setApplyIf(() -> FixesConfig.java12LotrCompat).addTargetedMod(TargetedMod.LOTR)),

    // Journeymap
    FIX_JOURNEYMAP_KEYBINDS(new Builder("Fix Journeymap Keybinds").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .addMixinClasses("journeymap.MixinConstants").setApplyIf(() -> FixesConfig.fixJourneymapKeybinds)
            .addTargetedMod(TargetedMod.JOURNEYMAP)),
    FIX_JOURNEYMAP_ILLEGAL_FILE_PATH_CHARACTER(new Builder("Fix Journeymap Illegal File Path Character")
            .setPhase(Phase.LATE).setSide(Side.CLIENT).addMixinClasses("journeymap.MixinWorldData")
            .setApplyIf(() -> FixesConfig.fixJourneymapFilePath).addTargetedMod(TargetedMod.JOURNEYMAP)),

    FIX_JOURNEYMAP_JUMPY_SCROLLING(new Builder("Fix Journeymap jumpy scrolling in the waypoint manager")
            .setPhase(Phase.LATE).setSide(Side.CLIENT).addMixinClasses("journeymap.MixinWaypointManager")
            .setApplyIf(() -> FixesConfig.fixJourneymapJumpyScrolling).addTargetedMod(TargetedMod.JOURNEYMAP)),

    // Xaero's World Map
    FIX_XAEROS_WORLDMAP_SCROLL(
            new Builder("Fix Xaero's World Map map screen scrolling").addMixinClasses("xaeroworldmap.MixinGuiMap")
                    .setPhase(Phase.LATE).setSide(Side.CLIENT).setApplyIf(() -> FixesConfig.fixXaerosWorldMapScroll)
                    .addTargetedMod(TargetedMod.XAEROWORLDMAP).addTargetedMod(TargetedMod.LWJGL3IFY)),

    // Xaero's Minimap
    FIX_XAEROS_MINIMAP_ENTITYDOT(new Builder("Fix Xaero's Minimap player entity dot rendering when arrow is chosen")
            .addMixinClasses("xaerominimap.MixinMinimapRenderer").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixXaerosMinimapEntityDot).addTargetedMod(TargetedMod.XAEROMINIMAP)),

    // Pam's Harvest the Nether
    FIX_IGNIS_FRUIT_AABB(new Builder("Ignis Fruit").setPhase(Phase.LATE).setSide(Side.BOTH)
            .addMixinClasses("harvestthenether.MixinBlockPamFruit").setApplyIf(() -> FixesConfig.fixIgnisFruitAABB)
            .addTargetedMod(TargetedMod.HARVESTTHENETHER)),
    FIX_NETHER_LEAVES_FACE_RENDERING(new Builder("Nether Leaves")
            .addMixinClasses("harvestthenether.MixinBlockNetherLeaves").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixIgnisFruitAABB).addTargetedMod(TargetedMod.HARVESTTHENETHER)),

    // Potion Render Offset Fixes - Various Mods
    FIX_BAUBLES_INVENTORY_OFFSET_WITH_POTIONS(new Builder("Baubles Inventory with Potions")
            .addMixinClasses("baubles.MixinGuiEvents").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.fixPotionRenderOffset).addTargetedMod(TargetedMod.BAUBLES)),
    FIX_GALACTICRAFT_INVENTORY_OFFSET_WITH_POTIONS(new Builder("Galacticraft Inventory with Potions")
            .addMixinClasses("galacticraftcore.MixinGuiExtendedInventory").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.fixPotionRenderOffset).addTargetedMod(TargetedMod.GALACTICRAFT_CORE)),
    FIX_TRAVELLERSGEAR_INVENTORY_OFFSET_WITH_POTIONS(new Builder("Travelers Gear with Potions")
            .addMixinClasses("travellersgear.MixinClientProxy").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .setApplyIf(() -> TweaksConfig.fixPotionRenderOffset).addTargetedMod(TargetedMod.TRAVELLERSGEAR)),
    FIX_TINKER_POTION_EFFECT_OFFSET(
            new Builder("Prevents the inventory from shifting when the player has active potion effects")
                    .setSide(Side.CLIENT).setPhase(Phase.LATE).addTargetedMod(TargetedMod.TINKERSCONSTRUCT)
                    .setApplyIf(() -> TweaksConfig.fixPotionRenderOffset)
                    .addMixinClasses("tconstruct.MixinTabRegistry")),

    // Extra Tinkers
    Fix_EXTRATIC_TECONFLICT(new Builder(
            "Disable ExtraTic's Integration with Metallurgy 3 Precious Materials Module: [Brass, Silver, Electrum & Platinum]")
                    .addMixinClasses("extratic.MixinPartsHandler", "extratic.MixinRecipeHandler").setPhase(Phase.LATE)
                    .setSide(Side.BOTH).setApplyIf(() -> FixesConfig.fixExtraTiCTEConflict)
                    .addTargetedMod(TargetedMod.EXTRATIC)),
    // Extra Utilities
    FIX_EXTRA_UTILITIES_UNENCHANTING(new Builder("Fix Exu Unenchanting")
            .addMixinClasses("extrautilities.MixinRecipeUnEnchanting").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesUnEnchanting).addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    DISABLE_AID_SPAWN_XU_SPIKES(
            new Builder("Fixes the vanilla zombie aid spawn triggering when killed by Extra Utilities Spikes")
                    .addMixinClasses("extrautilities.MixinBlockSpike").setPhase(Phase.LATE).setSide(Side.BOTH)
                    .setApplyIf(() -> TweaksConfig.disableAidSpawnByXUSpikes)
                    .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_EXTRA_UTILITIES_TRANSPARENT_ITEM_RENDER(new Builder("Fix extra utilities item rendering for transparent items")
            .addMixinClasses("extrautilities.MixinTransparentItemRender").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesItemRendering).addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_DRUM_EATING_CELLS(new Builder("Fix extra utilities drums eating ic2 cells and forestry capsules")
            .addMixinClasses("extrautilities.MixinBlockDrum").setSide(Side.BOTH).setPhase(Phase.LATE)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesDrumEatingCells)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_GREENSCREEN_MICROBLOCKS(new Builder("Fix extra utilities Lapis Caelestis microblocks")
            .addMixinClasses("extrautilities.MixinFullBrightMicroMaterial").setSide(Side.CLIENT).setPhase(Phase.LATE)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesGreenscreenMicroblocks)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_LAST_MILLENIUM_RAIN(new Builder("Remove rain from the Last Millenium (Extra Utilities)")
            .addMixinClasses("extrautilities.MixinChunkProviderEndOfTime").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesLastMilleniumRain)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_LAST_MILLENIUM_CREATURES(new Builder("Remove creatures from the Last Millenium (Extra Utilities)")
            .addMixinClasses("extrautilities.MixinWorldProviderEndOfTime").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesLastMilleniumCreatures)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_FLUID_RETRIEVAL_NODE(new Builder("Prevent fluid retrieval node from voiding (Extra Utilities)")
            .addMixinClasses("extrautilities.MixinFluidBufferRetrieval").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesFluidRetrievalNode)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),

    // Gliby's Voice Chat
    FIX_GLIBYS_VC_THREAD_SHUTDOWN_CLIENT(
            new Builder("Fix Gliby's voice chat not shutting down its client thread cleanly")
                    .addMixinClasses("glibysvoicechat.MixinClientNetwork").setPhase(Phase.LATE).setSide(Side.CLIENT)
                    .setApplyIf(() -> FixesConfig.fixGlibysVoiceChatThreadStop)
                    .addTargetedMod(TargetedMod.GLIBYS_VOICE_CHAT)),
    FIX_GLIBYS_VC_THREAD_SHUTDOWN_SERVER(
            new Builder("Fix Gliby's voice chat not shutting down its server thread cleanly")
                    .addMixinClasses("glibysvoicechat.MixinVoiceChatServer").setPhase(Phase.LATE).setSide(Side.BOTH)
                    .setApplyIf(() -> FixesConfig.fixGlibysVoiceChatThreadStop)
                    .addTargetedMod(TargetedMod.GLIBYS_VOICE_CHAT)),

    // PortalGun
    PORTALGUN_FIX_URLS(new Builder("Fix URLs used to download the sound pack")
            .addMixinClasses("portalgun.MixinThreadDownloadResources").addTargetedMod(TargetedMod.PORTAL_GUN)
            .setApplyIf(() -> FixesConfig.fixPortalGunURLs).setPhase(Phase.LATE).setSide(Side.CLIENT)),

    // VoxelMap
    REPLACE_VOXELMAP_REFLECTION(new Builder("Replace VoxelMap Reflection")
            .addMixinClasses(
                    "voxelmap.reflection.MixinAddonResourcePack",
                    "voxelmap.reflection.MixinColorManager",
                    "voxelmap.reflection.MixinMap",
                    "voxelmap.reflection.MixinRadar",
                    "voxelmap.reflection.MixinVoxelMap",
                    "voxelmap.reflection.MixinWaypointManager$1")
            .addTargetedMod(TargetedMod.VOXELMAP).setApplyIf(() -> SpeedupsConfig.replaceVoxelMapReflection)
            .setPhase(Phase.LATE).setSide(Side.CLIENT)),
    VOXELMAP_Y_FIX(new Builder("Fix off by one Y coord").addMixinClasses("voxelmap.MixinMap")
            .addTargetedMod(TargetedMod.VOXELMAP).setApplyIf(() -> FixesConfig.fixVoxelMapYCoord).setPhase(Phase.LATE)
            .setSide(Side.CLIENT)),
    VOXELMAP_NPE_FIX(new Builder("Fix VoxelMap NPEs with Chunks")
            .addMixinClasses("voxelmap.chunk.MixinCachedRegion", "voxelmap.chunk.MixinComparisonCachedRegion")
            .addTargetedMod(TargetedMod.VOXELMAP).setApplyIf(() -> FixesConfig.fixVoxelMapChunkNPE).setPhase(Phase.LATE)
            .setSide(Side.CLIENT)),
    VOXELMAP_FILE_EXT(new Builder("Change VoxelMap cache file extension")
            .addMixinClasses(
                    "voxelmap.cache.MixinCachedRegion",
                    "voxelmap.cache.MixinCachedRegion$1",
                    "voxelmap.cache.MixinComparisonCachedRegion")
            .addTargetedMod(TargetedMod.VOXELMAP).setApplyIf(() -> TweaksConfig.changeCacheFileExtension)
            .setPhase(Phase.LATE).setSide(Side.CLIENT)),

    // Witchery
    DISABLE_POTION_ARRAY_EXTENDER(new Builder("Disable Witchery potion array extender")
            .addMixinClasses("witchery.MixinPotionArrayExtender").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.disableWitcheryPotionExtender).addTargetedMod(TargetedMod.WITCHERY)),

    FIX_WITCHERY_REFLECTION_SKIN(new Builder("Fixes Witchery player skins reflections")
            .addMixinClasses("witchery.MixinExtendedPlayer", "witchery.MixinEntityReflection").setSide(Side.CLIENT)
            .setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixWitcheryReflections)
            .addTargetedMod(TargetedMod.WITCHERY)),

    FIX_WITCHERY_THUNDERING_DETECTION(new Builder(
            "Fixes Witchery Thunder Detection for rituals and Witch Hunters breaking with mods modifying thunder frequency")
                    .addMixinClasses(
                            "witchery.MixinBlockCircle",
                            "witchery.MixinEntityWitchHunter",
                            "witchery.MixinRiteClimateChange")
                    .setSide(Side.BOTH).setPhase(Phase.LATE).setApplyIf(() -> FixesConfig.fixWitcheryThunderDetection)
                    .addTargetedMod(TargetedMod.WITCHERY)),

    // Various Exploits/Fixes
    GC_TIME_COMMAND_FIX(new Builder("GC Time Fix").addMixinClasses("minecraft.MixinTimeCommandGalacticraftFix")
            .setPhase(Phase.EARLY).setSide(Side.BOTH).setApplyIf(() -> FixesConfig.fixTimeCommandWithGC)
            .addTargetedMod(TargetedMod.VANILLA).addTargetedMod(TargetedMod.GALACTICRAFT_CORE)),
    BIBLIOCRAFT_PACKET_FIX(new Builder("Packet Fix").addMixinClasses("bibliocraft.MixinBibliocraftPatchPacketExploits")
            .setPhase(Phase.LATE).setSide((Side.BOTH)).setApplyIf(() -> FixesConfig.fixBibliocraftPackets)
            .addTargetedMod(TargetedMod.BIBLIOCRAFT)),
    BIBLIOCRAFT_PATH_SANITIZATION_FIX(new Builder("Path sanitization fix")
            .addMixinClasses("bibliocraft.MixinPathSanitization").setPhase(Phase.LATE).setSide((Side.BOTH))
            .setApplyIf(() -> FixesConfig.fixBibliocraftPackets).addTargetedMod(TargetedMod.BIBLIOCRAFT)),
    ZTONES_PACKET_FIX(new Builder("Packet Fix").addMixinClasses("ztones.MixinZtonesPatchPacketExploits")
            .setPhase(Phase.LATE).setSide((Side.BOTH)).setApplyIf(() -> FixesConfig.fixZTonesPackets)
            .addTargetedMod(TargetedMod.ZTONES)),
    ASP_RECIPE_FIX(new Builder("MT Core recipe fix").addMixinClasses("advancedsolarpanels.MixinAdvancedSolarPanel")
            .addTargetedMod(TargetedMod.ADVANCED_SOLAR_PANELS).setApplyIf(() -> FixesConfig.fixMTCoreRecipe)
            .setPhase(Phase.LATE).setSide(Side.BOTH)),

    // Unbind Keybinds by default
    UNBIND_KEYS_TRAVELLERSGEAR(new Builder("Unbind Traveller's Gear keybinds")
            .addMixinClasses("travellersgear.MixinKeyHandler").setPhase(Phase.LATE).setSide((Side.CLIENT))
            .setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addTargetedMod(TargetedMod.TRAVELLERSGEAR)),
    UNBIND_KEYS_INDUSTRIALCRAFT(new Builder("Unbind Industrial craft keybinds")
            .addMixinClasses("ic2.MixinKeyboardClient").setPhase(Phase.LATE).setSide((Side.CLIENT))
            .setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addTargetedMod(TargetedMod.IC2)),
    UNBIND_KEYS_THAUMCRAFT(new Builder("Unbind Thaumcraft keybinds")
            .addMixinClasses("thaumcraft.MixinKeyHandlerThaumcraft").setPhase(Phase.LATE).setSide((Side.CLIENT))
            .setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addTargetedMod(TargetedMod.THAUMCRAFT)),
    UNBIND_KEYS_COFH(new Builder("Unbind COFH Core keybinds").addMixinClasses("cofhcore.MixinProxyClient")
            .setSide((Side.CLIENT)).setPhase(Phase.EARLY).setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault)
            .addTargetedMod(TargetedMod.COFH_CORE)),
    CHANGE_KEYBIND_CATEGORY_AUTOMAGY(new Builder("Change keybind category of Automagy")
            .addMixinClasses("automagy.MixinAutomagyKeyHandler").setPhase(Phase.LATE).setSide((Side.CLIENT))
            .setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addTargetedMod(TargetedMod.AUTOMAGY)),

    // Pollution
    POLLUTION_RENDER_BLOCKS(new Builder("Changes colors of certain blocks based on pollution levels")
            .addMixinClasses("minecraft.MixinRenderBlocks_PollutionWithoutOptifine").addTargetedMod(TargetedMod.GT5U)
            .addTargetedMod(TargetedMod.VANILLA).addExcludedMod(TargetedMod.OPTIFINE).setSide(Side.CLIENT)
            .setApplyIf(() -> PollutionRecolorConfig.pollutionBlockRecolor).setPhase(Phase.EARLY)),
    POLLUTION_RENDER_BLOCKS_OPTIFINE(new Builder("Changes colors of certain blocks based on pollution levels")
            .addMixinClasses("minecraft.MixinRenderBlocks_PollutionWithOptifine").addTargetedMod(TargetedMod.GT5U)
            .addTargetedMod(TargetedMod.VANILLA).addTargetedMod(TargetedMod.OPTIFINE)
            .addExcludedMod(TargetedMod.ANGELICA).setSide(Side.CLIENT)
            .setApplyIf(() -> PollutionRecolorConfig.pollutionBlockRecolor).setPhase(Phase.EARLY)),
    POLLUTION_RENDER_BLOCKS_BOP(new Builder("Changes colors of certain blocks based on pollution levels")
            .addMixinClasses("biomesoplenty.MixinFoliageRenderer_Pollution").addTargetedMod(TargetedMod.GT5U)
            .addTargetedMod(TargetedMod.BOP).setSide(Side.CLIENT)
            .setApplyIf(() -> PollutionRecolorConfig.pollutionBlockRecolor).setPhase(Phase.LATE)),
    POLLUTION_MINECRAFT_FURNACE(new Builder("Minecraft Furnace Pollutes").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinTileEntityFurnacePollution").setSide(Side.BOTH)
            .setApplyIf(() -> PollutionConfig.furnacesPollute).addTargetedMod(TargetedMod.VANILLA)),
    POLLUTION_IC2_IRON_FURNACE(new Builder("Ic2 Iron Furnace Pollutes")
            .addMixinClasses("ic2.MixinIC2IronFurnacePollution").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> PollutionConfig.furnacesPollute).addTargetedMod(TargetedMod.IC2)),
    POLLUTION_THAUMCRAFT_ALCHEMICAL_FURNACE(new Builder("Thaumcraft Alchemical Construct Pollutes")
            .addMixinClasses("thaumcraft.MixinThaumcraftAlchemyFurnacePollution").setPhase(Phase.LATE)
            .setSide(Side.BOTH).setApplyIf(() -> PollutionConfig.furnacesPollute)
            .addTargetedMod(TargetedMod.THAUMCRAFT)),
    POLLUTION_RAILCRAFT(new Builder("Make Railcraft Pollute")
            .addMixinClasses(
                    "railcraft.MixinRailcraftBoilerPollution",
                    "railcraft.MixinRailcraftCokeOvenPollution",
                    "railcraft.MixinRailcraftTunnelBorePollution")
            .setPhase(Phase.LATE).setSide(Side.BOTH).setApplyIf(() -> PollutionConfig.railcraftPollutes)
            .addTargetedMod(TargetedMod.RAILCRAFT)),
    POLLUTION_ROCKET(
            new Builder("Make Rockets Pollute").addMixinClasses("galacticraftcore.MixinGalacticraftRocketPollution")
                    .setPhase(Phase.LATE).setSide(Side.BOTH).setApplyIf(() -> PollutionConfig.rocketsPollute)
                    .addTargetedMod(TargetedMod.GALACTICRAFT_CORE)),
    IC2_CELL(new Builder("No IC2 Cell Consumption in tanks").addMixinClasses("ic2.MixinIC2ItemCell")
            .setPhase(Phase.LATE).setSide(Side.BOTH).setApplyIf(() -> TweaksConfig.ic2CellWithContainer)
            .addTargetedMod(TargetedMod.IC2)),
    TD_NASE_PREVENTION(new Builder("Prevent NegativeArraySizeException on itemduct transfers")
            .addMixinClasses("thermaldynamics.MixinSimulatedInv").setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.preventThermalDynamicsNASE).addTargetedMod(TargetedMod.THERMALDYNAMICS)
            .setPhase(Phase.LATE)),

    // Chunk generation/population
    DISABLE_CHUNK_TERRAIN_GENERATION(new Builder("Disable chunk terrain generation").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinChunkProviderServer_DisableTerrain").addTargetedMod(TargetedMod.VANILLA)
            .setSide(Side.BOTH).setApplyIf(() -> TweaksConfig.disableChunkTerrainGeneration)),
    DISABLE_WORLD_TYPE_CHUNK_POPULATION(
            new Builder("Disable chunk population tied to chunk generation (ores/structure)").setPhase(Phase.EARLY)
                    .addMixinClasses("minecraft.MixinChunkProviderServer_DisablePopulation")
                    .addTargetedMod(TargetedMod.VANILLA).setSide(Side.BOTH)
                    .setApplyIf(() -> TweaksConfig.disableWorldTypeChunkPopulation)),
    DISABLE_MODDED_CHUNK_POPULATION(new Builder("Disable all other mod chunk population (e.g. Natura clouds")
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinChunkProviderServer_DisableModGeneration")
            .addTargetedMod(TargetedMod.VANILLA).setSide(Side.BOTH)
            .setApplyIf(() -> TweaksConfig.disableModdedChunkPopulation)),

    // Candycraft
    FIX_SUGARBLOCK_NPE(new Builder("Fix NPE when interacting with sugar block")
            .addMixinClasses("candycraft.MixinBlockSugar").setPhase(Phase.LATE).setSide(Side.BOTH)
            .setApplyIf(() -> FixesConfig.fixCandycraftBlockSugarNPE).addTargetedMod(TargetedMod.CANDYCRAFT)),

    // Morpheus
    FIX_NOT_WAKING_PLAYERS(new Builder("Fix players not being woken properly when not everyone is sleeping")
            .addMixinClasses("morpheus.MixinMorpheusWakePlayers").setPhase(Phase.LATE).setSide(Side.SERVER)
            .setApplyIf(() -> FixesConfig.fixMorpheusWaking).addTargetedMod(TargetedMod.MORPHEUS));

    private final List<String> mixinClasses;
    private final List<TargetedMod> targetedMods;
    private final List<TargetedMod> excludedMods;
    private final Supplier<Boolean> applyIf;
    private final Phase phase;
    private final Side side;

    Mixins(Builder builder) {
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

    public static List<String> getEarlyMixins(Set<String> loadedCoreMods) {
        final List<String> mixins = new ArrayList<>();
        final List<String> notLoading = new ArrayList<>();
        for (Mixins mixin : Mixins.values()) {
            if (mixin.phase == Phase.EARLY) {
                if (mixin.shouldLoad(loadedCoreMods, Collections.emptySet())) {
                    mixins.addAll(mixin.mixinClasses);
                } else {
                    notLoading.addAll(mixin.mixinClasses);
                }
            }
        }
        Common.log.info("Not loading the following EARLY mixins: {}", notLoading.toString());
        return mixins;
    }

    public static List<String> getLateMixins(Set<String> loadedMods) {
        // NOTE: Any targetmod here needs a modid, not a coremod id
        final List<String> mixins = new ArrayList<>();
        final List<String> notLoading = new ArrayList<>();
        for (Mixins mixin : Mixins.values()) {
            if (mixin.phase == Phase.LATE) {
                if (mixin.shouldLoad(Collections.emptySet(), loadedMods)) {
                    mixins.addAll(mixin.mixinClasses);
                } else {
                    notLoading.addAll(mixin.mixinClasses);
                }
            }
        }
        Common.log.info("Not loading the following LATE mixins: {}", notLoading.toString());
        return mixins;
    }

    private boolean shouldLoadSide() {
        return side == Side.BOTH || (side == Side.SERVER && FMLLaunchHandler.side().isServer())
                || (side == Side.CLIENT && FMLLaunchHandler.side().isClient());
    }

    private boolean allModsLoaded(List<TargetedMod> targetedMods, Set<String> loadedCoreMods, Set<String> loadedMods) {
        if (targetedMods.isEmpty()) return false;

        for (TargetedMod target : targetedMods) {
            if (target == TargetedMod.VANILLA) continue;

            // Check coremod first
            if (!loadedCoreMods.isEmpty() && target.coreModClass != null
                    && !loadedCoreMods.contains(target.coreModClass))
                return false;
            else if (!loadedMods.isEmpty() && target.modId != null && !loadedMods.contains(target.modId)) return false;
        }

        return true;
    }

    private boolean noModsLoaded(List<TargetedMod> targetedMods, Set<String> loadedCoreMods, Set<String> loadedMods) {
        if (targetedMods.isEmpty()) return true;

        for (TargetedMod target : targetedMods) {
            if (target == TargetedMod.VANILLA) continue;

            // Check coremod first
            if (!loadedCoreMods.isEmpty() && target.coreModClass != null
                    && loadedCoreMods.contains(target.coreModClass))
                return false;
            else if (!loadedMods.isEmpty() && target.modId != null && loadedMods.contains(target.modId)) return false;
        }

        return true;
    }

    private boolean shouldLoad(Set<String> loadedCoreMods, Set<String> loadedMods) {
        return (shouldLoadSide() && applyIf.get()
                && allModsLoaded(targetedMods, loadedCoreMods, loadedMods)
                && noModsLoaded(excludedMods, loadedCoreMods, loadedMods));
    }

    private static class Builder {

        private final String name;
        private final List<String> mixinClasses = new ArrayList<>();
        private final List<TargetedMod> targetedMods = new ArrayList<>();
        private final List<TargetedMod> excludedMods = new ArrayList<>();
        private Supplier<Boolean> applyIf = null;
        private Phase phase = null;
        private Side side = null;

        public Builder(String name) {
            this.name = name;
        }

        public Builder addMixinClasses(String... mixinClasses) {
            this.mixinClasses.addAll(Arrays.asList(mixinClasses));
            return this;
        }

        public Builder setPhase(Phase phase) {
            if (this.phase != null) {
                throw new RuntimeException("Trying to define Phase twice for " + this.name);
            }
            this.phase = phase;
            return this;
        }

        public Builder setSide(Side side) {
            if (this.side != null) {
                throw new RuntimeException("Trying to define Side twice for " + this.name);
            }
            this.side = side;
            return this;
        }

        public Builder setApplyIf(Supplier<Boolean> applyIf) {
            this.applyIf = applyIf;
            return this;
        }

        public Builder addTargetedMod(TargetedMod mod) {
            this.targetedMods.add(mod);
            return this;
        }

        public Builder addExcludedMod(TargetedMod mod) {
            this.excludedMods.add(mod);
            return this;
        }
    }

    private enum Side {
        BOTH,
        CLIENT,
        SERVER
    }

    private enum Phase {
        EARLY,
        LATE,
    }
}
