package com.mitchej123.hodgepodge.mixins.early.minecraft.entity;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity_MergeCapturedDrops {

    @Shadow(remap = false) // forge added fields
    public boolean captureDrops;

    @Shadow(remap = false) // forge added fields
    public ArrayList<EntityItem> capturedDrops;

    @Shadow
    public abstract EntityItem func_145778_a(Item itemIn, int size, float p_145778_3_);

    @Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
    private void mergeCapturedDrop(Item itemIn, int size, CallbackInfoReturnable<EntityItem> cir) {
        if (!this.captureDrops || itemIn == null || size <= 0) {
            return;
        }
        // reverse order since the last inserted drop is the most likely to match
        for (int i = this.capturedDrops.size() - 1; i >= 0; i--) {
            final EntityItem entity = this.capturedDrops.get(i);
            final ItemStack stack = entity.getEntityItem();
            if (stack != null && !stack.hasTagCompound() && stack.getItem() == itemIn && stack.getItemDamage() == 0) {
                // the most recent stack of this item is full, so the older ones are too
                if (stack.stackSize >= stack.getMaxStackSize()) {
                    return;
                }
                final int space = stack.getMaxStackSize() - stack.stackSize;
                if (size <= space) {
                    stack.stackSize += size;
                    cir.setReturnValue(entity);
                } else {
                    stack.stackSize += space;
                    cir.setReturnValue(this.func_145778_a(itemIn, size - space, 0.0F));
                }
                return;
            }
        }
    }
}
