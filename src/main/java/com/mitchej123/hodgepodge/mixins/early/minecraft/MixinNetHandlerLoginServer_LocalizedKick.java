package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.IChatComponent;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.hooks.DisconnectMessageHooks;

import io.netty.util.concurrent.GenericFutureListener;

@Mixin(NetHandlerLoginServer.class)
public abstract class MixinNetHandlerLoginServer_LocalizedKick {

    @Shadow
    private static Logger logger;

    @Shadow
    public abstract String func_147317_d();

    /*
     * Same as the in game kick path, but for the ones sent while the player is still logging in (whitelist, full
     * server, login timeout).
     */
    @Inject(method = "func_147322_a", at = @At("HEAD"), cancellable = true, require = 1)
    private void hodgepodge$localizedLoginKick(String reason, CallbackInfo ci) {
        final IChatComponent message = DisconnectMessageHooks.localize(reason);
        if (message == null) {
            return;
        }
        ci.cancel();
        try {
            logger.info("Disconnecting " + this.func_147317_d() + ": " + reason);
            final NetHandlerLoginServer self = (NetHandlerLoginServer) (Object) this;
            self.field_147333_a.scheduleOutboundPacket(new S00PacketDisconnect(message), new GenericFutureListener[0]);
            self.field_147333_a.closeChannel(message);
        } catch (Exception e) {
            logger.error("Error whilst disconnecting player", e);
        }
    }
}
