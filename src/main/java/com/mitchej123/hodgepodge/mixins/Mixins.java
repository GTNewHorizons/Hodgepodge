package com.mitchej123.hodgepodge.mixins;

import com.mitchej123.hodgepodge.Hodgepodge;
import cpw.mods.fml.relauncher.FMLLaunchHandler;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public enum Mixins {
    // Vanilla Fixes
    FENCE_CONNECTIONS_FIX("minecraft.MixinBlockFence", () -> Hodgepodge.config.fixFenceConnections, TargetedMod.VANILLA),
    CHUNK_COORDINATES_HASHCODE("minecraft.MixinChunkCoordinates", () -> Hodgepodge.config.speedupChunkCoordinatesHashCode, TargetedMod.VANILLA),
    WORLD_UNPROTECTED_GET_BLOCK("minecraft.MixinWorldGetBlock", () -> Hodgepodge.config.fixVanillaUnprotectedGetBlock, TargetedMod.VANILLA),
    WORLD_UNPROTECTED_LIGHT_VALUE("minecraft.MixinWorldLightValue", () -> Hodgepodge.config.fixGetBlockLightValue, TargetedMod.VANILLA),
    FORGE_HOOKS_URL_FIX("minecraft.MixinForgeHooks", () -> Hodgepodge.config.fixUrlDetection, TargetedMod.VANILLA),
    NORTHWEST_BIAS_FIX("minecraft.MixinRandomPositionGenerator", () -> Hodgepodge.config.fixNorthWestBias, TargetedMod.VANILLA),
    SPEEDUP_VANILLA_FURNACE("minecraft.MixinFurnaceRecipes", () -> Hodgepodge.config.speedupVanillaFurnace, TargetedMod.VANILLA),
    GAMEOVER_GUI_LOCKED_DISABLED("minecraft.MixinGuiGameOver", Side.CLIENT, () -> Hodgepodge.config.fixGuiGameOver, TargetedMod.VANILLA),
    PREVENT_PICKUP_LOOT("minecraft.MixinEntityLivingPickup", () -> Hodgepodge.config.preventPickupLoot, TargetedMod.VANILLA),
    DROP_PICKED_LOOT_ON_DESPAWN("minecraft.MixinEntityLivingDrop", () -> Hodgepodge.config.dropPickedLootOnDespawn, TargetedMod.VANILLA),
    FIX_HOPPER_HIT_BOX("minecraft.MixinBlockHopper", () -> Hodgepodge.config.fixHopperHitBox, TargetedMod.VANILLA),
    TILE_RENDERER_PROFILER_DISPATCHER("minecraft.profiler.TileEntityRendererDispatcherMixin", Side.CLIENT, () -> Hodgepodge.config.enableTileRendererProfiler, TargetedMod.VANILLA),
    TILE_RENDERER_PROFILER_MINECRAFT("minecraft.profiler.MinecraftMixin", Side.CLIENT, () -> Hodgepodge.config.enableTileRendererProfiler, TargetedMod.VANILLA),
    DIMENSION_CHANGE_FIX("minecraft.MixinServerConfigurationManager", Side.BOTH, () -> Hodgepodge.config.fixDimensionChangeHearts, TargetedMod.VANILLA),
    CLONE_PLAYER_HEART_FIX("minecraft.MixinEntityPlayerMP", Side.BOTH, () -> Hodgepodge.config.fixDimensionChangeHearts, TargetedMod.VANILLA),
    INCREASE_PARTICLE_LIMIT("minecraft.MixinEffectRenderer", Side.CLIENT, () -> Hodgepodge.config.increaseParticleLimit, TargetedMod.VANILLA),
    FIX_POTION_LIMIT("minecraft.MixinPotionEffect", Side.BOTH, () -> Hodgepodge.config.fixPotionLimit, TargetedMod.VANILLA),
    FIX_HOPPER_VOIDING_ITEMS("minecraft.MixinTileEntityHopper", () -> Hodgepodge.config.fixHopperVoidingItems, TargetedMod.VANILLA),

    // Potentially obsolete vanilla fixes
    GRASS_GET_BLOCK_FIX("minecraft.MixinBlockGrass", () -> Hodgepodge.config.fixVanillaUnprotectedGetBlock, TargetedMod.VANILLA),
    FIRE_GET_BLOCK_FIX("minecraft.MixinBlockFireGetBlock", () -> Hodgepodge.config.fixVanillaUnprotectedGetBlock, TargetedMod.VANILLA),
    VILLAGE_GET_BLOCK_FIX("minecraft.MixinVillage", () -> Hodgepodge.config.fixVanillaUnprotectedGetBlock, TargetedMod.VANILLA),
    VILLAGE_COLLECTION_GET_BLOCK_FIX("minecraft.MixinVillageCollection", () -> Hodgepodge.config.fixVanillaUnprotectedGetBlock, TargetedMod.VANILLA),
    FLUID_CLASSIC_GET_BLOCK_FIX("minecraft.MixinBlockFluidClassic", () -> Hodgepodge.config.fixVanillaUnprotectedGetBlock, TargetedMod.VANILLA),
    
    // Ic2 adjustments
    IC2_UNPROTECTED_GET_BLOCK_FIX("ic2.MixinIc2WaterKinetic", () -> Hodgepodge.config.fixIc2UnprotectedGetBlock, TargetedMod.IC2),
    IC2_DIRECT_INV_ACCESS_SEED("ic2.MixinItemCropSeed", () -> Hodgepodge.config.fixIc2DirectInventoryAccess, TargetedMod.IC2),
    IC2_DIRECT_INV_ACCESS_CROP("ic2.MixinTileEntityCrop", () -> Hodgepodge.config.fixIc2DirectInventoryAccess, TargetedMod.IC2),
    IC2_NIGHT_VISION_NANO("ic2.MixinIc2NanoSuitNightVision", () -> Hodgepodge.config.fixIc2Nightvision, TargetedMod.IC2),
    IC2_NIGHT_VISION_QUANTUM("ic2.MixinIc2QuantumSuitNightVision", () -> Hodgepodge.config.fixIc2Nightvision, TargetedMod.IC2),
    IC2_NIGHT_VISION_GOGGLES("ic2.MixinIc2NightVisionGoggles", () -> Hodgepodge.config.fixIc2Nightvision, TargetedMod.IC2),
    IC2_REACTOR_DUPE("ic2.MixinTileEntityReactorChamberElectric",  () -> Hodgepodge.config.fixIc2ReactorDupe, TargetedMod.IC2),
    HIDE_IC2_REACTOR_COOLANT_SLOTS("ic2.MixinTileEntityNuclearReactorElectric", () -> Hodgepodge.config.hideIc2ReactorSlots, TargetedMod.IC2),
    IC2_HAZMAT("ic2.MixinIc2Hazmat", () -> Hodgepodge.config.fixIc2Hazmat, TargetedMod.IC2, TargetedMod.GT5U),
    IC2_FLUID_CONTAINER_TOOLTIP("ic2.MixinItemIC2FluidContainer", () -> Hodgepodge.config.displayIc2FluidLocalizedName, TargetedMod.IC2),

    // COFH
    COFH_CORE_UPDATE_CHECK("cofhcore.MixinCoFHCoreUpdateCheck", () -> Hodgepodge.config.removeUpdateChecks, TargetedMod.COFH_CORE),

    // Railcraft Anchors
    WAKE_ANCHORS_ON_LOGIN_PASSIVE("railcraft.MixinTileAnchorPassive", () -> Hodgepodge.config.installAnchorAlarm, TargetedMod.RAILCRAFT),
    WAKE_ANCHORS_ON_LOGIN_PERSONAL("railcraft.MixinTileAnchorPersonal", () -> Hodgepodge.config.installAnchorAlarm, TargetedMod.RAILCRAFT),
    
    // Hunger overhaul
    HUNGER_OVERHAUL_LOW_STAT_EFFECT("hungeroverhaul.MixinHungerOverhaulLowStatEffect", () -> Hodgepodge.config.fixHungerOverhaul, TargetedMod.HUNGER_OVERHAUL),
    HUNGER_OVERHAUL_REGEN("hungeroverhaul.MixinHungerOverhaulHealthRegen", () -> Hodgepodge.config.fixHungerOverhaul, TargetedMod.HUNGER_OVERHAUL),
    
    // Thaumcraft
    THAUMCRAFT_ARCANE_LAMP_GET_BLOCK_FIX("thaumcraft.MixinArcaneLamp", () -> Hodgepodge.config.fixThaumcraftUnprotectedGetBlock, TargetedMod.THAUMCRAFT),
    THAUMCRAFT_WISP_GET_BLOCK_FIX("thaumcraft.MixinEntityWisp", () -> Hodgepodge.config.fixThaumcraftUnprotectedGetBlock, TargetedMod.THAUMCRAFT),
    ADD_CV_SUPPORT_TO_WAND_PEDESTAL("thaumcraft.MixinTileWandPedestal", () -> Hodgepodge.config.addCVSupportToWandPedestal, TargetedMod.THAUMCRAFT),
    ;
    
    public final String mixinClass;
    private final Supplier<Boolean> applyIf;
    private final Side side;
    public final List<TargetedMod> targetedMods;
    

    Mixins(String mixinClass, Side side, Supplier<Boolean> applyIf, TargetedMod... targetedMods) {
        this.mixinClass = mixinClass;
        this.side = side;
        this.applyIf = applyIf;
        this.targetedMods = Arrays.asList(targetedMods);
    }

    Mixins(String mixinClass, Supplier<Boolean> applyIf, TargetedMod... targetedMods) {
        this.mixinClass = mixinClass;
        this.side = Side.BOTH;
        this.applyIf = applyIf;
        this.targetedMods = Arrays.asList(targetedMods);
    }

    public boolean shouldLoad(List<TargetedMod> loadedMods) {
        return (
            (side == Side.BOTH
                    || (side == Side.SERVER && FMLLaunchHandler.side().isServer())
                    || (side == Side.CLIENT && FMLLaunchHandler.side().isClient())
            )
                && applyIf.get()
                && loadedMods.containsAll(targetedMods)
        );
    }
    
    enum Side {
        BOTH,
        CLIENT,
        SERVER;
    }
}

