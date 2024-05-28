package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import codechicken.lib.render.CCRenderState;

@Mixin(value = com.rwtema.extrautils.multipart.FullBrightMicroMaterial.Lighting.class, remap = false)
public class MixinFullBrightMicroMaterial {

    /**
     * @author DvDmanDT
     * @reason Fix rendering of Lapis caelestis microblocks.
     */
    @Overwrite
    public void operate() {
        CCRenderState.setBrightnessStatic(240);
    }
}
