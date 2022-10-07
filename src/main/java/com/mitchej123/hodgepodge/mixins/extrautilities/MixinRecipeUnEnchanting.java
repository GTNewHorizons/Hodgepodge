package com.mitchej123.hodgepodge.mixins.extrautilities;

import com.rwtema.extrautils.crafting.RecipeUnEnchanting;
import java.util.Map;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RecipeUnEnchanting.class)
public class MixinRecipeUnEnchanting {

    @Redirect(
            method = "func_77572_b",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/enchantment/EnchantmentHelper;setEnchantments(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)V"))
    public void onEnchantmentApplied(Map map, ItemStack stack) {
        EnchantmentHelper.setEnchantments(map, stack);
        stack.stackSize = 1;
    }
}
