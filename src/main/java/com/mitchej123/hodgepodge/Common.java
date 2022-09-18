package com.mitchej123.hodgepodge;

import com.mitchej123.hodgepodge.core.LoadingConfig;
import java.io.File;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Common {
    public static final Logger log = LogManager.getLogger("Hodgepodge");
    public static LoadingConfig config;
    public static boolean thermosTainted;
    public static XSTR RNG = new XSTR();

    static {
        Common.log.info("Initializing Hodgepodge");
        Common.config = new LoadingConfig(new File(Launch.minecraftHome, "config/hodgepodge.cfg"));
        Common.log.info("Looking for Thermos/Bukkit taint.");
        try {
            Class.forName("org.bukkit.World");
            Common.thermosTainted = true;
            Common.log.warn(
                    "Thermos/Bukkit detected; This is an unsupported configuration -- Things may not function properly.");
            Common.log.warn(
                    " Using `{}` for CraftServer Package.  If this is not correct, please update your config file!",
                    Common.config.thermosCraftServerClass);
        } catch (ClassNotFoundException e) {
            Common.thermosTainted = false;
            Common.log.info("Thermos/Bukkit NOT detected :-D");
        }
    }
}
