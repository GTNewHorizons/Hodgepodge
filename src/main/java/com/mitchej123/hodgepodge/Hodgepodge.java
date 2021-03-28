package com.mitchej123.hodgepodge;

import com.mitchej123.hodgepodge.core.HodgePodgeClient;
import com.mitchej123.hodgepodge.core.util.AnchorAlarm;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Hodgepodge.MODID, version = Hodgepodge.VERSION, name = Hodgepodge.NAME, acceptableRemoteVersions = "*",
        dependencies = "required-after:spongemixins@[1.3.0,);")
public class Hodgepodge {

    public static final Logger log = LogManager.getLogger("Hodgepodge");
    public static final String MODID = "hodgepodge";
    public static final String VERSION = "GRADLETOKEN_VERSION";
    public static final String NAME = "A Hodgepodge of Patches";
    public static XSTR RNG = new XSTR();
    public final static AnchorAlarm ANCHOR_ALARM = new AnchorAlarm();

    @EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(ANCHOR_ALARM);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)  {
        if (event.getSide() == Side.CLIENT) {
            HodgePodgeClient.postInit();
        }
    }
}
