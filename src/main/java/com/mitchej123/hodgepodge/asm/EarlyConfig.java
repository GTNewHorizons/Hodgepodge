package com.mitchej123.hodgepodge.asm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import net.minecraft.launchwrapper.Launch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class EarlyConfig {

    private static final Logger LOGGER = LogManager.getLogger("HodgePodgeEarly");

    public static final boolean noNukeBaseMod;
    public static final boolean noLeanerForgeConfiguration;

    static {
        Properties config = new Properties();
        File configLocation = new File(Launch.minecraftHome, "config/hodgepodgeEarly.properties");
        try (Reader r = new BufferedReader(new FileReader(configLocation))) {
            config.load(r);
        } catch (FileNotFoundException e) {
            LOGGER.debug("No existing configuration file. Will use defaults");
        } catch (IOException e) {
            LOGGER.error("Error reading configuration file. Will use defaults", e);
        }
        noNukeBaseMod = Boolean.parseBoolean(config.getProperty("noNukeBaseMod"));
        config.setProperty("noNukeBaseMod", String.valueOf(noNukeBaseMod));
        noLeanerForgeConfiguration = Boolean.parseBoolean(config.getProperty("noLeanerForgeConfiguration"));
        config.setProperty("noLeanerForgeConfiguration", String.valueOf(noLeanerForgeConfiguration));
        try (Writer r = new BufferedWriter(new FileWriter(configLocation))) {
            config.store(r, "Configuration file for early hodgepodge class transformers");
        } catch (IOException e) {
            LOGGER.error("Error reading configuration file. Will use defaults", e);
        }
    }

    public static void ensureLoaded() {}
}
