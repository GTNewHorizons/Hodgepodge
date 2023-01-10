package com.mitchej123.hodgepodge.mixins;

public enum TargetedMod {
    VANILLA("Minecraft", null),
    GTNHLIB("GTNHLib", "com.gtnewhorizon.gtnhlib.core.GTNHLibCore", "gtnhlib"),
    IC2("IC2", "ic2.core.coremod.IC2core", "IC2"),
    FASTCRAFT("FastCraft", "fastcraft.Tweaker"),
    COFH_CORE("CoFHCore", "cofh.asm.LoadingPlugin", "CoFHCore"),
    THAUMCRAFT("Thaumcraft", null, "Thaumcraft"), // "thaumcraft.codechicken.core.launch.DepLoader"
    GT5U("GregTech5u", null, "gregtech"), // Also matches GT6.
    GT6("GregTech6", "gregtech.asm.GT_ASM", "gregapi"), // Can be used to exclude GT6 from the GT5U target.
    HUNGER_OVERHAUL("HungerOverhaul", null, "HungerOverhaul"),
    RAILCRAFT("Railcraft", null, "Railcraft"),
    BOP("BiomesOPlenty", null, "BiomesOPlenty"),
    MRTJPCORE("MrTJPCore", null, "MrTJPCoreMod"),
    AUTOMAGY("Automagy", null, "Automagy"),
    PROJECTE("ProjectE", null, "ProjectE"),
    HARVESTTHENETHER("harvestthenether", null, "harvestthenether"),
    GALACTICRAFT_CORE("GalacticraftCore", "micdoodle8.mods.galacticraft.core.asm.GCLoadingPlugin", "GalacticraftCore"),
    BAUBLES("Baubles", null, "Baubles"),
    TRAVELLERSGEAR("TravellersGear", null, "TravellersGear"),
    JOURNEYMAP("JourneyMap", null, "journeymap"),
    OPTIFINE("Optifine", "optifine.OptiFineForgeTweaker", "Optifine"),
    EXTRA_UTILITIES("ExtraUtilities", null, "ExtraUtilities"),
    BIBLIOCRAFT("Bibliocraft", null, "BiblioCraft"),
    ZTONES("ZTones", null, "Ztones"),
    BUKKIT("Bukkit/Thermos", "Bukkit", null);

    public final String modName;
    public final String coreModClass;
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
