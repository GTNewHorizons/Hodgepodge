package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemGlassBottle;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(ItemGlassBottle.class)
public class MixinItemGlassBottle {

    @ModifyExpressionValue(
            method = "onItemRightClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block hodgepodge$checkWaterBlock(Block original) {
        // If the block is not a Water block, return something with a non-water material to fail the check
        if (original == Blocks.water) return original;
        return Blocks.cobblestone;
    }
}
