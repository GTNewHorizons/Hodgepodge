package com.mitchej123.hodgepodge.mixins.late.glibysvoicechat;

import net.gliby.voicechat.common.VoiceChatServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VoiceChatServer.class)
public class MixinVoiceChatServer {

    @Redirect(method = "stop", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;stop()V"), remap = false)
    void hodgepodge$stopServerThread(Thread instance) {
        // A manual shutdown signal was already sent earlier, and Thread.stop crashes on newer Java versions
        // Force into daemon mode to allow a clean shutdown to happen.
        instance.setDaemon(true);
    }
}
