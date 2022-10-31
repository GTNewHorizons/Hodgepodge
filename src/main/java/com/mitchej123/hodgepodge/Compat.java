package com.mitchej123.hodgepodge;

import codechicken.nei.NEIClientConfig;
import cpw.mods.fml.common.Loader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/** This class cannot be used earlier than pre-init phase. */
public class Compat {
    private static boolean isNeiPresent;
    private static boolean doesNeiHaveBookmarkAPI;

    static void init() {
        isNeiPresent = Loader.isModLoaded("NotEnoughItems");
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

    public static boolean isNeiLeftPanelVisible() {
        return isNeiPresent
                && NEIClientConfig.isEnabled()
                && !NEIClientConfig.isHidden()
                && (!doesNeiHaveBookmarkAPI || !NEIClientConfig.isBookmarkPanelHidden());
    }
}
