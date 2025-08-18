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

    private static boolean isThaumcraftPresent;

    private static boolean isThaumicBasesPresent;

    private static boolean isRailcraftPresent;

    private static boolean isGalacticraftPresent;
    private static boolean isBiomesOPlentyPresent;
    private static boolean isDreamcraftPresent;
    private static boolean isCoreTweaksPresent;
    private static boolean isKleeSlabsPresent;
    private static boolean isSFMPresent;
    private static boolean isTravellersGearPresent;
    private static boolean isIC2Present;

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
            } catch (Exception ignored) {}
        }

        isGT5Present = Loader.isModLoaded("gregtech") && !Loader.isModLoaded("gregapi");

        isIC2CropPluginPresent = Loader.isModLoaded("Ic2Nei");

        isThaumcraftPresent = Loader.isModLoaded("Thaumcraft");

        isThaumicBasesPresent = Loader.isModLoaded("thaumicbases");

        isRailcraftPresent = Loader.isModLoaded("Railcraft");

        isGalacticraftPresent = Loader.isModLoaded("GalacticraftCore");

        isBiomesOPlentyPresent = Loader.isModLoaded("BiomesOPlenty");

        isDreamcraftPresent = Loader.isModLoaded("dreamcraft");

        isCoreTweaksPresent = Loader.isModLoaded("coretweaks");

        isKleeSlabsPresent = Loader.isModLoaded("kleeslabs");

        isSFMPresent = Loader.isModLoaded("StevesFactoryManager");

        isTravellersGearPresent = Loader.isModLoaded("TravellersGear");

        isIC2Present = Loader.isModLoaded("IC2");
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

    /**
     * Cannot be used before pre-init phase.
     */
    public static boolean isThaumcraftPresent() {
        return isThaumcraftPresent;
    }

    /**
     * Cannot be used before pre-init phase.
     */
    public static boolean isThaumicBasesPresent() {
        return isThaumicBasesPresent;
    }

    /**
     * Cannot be used before pre-init phase.
     */
    public static boolean isRailcraftPresent() {
        return isRailcraftPresent;
    }

    /**
     * Cannot be used before pre-init phase.
     */
    public static boolean isGalacticraftPresent() {
        return isGalacticraftPresent;
    }

    /**
     * Cannot be used before pre-init phase.
     */
    public static boolean isBiomesOPlentyPresent() {
        return isBiomesOPlentyPresent;
    }

    /**
     * Cannot be used before pre-init phase.
     */
    public static boolean isDreamcraftPresent() {
        return isDreamcraftPresent;
    }

    /**
     * Cannot be used before pre-init phase.
     */
    public static boolean isCoreTweaksPresent() {
        return isCoreTweaksPresent;
    }

    /**
     * Cannot be used before pre-init phase.
     */
    public static boolean isKleeSlabsPresent() {
        return isKleeSlabsPresent;
    }

    /**
     * Cannot be used before pre-init phase.
     */
    public static boolean isSFMPresent() {
        return isSFMPresent;
    }

    /**
     * Cannot be used before pre-init phase.
     */
    public static boolean isTravellersGearPresent() {
        return isTravellersGearPresent;
    }

    /**
     * Cannot be used before pre-init phase.
     */
    public static boolean isIC2Present() {
        return isIC2Present;
    }
}
