package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(ItemFood.class)
public abstract class MixinItemFood_DontConsumeCreative {

    @WrapWithCondition(
            method = "onEaten",
            at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemStack;stackSize:I", opcode = Opcodes.PUTFIELD))
    private boolean shouldReduceStack(ItemStack instance, int value, @Local(argsOnly = true) EntityPlayer player) {
        return !player.capabilities.isCreativeMode;
    }
}
