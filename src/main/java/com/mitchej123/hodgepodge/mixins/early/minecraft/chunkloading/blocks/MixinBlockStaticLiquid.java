package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading.blocks;

import java.util.Random;

import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockStaticLiquid.class)
public class MixinBlockStaticLiquid {

    @Inject(
            method = "updateTick",
            cancellable = true,
            at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 0, remap = false))
    private void checkExist(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
        final int offset = 3;
        if (!worldIn.checkChunksExist(x - offset, y, z - offset, x + offset, y, z + offset)) {
            ci.cancel();
        }
    }
}
