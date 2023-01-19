package com.mitchej123.hodgepodge.mixins.late.journeymap;

import journeymap.common.version.VersionCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = VersionCheck.class, remap = false)
public class MixinVersionCheck {

    @Shadow
    private static volatile Boolean versionIsChecked;

    @Shadow
    private static volatile Boolean versionIsCurrent;

    @Shadow
    private static volatile String versionAvailable;

    /**
     * @author Alexdoru
     * @reason yeet
     */
    @Overwrite
    private static synchronized void checkVersion() {
        versionIsChecked = false;
        versionIsCurrent = true;
        versionAvailable = "0";
    }
}
