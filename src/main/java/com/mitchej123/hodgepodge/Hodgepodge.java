package com.mitchej123.hodgepodge;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = Hodgepodge.MODID, version = Hodgepodge.VERSION, name = Hodgepodge.NAME, acceptableRemoteVersions = "*",
        dependencies = "required-after:spongemixins@[1.1.0,);")
public class Hodgepodge {

    public static final String MODID = "hodgepodge";
    public static final String VERSION = "1.4.0";
    public static final String NAME = "A Hodgepodge of Patches";
    public static XSTR RNG = new XSTR();

    @EventHandler
    public void init(FMLInitializationEvent event) {

    }
}
