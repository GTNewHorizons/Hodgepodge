package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import biomesoplenty.common.world.features.WorldGenBOPTallGrass;

@Mixin(WorldGenBOPTallGrass.class)
public class MixinWorldGenBOPTallGrass_CacheItemStack {

    @Shadow(remap = false)
    private Block tallGrass;

    @Shadow(remap = false)
    private int tallGrassMetadata;

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
        if (hodgepodge$cachedStack == null || hodgepodge$cachedBlock != tallGrass
                || hodgepodge$cachedMeta != tallGrassMetadata) {
            hodgepodge$cachedBlock = tallGrass;
            hodgepodge$cachedMeta = tallGrassMetadata;
            hodgepodge$cachedStack = new ItemStack(block, amount, meta);
        }
        return hodgepodge$cachedStack;
    }
}
