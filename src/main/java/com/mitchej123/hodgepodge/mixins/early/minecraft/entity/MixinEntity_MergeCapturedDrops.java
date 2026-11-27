package com.mitchej123.hodgepodge.mixins.early.minecraft.entity;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity_MergeCapturedDrops {

    @Shadow
    public double posY;

    @Shadow(remap = false) // forge added fields
    public boolean captureDrops;

    @Shadow(remap = false) // forge added fields
    public ArrayList<EntityItem> capturedDrops;

    @Inject(method = "entityDropItem", at = @At("HEAD"), cancellable = true)
    private void mergeCapturedDrop(ItemStack itemStackIn, float offsetY, CallbackInfoReturnable<EntityItem> cir) {
        if (!this.captureDrops || itemStackIn.stackSize <= 0
                || itemStackIn.getItem() == null
                || itemStackIn.hasTagCompound()
                || !itemStackIn.isStackable()) {
            return;
        }
        final double dropY = this.posY + (double) offsetY;
        // reverse order since the last inserted drop is the most likely to match
        for (int i = this.capturedDrops.size() - 1; i >= 0; i--) {
            final EntityItem entity = this.capturedDrops.get(i);
            if (entity.posY != dropY) {
                continue;
            }
            final ItemStack stack = entity.getEntityItem();
            if (stack != null && !stack.hasTagCompound()
                    && stack.getItem() == itemStackIn.getItem()
                    && stack.getItemDamage() == itemStackIn.getItemDamage()) {
                // the most recent stack of this item is full, so the older ones are too
                if (stack.stackSize >= stack.getMaxStackSize()) {
                    return;
                }
                final int space = stack.getMaxStackSize() - stack.stackSize;
                if (itemStackIn.stackSize <= space) {
                    stack.stackSize += itemStackIn.stackSize;
                    cir.setReturnValue(entity);
                } else {
                    stack.stackSize += space;
                    itemStackIn.stackSize -= space;
                }
                return;
            }
        }
    }
}
