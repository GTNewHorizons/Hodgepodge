package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.concurrent.TimeUnit;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.Common;

@Mixin(ServerConfigurationManager.class)
public class MixinServerConfigurationManager_DuplicateLogin {

    @Redirect(
            method = "createPlayerForUser",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/NetHandlerPlayServer;kickPlayerFromServer(Ljava/lang/String;)V"))
    private void hodgepodge$disconnectDuplicatePlayerImmediately(NetHandlerPlayServer handler, String reason) {
        NetworkManager connection = handler.func_147362_b();
        IChatComponent disconnectReason = new ChatComponentText(reason);

        handler.kickPlayerFromServer(reason);

        // The replacement login cannot proceed while the old session can still process packets. Kicking disables new
        // reads, but packets already in NetworkManager's inbound queue would otherwise survive the immediate save. Let
        // the kick packet's listener close the channel so the client receives the reason, but do not stall the server
        // indefinitely if a close callback blocks or fails.
        if (!connection.channel().closeFuture().awaitUninterruptibly(1L, TimeUnit.SECONDS)) {
            Common.log.warn("Timed out closing the duplicate player connection; forcing it closed");
            connection.channel().unsafe().closeForcibly();
        }

        // createPlayerForUser runs on the server thread for the new connection, so the old handler is no longer inside
        // its packet batch. It is now safe to save/remove it before the replacement player is created.
        handler.onDisconnect(disconnectReason);
    }
}
