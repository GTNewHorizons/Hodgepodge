package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemReed;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

@Mixin(ItemReed.class)
public class MixinItemReed {

    /**
     * @reason Sugar Canes can be placed on top of "replacable" blocks, but only if you evaluate the condition through
     *         canPlaceEntityOnSide. This is because ItemReed::onItemUse doesn't call Block::isReplaceable unlike
     *         ItemBlock::onItemUse.
     */
    @ModifyVariable(
            method = "onItemUse",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"),
            ordinal = 0,
            remap = false)
    private Block onItemUseWithIsReplaceable(Block original, @Local(ordinal = 0, argsOnly = true) World world,
            @Local(ordinal = 0, argsOnly = true) int x, @Local(ordinal = 1, argsOnly = true) int y,
            @Local(ordinal = 2, argsOnly = true) int z) {
        // We return Blocks.tallgrass because it results in the same behavior as if Block::isReplaceable had been
        // called.
        return original.isReplaceable(world, x, y, z) ? Blocks.tallgrass : original;
    }
}
