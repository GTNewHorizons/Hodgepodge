package com.mitchej123.hodgepodge;

import com.mitchej123.hodgepodge.core.HodgePodgeClient;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Hodgepodge.MODID, version = Hodgepodge.VERSION, name = Hodgepodge.NAME, acceptableRemoteVersions = "*",
        dependencies = "required-after:spongemixins@[1.1.0,);")
public class Hodgepodge {

    public static final Logger log = LogManager.getLogger("Hodgepodge");
    public static final String MODID = "hodgepodge";
    public static final String VERSION = "1.5.0";
    public static final String NAME = "A Hodgepodge of Patches";
    public static XSTR RNG = new XSTR();

    @EventHandler
    public void init(FMLInitializationEvent asd) {
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)  {
        if (event.getSide() == Side.CLIENT) {
            HodgePodgeClient.postInit();
        }
    }
}
