package com.mitchej123.hodgepodge.mixins;

import com.mitchej123.hodgepodge.Common;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public enum Mixins {
    // Vanilla Fixes
    FIX_ENCHANTMENT_LEVEL_NUMERALS(
            new Builder("Fix enchantment levels not displaying properly above a certain value")
                    .setPhase(Phase.EARLY)
                    .addMixinClasses("minecraft.MixinEnchantment_FixRomanNumerals")
                    .setSide(Side.BOTH)
                    .setApplyIf(() -> Common.config.fixEnchantmentNumerals)
                    .addTargetedMod(TargetedMod.VANILLA)),
    FIX_INVENTORY_POTION_EFFECT_NUMERALS(new Builder("Fix potion effects level not displaying in the inventory")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinInventoryEffectRenderer_FixPotionEffectNumerals")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixPotionEffectNumerals)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_FRIENDLY_CREATURE_SOUNDS(new Builder("Fix Friendly Creature Sounds")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinSoundHandler")
            .setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> Common.config.fixFriendlyCreatureSounds)),
    THROTTLE_ITEMPICKUPEVENT(new Builder("Throttle Item Pickup Event")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityPlayer")
            .setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.throttleItemPickupEvent)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_PERSPECTIVE_CAMERA(new Builder("Camera Perspective Fix")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityRenderer")
            .setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> Common.config.fixPerspectiveCamera)),
    FIX_DEBUG_BOUNDING_BOX(new Builder("Fix Bounding Box")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinRenderManager")
            .setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> Common.config.fixDebugBoundingBox)),
    FENCE_CONNECTIONS_FIX(new Builder("Fix Fence Connections")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinBlockFence")
            .addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> Common.config.fixFenceConnections)),
    FIX_INVENTORY_OFFSET_WITH_POTIONS(new Builder("Fix Inventory Offset with Potions")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinInventoryEffectRenderer_PotionOffset")
            .setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> Common.config.fixPotionRenderOffset)),
    FIX_POTION_EFFECT_RENDERING(new Builder("Fix Potion Effect Rendering")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinInventoryEffectRenderer_PotionEffectRendering")
            .setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> Common.config.fixPotionEffectRender)),
    FIX_IMMOBILE_FIREBALLS(new Builder("Fix Immobile Fireballs")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityFireball")
            .addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> Common.config.fixImmobileFireballs)),
    LONGER_CHAT(new Builder("Longer Chat")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiNewChat_LongerChat")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.longerChat)
            .addTargetedMod(TargetedMod.VANILLA)),
    TRANSPARENT_CHAT(new Builder("Transparent Chat")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiNewChat_TransparentChat")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.transparentChat)
            .addTargetedMod(TargetedMod.VANILLA)),
    CHUNK_COORDINATES_HASHCODE(new Builder("Optimize Chunk Coordinates Hashcode")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinChunkCoordinates")
            .addTargetedMod(TargetedMod.VANILLA)
            .setApplyIf(() -> Common.config.speedupChunkCoordinatesHashCode)),
    TCP_NODELAY(new Builder("Set TCP NODELAY")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinTcpNoDelay")
            .setApplyIf(() -> Common.config.tcpNoDelay)
            .addTargetedMod(TargetedMod.VANILLA)),
    WORLD_UNPROTECTED_GET_BLOCK(new Builder("Fix world unprotected getBlock")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinWorldGetBlock")
            .setApplyIf(() -> Common.config.fixVanillaUnprotectedGetBlock)
            .addTargetedMod(TargetedMod.VANILLA)),
    WORLD_UNPROTECTED_LIGHT_VALUE(new Builder("Fix world unprotected light value")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinWorldLightValue")
            .setApplyIf(() -> Common.config.fixGetBlockLightValue)
            .addTargetedMod(TargetedMod.VANILLA)),
    FORGE_HOOKS_URL_FIX(new Builder("Fix forge URL hooks")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinForgeHooks")
            .setApplyIf(() -> Common.config.fixUrlDetection)
            .addTargetedMod(TargetedMod.VANILLA)),
    NORTHWEST_BIAS_FIX(new Builder("Fix Northwest Bias")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinRandomPositionGenerator")
            .setApplyIf(() -> Common.config.fixNorthWestBias)
            .addTargetedMod(TargetedMod.VANILLA)),
    SPEEDUP_VANILLA_FURNACE(new Builder("Speedup Vanilla Furnace")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinFurnaceRecipes")
            .setApplyIf(() -> Common.config.speedupVanillaFurnace)
            .addTargetedMod(TargetedMod.VANILLA)
            .addTargetedMod(TargetedMod.GTNHLIB)),
    GAMEOVER_GUI_LOCKED_DISABLED(new Builder("Fix Gameover GUI")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinGuiGameOver")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixGuiGameOver)
            .addTargetedMod(TargetedMod.VANILLA)),
    PREVENT_PICKUP_LOOT(new Builder("Prevent monsters from picking up loot")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityLivingPickup")
            .setApplyIf(() -> Common.config.preventPickupLoot)
            .addTargetedMod(TargetedMod.VANILLA)),
    DROP_PICKED_LOOT_ON_DESPAWN(new Builder("Drop picked up loot on despawn")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityLivingDrop")
            .setApplyIf(() -> Common.config.dropPickedLootOnDespawn)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_HOPPER_HIT_BOX(new Builder("Fix Vanilla Hopper hit box")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinBlockHopper")
            .setApplyIf(() -> Common.config.fixHopperHitBox)
            .addTargetedMod(TargetedMod.VANILLA)),
    TILE_RENDERER_PROFILER_DISPATCHER(new Builder("TileEntity Render Dispatcher Fix")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.profiler.TileEntityRendererDispatcherMixin")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.enableTileRendererProfiler)
            .addTargetedMod(TargetedMod.VANILLA)),
    TILE_RENDERER_PROFILER_MINECRAFT(new Builder("Tile Entity Render Profiler")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.profiler.MinecraftMixin")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.enableTileRendererProfiler)
            .addTargetedMod(TargetedMod.VANILLA)),
    DIMENSION_CHANGE_FIX(new Builder("Dimension Change Heart Fix")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinServerConfigurationManager", "minecraft.MixinEntityPlayerMP")
            .setApplyIf(() -> Common.config.fixDimensionChangeHearts)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_EATING_STACKED_STEW(new Builder("Stacked Mushroom Stew Eating Fix")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinItemSoup")
            .setApplyIf(() -> Common.config.fixEatingStackedStew)
            .addTargetedMod(TargetedMod.VANILLA)),
    INCREASE_PARTICLE_LIMIT(new Builder("Increase Particle Limit")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEffectRenderer")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.increaseParticleLimit)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_POTION_LIMIT(new Builder("Fix Potion Limit")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinPotionEffect")
            .setApplyIf(() -> Common.config.fixPotionLimit)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_HOPPER_VOIDING_ITEMS(new Builder("Fix Hopper Voiding Items")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinTileEntityHopper")
            .setApplyIf(() -> Common.config.fixHopperVoidingItems)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_HUGE_CHAT_KICK(new Builder("Fix huge chat kick")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinS02PacketChat")
            .setApplyIf(() -> Common.config.fixHugeChatKick)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_WORLD_SERVER_LEAKING_UNLOADED_ENTITIES(new Builder("Fix world server leaking unloaded entities")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinWorldServerUpdateEntities")
            .setApplyIf(() -> Common.config.fixWorldServerLeakingUnloadedEntities)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_ARROW_WRONG_LIGHTING(new Builder("Fix arrow wrong lighting")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinRendererLivingEntity")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixGlStateBugs)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_RESIZABLE_FULLSCREEN(new Builder("Fix Resizable Fullscreen")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinMinecraft_ResizableFullscreen")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixResizableFullscreen)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_UNFOCUSED_FULLSCREEN(new Builder("Fix Unfocused Fullscreen")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinMinecraft_UnfocusedFullscreen")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixUnfocusedFullscreen)
            .addTargetedMod(TargetedMod.VANILLA)),
    FIX_OPTIFINE_CHUNKLOADING_CRASH(new Builder("Fix Optifine Chunkloading Crash")
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> Common.config.fixOptifineChunkLoadingCrash)
            .setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA)
            .addTargetedMod(TargetedMod.OPTIFINE)
            .addMixinClasses("minecraft.MixinGameSettings")),
    ADD_TOGGLE_DEBUG_MESSAGE(new Builder("Toggle Debug Message")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinMinecraft_ToggleDebugMessage")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.addToggleDebugMessage)
            .addTargetedMod(TargetedMod.VANILLA)),
    SPEEDUP_VANILLA_ANIMATIONS(new Builder("Speedup Vanilla Animations")
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> Common.config.speedupAnimations)
            .setSide(Side.CLIENT)
            .addTargetedMod(TargetedMod.VANILLA)
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
    SPEEDUP_VANILLA_ANIMATIONS_FC(new Builder("Speedup Vanilla Animations - Fastcraft")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.textures.client.fastcraft.MixinTextureMap")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.speedupAnimations)
            .addTargetedMod(TargetedMod.VANILLA)
            .addTargetedMod(TargetedMod.FASTCRAFT)
            .addExcludedMod(TargetedMod.OPTIFINE)),
    HIDE_POTION_PARTICLES(new Builder("Hide Potion Particles")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityLivingBase_HidePotionParticles")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.hidePotionParticlesFromSelf)
            .addTargetedMod(TargetedMod.VANILLA)),
    DIMENSION_MANAGER_DEBUG(new Builder("Dimension Manager Debug")
            .setPhase(Phase.EARLY)
            .setSide(Side.BOTH)
            .addMixinClasses("minecraft.MixinDimensionManager")
            .setApplyIf(() -> Common.config.dimensionManagerDebug)
            .addTargetedMod(TargetedMod.VANILLA)),
    OPTIMIZE_TILEENTITY_REMOVAL(new Builder("Optimize TileEntity Removal")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinWorldUpdateEntities")
            .setApplyIf(() -> Common.config.optimizeTileentityRemoval)
            .addTargetedMod(TargetedMod.VANILLA)
            .addExcludedMod(TargetedMod.FASTCRAFT)
            .addExcludedMod(TargetedMod.BUKKIT)),
    FIX_POTION_ITERATING(new Builder("Fix Potion Iterating")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinEntityLivingBase_FixPotionException")
            .setApplyIf(() -> Common.config.fixPotionIterating)
            .addTargetedMod(TargetedMod.VANILLA)),
    OPTIMIZE_ASMDATATABLE_INDEX(new Builder("Optimize ASM DataTable Index")
            .setPhase(Phase.EARLY)
            .addMixinClasses("forge.MixinASMDataTable")
            .setApplyIf(() -> Common.config.optimizeASMDataTable)
            .addTargetedMod(TargetedMod.VANILLA)),
    RENDER_DEBUG(new Builder("Render Debug")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinRenderGlobal")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.renderDebug)
            .addTargetedMod(TargetedMod.VANILLA)),
    STATIC_LAN_PORT(new Builder("Static Lan Port")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.server.MixinHttpUtil")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.enableDefaultLanPort)
            .addTargetedMod(TargetedMod.VANILLA)),
    CROSSHAIR_THIRDPERSON(new Builder("Crosshairs Thirdperson")
            .setPhase(Phase.EARLY)
            .addMixinClasses("forge.MixinGuiIngameForge")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.hideCrosshairInThirdPerson)
            .addTargetedMod(TargetedMod.VANILLA)),

    // Ic2 adjustments
    IC2_UNPROTECTED_GET_BLOCK_FIX(new Builder("IC2 Kinetic Fix")
            .setPhase(Phase.EARLY)
            .addMixinClasses("ic2.MixinIc2WaterKinetic")
            .setApplyIf(() -> Common.config.fixIc2UnprotectedGetBlock)
            .addTargetedMod(TargetedMod.IC2)),
    IC2_DIRECT_INV_ACCESS(new Builder("IC2 Direct Inventory Access Fix")
            .setPhase(Phase.EARLY)
            .addMixinClasses("ic2.MixinItemCropSeed", "ic2.MixinTileEntityCrop")
            .setApplyIf(() -> Common.config.fixIc2DirectInventoryAccess)
            .addTargetedMod(TargetedMod.IC2)),
    IC2_NIGHT_VISION_NANO(new Builder("IC2 Nightvision Fix")
            .setPhase(Phase.EARLY)
            .addMixinClasses(
                    "ic2.MixinIc2NanoSuitNightVision",
                    "ic2.MixinIc2QuantumSuitNightVision",
                    "ic2.MixinIc2NightVisionGoggles")
            .setApplyIf(() -> Common.config.fixIc2Nightvision)
            .addTargetedMod(TargetedMod.IC2)),
    IC2_REACTOR_DUPE(new Builder("IC2 Reactor Dupe Fix")
            .setPhase(Phase.EARLY)
            .addMixinClasses("ic2.MixinTileEntityReactorChamberElectricNoDupe")
            .setApplyIf(() -> Common.config.fixIc2ReactorDupe)
            .addTargetedMod(TargetedMod.IC2)),
    IC2_REACTOR_INVENTORY_SPEEDUP(new Builder("IC2 Reactor Inventory Speedup Fix")
            .setPhase(Phase.EARLY)
            .addMixinClasses("ic2.MixinTileEntityReactorChamberElectricInvSpeedup")
            .setApplyIf(() -> Common.config.optimizeIc2ReactorInventoryAccess)
            .addTargetedMod(TargetedMod.IC2)),
    HIDE_IC2_REACTOR_COOLANT_SLOTS(new Builder("IC2 Reactory Fix")
            .setPhase(Phase.EARLY)
            .addMixinClasses("ic2.MixinTileEntityNuclearReactorElectric")
            .setApplyIf(() -> Common.config.hideIc2ReactorSlots)
            .addTargetedMod(TargetedMod.IC2)),
    IC2_HAZMAT(new Builder("Hazmat")
            .setPhase(Phase.LATE)
            .addMixinClasses("ic2.MixinIc2Hazmat")
            .setApplyIf(() -> Common.config.fixIc2Hazmat)
            .addTargetedMod(TargetedMod.IC2)
            .addTargetedMod(TargetedMod.GT5U)
            .addExcludedMod(TargetedMod.GT6)),
    IC2_FLUID_CONTAINER_TOOLTIP(new Builder("IC2 Fluid Container Tooltip Fix")
            .setPhase(Phase.EARLY)
            .addMixinClasses("ic2.MixinItemIC2FluidContainer")
            .setApplyIf(() -> Common.config.displayIc2FluidLocalizedName)
            .addTargetedMod(TargetedMod.IC2)),
    IC2_FLUID_RENDER_FIX(new Builder("IC2 Fluid Render Fix")
            .setPhase(Phase.EARLY)
            .addMixinClasses("ic2.textures.MixinRenderLiquidCell")
            .setApplyIf(() -> Common.config.speedupAnimations)
            .addTargetedMod(TargetedMod.IC2)),

    // gregtech
    GREGTECH_FLUID_RENDER_FIX(new Builder("Fluid Render Fix")
            .addMixinClasses("gregtech.textures.MixinGT_GeneratedMaterial_Renderer")
            .setApplyIf(() -> Common.config.speedupAnimations)
            .addTargetedMod(TargetedMod.GT5U)),

    // COFH
    COFH_CORE_UPDATE_CHECK(new Builder("Yeet COFH Core Update Check")
            .setPhase(Phase.EARLY)
            .addMixinClasses("cofhcore.MixinCoFHCoreUpdateCheck")
            .setApplyIf(() -> Common.config.removeUpdateChecks)
            .addTargetedMod(TargetedMod.COFH_CORE)),

    // Railcraft Anchors
    WAKE_ANCHORS_ON_LOGIN_PASSIVE(new Builder("Wake passive anchors on login")
            .addMixinClasses("railcraft.MixinTileAnchorPassive")
            .setApplyIf(() -> Common.config.installAnchorAlarm)
            .addTargetedMod(TargetedMod.RAILCRAFT)),
    WAKE_ANCHORS_ON_LOGIN_PERSONAL(new Builder("Wake person anchors on login")
            .addMixinClasses("railcraft.MixinTileAnchorPersonal")
            .setApplyIf(() -> Common.config.installAnchorAlarm)
            .addTargetedMod(TargetedMod.RAILCRAFT)),

    // Hunger overhaul
    HUNGER_OVERHAUL_LOW_STAT_EFFECT(new Builder("Patch unintended low stat effects")
            .addMixinClasses("hungeroverhaul.MixinHungerOverhaulLowStatEffect")
            .setApplyIf(() -> Common.config.fixHungerOverhaul)
            .addTargetedMod(TargetedMod.HUNGER_OVERHAUL)),
    HUNGER_OVERHAUL_REGEN(new Builder("Patch Regen")
            .addMixinClasses("hungeroverhaul.MixinHungerOverhaulHealthRegen")
            .setApplyIf(() -> Common.config.fixHungerOverhaul)
            .addTargetedMod(TargetedMod.HUNGER_OVERHAUL)),

    // Thaumcraft
    ADD_CV_SUPPORT_TO_WAND_PEDESTAL(new Builder("CV Support for Wand Pedestal")
            .addMixinClasses("thaumcraft.MixinTileWandPedestal")
            .setApplyIf(() -> Common.config.addCVSupportToWandPedestal)
            .addTargetedMod(TargetedMod.THAUMCRAFT)),

    // BOP
    DEDUPLICATE_FORESTRY_COMPAT_IN_BOP(new Builder("BOP Forestry Compat")
            .addMixinClasses("biomesoplenty.MixinForestryIntegration")
            .setApplyIf(() -> Common.config.deduplicateForestryCompatInBOP)
            .addTargetedMod(TargetedMod.BOP)),
    SPEEDUP_BOP_BIOME_FOG(new Builder("BOP Biome Fog")
            .addMixinClasses("biomesoplenty.MixinFogHandler")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.speedupBOPFogHandling)
            .addTargetedMod(TargetedMod.BOP)),
    SPEEDUP_BOP_BIOME_FOG_ACCESSOR(new Builder("BOP Biome Fog Accessor")
            .addMixinClasses("biomesoplenty.AccessorFogHandler")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.speedupBOPFogHandling)
            .addTargetedMod(TargetedMod.BOP)),
    BIG_FIR_TREES(new Builder("BOP Fir Trees")
            .addMixinClasses("biomesoplenty.MixinBlockBOPSapling")
            .setApplyIf(() -> Common.config.makeBigFirsPlantable)
            .addTargetedMod(TargetedMod.BOP)),

    // MrTJPCore (Project Red)
    FIX_HUD_LIGHTING_GLITCH(new Builder("HUD Lighting glitch")
            .addMixinClasses("mrtjpcore.MixinFXEngine")
            .setApplyIf(() -> Common.config.fixHudLightingGlitch)
            .addTargetedMod(TargetedMod.MRTJPCORE)),
    FIX_POPPING_OFF(new Builder("Fix Popping Off")
            .addMixinClasses("mrtjpcore.MixinPlacementLib")
            .setApplyIf(() -> Common.config.fixComponentsPoppingOff)
            .addTargetedMod(TargetedMod.MRTJPCORE)),

    // Automagy
    IMPLEMENTS_CONTAINER_FOR_THIRSTY_TANK(new Builder("Thirsty Tank Container")
            .addMixinClasses("automagy.MixinItemBlockThirstyTank")
            .setApplyIf(() -> Common.config.thirstyTankContainer)
            .addTargetedMod(TargetedMod.AUTOMAGY)),

    // ProjectE
    FIX_FURNACE_ITERATION(new Builder("Speedup Furnaces")
            .addMixinClasses("projecte.MixinObjHandler")
            .setApplyIf(() -> Common.config.speedupVanillaFurnace)
            .addTargetedMod(TargetedMod.PROJECTE)),

    // Journeymap
    FIX_JOURNEYMAP_KEYBINDS(new Builder("Fix Journeymap Keybinds")
            .setSide(Side.CLIENT)
            .addMixinClasses("journeymap.MixinConstants")
            .setApplyIf(() -> Common.config.fixJourneymapKeybinds)
            .addTargetedMod(TargetedMod.JOURNEYMAP)),

    // Pam's Harvest the Nether
    FIX_IGNIS_FRUIT_AABB(new Builder("Ignis Fruit")
            .addMixinClasses("harvestthenether.MixinBlockPamFruit")
            .setApplyIf(() -> Common.config.fixIgnisFruitAABB)
            .addTargetedMod(TargetedMod.HARVESTTHENETHER)),
    FIX_NETHER_LEAVES_FACE_RENDERING(new Builder("Nether Leaves")
            .addMixinClasses("harvestthenether.MixinBlockNetherLeaves")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixIgnisFruitAABB)
            .addTargetedMod(TargetedMod.HARVESTTHENETHER)),

    // Potion Render Offset Fixes - Various Mods
    FIX_BAUBLES_INVENTORY_OFFSET_WITH_POTIONS(new Builder("Baubles Inventory with Potions")
            .addMixinClasses("baubles.MixinGuiEvents")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixPotionRenderOffset)
            .addTargetedMod(TargetedMod.BAUBLES)),
    FIX_GALACTICRAFT_INVENTORY_OFFSET_WITH_POTIONS(new Builder("Galacticraft Inventory with Potions")
            .addMixinClasses("galacticraftcore.MixinGuiExtendedInventory")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixPotionRenderOffset)
            .addTargetedMod(TargetedMod.GALACTICRAFT_CORE)),
    FIX_TRAVELLERSGEAR_INVENTORY_OFFSET_WITH_POTIONS(new Builder("Travelers Gear with Potions")
            .addMixinClasses("travellersgear.MixinClientProxy")
            .setSide(Side.CLIENT)
            .setApplyIf(() -> Common.config.fixPotionRenderOffset)
            .addTargetedMod(TargetedMod.TRAVELLERSGEAR)),

    // Exu Unenchanting fix
    FIX_EXTRA_UTILITIES_UNENCHANTING(new Builder("Fix Exu Unenchanting")
            .addMixinClasses("extrautilities.MixinRecipeUnEnchanting")
            .setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.fixExtraUtilitiesUnEnchanting)
            .addTargetedMod(TargetedMod.EXTRA_UTILITIES)),

    // Various Exploits/Fixes
    GC_TIME_COMMAND_FIX(new Builder("GC Time Fix")
            .addMixinClasses("minecraft.MixinTimeCommandGalacticraftFix")
            .setPhase(Phase.EARLY)
            .setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.fixTimeCommandWithGC)
            .addTargetedMod(TargetedMod.VANILLA)
            .addTargetedMod(TargetedMod.GALACTICRAFT_CORE)),
    BIBLIOCRAFT_PACKET_FIX(new Builder("Packet Fix")
            .addMixinClasses("bibliocraft.MixinBibliocraftPatchPacketExploits")
            .setSide((Side.BOTH))
            .setApplyIf(() -> Common.config.fixBibliocraftPackets)
            .addTargetedMod(TargetedMod.BIBLIOCRAFT)),
    ZTONES_PACKET_FIX(new Builder("Packet Fix")
            .addMixinClasses("ztones.MixinZtonesPatchPacketExploits")
            .setSide((Side.BOTH))
            .setApplyIf(() -> Common.config.fixZTonesPackets)
            .addTargetedMod(TargetedMod.ZTONES)),

    // Pollution
    POLLUTION_MINECRAFT_FURNACE(new Builder("Minecraft Furnace Pollutes")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinTileEntityFurnacePollution")
            .setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.furnacesPollute)
            .addTargetedMod(TargetedMod.VANILLA)),
    POLLUTION_IC2_IRON_FURNACE(new Builder("Ic2 Iron Furnace Pollutes")
            .addMixinClasses("ic2.MixinIC2IronFurnacePollution")
            .setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.furnacesPollute)
            .addTargetedMod(TargetedMod.IC2)),
    POLLUTION_THAUMCRAFT_ALCHEMICAL_FURNACE(new Builder("Thaumcraft Alchemical Construct Pollutes")
            .addMixinClasses("thaumcraft.MixinThaumcraftAlchemyFurnacePollution")
            .setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.furnacesPollute)
            .addTargetedMod(TargetedMod.THAUMCRAFT)),

    POLLUTION_RAILCRAFT(new Builder("Make Railcraft Pollute")
            .addMixinClasses(
                    "railcraft.MixinRailcraftBoilerPollution",
                    "railcraft.MixinRailcraftCokeOvenPollution",
                    "railcraft.MixinRailcraftTunnelBorePollution")
            .setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.railcraftPollutes)
            .addTargetedMod(TargetedMod.RAILCRAFT)),
    POLLUTION_ROCKET(new Builder("Make Rockets Pollute")
            .addMixinClasses("galacticraftcore.MixinGalacticraftRocketPollution")
            .setSide(Side.BOTH)
            .setApplyIf(() -> Common.config.rocketsPollute)
            .addTargetedMod(TargetedMod.GALACTICRAFT_CORE));

    public final String name;
    public final List<String> mixinClasses;
    private final Supplier<Boolean> applyIf;
    public final Phase phase;
    private final Side side;
    public final List<TargetedMod> targetedMods;
    public final List<TargetedMod> excludedMods;

    private static class Builder {
        private final String name;
        private final List<String> mixinClasses = new ArrayList<>();
        private Supplier<Boolean> applyIf;
        private Side side = Side.BOTH;
        private Phase phase = Phase.LATE;
        private final List<TargetedMod> targetedMods = new ArrayList<>();
        private final List<TargetedMod> excludedMods = new ArrayList<>();

        public Builder(String name) {
            this.name = name;
        }

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

    Mixins(Builder builder) {
        this.name = builder.name;
        this.mixinClasses = builder.mixinClasses;
        this.applyIf = builder.applyIf;
        this.side = builder.side;
        this.targetedMods = builder.targetedMods;
        this.excludedMods = builder.excludedMods;
        this.phase = builder.phase;
        if (this.targetedMods.isEmpty()) {
            throw new RuntimeException("No targeted mods specified for " + this.name);
        }
        if (this.applyIf == null) {
            throw new RuntimeException("No ApplyIf function specified for " + this.name);
        }
    }

    private boolean shouldLoadSide() {
        return (side == Side.BOTH
                || (side == Side.SERVER && FMLLaunchHandler.side().isServer())
                || (side == Side.CLIENT && FMLLaunchHandler.side().isClient()));
    }

    private boolean allModsLoaded(List<TargetedMod> targetedMods, Set<String> loadedCoreMods, Set<String> loadedMods) {
        if (targetedMods.isEmpty()) return false;

        for (TargetedMod target : targetedMods) {
            if (target == TargetedMod.VANILLA) continue;

            // Check coremod first
            if (!loadedCoreMods.isEmpty()
                    && target.coreModClass != null
                    && !loadedCoreMods.contains(target.coreModClass)) return false;
            else if (!loadedMods.isEmpty() && target.modId != null && !loadedMods.contains(target.modId)) return false;
        }

        return true;
    }

    private boolean noModsLoaded(List<TargetedMod> targetedMods, Set<String> loadedCoreMods, Set<String> loadedMods) {
        if (targetedMods.isEmpty()) return true;

        for (TargetedMod target : targetedMods) {
            if (target == TargetedMod.VANILLA) continue;

            // Check coremod first
            if (!loadedCoreMods.isEmpty()
                    && target.coreModClass != null
                    && loadedCoreMods.contains(target.coreModClass)) return false;
            else if (!loadedMods.isEmpty() && target.modId != null && loadedMods.contains(target.modId)) return false;
        }

        return true;
    }

    public boolean shouldLoad(Set<String> loadedCoreMods, Set<String> loadedMods) {
        return (shouldLoadSide()
                && applyIf.get()
                && allModsLoaded(targetedMods, loadedCoreMods, loadedMods)
                && noModsLoaded(excludedMods, loadedCoreMods, loadedMods));
    }

    enum Side {
        BOTH,
        CLIENT,
        SERVER
    }

    public enum Phase {
        EARLY,
        LATE,
    }
}
