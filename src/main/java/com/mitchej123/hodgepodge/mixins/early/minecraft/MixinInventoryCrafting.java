package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryCrafting.class)
public class MixinInventoryCrafting {

    @Shadow
    private ItemStack[] stackList;

    @Inject(method = "setInventorySlotContents", at = @At("HEAD"), cancellable = true)
    public void setInventorySlotContents(int index, ItemStack stack, CallbackInfo ci) {
        if (stack == null && this.stackList[index] == null) ci.cancel();
    }

}
