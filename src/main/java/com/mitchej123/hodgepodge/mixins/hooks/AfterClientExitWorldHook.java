package com.mitchej123.hodgepodge.mixins.hooks;

import net.minecraft.client.Minecraft;

public final class AfterClientExitWorldHook {

    /**
     * This gets called when the client loads a null world, and the previous world was not null, e.g. when exiting to main menu
     */
    public static void run(Minecraft mc) {}
}
