package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ActiveRenderInfo.class)
public class MixinActiveRenderInfo_CameraRotation {

    /**
     * MC-46445: EntityRenderer.renderWorld calls updateRenderInfo with mc.thePlayer instead of mc.renderViewEntity, so
     * with any camera-detaching mod the particle billboard vectors are built from the player's angles while the camera
     * looks elsewhere. Positions stay correct (they come from the unprojected modelview matrix), only the orientation
     * is wrong. Forge fixed this in 1.12 by switching the callsite to getRenderViewEntity().
     */
    @Inject(method = "updateRenderInfo", at = @At("TAIL"))
    private static void hodgepodge$rotateFromRenderViewEntity(EntityPlayer entity, boolean frontView, CallbackInfo ci) {
        final Minecraft mc = Minecraft.getMinecraft();
        final EntityLivingBase view = mc.renderViewEntity;
        // Only correct the vanilla-style call that means "the camera". Callers that deliberately pass the player
        // (BetterQuesting's entity previews) are left alone whenever the camera is not detached.
        if (view == null || view == entity || entity != mc.thePlayer) {
            return;
        }

        // Same math as vanilla, with (1 - i * 2) collapsed into s.
        final float s = frontView ? -1.0F : 1.0F;
        final float yaw = view.rotationYaw * (float) Math.PI / 180.0F;
        final float pitch = view.rotationPitch * (float) Math.PI / 180.0F;
        ActiveRenderInfo.rotationX = MathHelper.cos(yaw) * s;
        ActiveRenderInfo.rotationZ = MathHelper.sin(yaw) * s;
        ActiveRenderInfo.rotationYZ = -ActiveRenderInfo.rotationZ * MathHelper.sin(pitch) * s;
        ActiveRenderInfo.rotationXY = ActiveRenderInfo.rotationX * MathHelper.sin(pitch) * s;
        ActiveRenderInfo.rotationXZ = MathHelper.cos(pitch);
    }
}
