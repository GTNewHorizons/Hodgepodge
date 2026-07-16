package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.entity.item.EntityItemFrame;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItemFrame.class)
public class MixinRenderItemFrame_CullDistantContents {

    @Inject(
            method = "doRender(Lnet/minecraft/entity/item/EntityItemFrame;DDDFF)V",
            at = @At("HEAD"),
            cancellable = true)
    private void hodgepodge$cullDistantFrame(EntityItemFrame frame, double x, double y, double z, float yaw,
            float partialTicks, CallbackInfo ci) {
        if (frame.getDistanceSqToEntity(Minecraft.getMinecraft().renderViewEntity) >= 9216.0D) {
            ci.cancel();
        }
    }

    @Inject(method = "func_82402_b", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$cullDistantContents(EntityItemFrame frame, CallbackInfo ci) {
        if (frame.getDistanceSqToEntity(Minecraft.getMinecraft().renderViewEntity) >= 4096.0D) {
            ci.cancel();
        }
    }
}
