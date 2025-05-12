package com.mitchej123.hodgepodge.compat;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.Hodgepodge;

import cpw.mods.fml.common.Optional;
import vfyjxf.bettercrashes.utils.StateManager;

@Optional.Interface(modid = "angelica", iface = "vfyjxf.bettercrashes.utils.StateManager.IResettable")
public class BetterCrashesCompat implements StateManager.IResettable {

    private static BetterCrashesCompat INSTANCE;

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new BetterCrashesCompat();
        }
    }

    public BetterCrashesCompat() {
        Common.log.info("BetterCrashesCompat initialized");
        this.register();
    }

    @Override
    public void resetState() {
        Hodgepodge.IDSpeedupActive = false;
    }

}
