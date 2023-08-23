package com.mitchej123.hodgepodge.mixins.early.minecraft.packets;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.util.PacketPrevalidation;

@Mixin(S3FPacketCustomPayload.class)
public abstract class MixinS3FPacketCustomPayload_Validation extends Packet {

    /**
     * @author eigenraven
     * @reason Find out which custom payload channel identifier overflows a packet before it gets sent
     */
    @Inject(method = "<init>(Ljava/lang/String;[B)V", at = @At("TAIL"))
    private void hodgepodge$validateConstruction(String channelName, byte[] dataIn, CallbackInfo ci) {
        PacketPrevalidation.validateLimitedString(channelName, 20);
    }
}
