package com.mitchej123.hodgepodge.mixins.late.bfsleafdecay;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.hooks.LeafDecayHooks;

import biomesoplenty.common.blocks.BlockBOPColorizedLeaves;

@Mixin(BlockBOPColorizedLeaves.class)
public class MixinBlockBOPColorizedLeaves {

    @Inject(
            method = "updateTick",
            at = @At(
                    value = "FIELD",
                    target = "Lbiomesoplenty/common/blocks/BlockBOPColorizedLeaves;adjacentTreeBlocks:[I",
                    ordinal = 0,
                    opcode = Opcodes.GETFIELD,
                    remap = false),
            cancellable = true)
    private void hodgepodge$bfsDecay(World world, int x, int y, int z, Random random, CallbackInfo ci,
            @Local(name = "meta") int meta, @Local(name = "b0") byte range) {
        LeafDecayHooks.handleDecayChecked((Block) (Object) this, world, x, y, z, meta, range, ci);
    }
}
