package com.mitchej123.hodgepodge;

import codechicken.nei.NEIClientConfig;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/** This class cannot be used earlier than pre-init phase. */
public class Compat {
    private static boolean isClient;

    private static boolean isNeiPresent;
    private static boolean doesNeiHaveBookmarkAPI;

    static void init(Side side) {
        isClient = side == Side.CLIENT;

        isNeiPresent = Loader.isModLoaded("NotEnoughItems");
        if (isClient) {
            try {
                Method isBookmarkPanelHiddenMethod =
                        Class.forName("codechicken.nei.NEIClientConfig").getMethod("isBookmarkPanelHidden");
                if (Modifier.isStatic(isBookmarkPanelHiddenMethod.getModifiers())
                        && isBookmarkPanelHiddenMethod.getReturnType().equals(boolean.class)) {
                    doesNeiHaveBookmarkAPI = true;
                }
            } catch (Exception e) {
            }
        }
    }

    public static boolean isNeiLeftPanelVisible() {
        return isNeiPresent
                && isClient
                && NEIClientConfig.isEnabled()
                && !NEIClientConfig.isHidden()
                && (!doesNeiHaveBookmarkAPI || !NEIClientConfig.isBookmarkPanelHidden());
    }
}
