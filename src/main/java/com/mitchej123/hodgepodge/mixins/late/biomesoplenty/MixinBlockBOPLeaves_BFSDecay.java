package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.hooks.LeafDecayHooks;

import biomesoplenty.common.blocks.BlockBOPColorizedLeaves;
import biomesoplenty.common.blocks.BlockBOPLeaves;

@Mixin(value = { BlockBOPColorizedLeaves.class, BlockBOPLeaves.class })
public class MixinBlockBOPLeaves_BFSDecay {

    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$bfsDecay(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        LeafDecayHooks.handleDecayTick((Block) (Object) this, world, x, y, z, ci);
    }
}
