package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.rwtema.extrautils.item.ItemEthericSword;

@Mixin(ItemEthericSword.class)
public abstract class MixinItemEthericSword {

    @Redirect(
            method = "onBlockDestroyed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;damageItem(ILnet/minecraft/entity/EntityLivingBase;)V"))
    private void hodgepodge$preventDurabilityLoss(ItemStack stack, int amount, EntityLivingBase entity) {}
}
