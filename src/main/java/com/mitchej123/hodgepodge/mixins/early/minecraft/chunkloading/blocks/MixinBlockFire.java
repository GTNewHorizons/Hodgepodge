package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading.blocks;

import java.util.Random;

import net.minecraft.block.BlockFire;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFire.class)
public class MixinBlockFire {

    @Inject(
            method = "updateTick",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;",
                    ordinal = 0))
    private void checkExist(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
        final int offset = 2;
        if (!worldIn.checkChunksExist(x - offset, y - 1, z - offset, x + offset, y + 5, z + offset)) {
            ci.cancel();
        }
    }
}
