package com.mitchej123.hodgepodge.mixins.early.cofhcore;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cofh.mod.updater.UpdateCheckThread;

@Mixin(value = UpdateCheckThread.class, remap = false)
public class MixinCoFHCoreUpdateCheck {

    /**
     * @author mitchej123
     * @reason Update URL is long since gone
     */
    @Overwrite
    public void run() {
        // Do nothing
    }

}
