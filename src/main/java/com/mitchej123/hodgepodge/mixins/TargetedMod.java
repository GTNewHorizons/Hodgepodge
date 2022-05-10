package com.mitchej123.hodgepodge.mixins;

public enum TargetedMod {
    VANILLA("Minecraft", null),
    IC2("IC2", "industrialcraft-2-"),
    GT5U("GregTech5u", "gregtech-1.7.10-5", "GT5-Unofficial"),
    HUNGER_OVERHAUL("HungerOverhaul", "HungerOverhaul"),
    RAILCRAFT("Railcraft", "Railcraft"),
    THAUMCRAFT("Thaumcraft", "Thaumcraft-1.7.10"),
    COFH_CORE("CoFHCore", "CoFHCore", "cofh-core"),
    BOP("BiomesOPlenty", "BiomesOPlenty-1.7.10"),
    MRTJPCORE("MrTJPCore", "MrTJPCore"),
    AUTOMAGY("Automagy", "Automagy-1.7.10")
    ;

    public final String modName;
    public final String jarName;
    public final String devJarName;
    
    TargetedMod(String modName, String jarName) {
        this.modName = modName;
        this.jarName = jarName;
        this.devJarName = jarName;
    }
    TargetedMod(String modName, String jarName, String devJarName) {
        this.modName = modName;
        this.jarName = jarName;
        this.devJarName = devJarName;
    }
    
    @Override
    public String toString() {
        return "TargetedMod{modName='" + modName + "', jarName='" + jarName + "', devJarName='" + devJarName + "'}";
    }
}
