package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.util.MobDropsContainerContext;

/*
 * Cancel subsequent calls leading to entityDropItem, which spawns EntityItem right away, and send an ItemStack to the
 * container class
 */
@Mixin(Entity.class)
public abstract class MixinEntity_MergeMobDrops {

    @Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
    public EntityItem hodgepodge$dropItem(Item item, int count, CallbackInfoReturnable<EntityItem> cir) {
        ItemStack stack = new ItemStack(item, count, 0);
        MobDropsContainerContext.add(stack);
        cir.setReturnValue(null);
        return null;
    }

    @Inject(method = "entityDropItem", at = @At("HEAD"), cancellable = true)
    public EntityItem hodgepodge$entityDropItem(ItemStack stack, float offsetY,
            CallbackInfoReturnable<EntityItem> cir) {
        MobDropsContainerContext.add(stack);
        cir.setReturnValue(null);
        return null;
    }
}
