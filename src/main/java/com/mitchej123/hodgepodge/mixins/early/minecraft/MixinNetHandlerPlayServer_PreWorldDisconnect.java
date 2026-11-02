package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.LoginSessionState;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer_PreWorldDisconnect {

    @Shadow
    @Final
    public NetworkManager netManager;

    @Shadow
    @Final
    private MinecraftServer serverController;

    @Shadow
    public EntityPlayerMP playerEntity;

    @Inject(method = "onDisconnect", at = @At("HEAD"), cancellable = true, require = 1)
    private void hodgepodge$skipPreWorldLogout(CallbackInfo ci) {
        if (LoginSessionState.isPreWorldClose(this.netManager)
                && !this.serverController.getConfigurationManager().playerEntityList.contains(this.playerEntity)) {
            ci.cancel();
        }
    }
}
