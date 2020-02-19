package com.mitchej123.hodgepodge.fixgrasschunkloads.mixins.block;

import net.minecraft.block.BlockGrass;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(BlockGrass.class)
public class MixinBlockGrass {
    @Inject(method = "updateTick", at = @At( value = "INVOKE", target="Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true)
    protected void onBlockSet(World worldIn, int x, int y, int z, Random random, CallbackInfo ci, int l, int i1, int j1, int k1) {
        // Exit early if the chunk isn't loaded and avoid a chunk load
        if (!worldIn.blockExists(i1, j1, k1))
            ci.cancel();

    }
}
