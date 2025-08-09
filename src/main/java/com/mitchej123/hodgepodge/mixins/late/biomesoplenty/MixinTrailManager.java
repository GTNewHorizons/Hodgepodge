package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import biomesoplenty.common.utils.remote.TrailManager;

@Mixin(TrailManager.class)
public class MixinTrailManager {

    /**
     * @author Alexdoru
     * @reason Prevent blocking the main thread with an HTTP request
     */
    @Overwrite(remap = false)
    public static void retrieveTrails() {
        // do nothing
    }
}
