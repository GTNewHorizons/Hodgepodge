package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import biomesoplenty.common.world.features.WorldGenBOPFlora;

@Mixin(WorldGenBOPFlora.class)
public class MixinWorldGenBOPFlora_CacheItemStack {

    @Shadow(remap = false)
    public Block flora;

    @Shadow(remap = false)
    public int floraMeta;

    @Unique
    private ItemStack hodgepodge$cachedStack;

    @Unique
    private Block hodgepodge$cachedFlora;

    @Unique
    private int hodgepodge$cachedMeta = -1;

    @Redirect(
            method = "generate",
            at = @At(value = "NEW", target = "(Lnet/minecraft/block/Block;II)Lnet/minecraft/item/ItemStack;"))
    private ItemStack hodgepodge$reuseItemStack(Block block, int amount, int meta) {
        if (hodgepodge$cachedStack == null || hodgepodge$cachedFlora != flora || hodgepodge$cachedMeta != floraMeta) {
            hodgepodge$cachedFlora = flora;
            hodgepodge$cachedMeta = floraMeta;
            hodgepodge$cachedStack = new ItemStack(block, amount, meta);
        }
        return hodgepodge$cachedStack;
    }
}
