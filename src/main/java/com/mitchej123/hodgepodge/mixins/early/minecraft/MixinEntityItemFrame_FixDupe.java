package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

/**
 * Fixes the vanilla item frame item duplication trick.
 * <p>
 * Ensure the item inside a frame get cleared instantly and not later so if it's called twice in same tick (ex: left
 * click + piston) it doesn't duplicate the item displayed.
 */
@Mixin(EntityItemFrame.class)
public class MixinEntityItemFrame_FixDupe {

    @WrapOperation(
            method = "func_146065_b",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/item/EntityItemFrame;getDisplayedItem()Lnet/minecraft/item/ItemStack;"))
    private ItemStack hodgepodge$captureAndClearDisplayedItem(EntityItemFrame self, Operation<ItemStack> original) {
        ItemStack stack = original.call(self);
        if (stack != null && !self.worldObj.isRemote) {
            self.setDisplayedItem(null);
        }
        return stack;
    }
}
