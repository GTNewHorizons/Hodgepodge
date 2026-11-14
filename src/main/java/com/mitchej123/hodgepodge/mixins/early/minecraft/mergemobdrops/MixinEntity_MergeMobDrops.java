package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.util.MobDropsContainerContext;

/*
 * If it's called inside EntityLivingBase.onDeath, cancel subsequent calls leading to spawnEntityInWorld(entityItem) and
 * send an ItemStack to the container class. See TweaksConfig.mergeMobDrops and Mixins.MERGE_MOB_DROPS for more info
 */
@Mixin(Entity.class)
public abstract class MixinEntity_MergeMobDrops {

    @Inject(method = "entityDropItem", at = @At("HEAD"), cancellable = true)
    public void hodgepodge$accumulateEntityDropItem(ItemStack stack, float offsetY,
            CallbackInfoReturnable<EntityItem> cir) {
        if (MobDropsContainerContext.isCollecting()) {
            MobDropsContainerContext.add(stack);
            cir.setReturnValue(null);
            return;
        }
    }
}
