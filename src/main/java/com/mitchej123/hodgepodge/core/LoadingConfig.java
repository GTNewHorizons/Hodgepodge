package com.mitchej123.hodgepodge.core;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class LoadingConfig {
    public boolean fixNorthWestBias;
    public boolean fixGrassChunkLoads;
    public boolean fixFenceConnections;
    public boolean fixIc2DirectInventoryAccess;
    public static Configuration config;

    public LoadingConfig(File file) {
        config = new Configuration(file);
        fixNorthWestBias = config.get("fixes", "fixNorthWestBias", true, "Fix northwest bias on RandomPositionGenerator").getBoolean();
        fixGrassChunkLoads = config.get("fixes", "fixGrassChunkLoads", true, "Fix grass spreading from loading chunks").getBoolean();
        fixFenceConnections = config.get("fixes", "fixFenceConnections", true, "Fix fence connections with other types of fence").getBoolean();
        fixIc2DirectInventoryAccess = config.get("fixes", "fixIc2DirectInventoryAccess", true, "Fix IC2's direct inventory access").getBoolean();

        if (config.hasChanged())
            config.save();
    }
}
