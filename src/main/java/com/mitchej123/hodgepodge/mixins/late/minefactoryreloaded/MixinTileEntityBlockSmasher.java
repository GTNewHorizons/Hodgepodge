package com.mitchej123.hodgepodge.mixins.late.minefactoryreloaded;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import powercrystals.minefactoryreloaded.tile.machine.TileEntityBlockSmasher;
import powercrystals.minefactoryreloaded.world.SmashingWorld;

@Mixin(TileEntityBlockSmasher.class)
public class MixinTileEntityBlockSmasher {

    @Shadow(remap = false)
    private int _fortune = 0;

    @Shadow(remap = false)
    private SmashingWorld _smashingWorld;

    @ModifyExpressionValue(
            at = @At(
                    target = "powercrystals/minefactoryreloaded/world/SmashingWorld.smashBlock(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/Block;II)Ljava/util/ArrayList;",
                    value = "INVOKE"),
            method = "getOutput",
            remap = false)
    private ArrayList<ItemStack> hodgepodge$fireBlockHarvesting(ArrayList<ItemStack> drops, ItemStack lastInputStack,
            @Local(ordinal = 0) Block block, @Local(ordinal = 0) ItemBlock lastInputItem) {
        ForgeEventFactory.fireBlockHarvesting(
                drops,
                this._smashingWorld,
                block,
                0,
                1,
                0,
                lastInputItem.getMetadata(lastInputStack.getItemDamage()),
                this._fortune,
                1.0f,
                false,
                null);
        return drops;
    }
}
