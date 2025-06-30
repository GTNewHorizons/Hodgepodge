package com.mitchej123.hodgepodge.mixins;

import com.gtnewhorizon.gtnhlib.mixin.ITargetedMod;

import cpw.mods.fml.common.Mod;

public enum TargetedMod implements ITargetedMod {

    ADVANCED_SOLAR_PANELS(null, "AdvancedSolarPanel"),
    ANGELICA("com.gtnewhorizons.angelica.loading.AngelicaTweaker", "angelica"),
    ARCHAICFIX("org.embeddedt.archaicfix.ArchaicCore", "archaicfix"),
    AUTOMAGY(null, "Automagy"),
    BAUBLES(null, "Baubles"),
    BETTERHUD(null, "hud"),
    BIBLIOCRAFT(null, "BiblioCraft"),
    BOP(null, "BiomesOPlenty"),
    BUGTORCH("jss.bugtorch.mixinplugin.BugTorchEarlyMixins", "bugtorch"),
    BUKKIT("Bukkit", null),
    CANDYCRAFT(null, "candycraftmod"),
    COFH_CORE("cofh.asm.LoadingPlugin", "CoFHCore"),
    DAMAGE_INDICATORS(null, "DamageIndicatorsMod"),
    DREAMCRAFT("com.dreammaster.coremod.DreamCoreMod", "dreamcraft"),
    ETFURUMREQUIEM("ganymedes01.etfuturum.mixinplugin.EtFuturumEarlyMixins", "etfuturum"),
    EXTRATIC(null, "ExtraTiC"),
    EXTRA_UTILITIES(null, "ExtraUtilities"),
    FASTCRAFT("fastcraft.Tweaker", null),
    FALSETWEAKS("com.falsepattern.falsetweaks.asm.CoreLoadingPlugin", "falsetweaks"),
    GALACTICRAFT_CORE("micdoodle8.mods.galacticraft.core.asm.GCLoadingPlugin", "GalacticraftCore"),
    GLIBYS_VOICE_CHAT(null, "gvc"),
    GT5U(null, "gregtech"), // Also matches GT6.
    GT6("gregtech.asm.GT_ASM", "gregapi"), // Can be used to exclude GT6 from the GT5U target.
    GTNHLIB(null, "gtnhlib"),
    HARVESTCRAFT(null, "harvestcraft"),
    HARVESTTHENETHER(null, "harvestthenether"),
    HUNGER_OVERHAUL(null, "HungerOverhaul"),
    IC2("ic2.core.coremod.IC2core", "IC2"),
    IMMERSIVE_ENGINENEERING(null, "ImmersiveEngineering"),
    JOURNEYMAP(null, "journeymap"),
    LOTR("lotr.common.coremod.LOTRLoadingPlugin", "lotr"),
    LWJGL3IFY("me.eigenraven.lwjgl3ify.core.Lwjgl3ifyCoremod", "lwjgl3ify"),
    MINECHEM(null, "minechem"),
    MINEFACTORY_RELOADED(null, "MineFactoryReloaded"),
    MODULARPOWERSUITS(null, "powersuits"),
    MORPHEUS(null, "Morpheus"),
    MRTJPCORE(null, "MrTJPCoreMod"),
    NOTENOUGHITEMS("codechicken.nei.asm.NEICorePlugin", "NotEnoughItems"),
    OPTIFINE("optifine.OptiFineForgeTweaker", "Optifine"),
    PORTAL_GUN(null, "PortalGun"),
    PROJECTE(null, "ProjectE"),
    RAILCRAFT(null, "Railcraft"),
    THAUMCRAFT(null, "Thaumcraft"), // "thaumcraft.codechicken.core.launch.DepLoader"
    THERMALDYNAMICS(null, "ThermalDynamics"),
    THERMALEXPANSION(null, "ThermalExpansion"),
    TINKERSCONSTRUCT(null, "TConstruct"),
    TRAVELLERSGEAR(null, "TravellersGear"),
    ULTRAMINE("org.ultramine.server.UltraminePlugin", "UltramineServer"),
    VOXELMAP("com.thevoxelbox.voxelmap.litemod.VoxelMapTransformer", "voxelmap"),
    WITCHERY(null, "witchery"),
    XAEROMINIMAP(null, "XaeroMinimap"),
    XAEROWORLDMAP(null, "XaeroWorldMap"),
    ZTONES(null, "Ztones");

    /** Class that implements the IFMLLoadingPlugin interface */
    public final String coreModClass;
    /** The "modid" in the {@link Mod @Mod} annotation */
    public final String modId;

    TargetedMod(String coreModClass, String modId) {
        this.coreModClass = coreModClass;
        this.modId = modId;
    }

    @Override
    public String getCoreModClass() {
        return coreModClass;
    }

    @Override
    public String getModId() {
        return modId;
    }
}
