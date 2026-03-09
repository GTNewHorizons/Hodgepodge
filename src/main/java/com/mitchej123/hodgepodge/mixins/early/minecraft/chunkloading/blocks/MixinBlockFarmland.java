package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading.blocks;

import java.util.Random;

import net.minecraft.block.BlockFarmland;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFarmland.class)
public class MixinBlockFarmland {

    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    private void checkExist(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
        final int offset = 4;
        if (!worldIn.checkChunksExist(x - offset, y, z - offset, x + offset, y, z + offset)) {
            ci.cancel();
        }
    }
}
