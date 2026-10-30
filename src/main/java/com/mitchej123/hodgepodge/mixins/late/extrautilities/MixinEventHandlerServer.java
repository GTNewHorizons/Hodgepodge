package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.rwtema.extrautils.EventHandlerServer;

@Mixin(EventHandlerServer.class)
public class MixinEventHandlerServer {

    @Redirect(
            method = "ActivationRitual",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;",
                    remap = true),
            remap = false)
    private Block getSpecialBlock(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof BlockEnchantmentTable) return Blocks.enchanting_table;
        else return block;
    }
}
