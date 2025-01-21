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

import powercrystals.minefactoryreloaded.tile.machine.TileEntityBlockSmasher;
import powercrystals.minefactoryreloaded.world.SmashingWorld;

@Mixin(TileEntityBlockSmasher.class)
public abstract class MixinTileEntityBlockSmasher {

    @WrapOperation(
            method = "getOutput",
            at = @At(
                    value = "INVOKE",
                    target = "Lpowercrystals/minefactoryreloaded/world/SmashingWorld;smashBlock(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/Block;II)Ljava/util/ArrayList;"),
            remap = false)
    private ArrayList<ItemStack> hodgepodge$getOutput$fireDropEvent(SmashingWorld world, ItemStack stack, Block block,
            int metadata, int fortune, Operation<ArrayList<ItemStack>> original) {
        ArrayList<ItemStack> drops = original.call(world, stack, block, metadata, fortune);
        if (drops == null) {
            drops = block.getDrops((World) (Object) world, 0, 0, 0, metadata, fortune);
        }
        ForgeEventFactory.fireBlockHarvesting(
                drops,
                (World) (Object) world,
                block,
                0,
                1,
                0,
                metadata,
                fortune,
                1.0f,
                false,
                null);
        return drops;
    }
}
