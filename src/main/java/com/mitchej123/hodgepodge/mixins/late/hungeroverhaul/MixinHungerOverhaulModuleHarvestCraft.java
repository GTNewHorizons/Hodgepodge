package com.mitchej123.hodgepodge.mixins.late.hungeroverhaul;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.pam.harvestcraft.ItemRegistry;

import iguanaman.hungeroverhaul.food.FoodModifier;
import iguanaman.hungeroverhaul.module.ModuleHarvestCraft;
import squeek.applecore.api.food.FoodValues;

@Mixin(ModuleHarvestCraft.class)
public class MixinHungerOverhaulModuleHarvestCraft {

    private static final Map<Item, FoodValues> FOOD_VALUES;

    static {
        FOOD_VALUES = new HashMap<>();
        FOOD_VALUES.put(ItemRegistry.coffeeItem, new FoodValues(2, 0.1F));
        FOOD_VALUES.put(ItemRegistry.chaiteaItem, new FoodValues(2, 0.1F));
    }

    @Redirect(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Liguanaman/hungeroverhaul/food/FoodModifier;setModifiedFoodValues(Lnet/minecraft/item/Item;Lsqueek/applecore/api/food/FoodValues;)V"),
            remap = false)
    private static void patchFoodValue(Item item, FoodValues foodValue) {
        FoodValues patchedFoodValue = FOOD_VALUES.get(item);
        FoodModifier.setModifiedFoodValues(item, patchedFoodValue != null ? patchedFoodValue : foodValue);
    }
}
