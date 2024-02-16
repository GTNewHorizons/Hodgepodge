package com.mitchej123.hodgepodge.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "hodgepodge", category = "tweaks")
public class TweaksConfig {

    @Config.Comment("Add CV support to Thaumcraft wand recharge pedestal")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean addCVSupportToWandPedestal;

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

    @Config.Comment("Amount of chat lines kept [100(Vanilla) - 32767]")
    @Config.RangeInt(min = 100, max = 32767)
    @Config.DefaultInt(8191)
    public static int chatLength;

    @Config.Comment("Open an integrated server on a static port.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableDefaultLanPort;

    @Config.Comment("Specify default LAN port to open an integrated server on. Set to 0 to always open the server on an automatically allocated port.")
    @Config.RangeInt(min = 0, max = 65535)
    @Config.DefaultInt(25565)
    public static int defaultLanPort;

    @Config.Comment("Display fluid localized name in IC2 fluid cell tooltip")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean displayIc2FluidLocalizedName;

    @Config.Comment("Drop picked loot on entity despawn")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean dropPickedLootOnDespawn;

    @Config.Comment("Shows renderer's impact on FPS in vanilla lagometer")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableTileRendererProfiler;

    @Config.Comment("Moves the sprint keybind to the movement category")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean changeSprintCategory;

    @Config.Comment("Fix Project Red components popping off on unloaded chunks")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixComponentsPoppingOff;

    @Config.Comment("Fix hotbars being dark when Project Red is installed")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixHudLightingGlitch;
    @Config.Comment("Fix vanilla potion effects rendering above the NEI tooltips in the inventory")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixPotionEffectRender;
    @Config.Comment("Prevents the inventory from shifting when the player has active potion effects")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean fixPotionRenderOffset;
    @Config.Comment("Disable Minecraft Realms button on main menu")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean disableRealmsButton;
    @Config.Comment("Compacts identical consecutive chat messages together")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean compactChat;
    @Config.Comment("Stop inverting colors of crosshair")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean dontInvertCrosshairColor;
    @Config.Comment("Remove the blueish sky tint from night vision")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enhanceNightVision;
    @Config.Comment("Stops rendering the crosshair when you are playing in third person")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean hideCrosshairInThirdPerson;
    @Config.Comment("Prevent IC2's reactor's coolant slots from being accessed by automations if not a fluid reactor")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean hideIc2ReactorSlots;
    @Config.Comment("Increase particle limit")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean increaseParticleLimit;
    @Config.Comment("Wake up passive & personal anchors on player login")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean installAnchorAlarm;
    @Config.Comment("Makes the chat history longer instead of 100 lines")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean longerChat;
    @Config.Comment("Allows you to send longer chat messages, up to 256 characters, instead of 100 in vanilla.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean longerSentMessages;
    @Config.Comment("Allow 5 Fir Sapling planted together ('+' shape) to grow to a big fir tree")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean makeBigFirsPlantable;
    @Config.Comment("Prevent monsters from picking up loot.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean preventPickupLoot;
    @Config.Comment("Use CMD key on MacOS to COPY / INSERT / SELECT in text fields (Chat, NEI, Server IP etc.)")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMacosCmdShortcuts;
    @Config.Comment("Stop playing a sound when spawning a minecart in the world")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean removeSpawningMinecartSound;
    @Config.Comment("Removes the 'GL error' message that appears when using a shader in Optifine/Shadersmod")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean removeOptifineGLErrors;
    @Config.Comment("Implement container for thirsty tank")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean thirstyTankContainer;
    @Config.Comment("Doesn't render the black box behind messages when the chat is closed")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean transparentChat;
    @Config.Comment("Unbinds keybinds of certain ARR mods to avoid keybinds conflicts")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean unbindKeybindsByDefault;
    @Config.Comment("Disables the spawn of zombie aid when zombie is killed by Extra Utilities Spikes, since it can spawn them too far.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean disableAidSpawnByXUSpikes;
    @Config.Comment("give ic2 cells containers like gregtech cells do")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean ic2CellWithContainer;
    @Config.Comment("Adds pick block functionality to survival mode")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean modernPickBlock;
    @Config.Comment("Allows blocks to be placed at a faster rate (toggleable via keybind)")
    @Config.DefaultBoolean(false)
    public static boolean fastBlockPlacing;
    @Config.Comment("Sets the interval for auto saves in ticks (20 ticks = 1 second)")
    @Config.RangeInt(min = 1)
    @Config.DefaultInt(900)
    public static int autoSaveInterval;

    @Config.Comment("Server control for fast block placing; if false, the client-side setting will be ignored and keybind will do nothing")
    @Config.DefaultBoolean(true)
    public static boolean fastBlockPlacingServerControl;

    @Config.Comment("Stops rendering potion particles from yourself")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean hidePotionParticlesFromSelf;

    @Config.Comment("IC2 seed max stack size")
    @Config.RangeInt(min = 1, max = 64)
    @Config.DefaultInt(64)
    public static int ic2SeedMaxStackSize;
    @Config.Comment("Minechem Atropine High (Delirium) effect ID")
    @Config.RangeInt(min = 1, max = 255)
    @Config.DefaultInt(255)
    public static int atropineHighID;
    @Config.Comment("Particle limit [4000-16000]")
    @Config.RangeInt(min = 4000, max = 16000)
    @Config.DefaultInt(8000)
    public static int particleLimit;

}
