package com.mitchej123.hodgepodge.core.shared;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import net.minecraft.launchwrapper.Launch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class EarlyConfig {

    private static final Logger LOGGER = LogManager.getLogger("HodgepodgeEarly");

    public static final boolean noNukeBaseMod;
    public static final boolean noLeanerForgeConfiguration;
    public static final boolean noPrimitiveCallbackinfo;
    public static final boolean debugLogConfigParsingTimes;
    public static final boolean dumpASMClass;

    static {
        Properties config = new Properties();
        File configLocation = new File(Launch.minecraftHome, "config/hodgepodgeEarly.properties");

        if (configLocation.exists()) {
            try (Reader r = new BufferedReader(new FileReader(configLocation))) {
                config.load(r);
            } catch (IOException e) {
                LOGGER.error("Error reading configuration file. Will use defaults", e);
            }
        } else {
            LOGGER.debug("No existing configuration file. Will use defaults");
        }
        // =========== Config Definitions ===========
        noNukeBaseMod = Boolean.parseBoolean(config.getProperty("noNukeBaseMod"));
        config.setProperty("noNukeBaseMod", String.valueOf(noNukeBaseMod));
        noLeanerForgeConfiguration = Boolean.parseBoolean(config.getProperty("noLeanerForgeConfiguration"));
        config.setProperty("noLeanerForgeConfiguration", String.valueOf(noLeanerForgeConfiguration));
        noPrimitiveCallbackinfo = Boolean.parseBoolean(config.getProperty("noPrimitiveCallbackinfo"));
        config.setProperty("noPrimitiveCallbackinfo", String.valueOf(noPrimitiveCallbackinfo));
        debugLogConfigParsingTimes = Boolean.getBoolean("hodgepodge.logConfigTimes");
        dumpASMClass = Boolean.getBoolean("hodgepodge.dumpClass");
        // ==========================================

        // create config folder if it doesn't exist to prevent printed exception on first boot
        Path configDir = configLocation.toPath().getParent();
        if (!Files.exists(configDir)) {
            try {
                Files.createDirectory(configDir);
            } catch (IOException e) {
                LOGGER.error("Error creating configuration directory", e);
            }
        }

        try (Writer r = new BufferedWriter(new FileWriter(configLocation))) {
            config.store(r, "Configuration file for early hodgepodge class transformers");
        } catch (IOException e) {
            LOGGER.error("Error writing configuration file. Will use defaults", e);
        }
    }

    public static void ensureLoaded() {}
}
