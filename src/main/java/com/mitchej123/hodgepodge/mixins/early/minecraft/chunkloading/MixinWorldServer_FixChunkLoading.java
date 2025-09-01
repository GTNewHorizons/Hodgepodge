package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldServer.class)
public class MixinWorldServer_FixChunkLoading {

    @Shadow
    public ChunkProviderServer theChunkProviderServer;

    // prevent chunkloading on random ticks (crops for example)
    @Redirect(
            method = { "func_147456_g", "scheduleBlockUpdateWithPriority", "tickUpdates" },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;updateTick(Lnet/minecraft/world/World;IIILjava/util/Random;)V"))
    private void hodgepodge$onUpdateTick(Block block, World worldIn, int x, int y, int z, Random random) {
        theChunkProviderServer.loadChunkOnProvideRequest = false;
        try {
            block.updateTick(worldIn, x, y, z, random);
        } finally {
            theChunkProviderServer.loadChunkOnProvideRequest = true;
        }
    }

}
