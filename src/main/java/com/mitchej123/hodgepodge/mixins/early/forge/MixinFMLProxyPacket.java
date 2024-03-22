package com.mitchej123.hodgepodge.mixins.early.forge;

import net.minecraft.network.Packet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.mixins.hooks.NetworkDispatcherFallbackLookup;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;

@Mixin(value = FMLProxyPacket.class)
public abstract class MixinFMLProxyPacket extends Packet {

    @Shadow(remap = false)
    private Side target;

    @ModifyArg(
            method = "processPacket",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/FMLLog;log(Lorg/apache/logging/log4j/Level;Ljava/lang/Throwable;Ljava/lang/String;[Ljava/lang/Object;)V",
                    ordinal = 1,
                    remap = false))
    public Throwable hodgepodge$captureThrowable(Throwable e,
            @Share("throwable") LocalRef<Throwable> throwableStorage) {
        throwableStorage.set(e);
        return e;
    }

    @Redirect(
            method = "processPacket",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/network/handshake/NetworkDispatcher;rejectHandshake(Ljava/lang/String;)V",
                    remap = false),
            expect = 2)
    public void hodgepodge$rejectHandshakeNullSafe(NetworkDispatcher dispatcher, String message,
            @Share("throwable") LocalRef<Throwable> throwableStorage) {
        // If on a client/integrated server, use the real exception message for a more useful error screen.
        // Keep the real exception masked on dedicated servers to avoid leaking private information.
        if (FMLCommonHandler.instance().getSide().isClient()
                && "A fatal error has occured, this connection is terminated".equals(message)) {
            final Throwable t = throwableStorage.get();
            message = "A fatal error has occurred during the network handshake, this connection is terminated. Check the log for details. The exception caught was: "
                    + t;
        }

        if (dispatcher == null) {
            dispatcher = NetworkDispatcherFallbackLookup.getFallbackDispatcher(this.target);
        }

        if (dispatcher == null) {
            // The absolute final fallback to avoid NPEs here
            Common.log.warn("NetworkDispatcher is null, skipping rejectHandshake to avoid NPE");
            Common.log.warn("The message would have been: {}", message);
        } else {
            dispatcher.rejectHandshake(message);
        }
    }

}
