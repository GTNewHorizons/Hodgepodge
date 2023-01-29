package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.Common;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer {

    @Unique
    private int itemEntityCounter;

    @Shadow
    protected abstract void collideWithPlayer(Entity p_71044_1_);

    @Inject(
            method = "onLivingUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getEntitiesWithinAABBExcludingEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;",
                    shift = At.Shift.AFTER))
    public void hodgepodge$resetItemCounter(CallbackInfo ci) {
        itemEntityCounter = 0;
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
    public void hodgepodge$ThrottleItemPickupEvent(EntityPlayer instance, Entity entity) {
        if (entity instanceof EntityItem) {
            if (itemEntityCounter < Common.config.itemStacksPickedUpPerTick) {
                this.collideWithPlayer(entity);
            }
            itemEntityCounter++;
            return;
        }
        this.collideWithPlayer(entity);
    }
}
