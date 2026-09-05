package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.hooks.DisconnectMessageHooks;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer_LocalizedKick {

    /*
     * Send known vanilla kick reasons as a translation component, so the client renders them in its own language.
     * Anything else is left to vanilla.
     */
    @Inject(method = "kickPlayerFromServer", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$localizedKick(String reason, CallbackInfo ci) {
        final IChatComponent message = DisconnectMessageHooks.localize(reason);
        if (message == null) {
            return;
        }
        ci.cancel();
        final NetworkManager manager = ((NetHandlerPlayServer) (Object) this).netManager;
        manager.scheduleOutboundPacket(
                new S40PacketDisconnect(message),
                new GenericFutureListener[] { new GenericFutureListener<Future<? super Void>>() {

                    @Override
                    public void operationComplete(Future<? super Void> future) {
                        manager.closeChannel(message);
                    }
                } });
        manager.disableAutoRead();
    }
}
