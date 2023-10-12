package com.mitchej123.hodgepodge.mixins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.mitchej123.hodgepodge.Common;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

public enum Mixins {

    // Vanilla Fixes
    CHANGE_CATEGORY_SPRINT_KEY(new Builder("Moves the sprint keybind to the movement category")
            .addTargetedMod(TargetedMod.VANILLA).setSide(Side.CLIENT).setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGameSetttings").setApplyIf(() -> Common.config.changeSprintCategory)),
    FIX_RESOURCEPACK_FOLDER_OPENING(new Builder("Fix resource pack folder sometimes not opening on windows")
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinGuiScreenResourcePacks").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixResourcePackOpening).addTargetedMod(TargetedMod.VANILLA)),
    FIX_ENCHANTMENT_LEVEL_NUMERALS(new Builder("Fix enchantment levels not displaying properly above a certain value")
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinEnchantment_FixRomanNumerals").setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.fixEnchantmentNumerals || Common.config.arabicNumbersForEnchantsPotions)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_CONTAINER_PUT_STACKS_IN_SLOTS(new Builder("Prevents crash if server sends container with wrong itemStack size")
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinContainer").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixContainerPutStacksInSlots).addTargetedMod(TargetedMod.VANILLA)),
    FIX_NETHANDLERPLAYCLIENT_HANDLE_SET_SLOT(
            new Builder("Prevents crash if server sends itemStack with index larger than client's container")
                    .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinNetHandlerPlayClient_FixHandleSetSlot")
                    .setSide(Side.CLIENT).setApplyIf(() -> Common.config.fixNetHandlerPlayClientHandleSetSlot)
                    .addTargetedMod(TargetedMod.VANILLA)),
    FIX_NETHANDLERLOGINSERVER_OFFLINEMODE(
            new Builder("Allows the server to assign the logged in UUID to the same username when online_mode is false")
                    .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinNetHandlerLoginServer_OfflineMode")
                    .setSide(Side.SERVER).setApplyIf(() -> Common.config.fixNetHandlerLoginServerOfflineMode)
                    .addTargetedMod(TargetedMod.VANILLA)),
    FIX_INVENTORY_POTION_EFFECT_NUMERALS(
            new Builder("Fix potion effects level not displaying properly above a certain value").setPhase(Phase.EARLY)
                    .addMixinClasses(
                            "minecraft.MixinInventoryEffectRenderer_FixPotionEffectNumerals",
                            "minecraft.MixinItemPotion_FixRomanNumerals")
                    .setSide(Side.CLIENT)
                    .setApplyIf(
                            () -> Common.config.fixPotionEffectNumerals
                                    || Common.config.arabicNumbersForEnchantsPotions)
                    .addTargetedMod(TargetedMod.VANILLA)),
    FIX_HASTE_ARM_SWING_ANIMATION(new Builder("Fix arm not swinging when having too much haste").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityLivingBase_FixHasteArmSwing").setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.fixHasteArmSwing).addTargetedMod(TargetedMod.VANILLA)),
    OPTIMIZE_WORLD_UPDATE_LIGHT(new Builder("Optimize world updateLightByType method").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinWorld_FixLightUpdateLag").setSide(Side.BOTH)
            .addExcludedMod(TargetedMod.ARCHAICFIX).addExcludedMod(TargetedMod.ANGELICA)
            .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> Common.config.optimizeWorldUpdateLight)),
    FIX_FRIENDLY_CREATURE_SOUNDS(new Builder("Fix Friendly Creature Sounds").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinSoundHandler").setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> Common.config.fixFriendlyCreatureSounds)),
    THROTTLE_ITEMPICKUPEVENT(new Builder("Throttle Item Pickup Event").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityPlayer").setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.throttleItemPickupEvent).addTargetedMod(TargetedMod.VANILLA)),
    FIX_PERSPECTIVE_CAMERA(
            new Builder("Camera Perspective Fix").setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinEntityRenderer")
                    .setSide(Side.CLIENT).addExcludedMod(TargetedMod.ARCHAICFIX).addExcludedMod(TargetedMod.ANGELICA)
                    .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> Common.config.fixPerspectiveCamera)),
    FIX_DEBUG_BOUNDING_BOX(new Builder("Fix Bounding Box").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinRenderManager").setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> Common.config.fixDebugBoundingBox)),
    FENCE_CONNECTIONS_FIX(
            new Builder("Fix Fence Connections").setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinBlockFence")
                    .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> Common.config.fixFenceConnections)),
    FIX_INVENTORY_OFFSET_WITH_POTIONS(new Builder("Fix Inventory Offset with Potions").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinInventoryEffectRenderer_PotionOffset").setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> Common.config.fixPotionRenderOffset)),
    FIX_POTION_EFFECT_RENDERING(new Builder("Fix Potion Effect Rendering").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinInventoryEffectRenderer_PotionEffectRendering").setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> Common.config.fixPotionEffectRender)),
    FIX_IMMOBILE_FIREBALLS(
            new Builder("Fix Immobile Fireballs").setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinEntityFireball")
                    .addTargetedMod(TargetedMod.VANILLA).setApplyIf(() -> Common.config.fixImmobileFireballs)),
    LONGER_CHAT(new Builder("Longer Chat").setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinGuiNewChat_LongerChat")
            .setSide(Side.CLIENT).setApplyIf(() -> Common.config.longerChat).addTargetedMod(TargetedMod.VANILLA)),
    TRANSPARENT_CHAT(new Builder("Transparent Chat").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiNewChat_TransparentChat").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.transparentChat).addTargetedMod(TargetedMod.VANILLA)),
    SPEEDUP_GRASS_BLOCK_RANDOM_TICKING(new Builder("Speed up grass block random ticking").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinBlockGrass").addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> Common.config.speedupGrassBlockRandomTicking)),
    CHUNK_COORDINATES_HASHCODE(new Builder("Optimize Chunk Coordinates Hashcode").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinChunkCoordinates").addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> Common.config.speedupChunkCoordinatesHashCode)),
    TCP_NODELAY(new Builder("Set TCP NODELAY").setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinTcpNoDelay")
            .setApplyIf(() -> Common.config.tcpNoDelay).addTargetedMod(TargetedMod.VANILLA)),
    WORLD_UNPROTECTED_GET_BLOCK(new Builder("Fix world unprotected getBlock").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinWorldGetBlock")
            .setApplyIf(() -> Common.config.fixVanillaUnprotectedGetBlock).addTargetedMod(TargetedMod.VANILLA)),
    WORLD_UNPROTECTED_LIGHT_VALUE(new Builder("Fix world unprotected light value").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinWorldLightValue").setApplyIf(() -> Common.config.fixGetBlockLightValue)
            .addTargetedMod(TargetedMod.VANILLA)),
    VILLAGE_UNCHECKED_GET_BLOCK(new Builder("Fix Village unchecked getBlock").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinVillage", "minecraft.MixinVillageCollection")
            .setApplyIf(() -> Common.config.fixVillageUncheckedGetBlock).addTargetedMod(TargetedMod.VANILLA)),
    FORGE_HOOKS_URL_FIX(
            new Builder("Fix forge URL hooks").setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinForgeHooks")
                    .setApplyIf(() -> Common.config.fixUrlDetection).addTargetedMod(TargetedMod.VANILLA)),
    FORGE_UPDATE_CHECK_FIX(new Builder("Fix the forge update checker").setPhase(Phase.EARLY)
            .addMixinClasses("forge.MixinForgeVersion_FixUpdateCheck")
            .setApplyIf(() -> Common.config.fixForgeUpdateChecker).addTargetedMod(TargetedMod.VANILLA)),
    NORTHWEST_BIAS_FIX(new Builder("Fix Northwest Bias").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinRandomPositionGenerator").setApplyIf(() -> Common.config.fixNorthWestBias)
            .addTargetedMod(TargetedMod.VANILLA)),
    SPEEDUP_VANILLA_FURNACE(new Builder("Speedup Vanilla Furnace").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinFurnaceRecipes").setApplyIf(() -> Common.config.speedupVanillaFurnace)
            .addTargetedMod(TargetedMod.VANILLA).addTargetedMod(TargetedMod.GTNHLIB)),
    GAMEOVER_GUI_LOCKED_DISABLED(new Builder("Fix Gameover GUI").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiGameOver").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixGuiGameOver).addTargetedMod(TargetedMod.VANILLA)),
    PREVENT_PICKUP_LOOT(new Builder("Prevent monsters from picking up loot").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityLivingPickup").setApplyIf(() -> Common.config.preventPickupLoot)
            .addTargetedMod(TargetedMod.VANILLA)),
    DROP_PICKED_LOOT_ON_DESPAWN(new Builder("Drop picked up loot on despawn").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityLivingDrop").setApplyIf(() -> Common.config.dropPickedLootOnDespawn)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_HOPPER_HIT_BOX(new Builder("Fix Vanilla Hopper hit box").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinBlockHopper").setApplyIf(() -> Common.config.fixHopperHitBox)
            .addTargetedMod(TargetedMod.VANILLA)),
    TILE_RENDERER_PROFILER_DISPATCHER(new Builder("TileEntity Render Dispatcher Fix").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.profiler.TileEntityRendererDispatcherMixin").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.enableTileRendererProfiler).addTargetedMod(TargetedMod.VANILLA)),
    TILE_RENDERER_PROFILER_MINECRAFT(new Builder("Tile Entity Render Profiler").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.profiler.MinecraftMixin").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.enableTileRendererProfiler).addTargetedMod(TargetedMod.VANILLA)),
    DIMENSION_CHANGE_FIX(new Builder("Dimension Change Heart Fix").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinServerConfigurationManager", "minecraft.MixinEntityPlayerMP")
            .setApplyIf(() -> Common.config.fixDimensionChangeHearts).addTargetedMod(TargetedMod.VANILLA)),
    FIX_EATING_STACKED_STEW(new Builder("Stacked Mushroom Stew Eating Fix").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinItemSoup").setApplyIf(() -> Common.config.fixEatingStackedStew)
            .addTargetedMod(TargetedMod.VANILLA)),
    INCREASE_PARTICLE_LIMIT(new Builder("Increase Particle Limit").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEffectRenderer").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.increaseParticleLimit).addTargetedMod(TargetedMod.VANILLA)),
    ENLARGE_POTION_ARRAY(
            new Builder("Make the Potion array larger").setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinPotion")
                    .setApplyIf(() -> Common.config.enlargePotionArray).addTargetedMod(TargetedMod.VANILLA)),
    FIX_POTION_LIMIT(
            new Builder("Fix Potion Limit").setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinPotionEffect")
                    .setApplyIf(() -> Common.config.fixPotionLimit).addTargetedMod(TargetedMod.VANILLA)),
    FIX_HOPPER_VOIDING_ITEMS(new Builder("Fix Hopper Voiding Items").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinTileEntityHopper").setApplyIf(() -> Common.config.fixHopperVoidingItems)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_HUGE_CHAT_KICK(
            new Builder("Fix huge chat kick").setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinS02PacketChat")
                    .setApplyIf(() -> Common.config.fixHugeChatKick).addTargetedMod(TargetedMod.VANILLA)),
    FIX_WORLD_SERVER_LEAKING_UNLOADED_ENTITIES(new Builder("Fix world server leaking unloaded entities")
            .setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinWorldServerUpdateEntities")
            .setApplyIf(() -> Common.config.fixWorldServerLeakingUnloadedEntities).addTargetedMod(TargetedMod.VANILLA)),
    FIX_ARROW_WRONG_LIGHTING(new Builder("Fix arrow wrong lighting").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinRendererLivingEntity").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixGlStateBugs).addTargetedMod(TargetedMod.VANILLA)),
    FIX_RESIZABLE_FULLSCREEN(new Builder("Fix Resizable Fullscreen").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinMinecraft_ResizableFullscreen").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixResizableFullscreen).addTargetedMod(TargetedMod.VANILLA)),
    FIX_UNFOCUSED_FULLSCREEN(new Builder("Fix Unfocused Fullscreen").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinMinecraft_UnfocusedFullscreen").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixUnfocusedFullscreen).addTargetedMod(TargetedMod.VANILLA)),
    FIX_OPTIFINE_CHUNKLOADING_CRASH(new Builder("Fix Optifine Chunkloading Crash").setPhase(Phase.EARLY)
            .setApplyIf(() -> Common.config.fixOptifineChunkLoadingCrash).setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA).addTargetedMod(TargetedMod.OPTIFINE)
            .addMixinClasses("minecraft.MixinGameSettings")),
    ADD_TOGGLE_DEBUG_MESSAGE(new Builder("Toggle Debug Message").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinMinecraft_ToggleDebugMessage").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.addToggleDebugMessage).addTargetedMod(TargetedMod.VANILLA)),
    SPEEDUP_VANILLA_ANIMATIONS(new Builder("Speedup Vanilla Animations").setPhase(Phase.EARLY)
            .setApplyIf(() -> Common.config.speedupAnimations).setSide(Side.CLIENT).addTargetedMod(TargetedMod.VANILLA)
            .addMixinClasses(
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
            .setApplyIf(() -> Common.config.speedupAnimations).addTargetedMod(TargetedMod.VANILLA)
            .addTargetedMod(TargetedMod.FASTCRAFT).addExcludedMod(TargetedMod.OPTIFINE)),
    OPTIMIZE_TEXTURE_LOADING(new Builder("Optimize Texture Loading").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.textures.client.MixinTextureUtil").addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> Common.config.optimizeTextureLoading).setSide(Side.CLIENT)),
    HIDE_POTION_PARTICLES(new Builder("Hide Potion Particles").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityLivingBase_HidePotionParticles").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.hidePotionParticlesFromSelf).addTargetedMod(TargetedMod.VANILLA)),
    DIMENSION_MANAGER_DEBUG(new Builder("Dimension Manager Debug").setPhase(Phase.EARLY).setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinDimensionManager").setApplyIf(() -> Common.config.dimensionManagerDebug)
            .addTargetedMod(TargetedMod.VANILLA)),
    OPTIMIZE_TILEENTITY_REMOVAL(new Builder("Optimize TileEntity Removal").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinWorldUpdateEntities")
            .setApplyIf(() -> Common.config.optimizeTileentityRemoval).addTargetedMod(TargetedMod.VANILLA)
            .addExcludedMod(TargetedMod.FASTCRAFT).addExcludedMod(TargetedMod.BUKKIT)),
    FIX_POTION_ITERATING(new Builder("Fix Potion Iterating").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityLivingBase_FixPotionException")
            .setApplyIf(() -> Common.config.fixPotionIterating).addTargetedMod(TargetedMod.VANILLA)),
    ENHANCE_NIGHT_VISION(new Builder("Remove the blueish sky tint from night vision").setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA).setPhase(Phase.EARLY)
            .setApplyIf(() -> Common.config.enhanceNightVision)
            .addMixinClasses("minecraft.MixinEntityRenderer_EnhanceNightVision")),
    OPTIMIZE_ASMDATATABLE_INDEX(
            new Builder("Optimize ASM DataTable Index").setPhase(Phase.EARLY).addMixinClasses("forge.MixinASMDataTable")
                    .setApplyIf(() -> Common.config.optimizeASMDataTable).addTargetedMod(TargetedMod.VANILLA)),
    CHUNK_SAVE_CME_DEBUG(new Builder("Add debugging code to Chunk Save CME").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinNBTTagCompound").setApplyIf(() -> Common.config.chunkSaveCMEDebug)
            .addTargetedMod(TargetedMod.VANILLA)),
    RENDER_DEBUG(new Builder("Render Debug").setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinRenderGlobal")
            .setSide(Side.CLIENT).setApplyIf(() -> Common.config.renderDebug).addTargetedMod(TargetedMod.VANILLA)),
    STATIC_LAN_PORT(new Builder("Static Lan Port").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.server.MixinHttpUtil").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.enableDefaultLanPort).addTargetedMod(TargetedMod.VANILLA)),
    CROSSHAIR_THIRDPERSON(new Builder("Crosshairs thirdperson").setPhase(Phase.EARLY)
            .addMixinClasses("forge.MixinGuiIngameForge_CrosshairThirdPerson").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.hideCrosshairInThirdPerson).addTargetedMod(TargetedMod.VANILLA)),
    DONT_INVERT_CROSSHAIR_COLORS(new Builder("Don't invert crosshair colors").setPhase(Phase.EARLY)
            .addMixinClasses("forge.MixinGuiIngameForge_CrosshairInvertColors").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.dontInvertCrosshairColor).addTargetedMod(TargetedMod.VANILLA)),
    FIX_OPENGUIHANDLER_WINDOWID(new Builder("Fix OpenGuiHandler").setPhase(Phase.EARLY)
            .addMixinClasses("forge.MixinOpenGuiHandler").setApplyIf(() -> Common.config.fixForgeOpenGuiHandlerWindowId)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_KEYBIND_CONFLICTS(new Builder("Trigger all conflicting keybinds").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinKeyBinding", "minecraft.MixinMinecraft_UpdateKeys").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.triggerAllConflictingKeybindings).addTargetedMod(TargetedMod.VANILLA)),
    REMOVE_SPAWN_MINECART_SOUND(new Builder("Remove sound when spawning a minecart")
            .addMixinClasses("minecraft.MixinWorldClient").addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> Common.config.removeSpawningMinecartSound).setPhase(Phase.EARLY).setSide(Side.CLIENT)),
    MACOS_KEYS_TEXTFIELD_SHORTCUTS(new Builder("Macos use CMD to copy/select/delete text")
            .addMixinClasses("minecraft.MixinGuiTextField").addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(
                    () -> System.getProperty("os.name").toLowerCase().contains("mac")
                            && Common.config.enableMacosCmdShortcuts)
            .setPhase(Phase.EARLY).setSide(Side.CLIENT)),
    FIX_FONT_RENDERER_LINEWRAP_RECURSION(
            new Builder("Replace recursion with iteration in FontRenderer line wrapping code")
                    .addMixinClasses("minecraft.MixinFontRenderer").addTargetedMod(TargetedMod.VANILLA)
                    .setApplyIf(() -> Common.config.fixFontRendererLinewrapRecursion).setPhase(Phase.EARLY)
                    .setSide(Side.CLIENT)),
    BED_MESSAGE_ABOVE_HOTBAR(new Builder("Bed Message Above Hotbar").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinBlockBed").setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.bedMessageAboveHotbar).addTargetedMod(TargetedMod.VANILLA)),
    FIX_PLAYER_SKIN_FETCHING(new Builder("Fix player skin fetching").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinAbstractClientPlayer").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixPlayerSkinFetching).addTargetedMod(TargetedMod.VANILLA)),
    VALIDATE_PACKET_ENCODING_BEFORE_SENDING(new Builder("Validate packet encoding before sending").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.packets.MixinDataWatcher", "minecraft.packets.MixinS3FPacketCustomPayload")
            .setSide(Side.BOTH).setApplyIf(() -> Common.config.validatePacketEncodingBeforeSending)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_FLUID_CONTAINER_REGISTRY_KEY(new Builder("Fix Forge fluid container registry key").setPhase(Phase.EARLY)
            .addMixinClasses("forge.FluidContainerRegistryAccessor", "forge.MixinFluidRegistry").setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.fixFluidContainerRegistryKey).addTargetedMod(TargetedMod.VANILLA)),
    FIX_XRAY_BLOCK_WITHOUT_COLLISION_AABB(new Builder("Fix Xray through block without collision boundingBox")
            .addMixinClasses("minecraft.MixinBlock_FixXray", "minecraft.MixinWorld_FixXray")
            .setApplyIf(() -> Common.config.fixPerspectiveCamera).addTargetedMod(TargetedMod.VANILLA)
            .setPhase(Phase.EARLY)),
    DISABLE_CREATIVE_TAB_ALL_SEARCH(new Builder("Disable the creative tab with search bar").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiContainerCreative").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.removeCreativeSearchTab).addTargetedMod(TargetedMod.NOTENOUGHITEMS)),
    FIX_CHAT_COLOR_WRAPPING(new Builder("Fix wrapped chat lines missing colors").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiNewChat_FixColorWrapping", "minecraft.FontRendererAccessor")
            .setSide(Side.CLIENT).setApplyIf(() -> Common.config.fixChatWrappedColors)
            .addTargetedMod(TargetedMod.VANILLA)),
    COMPACT_CHAT(new Builder("Compact chat").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiNewChat_CompactChat").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.compactChat).addTargetedMod(TargetedMod.VANILLA)),
    NETTY_PATCH(new Builder("Fix NPE in Netty's Bootstrap class").addMixinClasses("netty.MixinBootstrap")
            .setPhase(Phase.EARLY).setSide(Side.CLIENT).setApplyIf(() -> Common.config.fixNettyNPE)
            .addTargetedMod(TargetedMod.VANILLA)),

    // Ic2 adjustments
    IC2_UNPROTECTED_GET_BLOCK_FIX(
            new Builder("IC2 Kinetic Fix").setPhase(Phase.EARLY).addMixinClasses("ic2.MixinIc2WaterKinetic")
                    .setApplyIf(() -> Common.config.fixIc2UnprotectedGetBlock).addTargetedMod(TargetedMod.IC2)),
    IC2_DIRECT_INV_ACCESS(new Builder("IC2 Direct Inventory Access Fix").setPhase(Phase.EARLY)
            .addMixinClasses("ic2.MixinItemCropSeed", "ic2.MixinTileEntityCrop")
            .setApplyIf(() -> Common.config.fixIc2DirectInventoryAccess).addTargetedMod(TargetedMod.IC2)),
    IC2_NIGHT_VISION_NANO(new Builder("IC2 Nightvision Fix").setPhase(Phase.EARLY)
            .addMixinClasses(
                    "ic2.MixinIc2NanoSuitNightVision",
                    "ic2.MixinIc2QuantumSuitNightVision",
                    "ic2.MixinIc2NightVisionGoggles")
            .setApplyIf(() -> Common.config.fixIc2Nightvision).addTargetedMod(TargetedMod.IC2)),
    IC2_REACTOR_DUPE(new Builder("IC2 Reactor Dupe Fix").setPhase(Phase.EARLY)
            .addMixinClasses("ic2.MixinTileEntityReactorChamberElectricNoDupe")
            .setApplyIf(() -> Common.config.fixIc2ReactorDupe).addTargetedMod(TargetedMod.IC2)),
    IC2_REACTOR_INVENTORY_SPEEDUP(new Builder("IC2 Reactor Inventory Speedup Fix").setPhase(Phase.EARLY)
            .addMixinClasses("ic2.MixinTileEntityReactorChamberElectricInvSpeedup")
            .setApplyIf(() -> Common.config.optimizeIc2ReactorInventoryAccess).addTargetedMod(TargetedMod.IC2)),
    HIDE_IC2_REACTOR_COOLANT_SLOTS(new Builder("IC2 Reactory Fix").setPhase(Phase.EARLY)
            .addMixinClasses("ic2.MixinTileEntityNuclearReactorElectric")
            .setApplyIf(() -> Common.config.hideIc2ReactorSlots).addTargetedMod(TargetedMod.IC2)),
    IC2_HAZMAT(new Builder("Hazmat").setPhase(Phase.LATE).addMixinClasses("ic2.MixinIc2Hazmat")
            .setApplyIf(() -> Common.config.fixIc2Hazmat).addTargetedMod(TargetedMod.IC2)
            .addTargetedMod(TargetedMod.GT5U).addExcludedMod(TargetedMod.GT6)),
    IC2_FLUID_CONTAINER_TOOLTIP(new Builder("IC2 Fluid Container Tooltip Fix").setPhase(Phase.EARLY)
            .addMixinClasses("ic2.MixinItemIC2FluidContainer")
            .setApplyIf(() -> Common.config.displayIc2FluidLocalizedName).addTargetedMod(TargetedMod.IC2)),
    IC2_FLUID_RENDER_FIX(new Builder("IC2 Fluid Render Fix").setPhase(Phase.EARLY)
            .addMixinClasses("ic2.textures.MixinRenderLiquidCell").setApplyIf(() -> Common.config.speedupAnimations)
            .addTargetedMod(TargetedMod.IC2)),
    IC2_HOVER_MODE_FIX(
            new Builder("IC2 Hover Mode Fix").setPhase(Phase.LATE).addMixinClasses("ic2.MixinIc2QuantumSuitHoverMode")
                    .setApplyIf(() -> Common.config.fixIc2HoverMode).addTargetedMod(TargetedMod.IC2)),
    IC2_ARMOR_LAG_FIX(new Builder("IC2 Armor Lag Fix").setPhase(Phase.LATE)
            .addMixinClasses(
                    "ic2.MixinElectricItemManager",
                    "ic2.MixinIC2ArmorHazmat",
                    "ic2.MixinIC2ArmorJetpack",
                    "ic2.MixinIC2ArmorNanoSuit",
                    "ic2.MixinIC2ArmorNightvisionGoggles",
                    "ic2.MixinIC2ArmorQuantumSuit",
                    "ic2.MixinIC2ArmorSolarHelmet",
                    "ic2.MixinIC2ArmorStaticBoots")
            .setApplyIf(() -> Common.config.fixIc2ArmorLag).addTargetedMod(TargetedMod.IC2)),

    // Disable update checkers
    BIBLIOCRAFT_UPDATE_CHECK(new Builder("Yeet Bibliocraft Update Check").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .addMixinClasses("bibliocraft.MixinVersionCheck").setApplyIf(() -> Common.config.removeUpdateChecks)
            .addTargetedMod(TargetedMod.BIBLIOCRAFT)),
    COFH_CORE_UPDATE_CHECK(new Builder("Yeet COFH Core Update Check").setPhase(Phase.EARLY)
            .addMixinClasses("cofhcore.MixinCoFHCoreUpdateCheck").setApplyIf(() -> Common.config.removeUpdateChecks)
            .addTargetedMod(TargetedMod.COFH_CORE)),
    JOURNEYMAP_UPDATE_CHECK(new Builder("Yeet Journeymap Update Check").setPhase(Phase.LATE).setSide(Side.CLIENT)
            .addMixinClasses("journeymap.MixinVersionCheck").setApplyIf(() -> Common.config.removeUpdateChecks)
            .addTargetedMod(TargetedMod.JOURNEYMAP)),

    // Railcraft Anchors
    WAKE_ANCHORS_ON_LOGIN_PASSIVE(
            new Builder("Wake passive anchors on login").addMixinClasses("railcraft.MixinTileAnchorPassive")
                    .setApplyIf(() -> Common.config.installAnchorAlarm).addTargetedMod(TargetedMod.RAILCRAFT)),
    WAKE_ANCHORS_ON_LOGIN_PERSONAL(
            new Builder("Wake person anchors on login").addMixinClasses("railcraft.MixinTileAnchorPersonal")
                    .setApplyIf(() -> Common.config.installAnchorAlarm).addTargetedMod(TargetedMod.RAILCRAFT)),

    // Hunger overhaul
    HUNGER_OVERHAUL_LOW_STAT_EFFECT(new Builder("Patch unintended low stat effects")
            .addMixinClasses("hungeroverhaul.MixinHungerOverhaulLowStatEffect")
            .setApplyIf(() -> Common.config.fixHungerOverhaul).addTargetedMod(TargetedMod.HUNGER_OVERHAUL)),
    HUNGER_OVERHAUL_REGEN(new Builder("Patch Regen").addMixinClasses("hungeroverhaul.MixinHungerOverhaulHealthRegen")
            .setApplyIf(() -> Common.config.fixHungerOverhaul).addTargetedMod(TargetedMod.HUNGER_OVERHAUL)),
    HUNGER_OVERHAUL_0_HUNGER(new Builder("Fix some items restore 0 hunger")
            .addMixinClasses("hungeroverhaul.MixinHungerOverhaulModuleHarvestCraft")
            .setApplyIf(() -> Common.config.fixHungerOverhaulRestore0Hunger).addTargetedMod(TargetedMod.HUNGER_OVERHAUL)
            .addTargetedMod(TargetedMod.HARVESTCRAFT)),

    // Thaumcraft
    ADD_CV_SUPPORT_TO_WAND_PEDESTAL(
            new Builder("CV Support for Wand Pedestal").addMixinClasses("thaumcraft.MixinTileWandPedestal")
                    .setApplyIf(() -> Common.config.addCVSupportToWandPedestal).addTargetedMod(TargetedMod.THAUMCRAFT)),
    FIX_ASPECT_SORTING(new Builder("Fix Thaumcraft Aspects not being sorted by name")
            .addMixinClasses(
                    "thaumcraft.MixinGuiResearchRecipe",
                    "thaumcraft.MixinGuiResearchTable",
                    "thaumcraft.MixinGuiThaumatorium",
                    "thaumcraft.MixinItem_SortAspectsByName")
            .setSide(Side.CLIENT).setApplyIf(() -> Common.config.fixThaumcraftAspectSorting)
            .addTargetedMod(TargetedMod.THAUMCRAFT)),
    FIX_GOLEM_MARKER_LOADING(new Builder("Fix golem marker loading failure when dimensionId larger than MAX_BYTE")
            .addMixinClasses("thaumcraft.MixinEntityGolemBase", "thaumcraft.MixinItemGolemBell")
            .setApplyIf(() -> Common.config.fixThaumcraftGolemMarkerLoading).addTargetedMod(TargetedMod.THAUMCRAFT)),

    // BOP
    FIX_QUICKSAND_XRAY(new Builder("Fix Xray through block without collision boundingBox")
            .addMixinClasses("biomesoplenty.MixinBlockMud_FixXray").setApplyIf(() -> Common.config.fixPerspectiveCamera)
            .addTargetedMod(TargetedMod.BOP)),
    DEDUPLICATE_FORESTRY_COMPAT_IN_BOP(
            new Builder("BOP Forestry Compat").addMixinClasses("biomesoplenty.MixinForestryIntegration")
                    .setApplyIf(() -> Common.config.deduplicateForestryCompatInBOP).addTargetedMod(TargetedMod.BOP)),
    SPEEDUP_BOP_BIOME_FOG(
            new Builder("BOP Biome Fog").addMixinClasses("biomesoplenty.MixinFogHandler").setSide(Side.CLIENT)
                    .setApplyIf(() -> Common.config.speedupBOPFogHandling).addTargetedMod(TargetedMod.BOP)),
    SPEEDUP_BOP_BIOME_FOG_ACCESSOR(new Builder("BOP Biome Fog Accessor")
            .addMixinClasses("biomesoplenty.AccessorFogHandler").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.speedupBOPFogHandling).addTargetedMod(TargetedMod.BOP)),
    BIG_FIR_TREES(new Builder("BOP Fir Trees").addMixinClasses("biomesoplenty.MixinBlockBOPSapling")
            .setApplyIf(() -> Common.config.makeBigFirsPlantable).addTargetedMod(TargetedMod.BOP)),
    JAVA12_BOP(new Builder("BOP Java12-safe reflection").addMixinClasses("biomesoplenty.MixinBOPBiomes")
            .addMixinClasses("biomesoplenty.MixinBOPReflectionHelper").setApplyIf(() -> Common.config.java12BopCompat)
            .addTargetedMod(TargetedMod.BOP)),

    // COFH
    COFH_REMOVE_TE_CACHE(
            new Builder("Remove CoFH tile entity cache").addMixinClasses("minecraft.MixinWorld_CoFH_TE_Cache")
                    .setSide(Side.BOTH).setApplyIf(() -> Common.config.cofhWorldTransformer)
                    .addTargetedMod(TargetedMod.COFH_CORE).setPhase(Phase.EARLY)),

    // Immersive engineering
    JAVA12_IMMERSIVE_ENGINERRING(new Builder("Immersive Engineering Java-12 safe potion array resizing")
            .addMixinClasses("immersiveengineering.MixinIEPotions")
            .setApplyIf(() -> Common.config.java12ImmersiveEngineeringCompat)
            .addTargetedMod(TargetedMod.IMMERSIVE_ENGINENEERING)),
    JAVA12_MINE_CHEM(
            new Builder("Minechem Java-12 safe potion array resizing").addMixinClasses("minechem.MixinPotionInjector")
                    .setApplyIf(() -> Common.config.java12MineChemCompat).addTargetedMod(TargetedMod.MINECHEM)),

    // MrTJPCore (Project Red)
    FIX_HUD_LIGHTING_GLITCH(new Builder("HUD Lighting glitch").addMixinClasses("mrtjpcore.MixinFXEngine")
            .setApplyIf(() -> Common.config.fixHudLightingGlitch).addTargetedMod(TargetedMod.MRTJPCORE)),
    FIX_POPPING_OFF(new Builder("Fix Popping Off").addMixinClasses("mrtjpcore.MixinPlacementLib")
            .setApplyIf(() -> Common.config.fixComponentsPoppingOff).addTargetedMod(TargetedMod.MRTJPCORE)),

    // Automagy
    IMPLEMENTS_CONTAINER_FOR_THIRSTY_TANK(
            new Builder("Thirsty Tank Container").addMixinClasses("automagy.MixinItemBlockThirstyTank")
                    .setApplyIf(() -> Common.config.thirstyTankContainer).addTargetedMod(TargetedMod.AUTOMAGY)),

    // Better HUD
    FIX_BETTERHUD_ARMOR_BAR(new Builder("Fix better HUD armor display breaking with skulls")
            .addMixinClasses("betterhud.MixinSkullDurabilityDisplay")
            .setApplyIf(() -> Common.config.fixBetterHUDArmorDisplay).addTargetedMod(TargetedMod.BETTERHUD)),

    FIX_BETTERHUD_HEARTS_FREEZE(new Builder("Fix better HUD freezing the game when trying to render high amounts of hp")
            .addMixinClasses("betterhud.MixinHealthRender").setApplyIf(() -> Common.config.fixBetterHUDHPDisplay)
            .addTargetedMod(TargetedMod.BETTERHUD)),

    // ProjectE
    FIX_FURNACE_ITERATION(new Builder("Speedup Furnaces").addMixinClasses("projecte.MixinObjHandler")
            .setApplyIf(() -> Common.config.speedupVanillaFurnace).addTargetedMod(TargetedMod.PROJECTE)),

    // LOTR
    FIX_LOTR_FURNACE_ERROR(new Builder("Patches lotr to work with the vanilla furnace speedup")
            .addMixinClasses("lotr.MixinLOTRRecipes").setApplyIf(() -> Common.config.speedupVanillaFurnace)
            .addTargetedMod(TargetedMod.VANILLA).addTargetedMod(TargetedMod.GTNHLIB).addTargetedMod(TargetedMod.LOTR)),

    FIX_LOTR_JAVA12(new Builder("Fix lotr java 12+ compat")
            .addMixinClasses(
                    "lotr.MixinLOTRLogReflection",
                    "lotr.MixinRedirectHuornAI",
                    "lotr.MixinRemoveUnlockFinalField")
            .setApplyIf(() -> Common.config.java12LotrCompat).addTargetedMod(TargetedMod.LOTR)),

    // Journeymap
    FIX_JOURNEYMAP_KEYBINDS(
            new Builder("Fix Journeymap Keybinds").setSide(Side.CLIENT).addMixinClasses("journeymap.MixinConstants")
                    .setApplyIf(() -> Common.config.fixJourneymapKeybinds).addTargetedMod(TargetedMod.JOURNEYMAP)),
    FIX_JOURNEYMAP_ILLEGAL_FILE_PATH_CHARACTER(new Builder("Fix Journeymap Illegal File Path Character")
            .setSide(Side.CLIENT).addMixinClasses("journeymap.MixinWorldData")
            .setApplyIf(() -> Common.config.fixJourneymapFilePath).addTargetedMod(TargetedMod.JOURNEYMAP)),

    FIX_JOURNEYMAP_JUMPY_SCROLLING(new Builder("Fix Journeymap jumpy scrolling in the waypoint manager")
            .setSide(Side.CLIENT).addMixinClasses("journeymap.MixinWaypointManager")
            .setApplyIf(() -> Common.config.fixJourneymapJumpyScrolling).addTargetedMod(TargetedMod.JOURNEYMAP)),

    // Xaero's Map
    FIX_XAEROS_WORLDMAP_SCROLL(
            new Builder("Fix Xaero's World Map map screen scrolling").addMixinClasses("xaeroworldmap.MixinGuiMap")
                    .setSide(Side.CLIENT).setApplyIf(() -> Common.config.fixXaerosWorldMapScroll)
                    .addTargetedMod(TargetedMod.XAEROWORLDMAP).addTargetedMod(TargetedMod.LWJGL3IFY)),

    // Pam's Harvest the Nether
    FIX_IGNIS_FRUIT_AABB(new Builder("Ignis Fruit").addMixinClasses("harvestthenether.MixinBlockPamFruit")
            .setApplyIf(() -> Common.config.fixIgnisFruitAABB).addTargetedMod(TargetedMod.HARVESTTHENETHER)),
    FIX_NETHER_LEAVES_FACE_RENDERING(
            new Builder("Nether Leaves").addMixinClasses("harvestthenether.MixinBlockNetherLeaves").setSide(Side.CLIENT)
                    .setApplyIf(() -> Common.config.fixIgnisFruitAABB).addTargetedMod(TargetedMod.HARVESTTHENETHER)),

    // Potion Render Offset Fixes - Various Mods
    FIX_BAUBLES_INVENTORY_OFFSET_WITH_POTIONS(
            new Builder("Baubles Inventory with Potions").addMixinClasses("baubles.MixinGuiEvents").setSide(Side.CLIENT)
                    .setApplyIf(() -> Common.config.fixPotionRenderOffset).addTargetedMod(TargetedMod.BAUBLES)),
    FIX_GALACTICRAFT_INVENTORY_OFFSET_WITH_POTIONS(new Builder("Galacticraft Inventory with Potions")
            .addMixinClasses("galacticraftcore.MixinGuiExtendedInventory").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixPotionRenderOffset).addTargetedMod(TargetedMod.GALACTICRAFT_CORE)),
    FIX_TRAVELLERSGEAR_INVENTORY_OFFSET_WITH_POTIONS(new Builder("Travelers Gear with Potions")
            .addMixinClasses("travellersgear.MixinClientProxy").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixPotionRenderOffset).addTargetedMod(TargetedMod.TRAVELLERSGEAR)),
    FIX_TINKER_POTION_EFFECT_OFFSET(
            new Builder("Prevents the inventory from shifting when the player has active potion effects")
                    .setSide(Side.CLIENT).setPhase(Phase.LATE).addTargetedMod(TargetedMod.TINKERSCONSTRUCT)
                    .setApplyIf(() -> Common.config.fixPotionRenderOffset)
                    .addMixinClasses("tconstruct.MixinTabRegistry")),

    // Extra Tinkers
    Fix_EXTRATIC_TECONFLICT(new Builder(
            "Disable ExtraTic's Integration with Metallurgy 3 Precious Materials Module: [Brass, Silver, Electrum & Platinum]")
                    .addMixinClasses("extratic.MixinPartsHandler").setSide(Side.BOTH)
                    .setApplyIf(() -> Common.config.fixExtraTiCTEConflict).addTargetedMod(TargetedMod.EXTRATIC)),
    // Extra Utilities
    FIX_EXTRA_UTILITIES_UNENCHANTING(new Builder("Fix Exu Unenchanting")
            .addMixinClasses("extrautilities.MixinRecipeUnEnchanting").setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.fixExtraUtilitiesUnEnchanting).addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    DISABLE_AID_SPAWN_XU_SPIKES(
            new Builder("Fixes the vanilla zombie aid spawn triggering when killed by Extra Utilities Spikes")
                    .addMixinClasses("extrautilities.MixinBlockSpike").setSide(Side.BOTH)
                    .setApplyIf(() -> Common.config.disableAidSpawnByXUSpikes)
                    .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_EXTRA_UTILITIES_TRANSPARENT_ITEM_RENDER(new Builder("Fix extra utilities item rendering for transparent items")
            .addMixinClasses("extrautilities.MixinTransparentItemRender").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixExtraUtilitiesItemRendering)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_DRUM_EATING_CELLS(new Builder("Fix extra utilities drums eating ic2 cells and forestry capsules")
            .addMixinClasses("extrautilities.MixinBlockDrum").setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.fixExtraUtilitiesDrumEatingCells)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),

    // Witchery
    DISABLE_POTION_ARRAY_EXTENDER(new Builder("Disable Witchery potion array extender")
            .addMixinClasses("witchery.MixinPotionArrayExtender").setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.disableWitcheryPotionExtender).addTargetedMod(TargetedMod.WITCHERY)),

    Fix_WitcheryReflectionSkin(new Builder("Fixes Witchery player skins reflections")
            .addMixinClasses("witchery.MixinExtendedPlayer", "witchery.MixinEntityReflection").setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixWitcheryReflections).addTargetedMod(TargetedMod.WITCHERY)),

    // Various Exploits/Fixes
    GC_TIME_COMMAND_FIX(new Builder("GC Time Fix").addMixinClasses("minecraft.MixinTimeCommandGalacticraftFix")
            .setPhase(Phase.EARLY).setSide(Side.BOTH).setApplyIf(() -> Common.config.fixTimeCommandWithGC)
            .addTargetedMod(TargetedMod.VANILLA).addTargetedMod(TargetedMod.GALACTICRAFT_CORE)),
    BIBLIOCRAFT_PACKET_FIX(new Builder("Packet Fix").addMixinClasses("bibliocraft.MixinBibliocraftPatchPacketExploits")
            .setSide((Side.BOTH)).setApplyIf(() -> Common.config.fixBibliocraftPackets)
            .addTargetedMod(TargetedMod.BIBLIOCRAFT)),
    BIBLIOCRAFT_PATH_SANITIZATION_FIX(new Builder("Path sanitization fix")
            .addMixinClasses("bibliocraft.MixinPathSanitization").setSide((Side.BOTH))
            .setApplyIf(() -> Common.config.fixBibliocraftPackets).addTargetedMod(TargetedMod.BIBLIOCRAFT)),
    ZTONES_PACKET_FIX(new Builder("Packet Fix").addMixinClasses("ztones.MixinZtonesPatchPacketExploits")
            .setSide((Side.BOTH)).setApplyIf(() -> Common.config.fixZTonesPackets).addTargetedMod(TargetedMod.ZTONES)),

    // Unbind Keybinds by default
    UNBIND_KEYS_TRAVELLERSGEAR(new Builder("Unbind Traveller's Gear keybinds")
            .addMixinClasses("travellersgear.MixinKeyHandler").setSide((Side.CLIENT))
            .setApplyIf(() -> Common.config.unbindKeybindsByDefault).addTargetedMod(TargetedMod.TRAVELLERSGEAR)),
    UNBIND_KEYS_INDUSTRIALCRAFT(new Builder("Unbind Industrial craft keybinds")
            .addMixinClasses("ic2.MixinKeyboardClient").setSide((Side.CLIENT))
            .setApplyIf(() -> Common.config.unbindKeybindsByDefault).addTargetedMod(TargetedMod.IC2)),
    UNBIND_KEYS_THAUMCRAFT(new Builder("Unbind Thaumcraft keybinds")
            .addMixinClasses("thaumcraft.MixinKeyHandlerThaumcraft").setSide((Side.CLIENT))
            .setApplyIf(() -> Common.config.unbindKeybindsByDefault).addTargetedMod(TargetedMod.THAUMCRAFT)),
    UNBIND_KEYS_COFH(new Builder("Unbind COFH Core keybinds").addMixinClasses("cofhcore.MixinProxyClient")
            .setSide((Side.CLIENT)).setPhase(Phase.EARLY).setApplyIf(() -> Common.config.unbindKeybindsByDefault)
            .addTargetedMod(TargetedMod.COFH_CORE)),
    CHANGE_KEYBIND_CATEGORY_AUTOMAGY(new Builder("Change keybind category of Automagy")
            .addMixinClasses("automagy.MixinAutomagyKeyHandler").setSide((Side.CLIENT))
            .setApplyIf(() -> Common.config.unbindKeybindsByDefault).addTargetedMod(TargetedMod.AUTOMAGY)),

    // Pollution
    POLLUTION_RENDER_BLOCKS(new Builder("Changes colors of certain blocks based on pollution levels")
            .addMixinClasses("minecraft.MixinRenderBlocks_PollutionWithoutOptifine").addTargetedMod(TargetedMod.GT5U)
            .addTargetedMod(TargetedMod.VANILLA).addExcludedMod(TargetedMod.OPTIFINE).setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.pollutionBlockRecolor).setPhase(Phase.EARLY)),
    POLLUTION_RENDER_BLOCKS_OPTIFINE(new Builder("Changes colors of certain blocks based on pollution levels")
            .addMixinClasses("minecraft.MixinRenderBlocks_PollutionWithOptifine").addTargetedMod(TargetedMod.GT5U)
            .addTargetedMod(TargetedMod.VANILLA).addTargetedMod(TargetedMod.OPTIFINE).setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.pollutionBlockRecolor).setPhase(Phase.EARLY)),
    POLLUTION_RENDER_BLOCKS_BOP(new Builder("Changes colors of certain blocks based on pollution levels")
            .addMixinClasses("biomesoplenty.MixinFoliageRenderer_Pollution").addTargetedMod(TargetedMod.GT5U)
            .addTargetedMod(TargetedMod.BOP).setSide(Side.CLIENT).setApplyIf(() -> Common.config.pollutionBlockRecolor)
            .setPhase(Phase.LATE)),
    POLLUTION_MINECRAFT_FURNACE(new Builder("Minecraft Furnace Pollutes").setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinTileEntityFurnacePollution").setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.furnacesPollute).addTargetedMod(TargetedMod.VANILLA)),
    POLLUTION_IC2_IRON_FURNACE(new Builder("Ic2 Iron Furnace Pollutes")
            .addMixinClasses("ic2.MixinIC2IronFurnacePollution").setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.furnacesPollute).addTargetedMod(TargetedMod.IC2)),
    POLLUTION_THAUMCRAFT_ALCHEMICAL_FURNACE(new Builder("Thaumcraft Alchemical Construct Pollutes")
            .addMixinClasses("thaumcraft.MixinThaumcraftAlchemyFurnacePollution").setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.furnacesPollute).addTargetedMod(TargetedMod.THAUMCRAFT)),
    POLLUTION_RAILCRAFT(new Builder("Make Railcraft Pollute")
            .addMixinClasses(
                    "railcraft.MixinRailcraftBoilerPollution",
                    "railcraft.MixinRailcraftCokeOvenPollution",
                    "railcraft.MixinRailcraftTunnelBorePollution")
            .setSide(Side.BOTH).setApplyIf(() -> Common.config.railcraftPollutes)
            .addTargetedMod(TargetedMod.RAILCRAFT)),
    POLLUTION_ROCKET(new Builder("Make Rockets Pollute")
            .addMixinClasses("galacticraftcore.MixinGalacticraftRocketPollution").setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.rocketsPollute).addTargetedMod(TargetedMod.GALACTICRAFT_CORE)),
    IC2_CELL(new Builder("No IC2 Cell Consumption in tanks").addMixinClasses("ic2.MixinIC2ItemCell").setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.ic2CellWithContainer).addTargetedMod(TargetedMod.IC2));

    private final List<String> mixinClasses;
    private final Supplier<Boolean> applyIf;
    private final Phase phase;
    private final Side side;
    private final List<TargetedMod> targetedMods;
    private final List<TargetedMod> excludedMods;

    Mixins(Builder builder) {
        this.mixinClasses = builder.mixinClasses;
        this.applyIf = builder.applyIf;
        this.side = builder.side;
        this.targetedMods = builder.targetedMods;
        this.excludedMods = builder.excludedMods;
        this.phase = builder.phase;
        if (this.targetedMods.isEmpty()) {
            throw new RuntimeException("No targeted mods specified for " + this.name());
        }
        if (this.applyIf == null) {
            throw new RuntimeException("No ApplyIf function specified for " + this.name());
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

        private final List<String> mixinClasses = new ArrayList<>();
        private Supplier<Boolean> applyIf;
        private Side side = Side.BOTH;
        private Phase phase = Phase.LATE;
        private final List<TargetedMod> targetedMods = new ArrayList<>();
        private final List<TargetedMod> excludedMods = new ArrayList<>();

        public Builder(@SuppressWarnings("unused") String description) {}

        public Builder addMixinClasses(String... mixinClasses) {
            this.mixinClasses.addAll(Arrays.asList(mixinClasses));
            return this;
        }

        public Builder setPhase(Phase phase) {
            this.phase = phase;
            return this;
        }

        public Builder setSide(Side side) {
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
