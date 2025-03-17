package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.rwtema.extrautils.item.ItemHealingAxe;

@Mixin(ItemHealingAxe.class)
abstract public class MixinItemHealingAxe {

    // The original method uses `getHealth() < getHealth()` which is the cause of the bug,
    // Replace the second getHealth call in the if statement with 0. (0 < getHealth())
    @Redirect(
            method = "onLeftClickEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLiving;getHealth()F", ordinal = 1))
    private float hodgepodge$redirectGetHealth(EntityLiving instance) {
        return 0F;
    }

    // Cancel the hunger reduction
    @Redirect(
            method = "onLeftClickEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;addExhaustion(F)V"))
    private void hodgepodge$redirectAddExhaustion(EntityPlayer player, float p_71020_1_) {

    }

    // Always deal the damage at the end of the method to avoid case where the player dies before
    // dealing damage to undead mobs
    @Inject(method = "onLeftClickEntity", at = @At("RETURN"), remap = false)
    private void hodgepodge$onLeftClickEntityEnd(ItemStack stack, EntityPlayer player, Entity entity,
            CallbackInfoReturnable<Boolean> cir, @Local float k) {
        if (player.getHealth() - (k - 0.5F) > 0F) {
            player.setHealth(player.getHealth() - (k - 0.5F));
        } else {
            player.attackEntityFrom(DamageSource.causePlayerDamage(player), (k - 0.5F));
        }
    }

}
