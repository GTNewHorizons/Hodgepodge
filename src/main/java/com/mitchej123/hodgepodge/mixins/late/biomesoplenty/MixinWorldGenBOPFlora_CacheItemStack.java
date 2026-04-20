package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import biomesoplenty.common.world.features.WorldGenBOPFlora;

@Mixin(WorldGenBOPFlora.class)
public class MixinWorldGenBOPFlora_CacheItemStack {

    @Unique
    private ItemStack hodgepodge$cachedStack;

    @Unique
    private Block hodgepodge$cachedBlock;

    @Unique
    private int hodgepodge$cachedMeta = -1;

    @Redirect(
            method = "generate",
            at = @At(value = "NEW", target = "(Lnet/minecraft/block/Block;II)Lnet/minecraft/item/ItemStack;"))
    private ItemStack hodgepodge$reuseItemStack(Block block, int amount, int meta) {
        if (hodgepodge$cachedStack == null || hodgepodge$cachedBlock != block || hodgepodge$cachedMeta != meta) {
            hodgepodge$cachedBlock = block;
            hodgepodge$cachedMeta = meta;
            hodgepodge$cachedStack = new ItemStack(block, amount, meta);
        }
        return hodgepodge$cachedStack;
    }
}
