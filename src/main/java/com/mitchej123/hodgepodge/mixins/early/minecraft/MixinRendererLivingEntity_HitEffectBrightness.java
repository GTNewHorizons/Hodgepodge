package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RendererLivingEntity.class)
public class MixinRendererLivingEntity_HitEffectBrightness {

    @Inject(
            method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V",
            at = { @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V",
                    shift = At.Shift.AFTER,
                    ordinal = 0),
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/entity/RendererLivingEntity;inheritRenderPass(Lnet/minecraft/entity/EntityLivingBase;IF)I",
                            shift = At.Shift.AFTER) },
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/entity/RendererLivingEntity;shouldRenderPass(Lnet/minecraft/entity/EntityLivingBase;IF)I")))
    private void hodgepodge$applyDefaultBlendFunc(EntityLivingBase entity, double p_76986_2_, double p_76986_4_,
            double p_76986_6_, float p_76986_8_, float p_76986_9_, CallbackInfo ci) {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }
}
