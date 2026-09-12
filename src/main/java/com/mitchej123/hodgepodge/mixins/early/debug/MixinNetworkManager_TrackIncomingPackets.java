package com.mitchej123.hodgepodge.mixins.early.debug;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.commands.PacketStatsCommand;

import io.netty.channel.ChannelHandlerContext;

@Mixin(NetworkManager.class)
public class MixinNetworkManager_TrackIncomingPackets {

    @Final
    @Shadow
    private boolean isClientSide;

    @Inject(
            method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V",
            at = @At("HEAD"))
    private void hodgepodge$trackIncomingPacket(ChannelHandlerContext ctx, Packet packet, CallbackInfo ci) {
        if (isClientSide) {
            PacketStatsCommand.recordIncoming(packet);
        }
    }
}
