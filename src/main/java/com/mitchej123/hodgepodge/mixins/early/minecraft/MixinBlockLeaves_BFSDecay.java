package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.hooks.LeafDecayHooks;

@Mixin(BlockLeaves.class)
public class MixinBlockLeaves_BFSDecay {

    /*
     * Replaces vanilla flood-fill based decay with a BFS early exit approach. Note: Uses inject + cancel (Overwrite
     * equivalent) - slight overhead due to CallbackInfo allocation, but avoids crashes with BugTorch's mixins.
     */
    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$bfsDecay(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        LeafDecayHooks.handleDecayTick((Block) (Object) this, world, x, y, z, ci);
    }
}
