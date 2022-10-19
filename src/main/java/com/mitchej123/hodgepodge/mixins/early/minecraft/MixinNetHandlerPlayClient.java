package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.interfaces.IMixinCleanup;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S07PacketRespawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
    @Shadow
    private WorldClient clientWorldController;

    @Inject(method = "cleanup", at = @At("HEAD"))
    private void betterCleanup(CallbackInfo ci) {
        Common.log.info("MixinNetHandlerPlayClient::BetterCleanup");
        // Do a better job at cleaning up
        if (this.clientWorldController != null) {
            ((IMixinCleanup) this.clientWorldController).cleanup();
        }
    }

    @Inject(
            method = "handleRespawn",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/multiplayer/WorldClient;getScoreboard()Lnet/minecraft/scoreboard/Scoreboard;"))
    private void handleRespawnClearWorldClient(S07PacketRespawn packetRespawn, CallbackInfo ci) {
        Common.log.info("MixinNetHandlerPlayClient::handleRespawnClearWorldClient");
        if (this.clientWorldController != null) {
            ((IMixinCleanup) this.clientWorldController).cleanup();
        }
    }
}
