package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.storage.ISaveHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer_FixChunkLoading extends World {

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

    @Redirect(
            method = "updateEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;updateEntities()V"))
    private void hodgepodge$onUpdateEntities(World world) {
        theChunkProviderServer.loadChunkOnProvideRequest = false;
        try {
            super.updateEntities();
        } finally {
            theChunkProviderServer.loadChunkOnProvideRequest = true;
        }
    }

    // for the compiler to shut up
    public MixinWorldServer_FixChunkLoading(ISaveHandler p_i45368_1_, String p_i45368_2_, WorldProvider p_i45368_3_,
            WorldSettings p_i45368_4_, Profiler p_i45368_5_) {
        super(p_i45368_1_, p_i45368_2_, p_i45368_3_, p_i45368_4_, p_i45368_5_);
    }

}
