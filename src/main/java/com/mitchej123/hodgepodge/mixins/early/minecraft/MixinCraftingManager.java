package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.config.SpeedupsConfig;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Mixin(CraftingManager.class)
public class MixinCraftingManager {

    @Shadow
    private List recipes;

    @Unique
    private int hp$cacheIndex = 0;

    @Inject(
            method = "findMatchingRecipe",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/crafting/IRecipe;getCraftingResult(Lnet/minecraft/inventory/InventoryCrafting;)Lnet/minecraft/item/ItemStack;",
                    shift = At.Shift.BEFORE))
    private void updateCachedRecipe(CallbackInfoReturnable<ItemStack> cir, @Local(ordinal = 1) int j) {
        // when we find a matching recipe, if it's too far from the start of the list,
        // we move it to the start of the list to make the next recipe matches faster
        if (j > SpeedupsConfig.recipeCacheSize) {
            final Object first = this.recipes.get(hp$cacheIndex);
            final Object match = this.recipes.get(j);
            this.recipes.set(hp$cacheIndex, match);
            this.recipes.set(j, first);
            hp$cacheIndex++;
            if (hp$cacheIndex == SpeedupsConfig.recipeCacheSize) hp$cacheIndex = 0;
        }
    }
}
