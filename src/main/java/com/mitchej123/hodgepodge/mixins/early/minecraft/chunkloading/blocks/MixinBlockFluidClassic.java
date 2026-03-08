package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading.blocks;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFluidClassic.class)
public class MixinBlockFluidClassic {

    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    private void checkExist(World world, int x, int y, int z, Random rand, CallbackInfo ci) {
        final int offset = 6;
        if (!world.checkChunksExist(x - offset, y, z - offset, x + offset, y, z + offset)) {
            ci.cancel();
        }
    }
}
