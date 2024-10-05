package com.mitchej123.hodgepodge.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "hodgepodge", category = "pollution")
@Config.RequiresMcRestart
public class PollutionConfig {

    // Minecraft

    @Config.Comment("Explosion pollution")
    @Config.DefaultFloat(33.34f)
    public static float explosionPollutionAmount;

    @Config.Comment("Make furnaces Pollute")
    @Config.DefaultBoolean(true)
    public static boolean furnacesPollute;

    @Config.Comment("Furnace pollution per second, min 1!")
    @Config.DefaultInt(20)
    public static int furnacePollutionAmount;

    // Galacticraft

    @Config.Comment("Pollution Amount for Rockets")
    @Config.DefaultInt(1000)
    public static int rocketPollutionAmount;

    @Config.Comment("Make rockets Pollute")
    @Config.DefaultBoolean(true)
    public static boolean rocketsPollute;

    // Railcraft

    @Config.Comment("Pollution Amount for Advanced Coke Ovens")
    @Config.DefaultInt(80)
    public static int advancedCokeOvenPollutionAmount;

    @Config.Comment("Pollution Amount for Coke Ovens")
    @Config.DefaultInt(3)
    public static int cokeOvenPollutionAmount;

    @Config.Comment("Pollution Amount for RC Firebox")
    @Config.DefaultInt(15)
    public static int fireboxPollutionAmount;

    @Config.Comment("Pollution Amount for hobbyist steam engine")
    @Config.DefaultInt(20)
    public static int hobbyistEnginePollutionAmount;

    @Config.Comment("Make Railcraft Pollute")
    @Config.DefaultBoolean(true)
    public static boolean railcraftPollutes;

    @Config.Comment("Pollution Amount for tunnel bore")
    @Config.DefaultInt(2)
    public static int tunnelBorePollutionAmount;

}
