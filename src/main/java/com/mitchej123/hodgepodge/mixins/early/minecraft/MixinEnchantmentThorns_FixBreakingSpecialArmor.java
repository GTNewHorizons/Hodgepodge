package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentThorns.class)
public class MixinEnchantmentThorns_FixBreakingSpecialArmor {

    /**
     * @author danyadev
     * @reason For example, when you're wearing an electric armor, you always expect it to discharge on damage, but
     *         Thorns enchantment always directly damages the armor, thus bypassing {@link ISpecialArmor#damageArmor}
     *         method. This can lead to destroying the armor when the durability is low enough
     * @see <a href="https://github.com/amadornes/MinecraftForge/commit/894efe7222f309dc5ccbe248e789232f2bba276f">A
     *      similar fix in 1.12.2 Forge</a>
     */
    @Redirect(

            method = "func_151367_b(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/Entity;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;damageItem(ILnet/minecraft/entity/EntityLivingBase;)V"))
    private void hodgepodge$damageArmor(ItemStack stack, int amount, EntityLivingBase wearer) {
        if (stack != null && stack.getItem() instanceof ISpecialArmor armor) {
            int slot = -1;

            for (int i = 0; i <= 3; i++) {
                if (wearer.getEquipmentInSlot(i + 1) == stack) {
                    slot = i;
                    break;
                }
            }

            if (slot != -1) {
                DamageSource src = DamageSource.causeThornsDamage(wearer);
                armor.damageArmor(wearer, stack, src, amount, slot);
                return;
            }
        }

        stack.damageItem(amount, wearer);
    }
}
