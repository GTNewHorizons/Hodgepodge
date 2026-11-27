package com.mitchej123.hodgepodge.mixins.early.minecraft.entity;

import java.util.ArrayList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_MergeMobDrops extends MixinEntity_MergeMobDrops {

    @WrapOperation(
            method = "onDeath",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/common/ForgeHooks;onLivingDrops(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;Ljava/util/ArrayList;IZI)Z",
                    remap = false))
    private boolean cacheEventDrops(EntityLivingBase entity, DamageSource source, ArrayList<EntityItem> drops,
            int lootingLevel, boolean recentlyHit, int specialDropValue, Operation<Boolean> original) {
        final ArrayList<EntityItem> eventDrops = new ArrayList<>();
        this.hp$eventDrops = eventDrops;
        final boolean canceled;
        try {
            canceled = original.call(entity, source, drops, lootingLevel, recentlyHit, specialDropValue);
        } finally {
            this.hp$eventDrops = null;
        }
        for (final EntityItem item : eventDrops) {
            this.worldObj.spawnEntityInWorld(item);
        }
        return canceled;
    }
}
