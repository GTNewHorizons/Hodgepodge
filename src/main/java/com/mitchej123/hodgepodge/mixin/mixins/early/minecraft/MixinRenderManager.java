package com.mitchej123.hodgepodge.mixin.mixins.early.minecraft;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderManager.class)
public class MixinRenderManager {

    @Redirect(
            method = "renderDebugBoundingBox",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/AxisAlignedBB;getBoundingBox(DDDDDD)Lnet/minecraft/util/AxisAlignedBB;"))
    public AxisAlignedBB hodgepodge$fixDebugBoundingBox(double minX, double minY, double minZ, double maxX, double maxY,
            double maxZ, Entity entity, double p_85094_2_, double p_85094_4_, double p_85094_6_, float p_85094_8_,
            float p_85094_9_) {
        if (entity instanceof EntityPlayerSP) {
            final float offset = -1.62F + (entity.isSneaking() ? 0.08F : 0);
            return AxisAlignedBB.getBoundingBox(minX, minY + offset, minZ, maxX, maxY + offset, maxZ);
        }
        return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
