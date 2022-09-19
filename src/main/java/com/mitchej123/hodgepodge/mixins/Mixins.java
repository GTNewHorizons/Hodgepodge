package com.mitchej123.hodgepodge.mixins;

import com.mitchej123.hodgepodge.Common;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public enum Mixins {
    // Vanilla Fixes
    THROTTLE_ITEMPICKUPEVENT(
            "minecraft.MixinEntityPlayer", Side.BOTH, () -> Common.config.throttleItemPickupEvent, TargetedMod.VANILLA),
    FIX_PERSPECTIVE_CAMERA(
            "minecraft.MixinEntityRenderer",
            Side.CLIENT,
            () -> Common.config.fixPerspectiveCamera,
            TargetedMod.VANILLA),
    FIX_DEBUG_BOUNDING_BOX(
            "minecraft.MixinRenderManager", Side.CLIENT, () -> Common.config.fixDebugBoundingBox, TargetedMod.VANILLA),
    FENCE_CONNECTIONS_FIX("minecraft.MixinBlockFence", () -> Common.config.fixFenceConnections, TargetedMod.VANILLA),
    FIX_INVENTORY_OFFSET_WITH_POTIONS(
            "minecraft.MixinInventoryEffectRenderer_PotionOffset",
            Side.CLIENT,
            () -> Common.config.fixPotionRenderOffset,
            TargetedMod.VANILLA),
    FIX_POTION_EFFECT_RENDERING(
            "minecraft.MixinInventoryEffectRenderer_PotionEffectRendering",
            Side.CLIENT,
            () -> Common.config.fixPotionEffectRender,
            TargetedMod.VANILLA),
    FIX_IMMOBILE_FIREBALLS(
            "minecraft.MixinEntityFireball", () -> Common.config.fixImmobileFireballs, TargetedMod.VANILLA),
    CHUNK_COORDINATES_HASHCODE(
            "minecraft.MixinChunkCoordinates",
            () -> Common.config.speedupChunkCoordinatesHashCode,
            TargetedMod.VANILLA),
    TCP_NODELAY("minecraft.MixinTcpNoDelay", () -> Common.config.tcpNoDelay, TargetedMod.VANILLA),
    WORLD_UNPROTECTED_GET_BLOCK(
            "minecraft.MixinWorldGetBlock", () -> Common.config.fixVanillaUnprotectedGetBlock, TargetedMod.VANILLA),
    WORLD_UNPROTECTED_LIGHT_VALUE(
            "minecraft.MixinWorldLightValue", () -> Common.config.fixGetBlockLightValue, TargetedMod.VANILLA),
    FORGE_HOOKS_URL_FIX("minecraft.MixinForgeHooks", () -> Common.config.fixUrlDetection, TargetedMod.VANILLA),
    NORTHWEST_BIAS_FIX(
            "minecraft.MixinRandomPositionGenerator", () -> Common.config.fixNorthWestBias, TargetedMod.VANILLA),
    SPEEDUP_VANILLA_FURNACE(
            "minecraft.MixinFurnaceRecipes", () -> Common.config.speedupVanillaFurnace, TargetedMod.GTNHLIB),
    GAMEOVER_GUI_LOCKED_DISABLED(
            "minecraft.MixinGuiGameOver", Side.CLIENT, () -> Common.config.fixGuiGameOver, TargetedMod.VANILLA),
    PREVENT_PICKUP_LOOT(
            "minecraft.MixinEntityLivingPickup", () -> Common.config.preventPickupLoot, TargetedMod.VANILLA),
    DROP_PICKED_LOOT_ON_DESPAWN(
            "minecraft.MixinEntityLivingDrop", () -> Common.config.dropPickedLootOnDespawn, TargetedMod.VANILLA),
    FIX_HOPPER_HIT_BOX("minecraft.MixinBlockHopper", () -> Common.config.fixHopperHitBox, TargetedMod.VANILLA),
    TILE_RENDERER_PROFILER_DISPATCHER(
            "minecraft.profiler.TileEntityRendererDispatcherMixin",
            Side.CLIENT,
            () -> Common.config.enableTileRendererProfiler,
            TargetedMod.VANILLA),
    TILE_RENDERER_PROFILER_MINECRAFT(
            "minecraft.profiler.MinecraftMixin",
            Side.CLIENT,
            () -> Common.config.enableTileRendererProfiler,
            TargetedMod.VANILLA),
    DIMENSION_CHANGE_FIX(
            "minecraft.MixinServerConfigurationManager",
            Side.BOTH,
            () -> Common.config.fixDimensionChangeHearts,
            TargetedMod.VANILLA),
    CLONE_PLAYER_HEART_FIX(
            "minecraft.MixinEntityPlayerMP",
            Side.BOTH,
            () -> Common.config.fixDimensionChangeHearts,
            TargetedMod.VANILLA),
    INCREASE_PARTICLE_LIMIT(
            "minecraft.MixinEffectRenderer",
            Side.CLIENT,
            () -> Common.config.increaseParticleLimit,
            TargetedMod.VANILLA),
    FIX_POTION_LIMIT("minecraft.MixinPotionEffect", Side.BOTH, () -> Common.config.fixPotionLimit, TargetedMod.VANILLA),
    FIX_HOPPER_VOIDING_ITEMS(
            "minecraft.MixinTileEntityHopper", () -> Common.config.fixHopperVoidingItems, TargetedMod.VANILLA),
    FIX_HUGE_CHAT_KICK("minecraft.MixinS02PacketChat", () -> Common.config.fixHugeChatKick, TargetedMod.VANILLA),
    FIX_WORLD_SERVER_LEAKING_UNLOADED_ENTITIES(
            "minecraft.MixinWorldServerUpdateEntities",
            () -> Common.config.fixWorldServerLeakingUnloadedEntities,
            TargetedMod.VANILLA),
    FIX_ARROW_WRONG_LIGHTING(
            "minecraft.MixinRendererLivingEntity",
            Side.CLIENT,
            () -> Common.config.fixGlStateBugs,
            TargetedMod.VANILLA),
    FIX_RESIZABLE_FULLSCREEN(
            "minecraft.MixinMinecraft_ResizableFullscreen",
            Side.CLIENT,
            () -> Common.config.fixResizableFullscreen,
            TargetedMod.VANILLA),
    FIX_UNFOCUSED_FULLSCREEN(
            "minecraft.MixinMinecraft_UnfocusedFullscreen",
            Side.CLIENT,
            () -> Common.config.fixUnfocusedFullscreen,
            TargetedMod.VANILLA),
    ADD_TOGGLE_DEBUG_MESSAGE(
            "minecraft.MixinMinecraft_ToggleDebugMessage",
            Side.CLIENT,
            () -> Common.config.addToggleDebugMessage,
            TargetedMod.GTNHLIB),
    SPEEDUP_VANILLA_ANIMATIONS(
            () -> Common.config.speedupAnimations,
            Side.CLIENT,
            TargetedMod.VANILLA,
            "minecraft.textures.client.MixinTextureAtlasSprite",
            "minecraft.textures.client.MixinTextureMap",
            "minecraft.textures.client.MixinBlockFire",
            "minecraft.textures.client.MixinMinecraftForgeClient",
            "minecraft.textures.client.MixinChunkCache",
            "minecraft.textures.client.MixinRenderBlocks",
            "minecraft.textures.client.MixinRenderBlockFluid",
            "minecraft.textures.client.MixinWorldRenderer",
            "minecraft.textures.client.MixinRenderItem"),
    SPEEDUP_VANILLA_ANIMATIONS_FC(
            "minecraft.textures.client.fastcraft.MixinTextureMap",
            Side.CLIENT,
            () -> Common.config.speedupAnimations,
            TargetedMod.FASTCRAFT),
    FIX_POTION_ITERATING(
            "minecraft.MixinEntityLivingPotions",
            Side.BOTH,
            () -> Common.config.fixPotionIterating,
            TargetedMod.VANILLA),
    OPTIMIZE_ASMDATATABLE_INDEX(
            "forge.MixinASMDataTable", Side.BOTH, () -> Common.config.optimizeASMDataTable, TargetedMod.VANILLA),
    SQUASH_BED_ERROR_MESSAGE(
            "minecraft.MixinNetHandlePlayClient",
            Side.CLIENT,
            () -> Common.config.squashBedErrorMessage,
            TargetedMod.VANILLA),
    RENDER_DEBUG("minecraft.MixinRenderGlobal", Side.CLIENT, () -> Common.config.renderDebug, TargetedMod.VANILLA),
    STATIC_LAN_PORT(
            "minecraft.server.MixinHttpUtil",
            Side.CLIENT,
            () -> Common.config.enableDefaultLanPort,
            TargetedMod.VANILLA),
    CROSSHAIR_THIRDPERSON(
            "forge.MixinGuiIngameForge",
            Side.BOTH,
            () -> Common.config.hideCrosshairInThirdPerson,
            TargetedMod.VANILLA),

    // Potentially obsolete vanilla fixes
    GRASS_GET_BLOCK_FIX(
            "minecraft.MixinBlockGrass", () -> Common.config.fixVanillaUnprotectedGetBlock, TargetedMod.VANILLA),
    FIRE_GET_BLOCK_FIX(
            "minecraft.MixinBlockFireGetBlock", () -> Common.config.fixVanillaUnprotectedGetBlock, TargetedMod.VANILLA),
    VILLAGE_GET_BLOCK_FIX(
            "minecraft.MixinVillage", () -> Common.config.fixVanillaUnprotectedGetBlock, TargetedMod.VANILLA),
    VILLAGE_COLLECTION_GET_BLOCK_FIX(
            "minecraft.MixinVillageCollection", () -> Common.config.fixVanillaUnprotectedGetBlock, TargetedMod.VANILLA),
    FLUID_CLASSIC_GET_BLOCK_FIX(
            "minecraft.MixinBlockFluidClassic", () -> Common.config.fixVanillaUnprotectedGetBlock, TargetedMod.VANILLA),

    // Ic2 adjustments
    IC2_UNPROTECTED_GET_BLOCK_FIX(
            "ic2.MixinIc2WaterKinetic", () -> Common.config.fixIc2UnprotectedGetBlock, TargetedMod.IC2),
    IC2_DIRECT_INV_ACCESS_SEED(
            "ic2.MixinItemCropSeed", () -> Common.config.fixIc2DirectInventoryAccess, TargetedMod.IC2),
    IC2_DIRECT_INV_ACCESS_CROP(
            "ic2.MixinTileEntityCrop", () -> Common.config.fixIc2DirectInventoryAccess, TargetedMod.IC2),
    IC2_NIGHT_VISION_NANO("ic2.MixinIc2NanoSuitNightVision", () -> Common.config.fixIc2Nightvision, TargetedMod.IC2),
    IC2_NIGHT_VISION_QUANTUM(
            "ic2.MixinIc2QuantumSuitNightVision", () -> Common.config.fixIc2Nightvision, TargetedMod.IC2),
    IC2_NIGHT_VISION_GOGGLES("ic2.MixinIc2NightVisionGoggles", () -> Common.config.fixIc2Nightvision, TargetedMod.IC2),
    IC2_REACTOR_DUPE(
            "ic2.MixinTileEntityReactorChamberElectricNoDupe", () -> Common.config.fixIc2ReactorDupe, TargetedMod.IC2),
    IC2_REACTOR_INVENTORY_SPEEDUP(
            "ic2.MixinTileEntityReactorChamberElectricInvSpeedup",
            () -> Common.config.optimizeIc2ReactorInventoryAccess,
            TargetedMod.IC2),
    HIDE_IC2_REACTOR_COOLANT_SLOTS(
            "ic2.MixinTileEntityNuclearReactorElectric", () -> Common.config.hideIc2ReactorSlots, TargetedMod.IC2),
    IC2_HAZMAT("ic2.MixinIc2Hazmat", () -> Common.config.fixIc2Hazmat, TargetedMod.IC2, TargetedMod.GT5U),
    IC2_FLUID_CONTAINER_TOOLTIP(
            "ic2.MixinItemIC2FluidContainer", () -> Common.config.displayIc2FluidLocalizedName, TargetedMod.IC2),
    IC2_FLUID_RENDER_FIX("ic2.textures.MixinRenderLiquidCell", () -> Common.config.speedupAnimations, TargetedMod.IC2),

    // gregtech
    GREGTECH_FLUID_RENDER_FIX(
            "gregtech.textures.MixinGT_GeneratedMaterial_Renderer",
            () -> Common.config.speedupAnimations,
            TargetedMod.GT5U),
    FIX_SAW_ICE_BREAK(
            "minecraft.MixinBlockIce", () -> Common.config.fixGTSawSpawningWaterWithIceBLock, TargetedMod.GT5U),

    // COFH
    COFH_CORE_UPDATE_CHECK(
            "cofhcore.MixinCoFHCoreUpdateCheck", () -> Common.config.removeUpdateChecks, TargetedMod.COFH_CORE),

    // Railcraft Anchors
    WAKE_ANCHORS_ON_LOGIN_PASSIVE(
            "railcraft.MixinTileAnchorPassive", () -> Common.config.installAnchorAlarm, TargetedMod.RAILCRAFT),
    WAKE_ANCHORS_ON_LOGIN_PERSONAL(
            "railcraft.MixinTileAnchorPersonal", () -> Common.config.installAnchorAlarm, TargetedMod.RAILCRAFT),

    // Hunger overhaul
    HUNGER_OVERHAUL_LOW_STAT_EFFECT(
            "hungeroverhaul.MixinHungerOverhaulLowStatEffect",
            () -> Common.config.fixHungerOverhaul,
            TargetedMod.HUNGER_OVERHAUL),
    HUNGER_OVERHAUL_REGEN(
            "hungeroverhaul.MixinHungerOverhaulHealthRegen",
            () -> Common.config.fixHungerOverhaul,
            TargetedMod.HUNGER_OVERHAUL),

    // Thaumcraft
    THAUMCRAFT_ARCANE_LAMP_GET_BLOCK_FIX(
            "thaumcraft.MixinArcaneLamp", () -> Common.config.fixThaumcraftUnprotectedGetBlock, TargetedMod.THAUMCRAFT),
    THAUMCRAFT_WISP_GET_BLOCK_FIX(
            "thaumcraft.MixinEntityWisp", () -> Common.config.fixThaumcraftUnprotectedGetBlock, TargetedMod.THAUMCRAFT),
    ADD_CV_SUPPORT_TO_WAND_PEDESTAL(
            "thaumcraft.MixinTileWandPedestal", () -> Common.config.addCVSupportToWandPedestal, TargetedMod.THAUMCRAFT),

    // BOP
    DEDUPLICATE_FORESTRY_COMPAT_IN_BOP(
            "biomesoplenty.MixinForestryIntegration",
            () -> Common.config.deduplicateForestryCompatInBOP,
            TargetedMod.BOP),
    SPEEDUP_BOP_BIOME_FOG(
            "biomesoplenty.MixinFogHandler", Side.CLIENT, () -> Common.config.speedupBOPFogHandling, TargetedMod.BOP),
    BIG_FIR_TREES("biomesoplenty.MixinBlockBOPSapling", () -> Common.config.makeBigFirsPlantable, TargetedMod.BOP),

    // MrTJPCore (Project Red)
    FIX_HUD_LIGHTING_GLITCH("mrtjpcore.MixinFXEngine", () -> Common.config.fixHudLightingGlitch, TargetedMod.MRTJPCORE),
    FIX_POPPING_OFF("mrtjpcore.MixinPlacementLib", () -> Common.config.fixComponentsPoppingOff, TargetedMod.MRTJPCORE),

    // Automagy
    IMPLEMENTS_CONTAINER_FOR_THIRSTY_TANK(
            "automagy.MixinItemBlockThirstyTank", () -> Common.config.thirstyTankContainer, TargetedMod.AUTOMAGY),

    // ProjectE
    FIX_FURNACE_ITERATION("projecte.MixinObjHandler", () -> Common.config.speedupVanillaFurnace, TargetedMod.PROJECTE),

    FIX_JOURNEYMAP_KEYBINDS(
            "journeymap.MixinConstants", () -> Common.config.fixJourneymapKeybinds, TargetedMod.JOURNEYMAP),

    // Pam's Harvest the Nether
    FIX_IGNIS_FRUIT_AABB(
            "harvestthenether.MixinBlockPamFruit", () -> Common.config.fixIgnisFruitAABB, TargetedMod.HARVESTTHENETHER),
    FIX_NETHER_LEAVES_FACE_RENDERING(
            "harvestthenether.MixinBlockNetherLeaves",
            Side.CLIENT,
            () -> Common.config.fixIgnisFruitAABB,
            TargetedMod.HARVESTTHENETHER),
    FIX_BAUBLES_INVENTORY_OFFSET_WITH_POTIONS(
            "baubles.MixinGuiEvents", Side.CLIENT, () -> Common.config.fixPotionRenderOffset, TargetedMod.BAUBLES),
    FIX_GALACTICRAFT_INVENTORY_OFFSET_WITH_POTIONS(
            "galacticraftcore.MixinGuiExtendedInventory",
            Side.CLIENT,
            () -> Common.config.fixPotionRenderOffset,
            TargetedMod.GALACTICRAFT_CORE),
    FIX_TRAVELLERSGEAR_INVENTORY_OFFSET_WITH_POTIONS(
            "travellersgear.MixinClientProxy",
            Side.CLIENT,
            () -> Common.config.fixPotionRenderOffset,
            TargetedMod.TRAVELLERSGEAR);

    public final List<String> mixinClass;
    private final Supplier<Boolean> applyIf;
    private final Side side;
    public final List<TargetedMod> targetedMods;

    Mixins(Supplier<Boolean> applyIf, Side side, TargetedMod targetedMod, String... mixinClass) {
        this.mixinClass = Arrays.asList(mixinClass);
        this.side = side;
        this.applyIf = applyIf;
        this.targetedMods = Collections.singletonList(targetedMod);
    }

    Mixins(String mixinClass, Side side, Supplier<Boolean> applyIf, TargetedMod... targetedMods) {
        this.mixinClass = Collections.singletonList(mixinClass);
        this.side = side;
        this.applyIf = applyIf;
        this.targetedMods = Arrays.asList(targetedMods);
    }

    Mixins(String mixinClass, Supplier<Boolean> applyIf, TargetedMod... targetedMods) {
        this.mixinClass = Collections.singletonList(mixinClass);
        this.side = Side.BOTH;
        this.applyIf = applyIf;
        this.targetedMods = Arrays.asList(targetedMods);
    }

    public boolean shouldLoad(List<TargetedMod> loadedMods) {
        return ((side == Side.BOTH
                        || (side == Side.SERVER && FMLLaunchHandler.side().isServer())
                        || (side == Side.CLIENT && FMLLaunchHandler.side().isClient()))
                && applyIf.get()
                && loadedMods.containsAll(targetedMods));
    }

    enum Side {
        BOTH,
        CLIENT,
        SERVER
    }
}
