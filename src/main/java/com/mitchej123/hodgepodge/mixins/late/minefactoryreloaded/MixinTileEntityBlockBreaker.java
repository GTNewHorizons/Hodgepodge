package com.mitchej123.hodgepodge.mixins.late.minefactoryreloaded;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import powercrystals.minefactoryreloaded.tile.machine.TileEntityBlockBreaker;

@Mixin(TileEntityBlockBreaker.class)
public class MixinTileEntityBlockBreaker {

    @ModifyExpressionValue(
            at = @At(
                    target = "Lnet/minecraft/block/Block;getDrops(Lnet/minecraft/world/World;IIIII)Ljava/util/ArrayList;",
                    value = "INVOKE"),
            method = "activateMachine",
            remap = false)
    private ArrayList<ItemStack> hodgepodge$fireBlockHarvesting(ArrayList<ItemStack> drops,
            @Local(ordinal = 0) World world, @Local(ordinal = 0) Block block, @Local(ordinal = 0) int x,
            @Local(ordinal = 1) int y, @Local(ordinal = 2) int z, @Local(ordinal = 3) int meta) {
        ForgeEventFactory.fireBlockHarvesting(drops, world, block, x, y, z, meta, 0, 1.0f, false, null);
        return drops;
    }
}
