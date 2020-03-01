package com.mitchej123.hodgepodge.core;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class LoadingConfig {
    public boolean fixNorthWestBias = true;
    public boolean fixGrassChunkLoads = true;
    public boolean fixIc2DirectInventoryAccess =  true;
    public static Configuration config;

    public LoadingConfig(File file) {
        config = new Configuration(file);
        fixNorthWestBias = config.get("fixes", "fixNorthWestBias", true, "Fix northwest bias on RandomPositionGenerator").getBoolean();
        fixGrassChunkLoads = config.get("fixes", "fixGrassChunkLoads", true, "Fix grass spreading from loading chunks").getBoolean();
        fixIc2DirectInventoryAccess = config.get("fixes", "fixIc2DirectInventoryAccess", true, "Fix IC2's direct inventory access").getBoolean();

        if (config.hasChanged())
            config.save();
    }
}
