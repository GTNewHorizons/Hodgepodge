package com.mitchej123.hodgepodge;

import com.mitchej123.hodgepodge.core.HodgePodgeClient;
import com.mitchej123.hodgepodge.core.commands.DebugCommand;
import com.mitchej123.hodgepodge.core.util.AnchorAlarm;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(
        modid = Hodgepodge.MODID,
        version = Hodgepodge.VERSION,
        name = Hodgepodge.NAME,
        acceptableRemoteVersions = "*",
        dependencies = "required-after:spongemixins@[1.4.0,);")
public class Hodgepodge {
    public static final AnchorAlarm ANCHOR_ALARM = new AnchorAlarm();
    public static final String MODID = "hodgepodge";
    public static final String VERSION = "GRADLETOKEN_VERSION";
    public static final String NAME = "A Hodgepodge of Patches";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(ANCHOR_ALARM);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            HodgePodgeClient.postInit();
        }
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent aEvent) {
        aEvent.registerServerCommand(new DebugCommand());
    }
}
