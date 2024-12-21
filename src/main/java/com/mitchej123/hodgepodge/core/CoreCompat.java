package com.mitchej123.hodgepodge.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mitchej123.hodgepodge.config.TweaksConfig;

public class CoreCompat {

    private static final Logger LOOGGER = LogManager.getLogger("HodgepodgeCoreCompat");

    public static void disableCoretweaksConflictingMixins() {
        if (TweaksConfig.threadedWorldDataSaving) {
            LOOGGER.info("Disabled CoreTweaks conflicting mixin: enhanceMapStorageErrors");
            makamys.coretweaks.Config.enhanceMapStorageErrors.disable();
        }
    }

}
