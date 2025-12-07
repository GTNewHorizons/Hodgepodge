package com.mitchej123.hodgepodge.core.fml.hooks.fml;

import com.mitchej123.hodgepodge.mixins.hooks.NetworkDispatcherFallbackLookup;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandlerContext;

@SuppressWarnings("unused") // used from asm
public class FMLIndexedMessageToMessageCodecHook {

    /**
     * Early handshake FMLProxyPackets don't have the dispatcher field set to the channel dispatcher. This leads to a
     * NullPointerException in the packet error handling code, leading to a very confusing crash report. Here, we try to
     * extract the real dispatcher using various possible attributes, until resorting to guessing based on the current
     * thread.
     */
    public static void addMissingDispatcher(ChannelHandlerContext ctx, FMLProxyPacket proxy) {
        if (proxy.getDispatcher() == null) {
            NetworkDispatcher fallback = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
            if (fallback == null) {
                Side side = ctx.channel().attr(NetworkRegistry.CHANNEL_SOURCE).get();
                if (side == null) {
                    side = FMLCommonHandler.instance().getEffectiveSide();
                }
                fallback = NetworkDispatcherFallbackLookup.getFallbackDispatcher(side);
            }
            proxy.setDispatcher(fallback);
        }
    }
}
