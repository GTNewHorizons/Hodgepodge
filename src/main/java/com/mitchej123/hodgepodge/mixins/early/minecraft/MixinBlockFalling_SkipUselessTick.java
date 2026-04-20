package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockFalling.class)
public class MixinBlockFalling_SkipUselessTick {

    @Redirect(
            method = "onBlockAdded",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;scheduleBlockUpdate(IIILnet/minecraft/block/Block;I)V"))
    private void hodgepodge$skipUselessTick(World world, int x, int y, int z, Block block, int delay) {
        if (BlockFalling.func_149831_e/* canFallBelow */(world, x, y - 1, z)) {
            world.scheduleBlockUpdate(x, y, z, block, delay);
        }
    }
}
