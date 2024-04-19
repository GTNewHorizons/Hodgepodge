package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer_DisableModGeneration {

    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lcpw/mods/fml/common/registry/GameRegistry;generateWorld(IILnet/minecraft/world/World;Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;)V",
                    value = "INVOKE"),
            method = "populate(Lnet/minecraft/world/chunk/IChunkProvider;II)V")
    private void hodgepodge$disableModGeneration(int chunkX, int chunkZ, World world, IChunkProvider chunkProvider,
            IChunkProvider chunkGenerator) {}
}
