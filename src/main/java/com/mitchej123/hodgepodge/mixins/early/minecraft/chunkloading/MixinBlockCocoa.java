package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading;

import java.util.Random;

import net.minecraft.block.BlockCocoa;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// inject after
// "squeek.applecore.mixins.early.minecraft.BlockCocoaMixin"
// which overwrites the method
@Mixin(value = BlockCocoa.class, priority = 1100)
public class MixinBlockCocoa {

    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    private void checkExist(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
        final int offset = 1;
        if (!worldIn.checkChunksExist(x - offset, y, z - offset, x + offset, y, z + offset)) {
            ci.cancel();
        }
    }
}
