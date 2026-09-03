package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.management.ServerConfigurationManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mitchej123.hodgepodge.mixins.interfaces.LoginSessionState;

@Mixin(ServerConfigurationManager.class)
public class MixinServerConfigurationManager_LoginSessionSave {

    // Guard the call before subclass overrides can save, including the integrated server's host-player NBT.
    // saveAllPlayerData covers autosave, /save-all and shutdown; playerLoggedOut covers the final disconnect save.
    @WrapWithCondition(
            method = { "saveAllPlayerData", "playerLoggedOut" },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/management/ServerConfigurationManager;writePlayerData(Lnet/minecraft/entity/player/EntityPlayerMP;)V"),
            expect = 2)
    private boolean hodgepodge$allowPlayerSave(ServerConfigurationManager scm, EntityPlayerMP player) {
        final NetHandlerPlayServer handler = player.playerNetServerHandler;
        return handler == null || !LoginSessionState.isPlayerSaveBlocked(handler.func_147362_b());
    }
}
