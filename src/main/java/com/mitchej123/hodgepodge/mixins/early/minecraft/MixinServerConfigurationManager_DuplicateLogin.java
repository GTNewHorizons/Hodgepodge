package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerConfigurationManager.class)
public class MixinServerConfigurationManager_DuplicateLogin {

    @Redirect(
            method = "createPlayerForUser",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/NetHandlerPlayServer;kickPlayerFromServer(Ljava/lang/String;)V"))
    private void hodgepodge$disconnectDuplicatePlayerImmediately(NetHandlerPlayServer handler, String reason) {
        handler.kickPlayerFromServer(reason);
        handler.onDisconnect(new ChatComponentText(reason));
    }
}
