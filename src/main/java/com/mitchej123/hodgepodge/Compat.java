package com.mitchej123.hodgepodge;

import codechicken.nei.NEIClientConfig;
import cpw.mods.fml.common.Loader;

/** This class cannot be used earlier than pre-init phase. */
public class Compat {
    private static boolean isNeiPresent;

    static void init() {
        isNeiPresent = Loader.isModLoaded("NotEnoughItems");
    }

    public static boolean isNeiHidden() {
        return !isNeiPresent || NEIClientConfig.isHidden();
    }
}
