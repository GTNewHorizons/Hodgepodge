package com.mitchej123.hodgepodge.core;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class LoadingConfig {
    public boolean fixNorthWestBias = true;
    public static Configuration config;

    public LoadingConfig(File file) {
        config = new Configuration(file);
        fixNorthWestBias = config.get("fixes", "fixNorthWestBias", true, "Fix northwest bias on RandomPositionGenerator").getBoolean();

        if (config.hasChanged())
            config.save();
    }
}
