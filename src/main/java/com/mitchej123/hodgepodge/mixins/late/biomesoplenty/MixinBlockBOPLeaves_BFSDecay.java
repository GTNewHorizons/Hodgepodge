package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.hooks.LeafDecayHooks;

import biomesoplenty.common.blocks.BlockBOPColorizedLeaves;
import biomesoplenty.common.blocks.BlockBOPLeaves;

@Mixin(value = { BlockBOPColorizedLeaves.class, BlockBOPLeaves.class })
public class MixinBlockBOPLeaves_BFSDecay {

    @Inject(
            method = "updateTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;checkChunksExist(IIIIII)Z"),
            cancellable = true)
    private void hodgepodge$bfsDecay(World world, int x, int y, int z, Random random, CallbackInfo ci,
            @Local(name = "meta") int meta, @Local(name = "b0") byte b0) {
        LeafDecayHooks.handleDecayChecked((Block) (Object) this, world, x, y, z, meta, b0, ci);
    }
}
