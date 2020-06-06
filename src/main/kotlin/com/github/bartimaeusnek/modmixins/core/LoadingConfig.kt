package com.github.bartimaeusnek.modmixins.core

import net.minecraftforge.common.config.Configuration
import java.io.File

object LoadingConfig {

    fun loadConfig(file: File) {
        config = Configuration(file)
        fixRailcraftBoilerPollution = config["fixes", "fixNorthWestBias", true, "Fix northwest bias on RandomPositionGenerator"].boolean
        RailcraftJarName = config["jars", "Railcraft Jar Name","Railcraft_1.7.10-9.12.2.1.jar", "Name of the Railcraft Jar"].string
        if (config.hasChanged())
            config.save()
    }

    private lateinit var config: Configuration
    var fixRailcraftBoilerPollution : Boolean = false
    lateinit var RailcraftJarName : String
}