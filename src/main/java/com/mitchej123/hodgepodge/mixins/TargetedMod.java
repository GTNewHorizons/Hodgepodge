package com.mitchej123.hodgepodge.mixins;

public enum TargetedMod {
    VANILLA("Minecraft", null),
    GTNHLIB("GTNHLib", "com.gtnewhorizon.gtnhlib.core.GTNHLibCore", "gtnhlib"),
    IC2("IC2", "ic2.core.coremod.IC2core", "IC2"),
    FASTCRAFT("FastCraft", "fastcraft.Tweaker"),
    COFH_CORE("CoFHCore", "cofh.asm.LoadingPlugin", "CoFHCore"),
    THAUMCRAFT("Thaumcraft", null, "Thaumcraft"), // "thaumcraft.codechicken.core.launch.DepLoader"
    GT5U("GregTech5u", null, "gregtech"),
    HUNGER_OVERHAUL("HungerOverhaul", null, "HungerOverhaul"),
    RAILCRAFT("Railcraft", null, "Railcraft"),
    BOP("BiomesOPlenty", null, "BiomesOPlenty"),
    MRTJPCORE("MrTJPCore", null, "MrTJPCoreMod"),
    AUTOMAGY("Automagy", null, "Automagy"),
    PROJECTE("ProjectE", null, "projecte"),
    HARVESTTHENETHER("harvestthenether", null, "harvestthenether"),
    GALACTICRAFT_CORE("GalacticraftCore", null, "GalacticraftCore"),
    BAUBLES("Baubles", null, "Baubles"),
    TRAVELLERSGEAR("TravellersGear", null, "TravellersGear"),
    JOURNEYMAP("JourneyMap", null, "journeymap"),
    OPTIFINE("Optifine", "optifine.OptiFineForgeTweaker", "Optifine"),
    EXTRA_UTILITIES("ExtraUtilities", null, "ExtraUtilities"),
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
