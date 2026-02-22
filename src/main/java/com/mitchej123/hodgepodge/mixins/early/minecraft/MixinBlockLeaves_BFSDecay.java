package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.hooks.LeafDecayHooks;

@Mixin(BlockLeaves.class)
public class MixinBlockLeaves_BFSDecay {

    /*
     * Replaces vanilla flood-fill based decay with a BFS early exit approach. Injects after vanilla's isRemote and
     * meta-bit checks so CallbackInfo is only allocated when decay actually needs processing. Should work with BugTorch
     */
    @Inject(
            method = "updateTick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/block/BlockLeaves;field_150128_a:[I", ordinal = 0),
            cancellable = true)
    private void hodgepodge$bfsDecay(World world, int x, int y, int z, Random random, CallbackInfo ci,
            @Local(ordinal = 0) int l, @Local(ordinal = 0) byte b0) {
        LeafDecayHooks.handleDecayChecked((Block) (Object) this, world, x, y, z, l, b0, ci);
    }
}
