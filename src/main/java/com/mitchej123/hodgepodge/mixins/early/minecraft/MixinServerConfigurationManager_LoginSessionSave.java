package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.mixins.interfaces.LoginSessionState;
import com.mitchej123.hodgepodge.util.LoginSessionIndex;

@Mixin(ServerConfigurationManager.class)
public class MixinServerConfigurationManager_LoginSessionSave {

    @Shadow
    @Final
    private MinecraftServer mcServer;

    @WrapOperation(
            method = { "playerLoggedIn", "respawnPlayer" },
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", remap = false),
            expect = 2)
    private boolean hodgepodge$indexPlayerAdded(List<EntityPlayerMP> players, Object entry,
            Operation<Boolean> original) {
        final boolean added = original.call(players, entry);
        if (added) {
            ((LoginSessionIndex.Provider) this.mcServer.func_147137_ag()).hodgepodge$getLoginSessionIndex()
                    .playerAdded((EntityPlayerMP) entry);
        }
        return added;
    }

    @WrapOperation(
            method = { "playerLoggedOut", "respawnPlayer" },
            at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z", remap = false),
            expect = 2)
    private boolean hodgepodge$indexPlayerRemoved(List<EntityPlayerMP> players, Object entry,
            Operation<Boolean> original) {
        final boolean removed = original.call(players, entry);
        if (removed) {
            ((LoginSessionIndex.Provider) this.mcServer.func_147137_ag()).hodgepodge$getLoginSessionIndex()
                    .playerRemoved((EntityPlayerMP) entry);
        }
        return removed;
    }

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
