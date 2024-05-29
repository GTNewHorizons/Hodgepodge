package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.BlockCauldron;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

@Mixin(BlockCauldron.class)
public abstract class MixinBlockCauldron {

    @Inject(
            method = "onBlockActivated",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
                    ordinal = 1,
                    shift = At.Shift.BY,
                    by = 3),
            cancellable = true)
    private void hodgepodge$returnIfCauldronNotFull(World world, int x, int y, int z, EntityPlayer player, int side,
            float subX, float subY, float subZ, CallbackInfoReturnable<Boolean> cir,
            @Local(ordinal = 5) int cauldronMetadata) {
        if (cauldronMetadata < 3) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @ModifyArg(
            method = "onBlockActivated",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockCauldron;func_150024_a(Lnet/minecraft/world/World;IIII)V",
                    ordinal = 1),
            index = 4)
    private int hodgepodge$consumeAllWater(int metadataToSet) {
        return 0;
    }
}
