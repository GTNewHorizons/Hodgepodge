package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Use regular lighting for arrows stuck in entities instead of item lighting.
 */
@Mixin(RendererLivingEntity.class)
public class MixinRendererLivingEntity {

    @Redirect(
            method = "renderArrowsStuckInEntity",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/RenderHelper;disableStandardItemLighting()V"))
    private void disableCorrectLighting() {
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    @Redirect(
            method = "renderArrowsStuckInEntity",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/RenderHelper;enableStandardItemLighting()V"))
    private void enableCorrectLighting() {
        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
