package com.mitchej123.hodgepodge;

import com.mitchej123.hodgepodge.core.HodgePodgeClient;
import com.mitchej123.hodgepodge.core.LoadingConfig;
import com.mitchej123.hodgepodge.core.commands.DebugCommand;
import com.mitchej123.hodgepodge.core.util.AnchorAlarm;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import java.io.File;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = Hodgepodge.MODID,
        version = Hodgepodge.VERSION,
        name = Hodgepodge.NAME,
        acceptableRemoteVersions = "*",
        dependencies = "required-after:spongemixins@[1.4.0,);")
public class Hodgepodge {
    public static final Logger log = LogManager.getLogger("Hodgepodge");
    public static final String MODID = "hodgepodge";
    public static final String VERSION = "GRADLETOKEN_VERSION";
    public static final String NAME = "A Hodgepodge of Patches";

    public static LoadingConfig config;
    public static boolean thermosTainted;

    public static XSTR RNG = new XSTR();
    public static final AnchorAlarm ANCHOR_ALARM = new AnchorAlarm();

    static {
        Hodgepodge.log.info("Initializing Hodgepodge");
        config = new LoadingConfig(new File(Launch.minecraftHome, "config/hodgepodge.cfg"));
        Hodgepodge.log.info("Looking for Thermos/Bukkit taint.");
        try {
            Class.forName("org.bukkit.World");
            thermosTainted = true;
            Hodgepodge.log.warn(
                    "Thermos/Bukkit detected; This is an unsupported configuration -- Things may not function properly.");
            Hodgepodge.log.warn(
                    " Using `{}` for CraftServer Package.  If this is not correct, please update your config file!",
                    config.thermosCraftServerClass);
        } catch (ClassNotFoundException e) {
            thermosTainted = false;
            Hodgepodge.log.info("Thermos/Bukkit NOT detected :-D");
        }
    }

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
