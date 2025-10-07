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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

/**
 * Prevent any random ticking blocks and entity ticking from loading chunks. Breaks vanilla behavior, but is generally
 * more desirable for performance and avoiding unintended chunkloads. Sibling mixin to
 * {@link MixinWorld_PreventChunkLoading}
 *
 * @author kuba6000
 */
@Mixin(WorldServer.class)
public abstract class MixinWorldServer_PreventChunkLoading extends World {

    @Shadow
    public ChunkProviderServer theChunkProviderServer;

    // prevent chunkloading on random ticks (crops for example)
    @WrapOperation(
            method = { "func_147456_g", "scheduleBlockUpdateWithPriority", "tickUpdates" },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;updateTick(Lnet/minecraft/world/World;IIILjava/util/Random;)V"))
    private void hodgepodge$onUpdateTick(Block instance, World worldIn, int x, int y, int z, Random random,
            Operation<Void> original) {
        if (!theChunkProviderServer.loadChunkOnProvideRequest) {
            original.call(instance, worldIn, x, y, z, random);
            return;
        }
        theChunkProviderServer.loadChunkOnProvideRequest = false;
        try {
            original.call(instance, worldIn, x, y, z, random);
        } finally {
            theChunkProviderServer.loadChunkOnProvideRequest = true;
        }
    }

    @WrapOperation(
            method = "updateEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;updateEntities()V"))
    private void hodgepodge$onUpdateEntities(WorldServer instance, Operation<Void> original) {
        if (!theChunkProviderServer.loadChunkOnProvideRequest) {
            original.call(instance);
            return;
        }
        theChunkProviderServer.loadChunkOnProvideRequest = false;
        try {
            original.call(instance);
        } finally {
            theChunkProviderServer.loadChunkOnProvideRequest = true;
        }
    }

    // for the compiler to shut up
    public MixinWorldServer_PreventChunkLoading(ISaveHandler p_i45368_1_, String p_i45368_2_, WorldProvider p_i45368_3_,
            WorldSettings p_i45368_4_, Profiler p_i45368_5_) {
        super(p_i45368_1_, p_i45368_2_, p_i45368_3_, p_i45368_4_, p_i45368_5_);
    }

}
