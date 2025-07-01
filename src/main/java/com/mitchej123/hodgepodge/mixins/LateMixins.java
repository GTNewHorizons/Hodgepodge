package com.mitchej123.hodgepodge.mixins;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhlib.mixin.IMixins;
import com.gtnewhorizon.gtnhlib.mixin.MixinBuilder;
import com.mitchej123.hodgepodge.config.ASMConfig;
import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;

public enum LateMixins implements IMixins {

    // spotless:off
    // Ic2 adjustments
    MEMORY_FIXES_IC2(new MixinBuilder("Removes allocation spam from the Direction.applyTo method")
            .addCommonMixins("ic2.MixinDirection_Memory")
            .setApplyIf(() -> FixesConfig.enableMemoryFixes)
            .addRequiredMod(TargetedMod.IC2)
            .setPhase(Phase.LATE)),
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

    // Disable update checkers
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

    // BOP
    FIX_QUICKSAND_XRAY(new MixinBuilder("Fix Xray through block without collision boundingBox")
            .addCommonMixins("biomesoplenty.MixinBlockMud_FixXray")
            .setApplyIf(() -> FixesConfig.fixPerspectiveCamera)
            .addRequiredMod(TargetedMod.BOP)
            .setPhase(Phase.LATE)),
    DEDUPLICATE_FORESTRY_COMPAT_IN_BOP(new MixinBuilder("BOP Forestry Compat")
            .addCommonMixins("biomesoplenty.MixinForestryIntegration")
            .setApplyIf(() -> FixesConfig.deduplicateForestryCompatInBOP)
            .addRequiredMod(TargetedMod.BOP)
            .setPhase(Phase.LATE)),
    SPEEDUP_BOP_BIOME_FOG(new MixinBuilder("BOP Biome Fog")
            .addClientMixins(
                    "biomesoplenty.MixinFogHandler",
                    "biomesoplenty.AccessorFogHandler")
            .setApplyIf(() -> SpeedupsConfig.speedupBOPFogHandling)
            .addRequiredMod(TargetedMod.BOP)
            .setPhase(Phase.LATE)),
    BIG_FIR_TREES(new MixinBuilder("BOP Fir Trees")
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
    DISABLE_QUICKSAND_GENERATION(new MixinBuilder("Disable BOP quicksand")
            .addCommonMixins("biomesoplenty.MixinDisableQuicksandGeneration")
            .setApplyIf(() -> TweaksConfig.removeBOPQuicksandGeneration)
            .addRequiredMod(TargetedMod.BOP)
            .setPhase(Phase.LATE)),
    // COFH
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
            .addCommonMixins("extrautilities.MixinChunkProviderEndOfTime")
            .setApplyIf(() -> FixesConfig.fixExtraUtilitiesLastMilleniumRain)
            .addRequiredMod(TargetedMod.EXTRA_UTILITIES)
            .setPhase(Phase.LATE)),
    FIX_LAST_MILLENIUM_CREATURES(new MixinBuilder("Remove creatures from the Last Millenium (Extra Utilities)")
            .addCommonMixins("extrautilities.MixinWorldProviderEndOfTime")
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

    LateMixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @Nonnull
    @Override
    public MixinBuilder getBuilder() {
        return builder;
    }
}
