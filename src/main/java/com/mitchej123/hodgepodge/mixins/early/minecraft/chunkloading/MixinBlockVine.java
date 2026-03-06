package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading;

import net.minecraft.block.BlockVine;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Cancellable;

@Mixin(BlockVine.class)
public class MixinBlockVine {

    @ModifyVariable(method = "updateTick", at = @At(value = "STORE", ordinal = 0))
    private byte checkExist(byte b0, World worldIn, int x, int y, int z, @Cancellable CallbackInfo ci) {
        if (!worldIn.checkChunksExist(x - b0, y - 1, z - b0, x + b0, y + 1, z + b0)) {
            ci.cancel();
        }
        return b0;
    }
}
