package com.mitchej123.hodgepodge;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import codechicken.nei.NEIClientConfig;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;

public class Compat {

    private static boolean isClient;

    private static boolean isNeiPresent;
    private static boolean doesNeiHaveBookmarkAPI;

    private static boolean isGT5Present;

    private static boolean isIC2CropPluginPresent;

    static void init(Side side) {
        isClient = side == Side.CLIENT;

        isNeiPresent = Loader.isModLoaded("NotEnoughItems");
        if (isClient) {
            try {
                Method isBookmarkPanelHiddenMethod = Class.forName("codechicken.nei.NEIClientConfig")
                        .getMethod("isBookmarkPanelHidden");
                if (Modifier.isStatic(isBookmarkPanelHiddenMethod.getModifiers())
                        && isBookmarkPanelHiddenMethod.getReturnType().equals(boolean.class)) {
                    doesNeiHaveBookmarkAPI = true;
                }
            } catch (Exception e) {}
        }

        isGT5Present = Loader.isModLoaded("gregtech") && !Loader.isModLoaded("gregapi");

        isIC2CropPluginPresent = Loader.isModLoaded("Ic2Nei");
    }

    /**
     * Cannot be used before pre-init phase.
     */
    public static boolean isNeiLeftPanelVisible() {
        return isNeiPresent && isClient
                && NEIClientConfig.isEnabled()
                && !NEIClientConfig.isHidden()
                && (!doesNeiHaveBookmarkAPI || !NEIClientConfig.isBookmarkPanelHidden());
    }

    /**
     * Cannot be used before pre-init phase.
     */
    public static boolean isGT5Present() {
        return isGT5Present;
    }

    /**
     * Cannot be used before pre-init phase.
     */
    public static boolean isIC2CropPluginPresent() {
        return isIC2CropPluginPresent;
    }
}
