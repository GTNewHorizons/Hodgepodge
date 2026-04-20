package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading.blocks;

import net.minecraft.block.BlockTorch;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

@Mixin(BlockTorch.class)
public class MixinBlockTorch {

    @WrapWithCondition(
            method = "updateTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockTorch;onBlockAdded(Lnet/minecraft/world/World;III)V"))
    private boolean checkExist(BlockTorch instance, World worldIn, int x, int y, int z) {
        final int offset = 1;
        return worldIn.checkChunksExist(x - offset, y, z - offset, x + offset, y, z + offset);
    }
}
