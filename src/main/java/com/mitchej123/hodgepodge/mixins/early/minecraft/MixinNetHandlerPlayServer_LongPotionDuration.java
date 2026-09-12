package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S1DPacketEntityEffect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.S1DPacketEntityEffectExt;
import com.mitchej123.hodgepodge.net.MessagePotionDuration;
import com.mitchej123.hodgepodge.net.NetworkHandler;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer_LongPotionDuration {

    @Shadow
    public EntityPlayerMP playerEntity;

    /**
     * Follow up every effect packet whose duration did not fit in a short with the real duration. Clients without
     * Hodgepodge simply never receive it and keep the vanilla behaviour.
     */
    @Inject(method = "sendPacket", at = @At("RETURN"))
    private void hodgepodge$sendRealPotionDuration(Packet packetIn, CallbackInfo ci) {
        if (!(packetIn instanceof S1DPacketEntityEffect)) return;

        final MessagePotionDuration message = ((S1DPacketEntityEffectExt) packetIn).hodgepodge$getDurationMessage();
        if (message != null) {
            NetworkHandler.instance.sendTo(message, this.playerEntity);
        }
    }
}
