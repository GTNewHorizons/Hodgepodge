package com.mitchej123.hodgepodge.mixins.early.cofhcore;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import cofh.lib.util.helpers.BlockHelper;

@Mixin(BlockHelper.class)
public class MixinBlockHelper {

    @ModifyExpressionValue(
            at = @At(
                    target = "Lnet/minecraft/block/Block;getDrops(Lnet/minecraft/world/World;IIIII)Ljava/util/ArrayList;",
                    value = "INVOKE"),
            method = "breakBlock(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;IIILnet/minecraft/block/Block;IZZ)Ljava/util/List;",
            remap = false)
    private static ArrayList<ItemStack> hodgepodge$fireBlockHarvesting(ArrayList<ItemStack> drops, World world,
            EntityPlayer player, int x, int y, int z, Block block, int fortune, boolean var7, boolean silkTouch,
            @Local(ordinal = 6) int meta) {
        ForgeEventFactory.fireBlockHarvesting(drops, world, block, x, y, z, meta, fortune, 1.0f, silkTouch, player);
        return drops;
    }
}
