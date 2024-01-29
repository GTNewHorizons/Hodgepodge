package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mitchej123.hodgepodge.config.FixesConfig;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer {

    @Shadow
    protected abstract void collideWithPlayer(Entity p_71044_1_);

    @Inject(
            method = "onLivingUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getEntitiesWithinAABBExcludingEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;",
                    shift = At.Shift.AFTER))
    public void hodgepodge$resetItemCounter(CallbackInfo ci,
            @Share("itemEntityCounter") LocalIntRef itemEntityCounter) {
        itemEntityCounter.set(0);
    }

    @Redirect(
            method = "onLivingUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/EntityPlayer;collideWithPlayer(Lnet/minecraft/entity/Entity;)V"),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/World;getEntitiesWithinAABBExcludingEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;")))
    public void hodgepodge$ThrottleItemPickupEvent(EntityPlayer instance, Entity entity,
            @Share("itemEntityCounter") LocalIntRef itemEntityCounter) {
        if (entity instanceof EntityItem) {
            if (itemEntityCounter.get() < FixesConfig.itemStacksPickedUpPerTick) {
                this.collideWithPlayer(entity);
            }
            itemEntityCounter.set(itemEntityCounter.get() + 1);
            return;
        }
        this.collideWithPlayer(entity);
    }
}
