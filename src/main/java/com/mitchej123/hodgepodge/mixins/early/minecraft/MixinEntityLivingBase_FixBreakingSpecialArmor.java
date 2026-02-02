package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase_FixBreakingSpecialArmor {

    /**
     * @author danyadev
     * @reason For example, when you're wearing an electric armor, you always expect it to discharge on damage, but when
     *         something falls on your head it directly damages the helmet, thus bypassing
     *         {@link ISpecialArmor#damageArmor} method. This can lead to destroying the helmet when its durability is
     *         low enough
     * @see MixinEnchantmentThorns_FixBreakingSpecialArmor with similar fix
     */
    @Redirect(
            method = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;damageItem(ILnet/minecraft/entity/EntityLivingBase;)V"))
    private void hodgepodge$damageArmor(ItemStack stack, int armorDamageAmount, EntityLivingBase wearer,
            DamageSource damageSource, float attackDamageAmount) {
        if (stack != null && stack.getItem() instanceof ISpecialArmor armor) {
            armor.damageArmor(wearer, stack, damageSource, armorDamageAmount, 3);
            return;
        }

        stack.damageItem(armorDamageAmount, wearer);
    }
}
