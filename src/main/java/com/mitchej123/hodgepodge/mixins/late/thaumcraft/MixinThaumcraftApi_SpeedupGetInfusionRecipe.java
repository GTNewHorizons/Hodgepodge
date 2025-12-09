package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import java.util.List;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.InfusionRecipe;

@Mixin(value = ThaumcraftApi.class, priority = 100)
public abstract class MixinThaumcraftApi_SpeedupGetInfusionRecipe {

    /**
     * @author Alexdoru
     * @reason Speed things up, this method gets called millions of times during startup
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Overwrite(remap = false)
    public static InfusionRecipe getInfusionRecipe(ItemStack itemStack) {
        List recipes = ThaumcraftApi.getCraftingRecipes();
        final int size = recipes.size();
        for (int i = 0; i < size; i++) {
            if (recipes.get(i) instanceof InfusionRecipe infusion) {
                final Object recipeOutput = infusion.getRecipeOutput();
                if (recipeOutput instanceof ItemStack stack && stack.isItemEqual(itemStack)) {
                    return infusion;
                }
            }
        }
        return null;
    }
}
