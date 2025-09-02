package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public abstract class MixinWorld_PreventChunkLoading {

    @Shadow
    public boolean isRemote;

    @Shadow
    abstract public boolean blockExists(int x, int y, int z);

    @Shadow
    abstract boolean chunkExists(int p_72916_1_, int p_72916_2_);

    @Redirect(
            method = "notifyBlockOfNeighborChange",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block hodgepodge$onNotifyBlockOfNeighborChange(World world, int x, int y, int z) {
        if (!this.isRemote && !blockExists(x, y, z)) {
            return Blocks.air;
        }
        return world.getBlock(x, y, z);
    }

    @Redirect(
            method = "getBlockLightValue_do",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block hodgepodge$onGetBlockLightValue_do_block(World world, int x, int y, int z) {
        if (!this.isRemote && !blockExists(x, y, z)) {
            return Blocks.air;
        }
        return world.getBlock(x, y, z);
    }

    @Redirect(
            method = "getBlockLightValue_do",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getChunkFromChunkCoords(II)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk hodgepodge$onGetBlockLightValue_do_chunk(World world, int x, int y) {
        if (((World) (Object) this) instanceof WorldServer worldServer && !chunkExists(x, y)) {
            return worldServer.theChunkProviderServer.defaultEmptyChunk;
        }
        return world.getChunkFromChunkCoords(x, y);
    }

    @Redirect(
            method = "getIndirectPowerLevelTo",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block hodgepodge$onGetIndirectPowerLevelTo(World world, int x, int y, int z) {
        if (!this.isRemote && !blockExists(x, y, z)) {
            return Blocks.air;
        }
        return world.getBlock(x, y, z);
    }

    @Redirect(
            method = "isBlockProvidingPowerTo",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block hodgepodge$onIsBlockProvidingPowerTo(World world, int x, int y, int z) {
        if (!this.isRemote && !blockExists(x, y, z)) {
            return Blocks.air;
        }
        return world.getBlock(x, y, z);
    }

    @Redirect(
            method = "func_147453_f",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block hodgepodge$notifyNeighborChange(World world, int x, int y, int z) {
        if (!this.isRemote && !this.blockExists(x, y, z)) {
            return Blocks.air;
        }
        return world.getBlock(x, y, z);
    }

}
