package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading.blocks;

import java.util.Random;

import net.minecraft.block.BlockSapling;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// need our code to be injected before
// squeek.applecore.mixins.early.minecraft.BlockSaplingMixin
@Mixin(value = BlockSapling.class, priority = 900)
public class MixinBlockSapling {

    @Inject(
            method = "updateTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockBush;updateTick(Lnet/minecraft/world/World;IIILjava/util/Random;)V",
                    shift = At.Shift.AFTER),
            cancellable = true)
    private void checkExist(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
        final int offset = 1;
        if (!worldIn.checkChunksExist(x - offset, y, z - offset, x + offset, y, z + offset)) {
            ci.cancel();
        }
    }
}
