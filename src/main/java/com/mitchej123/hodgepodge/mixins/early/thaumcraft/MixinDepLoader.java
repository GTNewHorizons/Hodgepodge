package com.mitchej123.hodgepodge.mixins.early.thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import thaumcraft.codechicken.core.launch.DepLoader;

@Mixin(DepLoader.class)
public class MixinDepLoader {

    /**
     * @author boubou19
     * @reason We don't want bauble to be downloaded by thaumcraft anymore
     *
     */
    @Overwrite(remap = false)
    public static void load() {
        // no-op
    }
}
