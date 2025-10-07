package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

/**
 * Add guards to various World interaction methods to prevent them from loading chunks. Breaks vanilla behavior, but is
 * generally more desirable for performance and avoiding unintended chunkloads. Sibling mixin to
 * {@link MixinWorldServer_PreventChunkLoading}
 *
 * @author kuba6000
 */
@Mixin(World.class)
public abstract class MixinWorld_PreventChunkLoading {

    @Shadow
    public boolean isRemote;

    @Shadow
    abstract public boolean blockExists(int x, int y, int z);

    @Shadow
    abstract boolean chunkExists(int p_72916_1_, int p_72916_2_);

    @WrapOperation(
            method = "notifyBlockOfNeighborChange",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block hodgepodge$onNotifyBlockOfNeighborChange(World instance, int x, int y, int z,
            Operation<Block> original) {
        if (!this.isRemote && !blockExists(x, y, z)) {
            return Blocks.air;
        }
        return original.call(instance, x, y, z);
    }

    @WrapOperation(
            method = "getBlockLightValue_do",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block hodgepodge$onGetBlockLightValue_do_block(World instance, int x, int y, int z,
            Operation<Block> original) {
        if (!this.isRemote && !blockExists(x, y, z)) {
            return Blocks.air;
        }
        return original.call(instance, x, y, z);
    }

    @WrapOperation(
            method = "getBlockLightValue_do",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getChunkFromChunkCoords(II)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk hodgepodge$onGetBlockLightValue_do_chunk(World instance, int x, int y, Operation<Chunk> original) {
        if (((World) (Object) this) instanceof WorldServer worldServer && !chunkExists(x, y)) {
            return worldServer.theChunkProviderServer.defaultEmptyChunk;
        }
        return original.call(instance, x, y);
    }

    @WrapOperation(
            method = "getIndirectPowerLevelTo",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block hodgepodge$onGetIndirectPowerLevelTo(World instance, int x, int y, int z, Operation<Block> original) {
        if (!this.isRemote && !blockExists(x, y, z)) {
            return Blocks.air;
        }
        return original.call(instance, x, y, z);
    }

    @WrapOperation(
            method = "isBlockProvidingPowerTo",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block hodgepodge$onIsBlockProvidingPowerTo(World instance, int x, int y, int z, Operation<Block> original) {
        if (!this.isRemote && !blockExists(x, y, z)) {
            return Blocks.air;
        }
        return original.call(instance, x, y, z);
    }

    @WrapOperation(
            method = "func_147453_f",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block hodgepodge$notifyNeighborChange(World instance, int x, int y, int z, Operation<Block> original) {
        if (!this.isRemote && !this.blockExists(x, y, z)) {
            return Blocks.air;
        }
        return original.call(instance, x, y, z);
    }

}
