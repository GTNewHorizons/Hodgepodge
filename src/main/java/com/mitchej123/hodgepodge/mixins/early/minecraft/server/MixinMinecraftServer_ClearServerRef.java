package com.mitchej123.hodgepodge.mixins.early.minecraft.server;

import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer_ClearServerRef {

    @Shadow
    private static MinecraftServer mcServer;

    @Inject(
            method = "run",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;systemExitNow()V",
                    shift = At.Shift.BEFORE,
                    remap = true))
    private void clearServerRef(CallbackInfo ci) {
        mcServer = null;
    }
}
