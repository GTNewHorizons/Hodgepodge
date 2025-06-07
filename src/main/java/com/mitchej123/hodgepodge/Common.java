package com.mitchej123.hodgepodge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.mitchej123.hodgepodge.config.ASMConfig;

public class Common {

    public static final Logger log = LogManager.getLogger("Hodgepodge");
    public static final Marker securityMarker = MarkerManager.getMarker("SuspiciousPackets");
    public static boolean thermosTainted;
    public static XSTR RNG = new XSTR();

    static {
        Common.log.info("Initializing Hodgepodge");
        Common.log.info("Looking for Thermos/Bukkit taint.");
        try {
            Class.forName("org.bukkit.World");
            Common.thermosTainted = true;
            Common.log.warn(
                    "Thermos/Bukkit detected; This is an unsupported configuration -- Things may not function properly.");
            Common.log.warn(
                    " Using `{}` for CraftServer Package.  If this is not correct, please update your config file!",
                    ASMConfig.thermosCraftServerClass);
        } catch (ClassNotFoundException e) {
            Common.thermosTainted = false;
            Common.log.info("Thermos/Bukkit NOT detected :-D");
        }
    }
}
