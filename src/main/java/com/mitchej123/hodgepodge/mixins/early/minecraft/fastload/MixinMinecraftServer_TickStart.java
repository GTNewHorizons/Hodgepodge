package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.hooks.ChunkGenScheduler;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer_TickStart {

    @Inject(method = "tick", at = @At("HEAD"))
    private void hodgepodge$onTickStart(CallbackInfo ci) {
        ChunkGenScheduler.onServerTickStart();
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void hodgepodge$onTickEnd(CallbackInfo ci) {
        ChunkGenScheduler.enableChunkLoads();
    }
}
