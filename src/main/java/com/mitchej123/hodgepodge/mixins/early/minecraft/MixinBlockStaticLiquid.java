package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockStaticLiquid.class)
public class MixinBlockStaticLiquid {

    @Redirect(
            method = "updateTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    public Block wrapperUpdateTickGetBlock(World world, int x, int y, int z) {
        if (!world.chunkExists(x >> 4, z >> 4)) {
            // return something that isn't air, but also doesn't block movement so that the loop continues
            return Blocks.grass;
        } else {
            return world.getBlock(x, y, z);
        }
    }

    @Redirect(method = "updateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isAirBlock(III)Z"))
    public boolean wrapperUpdateTickIsAirBlock(World world, int x, int y, int z) {
        if (!world.chunkExists(x >> 4, z >> 4)) {
            // return false so that this.isFlammable doesn't run
            return false;
        } else {
            return world.isAirBlock(x, y, z);
        }
    }

    @Redirect(
            method = "isFlammable",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    public Block wrapperIsFlammableGetBlock(World world, int x, int y, int z) {
        if (!world.chunkExists(x >> 4, z >> 4)) {
            // return something that isn't flammable so that the or's short circuit
            return Blocks.air;
        } else {
            return world.getBlock(x, y, z);
        }
    }
}
