package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.server.network.NetHandlerHandshakeTCP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * Forge's NetHandlerHandshakeTCP patch rejects logins while the server is still starting with a hardcoded English
 * ChatComponentText, so the message cannot be localized. Replicate the shouldAllowPlayerLogins() guard at HEAD and send
 * a ChatComponentTranslation instead, letting clients resolve it from their own lang file.
 */
@Mixin(NetHandlerHandshakeTCP.class)
public abstract class MixinNetHandlerHandshakeTCP_TranslatableKick {

    @Shadow
    @Final
    private NetworkManager field_147386_b;

    @Inject(method = "processHandshake", at = @At("HEAD"), cancellable = true, require = 1)
    private void hodgepodge$translatableStartingKick(C00Handshake packet, CallbackInfo ci) {
        if (!FMLCommonHandler.instance().shouldAllowPlayerLogins()) {
            IChatComponent message = new ChatComponentTranslation("hodgepodge.disconnect.server_starting");
            this.field_147386_b.scheduleOutboundPacket(new S00PacketDisconnect(message), new GenericFutureListener[0]);
            this.field_147386_b.closeChannel(message);
            ci.cancel();
        }
    }
}
