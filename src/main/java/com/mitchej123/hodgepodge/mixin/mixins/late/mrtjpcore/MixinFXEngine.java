package com.mitchej123.hodgepodge.mixin.mixins.late.mrtjpcore;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mrtjp.core.fx.FXEngine$;

@Mixin(FXEngine$.class)
public class MixinFXEngine {

    /**
     * @reason Lighting should be kept disabled at the end of RenderWorldLastEvent in 1.7.
     */
    @Inject(method = "renderParticles", at = @At("TAIL"), remap = false)
    private void hodgepodge$keepLightingDisabled(int dim, float frame, CallbackInfo ci) {
        GL11.glDisable(GL11.GL_LIGHTING);
    }
}
