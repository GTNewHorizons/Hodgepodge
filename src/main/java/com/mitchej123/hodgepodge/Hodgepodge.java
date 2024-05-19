package com.mitchej123.hodgepodge;

import com.mitchej123.hodgepodge.client.HodgepodgeClient;
import com.mitchej123.hodgepodge.commands.DebugCommand;
import com.mitchej123.hodgepodge.net.NetworkHandler;
import com.mitchej123.hodgepodge.util.AnchorAlarm;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(
        modid = Hodgepodge.MODID,
        version = Hodgepodge.VERSION,
        name = Hodgepodge.NAME,
        acceptableRemoteVersions = "*",
        dependencies = "required-after:gtnhmixins@[2.0.1,);" + "required-after:unimixins@[0.0.14,);"
                + "required-after:gtnhlib@[0.2.2,);",
        guiFactory = "com.mitchej123.hodgepodge.config.gui.HodgepodgeGuiConfigFactory")
public class Hodgepodge {

    public static final AnchorAlarm ANCHOR_ALARM = new AnchorAlarm();
    public static final HodgepodgeEventHandler EVENT_HANDLER = new HodgepodgeEventHandler();
    public static final String MODID = "hodgepodge";
    public static final String VERSION = Tags.VERSION;
    public static final String NAME = "Hodgepodge";

    public static final boolean isGTNH;

    static {
        isGTNH = true;
    }

    public Hodgepodge() {
        FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable() {
            @Override
            public String getLabel() {
                return "CPU Threads";
            }

            @Override
            public String call() {
                return String.valueOf(Runtime.getRuntime().availableProcessors());
            }
        });
    }

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        Compat.init(event.getSide());
        EVENT_HANDLER.preinit();

        if (event.getSide() == Side.CLIENT) {
            HodgepodgeClient.preInit();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(ANCHOR_ALARM);
        NetworkHandler.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            HodgepodgeClient.postInit();
        }
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent aEvent) {
        aEvent.registerServerCommand(new DebugCommand());

        // needed in case ExtraUtilities' Spike was crashed (and game was switched to a main menu), so it didn't update
        // the variable
        EVENT_HANDLER.setAidTriggerDisabled(false);
    }
}
