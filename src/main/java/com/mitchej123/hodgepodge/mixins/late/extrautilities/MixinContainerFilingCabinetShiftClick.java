package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Local;
import com.rwtema.extrautils.gui.ContainerFilingCabinet;

@Mixin(value = ContainerFilingCabinet.class)
public abstract class MixinContainerFilingCabinetShiftClick extends Container {
    // The original code created a temp ItemStack, but still modified the original ItemStack's size before checking if
    // the transfer could go through

    // modify temp ItemStack's size instead of original ItemStack
    @Redirect(
            method = "transferStackInSlot",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/item/ItemStack;stackSize:I",
                    opcode = Opcodes.PUTFIELD,
                    ordinal = 0))
    private void hodgepodge$modifyTempVariableStackSize(ItemStack itemstack1, int m,
            @Local(name = "itemstack") ItemStack itemstack) {
        itemstack.stackSize = m;
    }

    // use temp ItemStack in the merge function instead of the original
    @ModifyArg(
            method = "transferStackInSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/rwtema/extrautils/gui/ContainerFilingCabinet;mergeItemStack(Lnet/minecraft/item/ItemStack;IIZ)Z",
                    ordinal = 0))
    private ItemStack hodgepodge$mergeItemStackWithTempVariable(ItemStack itemstack1,
            @Local(name = "itemstack") ItemStack itemstack) {
        return itemstack;
    }

    // since mergeItemStack sets the size of the ItemStack to 0, use the outer "m" variable to decrease the original
    // ItemStack's size
    @Redirect(
            method = "transferStackInSlot",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/item/ItemStack;stackSize:I",
                    opcode = Opcodes.PUTFIELD,
                    ordinal = 1))
    private void hodgepodge$decreaseStackSizeCorrectly(ItemStack itemstack1, int m, @Local(name = "m") int m_outer) {
        itemstack1.stackSize -= m_outer;
    }
}
