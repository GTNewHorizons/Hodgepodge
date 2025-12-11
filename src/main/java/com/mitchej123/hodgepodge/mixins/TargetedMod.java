package com.mitchej123.hodgepodge.mixins;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod;
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder;

public enum TargetedMod implements ITargetMod {

    ADVANCED_SOLAR_PANELS("AdvancedSolarPanel"),
    ANGELICA("com.gtnewhorizons.angelica.loading.AngelicaTweaker", "angelica"),
    ARCHAICFIX("org.embeddedt.archaicfix.ArchaicCore", "archaicfix"),
    AUTOMAGY("Automagy"),
    BAUBLES("Baubles"),
    BETTERHUD("hud"),
    BIBLIOCRAFT("BiblioCraft"),
    BOP("BiomesOPlenty"),
    BIBLIOWOODSFORESTRY("BiblioWoodsForestry"),
    BUGTORCH("jss.bugtorch.mixinplugin.BugTorchEarlyMixins", "bugtorch"),
    BUKKIT(null, null, "org.bukkit.World"),
    CANDYCRAFT("candycraftmod"),
    COFH_CORE("cofh.asm.LoadingPlugin", "CoFHCore"),
    DAMAGE_INDICATORS("DamageIndicatorsMod"),
    DRAGONAPI("Reika.DragonAPI.Auxiliary.DragonAPIASMHandler", "DragonAPI"),
    DREAMCRAFT("com.dreammaster.coremod.DreamCoreMod", "dreamcraft"),
    // this is not ender io but a library for it, it used to contain the
    // transformer that got removed and added via mixin in this mod, so we don't
    // load our mixins if an old version of the mod that contains this class is loaded
    ENDERCORE_WITH_MODLIST(null, null, "com.enderio.core.common.transform.EnderCoreTransformerClient"),
    ETFURUMREQUIEM("ganymedes01.etfuturum.mixinplugin.EtFuturumEarlyMixins", "etfuturum"),
    EXTRATIC("ExtraTiC"),
    EXTRA_UTILITIES("ExtraUtilities"),
    FALSETWEAKS("com.falsepattern.falsetweaks.asm.CoreLoadingPlugin", "falsetweaks"),
    FASTCRAFT(null, null, "fastcraft.Tweaker"),
    GALACTICRAFT_CORE("micdoodle8.mods.galacticraft.core.asm.GCLoadingPlugin", "GalacticraftCore"),
    GLIBYS_VOICE_CHAT("gvc"),
    GT5U("gregtech"), // Also matches GT6.
    GT6("gregtech.asm.GT_ASM", "gregapi"), // Can be used to exclude GT6 from the GT5U target.
    // this is not a class name, but it's what actually shows up in the coremod list
    GTNHLIB("GTNHLib Core", "gtnhlib"),
    HARVESTCRAFT("harvestcraft"),
    HARVESTTHENETHER("harvestthenether"),
    HUNGER_OVERHAUL("HungerOverhaul"),
    // Target only IC2, not IC2 Classic. Both have the same mod id.
    IC2(new TargetModBuilder().setTargetClass("ic2.core.IC2")
            .testModAnnotation(modId -> modId.equals("IC2"), name -> !name.contains("Classic"), null)),
    IMMERSIVE_ENGINENEERING("ImmersiveEngineering"),
    JOURNEYMAP("journeymap"),
    LOTR("lotr.common.coremod.LOTRLoadingPlugin", "lotr"),
    LWJGL3IFY("me.eigenraven.lwjgl3ify.core.Lwjgl3ifyCoremod", "lwjgl3ify"),
    MINECHEM("minechem"),
    MINEFACTORY_RELOADED("MineFactoryReloaded"),
    MODERNKEYBINDING(null, null, "committee.nova.mkb.ModernKeyBinding"),
    MODULARPOWERSUITS("powersuits"),
    MORPHEUS("Morpheus"),
    MRTJPCORE("MrTJPCoreMod"),
    NOTENOUGHITEMS("codechicken.nei.asm.NEICorePlugin", "NotEnoughItems"),
    OPTIFINE("optifine.OptiFineForgeTweaker", "Optifine"),
    PORTAL_GUN("PortalGun"),
    PROJECTE("ProjectE"),
    RAILCRAFT("Railcraft"),
    THAUMCRAFT("Thaumcraft"), // "thaumcraft.codechicken.core.launch.DepLoader"
    THERMALDYNAMICS("ThermalDynamics"),
    THERMALEXPANSION("ThermalExpansion"),
    TINKERSCONSTRUCT("TConstruct"),
    TRAVELLERSGEAR("TravellersGear"),
    ULTRAMINE(null, null, "org.ultramine.server.UltraminePlugin"),
    VOXELMAP("com.thevoxelbox.voxelmap.litemod.VoxelMapTransformer", "voxelmap"),
    WITCHERY("witchery"),
    XAEROMINIMAP("XaeroMinimap"),
    XAEROWORLDMAP("XaeroWorldMap"),
    ZTONES("Ztones");

    private final TargetModBuilder builder;

    TargetedMod(TargetModBuilder builder) {
        this.builder = builder;
    }

    TargetedMod(String modId) {
        this(null, modId, null);
    }

    TargetedMod(String coreModClass, String modId) {
        this(coreModClass, modId, null);
    }

    TargetedMod(String coreModClass, String modId, String targetClass) {
        this.builder = new TargetModBuilder().setCoreModClass(coreModClass).setModId(modId).setTargetClass(targetClass);
    }

    @Nonnull
    @Override
    public TargetModBuilder getBuilder() {
        return builder;
    }
}
