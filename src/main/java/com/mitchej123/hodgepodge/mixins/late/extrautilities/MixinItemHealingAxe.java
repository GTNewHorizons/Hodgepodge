package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.rwtema.extrautils.item.ItemHealingAxe;

@Mixin(ItemHealingAxe.class)
public abstract class MixinItemHealingAxe {

    // The original method uses `getHealth() < getHealth()` which is the cause of the bug,
    // Replace the third getHealth call in the if statement with max health. (getHealth() < getMaxHealth())
    @Redirect(
            method = "onLeftClickEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLiving;getHealth()F", ordinal = 2))
    private float hodgepodge$redirectGetHealth(EntityLiving instance) {
        return instance.getMaxHealth();
    }
    
    // If some amount of healing is done, also damage the player
    @WrapOperation(
            method = "onLeftClickEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLiving;heal(F)V"))
    private void hodgepodge$onLeftClickHealEntity(EntityLiving entityToHeal, float healAmount, Operation<Void> original,
            @Local(argsOnly = true) EntityPlayer player) {
        if (player.getHealth() - (healAmount - 0.5F) > 0F) {
            player.setHealth(player.getHealth() - (healAmount - 0.5F));
        } else {
            player.attackEntityFrom(DamageSource.causePlayerDamage(player), (healAmount - 0.5F));
        }
        original.call(entityToHeal, healAmount);
    }
}
