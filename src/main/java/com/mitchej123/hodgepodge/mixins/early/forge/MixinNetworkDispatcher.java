package com.mitchej123.hodgepodge.mixins.early.forge;

import java.lang.ref.WeakReference;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.server.management.ServerConfigurationManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mitchej123.hodgepodge.mixins.early.minecraft.NetworkManagerAccessor;
import com.mitchej123.hodgepodge.mixins.hooks.NetworkDispatcherFallbackLookup;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;

@Mixin(NetworkDispatcher.class)
public abstract class MixinNetworkDispatcher extends SimpleChannelInboundHandler<Packet>
        implements ChannelOutboundHandler {

    @Inject(method = "<init>(Lnet/minecraft/network/NetworkManager;)V", at = @At("RETURN"))
    public void hodgepodge$setClientFallback(NetworkManager manager, CallbackInfo ci) {
        NetworkDispatcherFallbackLookup.CLIENT_DISPATCHER = new WeakReference<>((NetworkDispatcher) (Object) this);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/network/NetworkManager;Lnet/minecraft/server/management/ServerConfigurationManager;)V",
            at = @At("RETURN"))
    public void hodgepodge$setServerFallback(NetworkManager manager, ServerConfigurationManager scm, CallbackInfo ci) {
        NetworkDispatcherFallbackLookup.SERVER_DISPATCHER = new WeakReference<>((NetworkDispatcher) (Object) this);
    }

    @ModifyReturnValue(method = "get", at = @At("RETURN"), remap = false)
    private static NetworkDispatcher hodgepodge$saferGet(NetworkDispatcher dispatcher, NetworkManager mgr) {
        if (dispatcher != null) {
            return dispatcher;
        }
        if (mgr != null) {
            NetworkManagerAccessor accessor = (NetworkManagerAccessor) mgr;
            return NetworkDispatcherFallbackLookup
                    .getFallbackDispatcher(accessor.hodgepodge$isClientSide() ? Side.CLIENT : Side.SERVER);
        }
        return NetworkDispatcherFallbackLookup.getFallbackDispatcher(FMLCommonHandler.instance().getEffectiveSide());
    }
}
