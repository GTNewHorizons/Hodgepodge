package com.mitchej123.hodgepodge.mixins.early.memory;

import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.hooks.AfterServerStoppedHook;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer_ShutdownHook {

    @Inject(
            method = "run",
            remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;systemExitNow()V", remap = true))
    private void runShutdownHook(CallbackInfo ci) {
        AfterServerStoppedHook.run();
    }
}
