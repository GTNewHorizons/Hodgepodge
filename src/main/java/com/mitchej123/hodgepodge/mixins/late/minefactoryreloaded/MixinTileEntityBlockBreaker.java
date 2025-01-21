package com.mitchej123.hodgepodge.mixins.late.minefactoryreloaded;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import powercrystals.minefactoryreloaded.tile.machine.TileEntityBlockBreaker;

@Mixin(TileEntityBlockBreaker.class)
public abstract class MixinTileEntityBlockBreaker {

    @WrapOperation(
            method = "activateMachine",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getDrops(Lnet/minecraft/world/World;IIIII)Ljava/util/ArrayList;"),
            remap = false)
    private ArrayList<ItemStack> hodgepodge$activateMachine$fireDropEvent(Block block, World world, int x, int y, int z,
            int metadata, int fortune, Operation<ArrayList<ItemStack>> original) {
        ArrayList<ItemStack> drops = original.call(block, world, x, y, z, metadata, fortune);
        ForgeEventFactory.fireBlockHarvesting(drops, world, block, 0, 1, 0, metadata, fortune, 1.0f, false, null);
        return drops;
    }
}
