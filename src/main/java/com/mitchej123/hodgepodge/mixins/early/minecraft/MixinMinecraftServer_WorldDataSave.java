package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.util.WorldDataSaver;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer_WorldDataSave {

    // Run before serverStopped becomes true, including when stopServer throws during crash shutdown.
    // Stay after stopServer: ServerUtilities restores saving at its HEAD, then shutdown queues the final saves.
    @WrapOperation(
            method = "run",
            remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;stopServer()V", remap = true))
    private void hodgepodge$closeWorldData(MinecraftServer server, Operation<Void> original) {
        try {
            original.call(server);
        } finally {
            WorldDataSaver.INSTANCE.closeSession();
        }
    }
}
