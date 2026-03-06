package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading.blocks;

import net.minecraft.block.BlockMushroom;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Cancellable;

@Mixin(BlockMushroom.class)
public class MixinBlockMushroom {

    @ModifyVariable(method = "updateTick", at = @At(value = "STORE", ordinal = 0))
    private byte checkExist(byte b0, World worldIn, int x, int y, int z, @Cancellable CallbackInfo ci) {
        final int offset = 5;
        if (!worldIn.checkChunksExist(x - offset, y, z - offset, x + offset, y, z + offset)) {
            ci.cancel();
        }
        return b0;
    }
}
