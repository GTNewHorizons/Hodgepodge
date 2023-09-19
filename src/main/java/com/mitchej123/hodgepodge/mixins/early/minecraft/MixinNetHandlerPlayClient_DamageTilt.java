package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient_DamageTilt {

    @Shadow
    private Minecraft gameController;

    @Inject(
            method = "handleEntityVelocity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void hodgepodge$fixDamageTilt(S12PacketEntityVelocity packetIn, CallbackInfo ci, Entity entity) {
        if (this.gameController.thePlayer == entity) {
            final EntityClientPlayerMP player = this.gameController.thePlayer;
            final double x = (double) packetIn.func_149411_d() / 8000.0D;
            final double z = (double) packetIn.func_149409_f() / 8000.0D;
            float f = (float) (StrictMath.atan2(player.motionZ - z, player.motionX - x) * (180D / Math.PI)
                    - (double) player.rotationYaw);
            if (Float.isFinite(f)) {
                player.attackedAtYaw = f;
            }
        }
    }

}
