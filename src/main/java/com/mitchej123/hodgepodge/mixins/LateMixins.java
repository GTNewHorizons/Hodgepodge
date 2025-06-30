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
    MEMORY_FIXES_IC2(new MixinBuilder("Removes allocation spam from the Direction.applyTo method").addCommonMixins("ic2.MixinDirection_Memory").setApplyIf(() -> FixesConfig.enableMemoryFixes).addRequiredMod(TargetedMod.IC2)),
    IC2_HOVER_MODE_FIX(new MixinBuilder("IC2 Hover Mode Fix").addCommonMixins("ic2.MixinIc2QuantumSuitHoverMode").setApplyIf(() -> FixesConfig.fixIc2HoverMode).addRequiredMod(TargetedMod.IC2)),
    IC2_ARMOR_LAG_FIX(new MixinBuilder("IC2 Armor Lag Fix").addCommonMixins("ic2.MixinElectricItemManager", "ic2.MixinIC2ArmorHazmat", "ic2.MixinIC2ArmorJetpack", "ic2.MixinIC2ArmorNanoSuit", "ic2.MixinIC2ArmorNightvisionGoggles", "ic2.MixinIC2ArmorQuantumSuit", "ic2.MixinIC2ArmorSolarHelmet", "ic2.MixinIC2ArmorStaticBoots").setApplyIf(() -> FixesConfig.fixIc2ArmorLag).addRequiredMod(TargetedMod.IC2)),
    IC2_CROP_TRAMPLING_FIX(new MixinBuilder("IC2 Crop Trampling Fix").addCommonMixins("ic2.MixinIC2TileEntityCrop").setApplyIf(() -> FixesConfig.fixIc2CropTrampling).addRequiredMod(TargetedMod.IC2)),
    IC2_SYNC_REACTORS(new MixinBuilder("Synchronize IC2 reactors for more consistent operation").addCommonMixins("ic2.sync.MixinTEReactorChamber", "ic2.sync.MixinTEReactor").setApplyIf(() -> TweaksConfig.synchronizeIC2Reactors).addRequiredMod(TargetedMod.IC2)),
    IC2_CELL(new MixinBuilder("No IC2 Cell Consumption in tanks").addCommonMixins("ic2.MixinIC2ItemCell").setApplyIf(() -> TweaksConfig.ic2CellWithContainer).addRequiredMod(TargetedMod.IC2)),

    // Disable update checkers
    BIBLIOCRAFT_UPDATE_CHECK(new MixinBuilder("Yeet Bibliocraft Update Check").addClientMixins("bibliocraft.MixinVersionCheck").setApplyIf(() -> FixesConfig.removeUpdateChecks).addRequiredMod(TargetedMod.BIBLIOCRAFT)),
    DAMAGE_INDICATORS_UPDATE_CHECK(new MixinBuilder("Yeet Damage Indicators Update Check").addClientMixins("damageindicators.MixinDIClientProxy").setApplyIf(() -> FixesConfig.removeUpdateChecks).addRequiredMod(TargetedMod.DAMAGE_INDICATORS)),

    // Railcraft Anchors
    WAKE_ANCHORS_ON_LOGIN_PASSIVE(new MixinBuilder("Wake passive anchors on login").addCommonMixins("railcraft.MixinTileAnchorPassive").setApplyIf(() -> TweaksConfig.installAnchorAlarm).addRequiredMod(TargetedMod.RAILCRAFT)),
    WAKE_ANCHORS_ON_LOGIN_PERSONAL(new MixinBuilder("Wake person anchors on login").addCommonMixins("railcraft.MixinTileAnchorPersonal").setApplyIf(() -> TweaksConfig.installAnchorAlarm).addRequiredMod(TargetedMod.RAILCRAFT)),

    // Hunger overhaul
    HUNGER_OVERHAUL_LOW_STAT_EFFECT(new MixinBuilder("Patch unintended low stat effects").addCommonMixins("hungeroverhaul.MixinHungerOverhaulLowStatEffect").setApplyIf(() -> FixesConfig.fixHungerOverhaul).addRequiredMod(TargetedMod.HUNGER_OVERHAUL)),
    HUNGER_OVERHAUL_REGEN(new MixinBuilder("Patch Regen").addCommonMixins("hungeroverhaul.MixinHungerOverhaulHealthRegen").setApplyIf(() -> FixesConfig.fixHungerOverhaul).addRequiredMod(TargetedMod.HUNGER_OVERHAUL)),
    HUNGER_OVERHAUL_0_HUNGER(new MixinBuilder("Fix some items restore 0 hunger").addCommonMixins("hungeroverhaul.MixinHungerOverhaulModuleHarvestCraft").setApplyIf(() -> FixesConfig.fixHungerOverhaulRestore0Hunger).addRequiredMod(TargetedMod.HUNGER_OVERHAUL).addRequiredMod(TargetedMod.HARVESTCRAFT)),

    // Thaumcraft
    THREADED_THAUMCRAFT_MAZE_SAVING(new MixinBuilder("Threaded Thaumcraft Maze Saving").addCommonMixins("thaumcraft.MixinMazeHandler_threadedIO").setApplyIf(() -> TweaksConfig.threadedWorldDataSaving).addRequiredMod(TargetedMod.THAUMCRAFT)),
    ADD_CV_SUPPORT_TO_WAND_PEDESTAL(new MixinBuilder("CV Support for Wand Pedestal").addCommonMixins("thaumcraft.MixinTileWandPedestal").setApplyIf(() -> TweaksConfig.addCVSupportToWandPedestal).addRequiredMod(TargetedMod.THAUMCRAFT)),
    FIX_ASPECT_SORTING(new MixinBuilder("Fix Thaumcraft Aspects not being sorted by name").addClientMixins("thaumcraft.MixinGuiResearchRecipe", "thaumcraft.MixinGuiResearchTable", "thaumcraft.MixinGuiThaumatorium", "thaumcraft.MixinItem_SortAspectsByName").setApplyIf(() -> FixesConfig.fixThaumcraftAspectSorting).addRequiredMod(TargetedMod.THAUMCRAFT)),
    FIX_GOLEM_MARKER_LOADING(new MixinBuilder("Fix golem marker loading failure when dimensionId larger than MAX_BYTE").addCommonMixins("thaumcraft.MixinEntityGolemBase", "thaumcraft.MixinItemGolemBell").setApplyIf(() -> FixesConfig.fixThaumcraftGolemMarkerLoading).addRequiredMod(TargetedMod.THAUMCRAFT)),
    FIX_WORLD_COORDINATE_HASHING_METHOD(new MixinBuilder("Implement a proper hashing method for WorldCoordinates").addCommonMixins("thaumcraft.MixinWorldCoordinates").setApplyIf(() -> FixesConfig.fixThaumcraftWorldCoordinatesHashingMethod).addRequiredMod(TargetedMod.THAUMCRAFT)),
    FIX_MAGICAL_LEAVES_LAG(new MixinBuilder("Fix Thaumcraft leaves frequent ticking").addCommonMixins("thaumcraft.MixinBlockMagicalLeaves", "thaumcraft.MixinBlockMagicalLog").setApplyIf(() -> FixesConfig.fixThaumcraftLeavesLag).addRequiredMod(TargetedMod.THAUMCRAFT)),
    FIX_THAUMCRAFT_VIS_DUPLICATION(new MixinBuilder("Fix Thaumcraft Vis Duplication").addCommonMixins("thaumcraft.MixinTileWandPedestal_VisDuplication").setApplyIf(() -> FixesConfig.fixWandPedestalVisDuplication).addRequiredMod(TargetedMod.THAUMCRAFT)),
    FIX_NULL_HANDLING_ITEMWISPESSENCE_CLIENT(new MixinBuilder("Fix handling of null stacks in ItemWispEssence").addClientMixins("thaumcraft.MixinItemWispEssence_Client").setApplyIf(() -> FixesConfig.fixNullHandlingItemWispEssence).addRequiredMod(TargetedMod.THAUMCRAFT)),
    FIX_NULL_HANDLING_ITEMWISPESSENCE_BOTH(new MixinBuilder("Fix handling of null stacks in ItemWispEssence").addCommonMixins("thaumcraft.MixinItemWispEssence_Both").setApplyIf(() -> FixesConfig.fixNullHandlingItemWispEssence).addRequiredMod(TargetedMod.THAUMCRAFT)),

    // BOP
    FIX_QUICKSAND_XRAY(new MixinBuilder("Fix Xray through block without collision boundingBox").addCommonMixins("biomesoplenty.MixinBlockMud_FixXray").setApplyIf(() -> FixesConfig.fixPerspectiveCamera).addRequiredMod(TargetedMod.BOP)),
    DEDUPLICATE_FORESTRY_COMPAT_IN_BOP(new MixinBuilder("BOP Forestry Compat").addCommonMixins("biomesoplenty.MixinForestryIntegration").setApplyIf(() -> FixesConfig.deduplicateForestryCompatInBOP).addRequiredMod(TargetedMod.BOP)),
    SPEEDUP_BOP_BIOME_FOG(new MixinBuilder("BOP Biome Fog").addClientMixins("biomesoplenty.MixinFogHandler").setApplyIf(() -> SpeedupsConfig.speedupBOPFogHandling).addRequiredMod(TargetedMod.BOP)),
    SPEEDUP_BOP_BIOME_FOG_ACCESSOR(new MixinBuilder("BOP Biome Fog Accessor").addClientMixins("biomesoplenty.AccessorFogHandler").setApplyIf(() -> SpeedupsConfig.speedupBOPFogHandling).addRequiredMod(TargetedMod.BOP)),
    BIG_FIR_TREES(new MixinBuilder("BOP Fir Trees").addCommonMixins("biomesoplenty.MixinBlockBOPSapling").setApplyIf(() -> TweaksConfig.makeBigFirsPlantable).addRequiredMod(TargetedMod.BOP)),
    JAVA12_BOP(new MixinBuilder("BOP Java12-safe reflection").addCommonMixins("biomesoplenty.MixinBOPBiomes").addCommonMixins("biomesoplenty.MixinBOPReflectionHelper").setApplyIf(() -> FixesConfig.java12BopCompat).addRequiredMod(TargetedMod.BOP)),
    DISABLE_QUICKSAND_GENERATION(new MixinBuilder("Disable BOP quicksand").addCommonMixins("biomesoplenty.MixinDisableQuicksandGeneration").setApplyIf(() -> TweaksConfig.removeBOPQuicksandGeneration).addRequiredMod(TargetedMod.BOP)),
    // COFH
    MFR_FIX_COFH_VALIDATE(new MixinBuilder("Remove CoFH TE cache usage from MFR").addCommonMixins("minefactoryreloaded.MixinTileEntityBase", "minefactoryreloaded.MixinTileEntityRedNetCable").addRequiredMod(TargetedMod.MINEFACTORY_RELOADED).setApplyIf(() -> ASMConfig.cofhWorldTransformer)),
    TE_FIX_COFH_VALIDATE(new MixinBuilder("Remove CoFH TE cache usage from TE").addCommonMixins("thermalexpansion.MixinTileInventoryTileLightFalse").addRequiredMod(TargetedMod.THERMALEXPANSION).setApplyIf(() -> ASMConfig.cofhWorldTransformer)),

    // Minefactory Reloaded
    DISARM_SACRED_TREE(new MixinBuilder("Prevents Sacred Rubber Tree Generation").addCommonMixins("minefactoryreloaded.MixinBlockRubberSapling").addRequiredMod(TargetedMod.MINEFACTORY_RELOADED).setApplyIf(() -> FixesConfig.disableMassiveSacredTreeGeneration)),
    MFR_IMPROVE_BLOCKSMASHER(new MixinBuilder("Improve MFR block smasher").addCommonMixins("minefactoryreloaded.MixinTileEntityBlockSmasher").addRequiredMod(TargetedMod.MINEFACTORY_RELOADED).setApplyIf(() -> TweaksConfig.improveMfrBlockSmasher)),
    MFR_IMPROVE_BLOCKBREAKER(new MixinBuilder("Improve MFR block breaker").addCommonMixins("minefactoryreloaded.MixinTileEntityBlockBreaker").addRequiredMod(TargetedMod.MINEFACTORY_RELOADED).setApplyIf(() -> TweaksConfig.improveMfrBlockBreaker)),

    // Immersive engineering
    JAVA12_IMMERSIVE_ENGINERRING(new MixinBuilder("Immersive Engineering Java-12 safe potion array resizing").addCommonMixins("immersiveengineering.MixinIEPotions").setApplyIf(() -> FixesConfig.java12ImmersiveEngineeringCompat).addRequiredMod(TargetedMod.IMMERSIVE_ENGINENEERING)),
    JAVA12_MINE_CHEM(new MixinBuilder("Minechem Java-12 safe potion array resizing").addCommonMixins("minechem.MixinPotionInjector").setApplyIf(() -> FixesConfig.java12MineChemCompat).addRequiredMod(TargetedMod.MINECHEM)),

    // Modular Powersuits
    MPS_PREVENT_RF_ENERGY_SYPHON(new MixinBuilder("Prevent MPS from charging and draining RF from Inventory").addCommonMixins("mps.MixinElectricAdapterRF").setApplyIf(() -> TweaksConfig.preventMPSEnergyTransferRF).addRequiredMod(TargetedMod.MODULARPOWERSUITS)),
    MPS_PREVENT_EU_ENERGY_SYPHON(new MixinBuilder("Prevent MPS from charging and draining EU from Inventory").addCommonMixins("mps.MixinElectricAdapterEU").setApplyIf(() -> TweaksConfig.preventMPSEnergyTransferEU).addRequiredMod(TargetedMod.MODULARPOWERSUITS)),
    MPS_PREVENT_ME_ENERGY_SYPHON(new MixinBuilder("Prevent MPS from charging and draining ME from Inventory").addCommonMixins("mps.MixinElectricAdapterME").setApplyIf(() -> TweaksConfig.preventMPSEnergyTransferME).addRequiredMod(TargetedMod.MODULARPOWERSUITS)),

    // MrTJPCore (Project Red)
    FIX_HUD_LIGHTING_GLITCH(new MixinBuilder("HUD Lighting glitch").addCommonMixins("mrtjpcore.MixinFXEngine").setApplyIf(() -> TweaksConfig.fixHudLightingGlitch).addRequiredMod(TargetedMod.MRTJPCORE)),
    FIX_POPPING_OFF(new MixinBuilder("Fix Popping Off").addCommonMixins("mrtjpcore.MixinPlacementLib").setApplyIf(() -> TweaksConfig.fixComponentsPoppingOff).addRequiredMod(TargetedMod.MRTJPCORE)),

    // Automagy
    IMPLEMENTS_CONTAINER_FOR_THIRSTY_TANK(new MixinBuilder("Thirsty Tank Container").addCommonMixins("automagy.MixinItemBlockThirstyTank").setApplyIf(() -> TweaksConfig.thirstyTankContainer).addRequiredMod(TargetedMod.AUTOMAGY)),

    // Better HUD
    FIX_BETTERHUD_ARMOR_BAR(new MixinBuilder("Fix better HUD armor display breaking with skulls").addCommonMixins("betterhud.MixinSkullDurabilityDisplay").setApplyIf(() -> FixesConfig.fixBetterHUDArmorDisplay).addRequiredMod(TargetedMod.BETTERHUD)),
    FIX_BETTERHUD_HEARTS_FREEZE(new MixinBuilder("Fix better HUD freezing the game when trying to render high amounts of hp").addCommonMixins("betterhud.MixinHealthRender").setApplyIf(() -> FixesConfig.fixBetterHUDHPDisplay).addRequiredMod(TargetedMod.BETTERHUD)),

    // ProjectE
    FIX_FURNACE_ITERATION(new MixinBuilder("Speedup Furnaces").addCommonMixins("projecte.MixinObjHandler").setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace).addRequiredMod(TargetedMod.PROJECTE)),

    // LOTR
    FIX_LOTR_FURNACE_ERROR(new MixinBuilder("Patches lotr to work with the vanilla furnace speedup").addCommonMixins("lotr.MixinLOTRRecipes").setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace).addRequiredMod(TargetedMod.GTNHLIB).addRequiredMod(TargetedMod.LOTR)),
    FIX_LOTR_JAVA12(new MixinBuilder("Fix lotr java 12+ compat").addCommonMixins("lotr.MixinLOTRLogReflection", "lotr.MixinRedirectHuornAI", "lotr.MixinRemoveUnlockFinalField").setApplyIf(() -> FixesConfig.java12LotrCompat).addRequiredMod(TargetedMod.LOTR)),

    // Journeymap
    FIX_JOURNEYMAP_KEYBINDS(new MixinBuilder("Fix Journeymap Keybinds").addClientMixins("journeymap.MixinConstants").setApplyIf(() -> FixesConfig.fixJourneymapKeybinds).addRequiredMod(TargetedMod.JOURNEYMAP)),
    FIX_JOURNEYMAP_ILLEGAL_FILE_PATH_CHARACTER(new MixinBuilder("Fix Journeymap Illegal File Path Character").addClientMixins("journeymap.MixinWorldData").setApplyIf(() -> FixesConfig.fixJourneymapFilePath).addRequiredMod(TargetedMod.JOURNEYMAP)),
    FIX_JOURNEYMAP_JUMPY_SCROLLING(new MixinBuilder("Fix Journeymap jumpy scrolling in the waypoint manager").addClientMixins("journeymap.MixinWaypointManager").setApplyIf(() -> FixesConfig.fixJourneymapJumpyScrolling).addRequiredMod(TargetedMod.JOURNEYMAP)),

    // Xaero's World Map
    FIX_XAEROS_WORLDMAP_SCROLL(new MixinBuilder("Fix Xaero's World Map map screen scrolling").addClientMixins("xaeroworldmap.MixinGuiMap").setApplyIf(() -> FixesConfig.fixXaerosWorldMapScroll).addRequiredMod(TargetedMod.XAEROWORLDMAP).addRequiredMod(TargetedMod.LWJGL3IFY)),

    // Xaero's Minimap
    FIX_XAEROS_MINIMAP_ENTITYDOT(new MixinBuilder("Fix Xaero's Minimap player entity dot rendering when arrow is chosen").addClientMixins("xaerominimap.MixinMinimapRenderer").setApplyIf(() -> FixesConfig.fixXaerosMinimapEntityDot).addRequiredMod(TargetedMod.XAEROMINIMAP)),

    // Pam's Harvest the Nether
    FIX_IGNIS_FRUIT_AABB(new MixinBuilder("Ignis Fruit").addCommonMixins("harvestthenether.MixinBlockPamFruit").setApplyIf(() -> FixesConfig.fixIgnisFruitAABB).addRequiredMod(TargetedMod.HARVESTTHENETHER)),
    FIX_NETHER_LEAVES_FACE_RENDERING(new MixinBuilder("Nether Leaves").addClientMixins("harvestthenether.MixinBlockNetherLeaves").setApplyIf(() -> FixesConfig.fixNetherLeavesFaceRendering).addRequiredMod(TargetedMod.HARVESTTHENETHER)),

    // Potion Render Offset Fixes - Various Mods
    FIX_BAUBLES_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder("Baubles Inventory with Potions").addClientMixins("baubles.MixinGuiEvents").setApplyIf(() -> TweaksConfig.fixPotionRenderOffset).addRequiredMod(TargetedMod.BAUBLES)),
    FIX_GALACTICRAFT_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder("Galacticraft Inventory with Potions").addClientMixins("galacticraftcore.MixinGuiExtendedInventory").setApplyIf(() -> TweaksConfig.fixPotionRenderOffset).addRequiredMod(TargetedMod.GALACTICRAFT_CORE)),
    FIX_TRAVELLERSGEAR_INVENTORY_OFFSET_WITH_POTIONS(new MixinBuilder("Travelers Gear with Potions").addClientMixins("travellersgear.MixinClientProxy").setApplyIf(() -> TweaksConfig.fixPotionRenderOffset).addRequiredMod(TargetedMod.TRAVELLERSGEAR)),
    FIX_TINKER_POTION_EFFECT_OFFSET(new MixinBuilder("Prevents the inventory from shifting when the player has active potion effects").addRequiredMod(TargetedMod.TINKERSCONSTRUCT).setApplyIf(() -> TweaksConfig.fixPotionRenderOffset).addClientMixins("tconstruct.MixinTabRegistry")),

    // Extra Tinkers
    Fix_EXTRATIC_TECONFLICT(new MixinBuilder("Disable ExtraTic's Integration with Metallurgy 3 Precious Materials Module: [Brass, Silver, Electrum & Platinum]").addCommonMixins("extratic.MixinPartsHandler", "extratic.MixinRecipeHandler").setApplyIf(() -> FixesConfig.fixExtraTiCTEConflict).addRequiredMod(TargetedMod.EXTRATIC)),
    // Extra Utilities
    FIX_EXTRA_UTILITIES_UNENCHANTING(new MixinBuilder("Fix Exu Unenchanting").addCommonMixins("extrautilities.MixinRecipeUnEnchanting").setApplyIf(() -> FixesConfig.fixExtraUtilitiesUnEnchanting).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    DISABLE_AID_SPAWN_XU_SPIKES(new MixinBuilder("Fixes the vanilla zombie aid spawn triggering when killed by Extra Utilities Spikes").addCommonMixins("extrautilities.MixinBlockSpike").setApplyIf(() -> TweaksConfig.disableAidSpawnByXUSpikes).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_EXTRA_UTILITIES_TRANSPARENT_ITEM_RENDER(new MixinBuilder("Fix extra utilities item rendering for transparent items").addClientMixins("extrautilities.MixinTransparentItemRender").setApplyIf(() -> FixesConfig.fixExtraUtilitiesItemRendering).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_DRUM_EATING_CELLS(new MixinBuilder("Fix extra utilities drums eating ic2 cells and forestry capsules").addCommonMixins("extrautilities.MixinBlockDrum").setApplyIf(() -> FixesConfig.fixExtraUtilitiesDrumEatingCells).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_GREENSCREEN_MICROBLOCKS(new MixinBuilder("Fix extra utilities Lapis Caelestis microblocks").addClientMixins("extrautilities.MixinFullBrightMicroMaterial").setApplyIf(() -> FixesConfig.fixExtraUtilitiesGreenscreenMicroblocks).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_LAST_MILLENIUM_RAIN(new MixinBuilder("Remove rain from the Last Millenium (Extra Utilities)").addCommonMixins("extrautilities.MixinChunkProviderEndOfTime").setApplyIf(() -> FixesConfig.fixExtraUtilitiesLastMilleniumRain).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_LAST_MILLENIUM_CREATURES(new MixinBuilder("Remove creatures from the Last Millenium (Extra Utilities)").addCommonMixins("extrautilities.MixinWorldProviderEndOfTime").setApplyIf(() -> FixesConfig.fixExtraUtilitiesLastMilleniumCreatures).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_FLUID_RETRIEVAL_NODE(new MixinBuilder("Prevent fluid retrieval node from voiding (Extra Utilities)").addCommonMixins("extrautilities.MixinFluidBufferRetrieval").setApplyIf(() -> FixesConfig.fixExtraUtilitiesFluidRetrievalNode).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_FILING_CABINET_DUPE(new MixinBuilder("Caps hotkey'd stacks to their maximum stack size in filing cabinets").addCommonMixins("extrautilities.MixinContainerFilingCabinet").setApplyIf(() -> FixesConfig.fixExtraUtilitiesFilingCabinetDupe).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_FILTER_DUPE(new MixinBuilder("Prevent hotkeying other items onto item filters while they are open").addCommonMixins("extrautilities.MixinContainerFilter").setApplyIf(() -> FixesConfig.fixExtraUtilitiesFilterDupe).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    CONFIGURABLE_ENDERQUARRY_ENERGY(new MixinBuilder("Ender Quarry energy storage override").addCommonMixins("extrautilities.MixinTileEntityEnderQuarry").setApplyIf(() -> TweaksConfig.extraUtilitiesEnderQuarryOverride > 0).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_ENDERQUARRY_FREEZE(new MixinBuilder("Fix Ender Quarry freezes randomly").addCommonMixins("extrautilities.MixinTileEntityEnderQuarry_FixFreeze").setApplyIf(() -> FixesConfig.fixExtraUtilitiesEnderQuarryFreeze).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_HEALING_AXE_HEAL(new MixinBuilder("Fix the healing axe not healing entities when attacking them").addCommonMixins("extrautilities.MixinItemHealingAxe").setApplyIf(() -> FixesConfig.fixExtraUtilitiesHealingAxeHeal).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_CHEST_COMPARATOR_UPDATE(new MixinBuilder("Fix Extra Utilities chests not updating comparator redstone signals when their inventories change").addCommonMixins("extrautilities.MixinExtraUtilsChest").setApplyIf(() -> FixesConfig.fixExtraUtilitiesChestComparatorUpdate).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_ETHERIC_SWORD_UNBREAKABLE(new MixinBuilder("Make Etheric Sword truly unbreakable").addCommonMixins("extrautilities.MixinItemEthericSword").setApplyIf(() -> FixesConfig.fixExtraUtilitiesEthericSwordUnbreakable).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),
    FIX_ENDER_COLLECTOR_CRASH(new MixinBuilder("Prevent Extra Utilities Ender Collector from inserting into auto-dropping Blocks that create a crash-loop").addCommonMixins("extrautilities.MixinTileEnderCollector").setApplyIf(() -> FixesConfig.fixExtraUtilitiesEnderCollectorCrash).addRequiredMod(TargetedMod.EXTRA_UTILITIES)),

    // Gliby's Voice Chat
    FIX_GLIBYS_VC_THREAD_SHUTDOWN_CLIENT(new MixinBuilder("Fix Gliby's voice chat not shutting down its client thread cleanly").addClientMixins("glibysvoicechat.MixinClientNetwork").setApplyIf(() -> FixesConfig.fixGlibysVoiceChatThreadStop).addRequiredMod(TargetedMod.GLIBYS_VOICE_CHAT)),
    FIX_GLIBYS_VC_THREAD_SHUTDOWN_SERVER(new MixinBuilder("Fix Gliby's voice chat not shutting down its server thread cleanly").addCommonMixins("glibysvoicechat.MixinVoiceChatServer").setApplyIf(() -> FixesConfig.fixGlibysVoiceChatThreadStop).addRequiredMod(TargetedMod.GLIBYS_VOICE_CHAT)),

    // PortalGun
    PORTALGUN_FIX_URLS(new MixinBuilder("Fix URLs used to download the sound pack").addClientMixins("portalgun.MixinThreadDownloadResources").addRequiredMod(TargetedMod.PORTAL_GUN).setApplyIf(() -> FixesConfig.fixPortalGunURLs)),

    // VoxelMap
    REPLACE_VOXELMAP_REFLECTION(new MixinBuilder("Replace VoxelMap Reflection").addClientMixins("voxelmap.reflection.MixinAddonResourcePack", "voxelmap.reflection.MixinColorManager", "voxelmap.reflection.MixinMap", "voxelmap.reflection.MixinRadar", "voxelmap.reflection.MixinVoxelMap", "voxelmap.reflection.MixinWaypointManager$1").addRequiredMod(TargetedMod.VOXELMAP).setApplyIf(() -> SpeedupsConfig.replaceVoxelMapReflection)),
    VOXELMAP_Y_FIX(new MixinBuilder("Fix off by one Y coord").addClientMixins("voxelmap.MixinMap").addRequiredMod(TargetedMod.VOXELMAP).setApplyIf(() -> FixesConfig.fixVoxelMapYCoord)),
    VOXELMAP_NPE_FIX(new MixinBuilder("Fix VoxelMap NPEs with Chunks").addClientMixins("voxelmap.chunk.MixinCachedRegion", "voxelmap.chunk.MixinComparisonCachedRegion").addRequiredMod(TargetedMod.VOXELMAP).setApplyIf(() -> FixesConfig.fixVoxelMapChunkNPE)),
    VOXELMAP_FILE_EXT(new MixinBuilder("Change VoxelMap cache file extension").addClientMixins("voxelmap.cache.MixinCachedRegion", "voxelmap.cache.MixinCachedRegion$1", "voxelmap.cache.MixinComparisonCachedRegion").addRequiredMod(TargetedMod.VOXELMAP).setApplyIf(() -> TweaksConfig.changeCacheFileExtension)),

    // Witchery
    DISABLE_POTION_ARRAY_EXTENDER(new MixinBuilder("Disable Witchery potion array extender").addCommonMixins("witchery.MixinPotionArrayExtender").setApplyIf(() -> FixesConfig.disableWitcheryPotionExtender).addRequiredMod(TargetedMod.WITCHERY)),
    FIX_WITCHERY_REFLECTION_SKIN(new MixinBuilder("Fixes Witchery player skins reflections").addClientMixins("witchery.MixinExtendedPlayer", "witchery.MixinEntityReflection").setApplyIf(() -> FixesConfig.fixWitcheryReflections).addRequiredMod(TargetedMod.WITCHERY)),
    FIX_WITCHERY_THUNDERING_DETECTION(new MixinBuilder("Fixes Witchery Thunder Detection for rituals and Witch Hunters breaking with mods modifying thunder frequency").addCommonMixins("witchery.MixinBlockCircle", "witchery.MixinEntityWitchHunter", "witchery.MixinRiteClimateChange").setApplyIf(() -> FixesConfig.fixWitcheryThunderDetection).addRequiredMod(TargetedMod.WITCHERY)),
    FIX_WITCHERY_RENDERING(new MixinBuilder("Fixes Witchery Rendering errors").addClientMixins("witchery.MixinBlockCircleGlyph").setApplyIf(() -> FixesConfig.fixWitcheryRendering).addRequiredMod(TargetedMod.WITCHERY)),

    // Various Exploits/Fixes
    BIBLIOCRAFT_PACKET_FIX(new MixinBuilder("Packet Fix").addCommonMixins("bibliocraft.MixinBibliocraftPatchPacketExploits").setApplyIf(() -> FixesConfig.fixBibliocraftPackets).addRequiredMod(TargetedMod.BIBLIOCRAFT)),
    BIBLIOCRAFT_PATH_SANITIZATION_FIX(new MixinBuilder("Path sanitization fix").addCommonMixins("bibliocraft.MixinPathSanitization").setApplyIf(() -> FixesConfig.fixBibliocraftPathSanitization).addRequiredMod(TargetedMod.BIBLIOCRAFT)),
    ZTONES_PACKET_FIX(new MixinBuilder("Packet Fix").addCommonMixins("ztones.MixinZtonesPatchPacketExploits").setApplyIf(() -> FixesConfig.fixZTonesPackets).addRequiredMod(TargetedMod.ZTONES)),
    ASP_RECIPE_FIX(new MixinBuilder("MT Core recipe fix").addCommonMixins("advancedsolarpanels.MixinAdvancedSolarPanel").addRequiredMod(TargetedMod.ADVANCED_SOLAR_PANELS).addExcludedMod(TargetedMod.DREAMCRAFT).setApplyIf(() -> FixesConfig.fixMTCoreRecipe)),
    TD_NASE_PREVENTION(new MixinBuilder("Prevent NegativeArraySizeException on itemduct transfers").addCommonMixins("thermaldynamics.MixinSimulatedInv").setApplyIf(() -> FixesConfig.preventThermalDynamicsNASE).addRequiredMod(TargetedMod.THERMALDYNAMICS)),
    TD_FLUID_GRID_CCE(new MixinBuilder("Prevent ClassCastException on forming invalid Thermal Dynamic fluid grid").addCommonMixins("thermaldynamics.MixinTileFluidDuctSuper").setApplyIf(() -> FixesConfig.preventFluidGridCrash).addRequiredMod(TargetedMod.THERMALDYNAMICS)),

    // Unbind Keybinds by default
    UNBIND_KEYS_TRAVELLERSGEAR(new MixinBuilder("Unbind Traveller's Gear keybinds").addClientMixins("travellersgear.MixinKeyHandler").setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addRequiredMod(TargetedMod.TRAVELLERSGEAR)),
    UNBIND_KEYS_INDUSTRIALCRAFT(new MixinBuilder("Unbind Industrial craft keybinds").addClientMixins("ic2.MixinKeyboardClient").setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addRequiredMod(TargetedMod.IC2)),
    UNBIND_KEYS_THAUMCRAFT(new MixinBuilder("Unbind Thaumcraft keybinds").addClientMixins("thaumcraft.MixinKeyHandlerThaumcraft").setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addRequiredMod(TargetedMod.THAUMCRAFT)),
    CHANGE_KEYBIND_CATEGORY_AUTOMAGY(new MixinBuilder("Change keybind category of Automagy").addClientMixins("automagy.MixinAutomagyKeyHandler").setApplyIf(() -> TweaksConfig.unbindKeybindsByDefault).addRequiredMod(TargetedMod.AUTOMAGY)),

    // Candycraft
    FIX_SUGARBLOCK_NPE(new MixinBuilder("Fix NPE when interacting with sugar block").addCommonMixins("candycraft.MixinBlockSugar").setApplyIf(() -> FixesConfig.fixCandycraftBlockSugarNPE).addRequiredMod(TargetedMod.CANDYCRAFT)),

    // Morpheus
    FIX_NOT_WAKING_PLAYERS(new MixinBuilder("Fix players not being woken properly when not everyone is sleeping").addServerMixins("morpheus.MixinMorpheusWakePlayers").setApplyIf(() -> FixesConfig.fixMorpheusWaking).addRequiredMod(TargetedMod.MORPHEUS));

    // spotless:on
    private final MixinBuilder builder;

    LateMixins(MixinBuilder builder) {
        this.builder = builder.setPhase(Phase.LATE);
    }

    @Nonnull
    @Override
    public MixinBuilder getBuilder() {
        return builder;
    }
}
