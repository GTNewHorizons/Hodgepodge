package com.mitchej123.hodgepodge.mixins.removeUpdateChecks.CoFHCore;

import cofh.mod.updater.UpdateCheckThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(UpdateCheckThread.class)
public class MixinCoFHCoreUpdateCheck {
    /**
     * @author mitchej123
     * //@reason Update URL is long since gone
     */
    @Overwrite(remap = false)
    public void run() {
        // Do nothing
    }
}
