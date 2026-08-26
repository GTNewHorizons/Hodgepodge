package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerConfigurationManager.class)
public class MixinServerConfigurationManager_ImmediateKick {

    @Shadow
    public @Final List<EntityPlayerMP> playerEntityList;

    @Inject(method = "removeAllPlayers", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$removeAllPlayersFromSnapshot(CallbackInfo ci) {
        // Kicking now removes players from playerEntityList immediately, so iterate a snapshot to avoid skipping any.
        for (EntityPlayerMP player : this.playerEntityList.toArray(new EntityPlayerMP[0])) {
            player.playerNetServerHandler.kickPlayerFromServer("Server closed");
        }
        ci.cancel();
    }
}
