package com.mitchej123.hodgepodge.mixins.early.minecraft.entity;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity_MergeMobDrops {

    @Shadow
    public World worldObj;

    @Shadow
    public double posX;

    @Shadow
    public double posY;

    @Shadow
    public double posZ;

    @Shadow(remap = false) // forge added fields
    public boolean captureDrops;

    @Shadow(remap = false) // forge added fields
    public ArrayList<EntityItem> capturedDrops;

    // we use a different list to buffer the items during the ForgeHooks.onLivingDrops event
    // because using the capturedDrops could create a ConcurrentModification exception among other issues
    @Unique
    protected ArrayList<EntityItem> hp$eventDrops;

    @Inject(method = "entityDropItem", at = @At("HEAD"), cancellable = true)
    private void mergeCapturedDrop(ItemStack itemStackIn, float offsetY, CallbackInfoReturnable<EntityItem> cir) {
        if (!this.captureDrops && this.hp$eventDrops == null) {
            return;
        }
        if (itemStackIn.stackSize <= 0 || itemStackIn.getItem() == null
                || itemStackIn.hasTagCompound()
                || !itemStackIn.isStackable()) {
            return;
        }
        final ArrayList<EntityItem> drops = this.captureDrops ? this.capturedDrops : this.hp$eventDrops;
        final double dropY = this.posY + (double) offsetY;
        // reverse order since the last inserted drop is the most likely to match
        for (int i = drops.size() - 1; i >= 0; i--) {
            final EntityItem entity = drops.get(i);
            if (entity.posX != this.posX || entity.posY != dropY || entity.posZ != this.posZ) {
                continue;
            }
            final ItemStack stack = entity.getEntityItem();
            if (stack != null && !stack.hasTagCompound()
                    && stack.getItem() == itemStackIn.getItem()
                    && stack.getItemDamage() == itemStackIn.getItemDamage()) {
                final int space = stack.getMaxStackSize() - stack.stackSize;
                if (itemStackIn.stackSize <= space) {
                    stack.stackSize += itemStackIn.stackSize;
                    cir.setReturnValue(entity);
                    return;
                }
                if (space > 0) {
                    stack.stackSize += space;
                    itemStackIn.stackSize -= space;
                }
                // the most recent stack of this item is now full, so the older ones are too
                break;
            }
        }
        if (!this.captureDrops) {
            // same as vanilla but added to our list instead of being spawned in the world
            final EntityItem entityItem = new EntityItem(this.worldObj, this.posX, dropY, this.posZ, itemStackIn);
            entityItem.delayBeforeCanPickup = 10;
            drops.add(entityItem);
            cir.setReturnValue(entityItem);
        }
    }
}
