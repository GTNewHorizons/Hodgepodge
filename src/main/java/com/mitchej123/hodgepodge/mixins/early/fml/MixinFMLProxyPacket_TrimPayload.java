package com.mitchej123.hodgepodge.mixins.early.fml;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.mixins.hooks.ProxyPacketPayload;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;

@Mixin(FMLProxyPacket.class)
public class MixinFMLProxyPacket_TrimPayload {

    @Redirect(
            method = { "toC17Packet", "toS3FPacket" },
            at = @At(value = "INVOKE", target = "Lio/netty/buffer/ByteBuf;array()[B", remap = false),
            remap = false,
            expect = 2)
    private byte[] hodgepodge$sendWrittenBytesOnly(ByteBuf payload) {
        return ProxyPacketPayload.withoutUnusedCapacity(payload);
    }
}
