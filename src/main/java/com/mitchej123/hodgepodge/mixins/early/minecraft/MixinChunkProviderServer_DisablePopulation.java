package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer_DisablePopulation {

    @Redirect(
            at = @At(
                    target = "Lnet/minecraft/world/chunk/IChunkProvider;populate(Lnet/minecraft/world/chunk/IChunkProvider;II)V",
                    value = "INVOKE"),
            method = "populate(Lnet/minecraft/world/chunk/IChunkProvider;II)V")
    private void hodgepodge$ignoreChunkPopulation(IChunkProvider chunkProvider, IChunkProvider chunkProvider2, int x,
            int z) {}

}
