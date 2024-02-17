package com.mitchej123.hodgepodge.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "hodgepodge", category = "tweaks")
public class TweaksConfig {

    // Minecraft

    @Config.Comment("Adds system info to the F3 overlay (Java version and vendor; GPU name; OpenGL version; CPU cores; OS name, version and architecture)")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean addSystemInfo;

    @Config.Comment("Add a debug message in the chat when toggling vanilla debug options")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean addToggleDebugMessage;

    @Config.Comment("Uses arabic numbers for enchantment levels and potion amplifier levels instead of roman numbers")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean arabicNumbersForEnchantsPotions;

    @Config.Comment("Show \"cannot sleep\" messages above hotbar")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean bedMessageAboveHotbar;

    @Config.Comment("Moves the sprint keybind to the movement category")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean changeSprintCategory;

    @Config.Comment("Amount of chat lines kept (Vanilla: 100)")
    @Config.RangeInt(min = 100, max = 32767)
    @Config.DefaultInt(8191)
    public static int chatLength;

    @Config.Comment("Compacts identical consecutive chat messages together")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean compactChat;

    @Config.Comment("Specify default LAN port to open an integrated server on. Set to 0 to always open the server on an automatically allocated port.")
    @Config.RangeInt(min = 0, max = 65535)
    @Config.DefaultInt(25565)
    public static int defaultLanPort;

    @Config.Comment("Disable Minecraft Realms button on main menu")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean disableRealmsButton;

    @Config.Comment("Stop inverting colors of crosshair")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean dontInvertCrosshairColor;

    @Config.Comment("Drop picked loot on entity despawn")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean dropPickedLootOnDespawn;

    @Config.Comment("Open an integrated server on a static port.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableDefaultLanPort;

    @Config.Comment("Use CMD key on MacOS to COPY / INSERT / SELECT in text fields (Chat, NEI, Server IP etc.)")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMacosCmdShortcuts;

    @Config.Comment("Shows renderer's impact on FPS in vanilla lagometer")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableTileRendererProfiler;

    @Config.Comment("Remove the blueish sky tint from night vision")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean enhanceNightVision;

    @Config.Comment("Allows blocks to be placed at a faster rate (toggleable via keybind)")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean fastBlockPlacing;

    @Config.Comment("Prevents the inventory from shifting when the player has active potion effects")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixPotionRenderOffset;

    @Config.Comment("Stops rendering the crosshair when you are playing in third person")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean hideCrosshairInThirdPerson;

    @Config.Comment("Increase particle limit")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean increaseParticleLimit;

    @Config.Comment("Makes the chat history longer instead of 100 lines")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean longerChat;

    @Config.Comment("Allows you to send longer chat messages, up to 256 characters, instead of 100 in vanilla.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean longerSentMessages;

    @Config.Comment("Adds pick block functionality to survival mode")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean modernPickBlock;

    @Config.Comment("Particle limit (Vanilla: 4000)")
    @Config.RangeInt(min = 4000, max = 16000)
    @Config.DefaultInt(8000)
    public static int particleLimit;

    @Config.Comment("Prevent monsters from picking up loot.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean preventPickupLoot;

    @Config.Comment("Stop playing a sound when spawning a minecart in the world")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean removeSpawningMinecartSound;

    @Config.Comment("Doesn't render the black box behind messages when the chat is closed")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean transparentChat;

    // affecting multiple mods

    @Config.Comment("Unbinds keybinds of certain ARR mods to avoid keybinds conflicts")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean unbindKeybindsByDefault;

    // Automagy

    @Config.Comment("Implement container for thirsty tank")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean thirstyTankContainer;

    // Biomes O' Plenty

    @Config.Comment("Allow 5 Fir Sapling planted together ('+' shape) to grow to a big fir tree")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean makeBigFirsPlantable;

    // Extra Utilities

    @Config.Comment("Disables the spawn of zombie aid when zombie is killed by Extra Utilities Spikes, since it can spawn them too far.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean disableAidSpawnByXUSpikes;

    // Industrialcraft

    @Config.Comment("Display fluid localized name in IC2 fluid cell tooltip")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean displayIc2FluidLocalizedName;

    @Config.Comment("Prevent IC2's reactor's coolant slots from being accessed by automations if not a fluid reactor")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean hideIc2ReactorSlots;

    @Config.Comment("Give IC2 cells containers like GregTech cells do")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean ic2CellWithContainer;

    @Config.Comment("IC2 seed max stack size")
    @Config.RangeInt(min = 1, max = 64)
    @Config.DefaultInt(64)
    public static int ic2SeedMaxStackSize;

    // Minechem

    @Config.Comment("Minechem Atropine High (Delirium) effect ID")
    @Config.RangeInt(min = 1, max = 255)
    @Config.DefaultInt(255)
    public static int atropineHighID;

    // NotEnoughItems

    @Config.Comment("Fix vanilla potion effects rendering above the NEI tooltips in the inventory")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixPotionEffectRender;

    // Optifine

    @Config.Comment("Removes the 'GL error' message that appears when using a shader in Optifine/Shadersmod")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean removeOptifineGLErrors;

    // ProjectRed

    @Config.Comment("Fix Project Red components popping off on unloaded chunks")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixComponentsPoppingOff;

    @Config.Comment("Fix hotbars being dark when Project Red is installed")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixHudLightingGlitch;

    // Railcraft

    @Config.Comment("Wake up passive & personal anchors on player login")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean installAnchorAlarm;

    // Thaumcraft

    @Config.Comment("Add CV support to Thaumcraft wand recharge pedestal")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean addCVSupportToWandPedestal;

    @Config.Comment("Stops rendering potion particles from yourself")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean hidePotionParticlesFromSelf;

}
