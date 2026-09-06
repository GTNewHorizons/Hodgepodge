package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.entity.item.EntityItemFrame;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItemFrame.class)
public class MixinRenderItemFrame_CullDistantContents {

    // Compared with squared distances to avoid a square root: 64 * 64 means a 64-block radius.
    @Unique
    private static final double CONTENT_RENDER_DISTANCE_SQ = 64.0D * 64.0D;

    @Unique
    private static final double FRAME_RENDER_DISTANCE_SQ = 96.0D * 96.0D;

    @Inject(
            method = "doRender(Lnet/minecraft/entity/item/EntityItemFrame;DDDFF)V",
            at = @At("HEAD"),
            cancellable = true)
    private void hodgepodge$cullDistantFrame(EntityItemFrame frame, double x, double y, double z, float yaw,
            float partialTicks, CallbackInfo ci) {
        if (frame.getDistanceSqToEntity(Minecraft.getMinecraft().renderViewEntity) >= FRAME_RENDER_DISTANCE_SQ) {
            ci.cancel();
        }
    }

    @Inject(method = "func_82402_b", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$cullDistantContents(EntityItemFrame frame, CallbackInfo ci) {
        if (frame.getDistanceSqToEntity(Minecraft.getMinecraft().renderViewEntity) >= CONTENT_RENDER_DISTANCE_SQ) {
            ci.cancel();
        }
    }
}
