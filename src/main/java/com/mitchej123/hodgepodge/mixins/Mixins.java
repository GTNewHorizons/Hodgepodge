package com.mitchej123.hodgepodge.mixins;

import com.mitchej123.hodgepodge.Hodgepodge;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public enum Mixins {
    // Vanilla Fixes
    FIX_PERSPECTIVE_CAMERA(
            "minecraft.MixinEntityRenderer", () -> Hodgepodge.config.fixPerspectiveCamera, TargetedMod.VANILLA),
    FENCE_CONNECTIONS_FIX(
            "minecraft.MixinBlockFence", () -> Hodgepodge.config.fixFenceConnections, TargetedMod.VANILLA),
    FIX_INVENTORY_OFFSET_WITH_POTIONS(
            "minecraft.MixinInventoryEffectRenderer_PotionOffset",
            Side.CLIENT,
            () -> Hodgepodge.config.fixPotionRenderOffset,
            TargetedMod.VANILLA),
    FIX_POTION_EFFECT_RENDERING(
            "minecraft.MixinInventoryEffectRenderer_PotionEffectRendering",
            Side.CLIENT,
            () -> Hodgepodge.config.fixPotionEffectRender,
            TargetedMod.VANILLA),
    CHUNK_COORDINATES_HASHCODE(
            "minecraft.MixinChunkCoordinates",
            () -> Hodgepodge.config.speedupChunkCoordinatesHashCode,
            TargetedMod.VANILLA),
    TCP_NODELAY("minecraft.MixinTcpNoDelay", () -> Hodgepodge.config.tcpNoDelay, TargetedMod.VANILLA),
    WORLD_UNPROTECTED_GET_BLOCK(
            "minecraft.MixinWorldGetBlock", () -> Hodgepodge.config.fixVanillaUnprotectedGetBlock, TargetedMod.VANILLA),
    WORLD_UNPROTECTED_LIGHT_VALUE(
            "minecraft.MixinWorldLightValue", () -> Hodgepodge.config.fixGetBlockLightValue, TargetedMod.VANILLA),
    FORGE_HOOKS_URL_FIX("minecraft.MixinForgeHooks", () -> Hodgepodge.config.fixUrlDetection, TargetedMod.VANILLA),
    NORTHWEST_BIAS_FIX(
            "minecraft.MixinRandomPositionGenerator", () -> Hodgepodge.config.fixNorthWestBias, TargetedMod.VANILLA),
    SPEEDUP_VANILLA_FURNACE(
            "minecraft.MixinFurnaceRecipes", () -> Hodgepodge.config.speedupVanillaFurnace, TargetedMod.VANILLA),
    GAMEOVER_GUI_LOCKED_DISABLED(
            "minecraft.MixinGuiGameOver", Side.CLIENT, () -> Hodgepodge.config.fixGuiGameOver, TargetedMod.VANILLA),
    PREVENT_PICKUP_LOOT(
            "minecraft.MixinEntityLivingPickup", () -> Hodgepodge.config.preventPickupLoot, TargetedMod.VANILLA),
    DROP_PICKED_LOOT_ON_DESPAWN(
            "minecraft.MixinEntityLivingDrop", () -> Hodgepodge.config.dropPickedLootOnDespawn, TargetedMod.VANILLA),
    FIX_HOPPER_HIT_BOX("minecraft.MixinBlockHopper", () -> Hodgepodge.config.fixHopperHitBox, TargetedMod.VANILLA),
    TILE_RENDERER_PROFILER_DISPATCHER(
            "minecraft.profiler.TileEntityRendererDispatcherMixin",
            Side.CLIENT,
            () -> Hodgepodge.config.enableTileRendererProfiler,
            TargetedMod.VANILLA),
    TILE_RENDERER_PROFILER_MINECRAFT(
            "minecraft.profiler.MinecraftMixin",
            Side.CLIENT,
            () -> Hodgepodge.config.enableTileRendererProfiler,
            TargetedMod.VANILLA),
    DIMENSION_CHANGE_FIX(
            "minecraft.MixinServerConfigurationManager",
            Side.BOTH,
            () -> Hodgepodge.config.fixDimensionChangeHearts,
            TargetedMod.VANILLA),
    CLONE_PLAYER_HEART_FIX(
            "minecraft.MixinEntityPlayerMP",
            Side.BOTH,
            () -> Hodgepodge.config.fixDimensionChangeHearts,
            TargetedMod.VANILLA),
    INCREASE_PARTICLE_LIMIT(
            "minecraft.MixinEffectRenderer",
            Side.CLIENT,
            () -> Hodgepodge.config.increaseParticleLimit,
            TargetedMod.VANILLA),
    FIX_POTION_LIMIT(
            "minecraft.MixinPotionEffect", Side.BOTH, () -> Hodgepodge.config.fixPotionLimit, TargetedMod.VANILLA),
    FIX_HOPPER_VOIDING_ITEMS(
            "minecraft.MixinTileEntityHopper", () -> Hodgepodge.config.fixHopperVoidingItems, TargetedMod.VANILLA),
    FIX_HUGE_CHAT_KICK("minecraft.MixinS02PacketChat", () -> Hodgepodge.config.fixHugeChatKick, TargetedMod.VANILLA),
    FIX_WORLD_SERVER_LEAKING_UNLOADED_ENTITIES(
            "minecraft.MixinWorldServerUpdateEntities",
            () -> Hodgepodge.config.fixWorldServerLeakingUnloadedEntities,
            TargetedMod.VANILLA),
    FIX_ARROW_WRONG_LIGHTING(
            "minecraft.MixinRendererLivingEntity",
            Side.CLIENT,
            () -> Hodgepodge.config.fixGlStateBugs,
            TargetedMod.VANILLA),
    FIX_RESIZABLE_FULLSCREEN(
            "minecraft.MixinMinecraft_ResizableFullscreen",
            Side.CLIENT,
            () -> Hodgepodge.config.fixResizableFullscreen,
            TargetedMod.VANILLA),
    FIX_UNFOCUSED_FULLSCREEN(
            "minecraft.MixinMinecraft_UnfocusedFullscreen",
            Side.CLIENT,
            () -> Hodgepodge.config.fixUnfocusedFullscreen,
            TargetedMod.VANILLA),
    ADD_TOGGLE_DEBUG_MESSAGE(
            "minecraft.MixinMinecraft_ToggleDebugMessage",
            Side.CLIENT,
            () -> Hodgepodge.config.addToggleDebugMessage,
            TargetedMod.VANILLA),
    SPEEDUP_VANILLA_ANIMATIONS(
            () -> Hodgepodge.config.speedupAnimations,
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
            () -> Hodgepodge.config.speedupAnimations,
            TargetedMod.FASTCRAFT),
    FIX_POTION_ITERATING(
            "minecraft.MixinEntityLivingPotions",
            Side.BOTH,
            () -> Hodgepodge.config.fixPotionIterating,
            TargetedMod.VANILLA),
    OPTIMIZE_ASMDATATABLE_INDEX(
            "forge.MixinASMDataTable", Side.BOTH, () -> Hodgepodge.config.optimizeASMDataTable, TargetedMod.VANILLA),
    RENDER_DEBUG("minecraft.MixinRenderGlobal", Side.CLIENT, () -> Hodgepodge.config.renderDebug, TargetedMod.VANILLA),
    STATIC_LAN_PORT("minecraft.server.MixinHttpUtil", Side.CLIENT, () -> Hodgepodge.config.enableDefaultLanPort, TargetedMod.VANILLA),

    // Potentially obsolete vanilla fixes
    GRASS_GET_BLOCK_FIX(
            "minecraft.MixinBlockGrass", () -> Hodgepodge.config.fixVanillaUnprotectedGetBlock, TargetedMod.VANILLA),
    FIRE_GET_BLOCK_FIX(
            "minecraft.MixinBlockFireGetBlock",
            () -> Hodgepodge.config.fixVanillaUnprotectedGetBlock,
            TargetedMod.VANILLA),
    VILLAGE_GET_BLOCK_FIX(
            "minecraft.MixinVillage", () -> Hodgepodge.config.fixVanillaUnprotectedGetBlock, TargetedMod.VANILLA),
    VILLAGE_COLLECTION_GET_BLOCK_FIX(
            "minecraft.MixinVillageCollection",
            () -> Hodgepodge.config.fixVanillaUnprotectedGetBlock,
            TargetedMod.VANILLA),
    FLUID_CLASSIC_GET_BLOCK_FIX(
            "minecraft.MixinBlockFluidClassic",
            () -> Hodgepodge.config.fixVanillaUnprotectedGetBlock,
            TargetedMod.VANILLA),

    // Ic2 adjustments
    IC2_UNPROTECTED_GET_BLOCK_FIX(
            "ic2.MixinIc2WaterKinetic", () -> Hodgepodge.config.fixIc2UnprotectedGetBlock, TargetedMod.IC2),
    IC2_DIRECT_INV_ACCESS_SEED(
            "ic2.MixinItemCropSeed", () -> Hodgepodge.config.fixIc2DirectInventoryAccess, TargetedMod.IC2),
    IC2_DIRECT_INV_ACCESS_CROP(
            "ic2.MixinTileEntityCrop", () -> Hodgepodge.config.fixIc2DirectInventoryAccess, TargetedMod.IC2),
    IC2_NIGHT_VISION_NANO(
            "ic2.MixinIc2NanoSuitNightVision", () -> Hodgepodge.config.fixIc2Nightvision, TargetedMod.IC2),
    IC2_NIGHT_VISION_QUANTUM(
            "ic2.MixinIc2QuantumSuitNightVision", () -> Hodgepodge.config.fixIc2Nightvision, TargetedMod.IC2),
    IC2_NIGHT_VISION_GOGGLES(
            "ic2.MixinIc2NightVisionGoggles", () -> Hodgepodge.config.fixIc2Nightvision, TargetedMod.IC2),
    IC2_REACTOR_DUPE(
            "ic2.MixinTileEntityReactorChamberElectricNoDupe",
            () -> Hodgepodge.config.fixIc2ReactorDupe,
            TargetedMod.IC2),
    IC2_REACTOR_INVENTORY_SPEEDUP(
            "ic2.MixinTileEntityReactorChamberElectricInvSpeedup",
            () -> Hodgepodge.config.optimizeIc2ReactorInventoryAccess,
            TargetedMod.IC2),
    HIDE_IC2_REACTOR_COOLANT_SLOTS(
            "ic2.MixinTileEntityNuclearReactorElectric", () -> Hodgepodge.config.hideIc2ReactorSlots, TargetedMod.IC2),
    IC2_HAZMAT("ic2.MixinIc2Hazmat", () -> Hodgepodge.config.fixIc2Hazmat, TargetedMod.IC2, TargetedMod.GT5U),
    IC2_FLUID_CONTAINER_TOOLTIP(
            "ic2.MixinItemIC2FluidContainer", () -> Hodgepodge.config.displayIc2FluidLocalizedName, TargetedMod.IC2),
    IC2_FLUID_RENDER_FIX(
            "ic2.textures.MixinRenderLiquidCell", () -> Hodgepodge.config.speedupAnimations, TargetedMod.IC2),

    // gregtech
    GREGTECH_FLUID_RENDER_FIX(
            "gregtech.textures.MixinGT_GeneratedMaterial_Renderer",
            () -> Hodgepodge.config.speedupAnimations,
            TargetedMod.GT5U),

    // COFH
    COFH_CORE_UPDATE_CHECK(
            "cofhcore.MixinCoFHCoreUpdateCheck", () -> Hodgepodge.config.removeUpdateChecks, TargetedMod.COFH_CORE),

    // Railcraft Anchors
    WAKE_ANCHORS_ON_LOGIN_PASSIVE(
            "railcraft.MixinTileAnchorPassive", () -> Hodgepodge.config.installAnchorAlarm, TargetedMod.RAILCRAFT),
    WAKE_ANCHORS_ON_LOGIN_PERSONAL(
            "railcraft.MixinTileAnchorPersonal", () -> Hodgepodge.config.installAnchorAlarm, TargetedMod.RAILCRAFT),

    // Hunger overhaul
    HUNGER_OVERHAUL_LOW_STAT_EFFECT(
            "hungeroverhaul.MixinHungerOverhaulLowStatEffect",
            () -> Hodgepodge.config.fixHungerOverhaul,
            TargetedMod.HUNGER_OVERHAUL),
    HUNGER_OVERHAUL_REGEN(
            "hungeroverhaul.MixinHungerOverhaulHealthRegen",
            () -> Hodgepodge.config.fixHungerOverhaul,
            TargetedMod.HUNGER_OVERHAUL),

    // Thaumcraft
    THAUMCRAFT_ARCANE_LAMP_GET_BLOCK_FIX(
            "thaumcraft.MixinArcaneLamp",
            () -> Hodgepodge.config.fixThaumcraftUnprotectedGetBlock,
            TargetedMod.THAUMCRAFT),
    THAUMCRAFT_WISP_GET_BLOCK_FIX(
            "thaumcraft.MixinEntityWisp",
            () -> Hodgepodge.config.fixThaumcraftUnprotectedGetBlock,
            TargetedMod.THAUMCRAFT),
    ADD_CV_SUPPORT_TO_WAND_PEDESTAL(
            "thaumcraft.MixinTileWandPedestal",
            () -> Hodgepodge.config.addCVSupportToWandPedestal,
            TargetedMod.THAUMCRAFT),

    // BOP
    DEDUPLICATE_FORESTRY_COMPAT_IN_BOP(
            "biomesoplenty.MixinForestryIntegration",
            () -> Hodgepodge.config.deduplicateForestryCompatInBOP,
            TargetedMod.BOP),
    SPEEDUP_BOP_BIOME_FOG(
            "biomesoplenty.MixinFogHandler",
            Side.CLIENT,
            () -> Hodgepodge.config.speedupBOPFogHandling,
            TargetedMod.BOP),
    BIG_FIR_TREES("biomesoplenty.MixinBlockBOPSapling", () -> Hodgepodge.config.makeBigFirsPlantable, TargetedMod.BOP),

    // MrTJPCore (Project Red)
    FIX_HUD_LIGHTING_GLITCH(
            "mrtjpcore.MixinFXEngine", () -> Hodgepodge.config.fixHudLightingGlitch, TargetedMod.MRTJPCORE),
    FIX_POPPING_OFF(
            "mrtjpcore.MixinPlacementLib", () -> Hodgepodge.config.fixComponentsPoppingOff, TargetedMod.MRTJPCORE),

    // Automagy
    IMPLEMENTS_CONTAINER_FOR_THIRSTY_TANK(
            "automagy.MixinItemBlockThirstyTank", () -> Hodgepodge.config.thirstyTankContainer, TargetedMod.AUTOMAGY),

    // ProjectE
    FIX_FURNACE_ITERATION(
            "projecte.MixinObjHandler", () -> Hodgepodge.config.speedupVanillaFurnace, TargetedMod.PROJECTE),

    // Pam's Harvest the Nether
    FIX_IGNIS_FRUIT_AABB(
            "harvestthenether.MixinBlockPamFruit",
            () -> Hodgepodge.config.fixIgnisFruitAABB,
            TargetedMod.HARVESTTHENETHER),
    FIX_NETHER_LEAVES_FACE_RENDERING(
            "harvestthenether.MixinBlockNetherLeaves",
            Side.CLIENT,
            () -> Hodgepodge.config.fixIgnisFruitAABB,
            TargetedMod.HARVESTTHENETHER),
    FIX_BAUBLES_INVENTORY_OFFSET_WITH_POTIONS(
            "baubles.MixinGuiEvents", Side.CLIENT, () -> Hodgepodge.config.fixPotionRenderOffset, TargetedMod.BAUBLES),
    FIX_GALACTICRAFT_INVENTORY_OFFSET_WITH_POTIONS(
            "galacticraftcore.MixinGuiExtendedInventory",
            Side.CLIENT,
            () -> Hodgepodge.config.fixPotionRenderOffset,
            TargetedMod.GALACTICRAFT_CORE),
    FIX_TRAVELLERSGEAR_INVENTORY_OFFSET_WITH_POTIONS(
            "travellersgear.MixinClientProxy",
            Side.CLIENT,
            () -> Hodgepodge.config.fixPotionRenderOffset,
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
