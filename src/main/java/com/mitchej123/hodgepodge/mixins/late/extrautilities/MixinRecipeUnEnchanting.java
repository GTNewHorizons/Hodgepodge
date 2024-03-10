package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import java.util.Map;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.rwtema.extrautils.crafting.RecipeUnEnchanting;

@Mixin(RecipeUnEnchanting.class)
public class MixinRecipeUnEnchanting {

    @Redirect(
            method = "getCraftingResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;setEnchantments(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)V"))
    public void onEnchantmentApplied(Map<Integer, Integer> map, ItemStack stack) {
        EnchantmentHelper.setEnchantments(map, stack);
        stack.stackSize = 1;
    }
}
