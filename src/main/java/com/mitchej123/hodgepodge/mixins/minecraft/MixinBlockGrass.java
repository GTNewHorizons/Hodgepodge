package com.mitchej123.hodgepodge.mixins.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockGrass.class)
public class MixinBlockGrass {

    @Redirect(
            method = "updateTick(Lnet/minecraft/world/World;IIILjava/util/Random;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    protected Block getBlock(World world, int x, int y, int z) {
        if (!world.blockExists(x, y, z)) return Blocks.air;

        return world.getBlock(x, y, z);
    }
}
