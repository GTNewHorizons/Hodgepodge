package com.mitchej123.hodgepodge.mixins.early.minecraft.difficulty;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.interfaces.IWorldDifficulty;
import com.mitchej123.hodgepodge.net.MessageServerDifficulty;
import com.mitchej123.hodgepodge.net.NetworkHandler;

@Mixin(ServerConfigurationManager.class)
public abstract class MixinServerConfigurationManager {

    @Inject(
            method = "initializeConnectionToPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/play/server/S3FPacketCustomPayload;<init>(Ljava/lang/String;[B)V"))
    private void onPlayerJoin(NetworkManager netManager, EntityPlayerMP player,
            NetHandlerPlayServer nethandlerplayserver, CallbackInfo ci, @Local WorldServer worldserver) {
        if (worldserver.getWorldInfo() instanceof IWorldDifficulty info) {
            NetworkHandler.instance
                    .sendTo(new MessageServerDifficulty(info.getDifficulty(), info.isDifficultyLocked()), player);
        }
    }
}
