package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

@Mixin(ItemGlassBottle.class)
public abstract class MixinItemGlassBottle {

    @Inject(
            method = "onItemRightClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getMaterial()Lnet/minecraft/block/material/Material;",
                    shift = At.Shift.BY,
                    by = 3))
    private void hodgepodge$removeWaterSourceBlock(ItemStack bottle, World world, EntityPlayer player,
            CallbackInfoReturnable<ItemStack> cri, @Local(ordinal = 0) MovingObjectPosition movingobjectposition) {
        int metadata = world.getBlockMetadata(
                movingobjectposition.blockX,
                movingobjectposition.blockY,
                movingobjectposition.blockZ);
        if (metadata == 0)
            world.setBlockToAir(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);
    }
}
