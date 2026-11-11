package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading.blocks;

import java.util.Random;

import net.minecraft.block.BlockMushroom;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockMushroom.class)
public class MixinBlockMushroom {

    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    private void checkExist(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
        final int offset = 5;
        if (!worldIn.checkChunksExist(x - offset, y, z - offset, x + offset, y, z + offset)) {
            ci.cancel();
        }
    }
}
