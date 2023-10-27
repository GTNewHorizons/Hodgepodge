package com.mitchej123.hodgepodge.mixins.early.netty;

import java.net.InetAddress;
import java.net.SocketAddress;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

@Mixin(Bootstrap.class)
public class MixinBootstrap {

    @Inject(
            method = "checkAddress",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/net/InetAddress;getHostAddress()Ljava/lang/String;",
                    shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            cancellable = true)
    private void hodgpodge$fixNPE(SocketAddress remoteAddress, CallbackInfoReturnable<ChannelFuture> cir,
            InetAddress address) {
        if (address == null) {
            cir.setReturnValue(null);
        }
    }
}
