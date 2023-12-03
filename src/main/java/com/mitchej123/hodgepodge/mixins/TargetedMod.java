package com.mitchej123.hodgepodge.mixins;

import cpw.mods.fml.common.Mod;

public enum TargetedMod {

    ANGELICA("Angelica", "com.gtnewhorizons.angelica.loading.AngelicaTweaker", "angelica"),
    ARCHAICFIX("ArchaicFix", "org.embeddedt.archaicfix.ArchaicCore", "archaicfix"),
    AUTOMAGY("Automagy", null, "Automagy"),
    BAUBLES("Baubles", null, "Baubles"),
    BETTERHUD("Better HUD", null, "hud"),
    BIBLIOCRAFT("Bibliocraft", null, "BiblioCraft"),
    BOP("BiomesOPlenty", null, "BiomesOPlenty"),
    BUKKIT("Bukkit/Thermos", "Bukkit", null),
    BUGTORCH("BugTorch", "jss.bugtorch.mixinplugin.BugTorchEarlyMixins", "bugtorch"),
    COFH_CORE("CoFHCore", "cofh.asm.LoadingPlugin", "CoFHCore"),
    EXTRA_UTILITIES("ExtraUtilities", null, "ExtraUtilities"),
    FASTCRAFT("FastCraft", "fastcraft.Tweaker"),
    GALACTICRAFT_CORE("GalacticraftCore", "micdoodle8.mods.galacticraft.core.asm.GCLoadingPlugin", "GalacticraftCore"),
    GT5U("GregTech5u", null, "gregtech"), // Also matches GT6.
    GT6("GregTech6", "gregtech.asm.GT_ASM", "gregapi"), // Can be used to exclude GT6 from the GT5U target.
    GTNHLIB("GTNHLib", "com.gtnewhorizon.gtnhlib.core.GTNHLibCore", "gtnhlib"),
    HARVESTCRAFT("harvestcraft", null, "harvestcraft"),
    HARVESTTHENETHER("harvestthenether", null, "harvestthenether"),
    HUNGER_OVERHAUL("HungerOverhaul", null, "HungerOverhaul"),
    IC2("IC2", "ic2.core.coremod.IC2core", "IC2"),
    IMMERSIVE_ENGINENEERING("Immersive Engineering", null, "ImmersiveEngineering"),
    JOURNEYMAP("JourneyMap", null, "journeymap"),
    LOTR("The Lord of the rings mod", "lotr.common.coremod.LOTRLoadingPlugin", "lotr"),
    LWJGL3IFY("lwjgl3ify", "me.eigenraven.lwjgl3ify.core.Lwjgl3ifyCoremod", "lwjgl3ify"),
    MINECHEM("Minechem", null, "minechem"),
    MRTJPCORE("MrTJPCore", null, "MrTJPCoreMod"),
    NOTENOUGHITEMS("NotEnoughItems", "codechicken.nei.asm.NEICorePlugin", "NotEnoughItems"),
    OPTIFINE("Optifine", "optifine.OptiFineForgeTweaker", "Optifine"),
    PROJECTE("ProjectE", null, "ProjectE"),
    RAILCRAFT("Railcraft", null, "Railcraft"),
    THAUMCRAFT("Thaumcraft", null, "Thaumcraft"), // "thaumcraft.codechicken.core.launch.DepLoader"
    TINKERSCONSTRUCT("TConstruct", null, "TConstruct"),
    EXTRATIC("ExtraTiC", null, "ExtraTiC"),
    TRAVELLERSGEAR("TravellersGear", null, "TravellersGear"),
    VANILLA("Minecraft", null),
    VOXELMAP("VoxelMap", "com.thevoxelbox.voxelmap.litemod.VoxelMapTransformer"),
    WITCHERY("Witchery", null, "witchery"),
    XAEROWORLDMAP("Xaero's World Map", null, "XaeroWorldMap"),
    ZTONES("ZTones", null, "Ztones");

    /** The "name" in the {@link Mod @Mod} annotation */
    public final String modName;
    /** Class that implements the IFMLLoadingPlugin interface */
    public final String coreModClass;
    /** The "modid" in the {@link Mod @Mod} annotation */
    public final String modId;

    TargetedMod(String modName, String coreModClass) {
        this(modName, coreModClass, null);
    }

    TargetedMod(String modName, String coreModClass, String modId) {
        this.modName = modName;
        this.coreModClass = coreModClass;
        this.modId = modId;
    }

    @Override
    public String toString() {
        return "TargetedMod{modName='" + modName + "', coreModClass='" + coreModClass + "', modId='" + modId + "'}";
    }
}
