package com.mitchej123.hodgepodge;

import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = Hodgepodge.MODID, version = Hodgepodge.VERSION, name = Hodgepodge.NAME, acceptableRemoteVersions = "*")
public class Hodgepodge
{

    public static final String MODID = "hodgepodge";
    public static final String VERSION = "1.0";
    public static final String NAME = "A Hodgepodge of Patches";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		// some example code
        System.out.println("DIRT BLOCK >> "+Blocks.dirt.getUnlocalizedName());
    }
}
