package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal_Pass0Blend {

    @Inject(method = "renderAllRenderLists(ID)V", at = @At("HEAD"))
    public void hodgepodge$preRenderLists(int renderPass, double partialTickTime, CallbackInfo ci) {
        if (renderPass == 0) {
            GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
        }
    }

    @Inject(method = "renderAllRenderLists(ID)V", at = @At("RETURN"))
    public void hodgepodge$postRenderLists(int renderPass, double partialTickTime, CallbackInfo ci) {
        if (renderPass == 0) {
            GL11.glPopAttrib();
        }
    }

    @ModifyConstant(method = "renderSortedRenderers(IIID)I", constant = @Constant(intValue = 1, ordinal = 1))
    public int hodgepodge$reverseSort(int constant, int startRenderer, int numRenderers, int renderPass,
            double partialTickTime) {
        return renderPass;
    }
}
