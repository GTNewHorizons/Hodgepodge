package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.rwtema.extrautils.item.ItemDivisionSigil;

@Mixin(ItemDivisionSigil.class)
public class MixinItemDivisionSigil {

    @Redirect(
            method = "onItemUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block checkIsSpecialBlock(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof BlockEnchantmentTable) return Blocks.enchanting_table;
        else if (block instanceof BlockBeacon) return Blocks.beacon;
        else return block;
    }
}
