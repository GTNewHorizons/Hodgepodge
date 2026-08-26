package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer_DisconnectOnce {

    @Unique
    private boolean hodgepodge$disconnected;

    @Inject(method = "onDisconnect", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$disconnectOnce(IChatComponent reason, CallbackInfo ci) {
        // Duplicate-login cleanup runs before NetworkSystem observes the closed channel and invokes this again.
        if (this.hodgepodge$disconnected) {
            ci.cancel();
        } else {
            this.hodgepodge$disconnected = true;
        }
    }
}
