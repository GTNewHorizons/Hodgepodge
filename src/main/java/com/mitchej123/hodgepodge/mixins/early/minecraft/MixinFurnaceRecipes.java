package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.gtnewhorizon.gtnhlib.util.map.ItemStackMap;
import com.mitchej123.hodgepodge.Common;

@Mixin(FurnaceRecipes.class)
public abstract class MixinFurnaceRecipes {

    /*
     * Speed up FurnaceRecipes.getSmeltingResult by: 1) Hijacking the constructor here to recreate the lists with a
     * replacement hash map and an ItemStack hashing strategy 2) No longer looping over every. single. recipe. in the
     * list and using the .get()
     */
    @Shadow
    private Map<ItemStack, ItemStack> smeltingList = new ItemStackMap<ItemStack>(false);

    @Shadow
    private Map<ItemStack, Float> experienceList = new ItemStackMap<Float>(false);

    /**
     * @author mitchej123
     * @reason Significantly faster Inspired by later versions of forge
     */
    @Overwrite
    public void func_151394_a /* addSmeltingRecipe */(ItemStack input, ItemStack stack, float experience) {
        if (getSmeltingResult(input) != null) {
            Common.log.debug(
                    "Overwriting smelting recipe for input: {} and output {} with {}",
                    input,
                    getSmeltingResult(input),
                    stack);
        }
        this.smeltingList.put(input, stack);
        this.experienceList.put(stack, experience);
    }

    /**
     * @author mitchej123
     * @reason Significantly faster
     */
    @Overwrite
    public ItemStack getSmeltingResult(ItemStack stack) {
        return (ItemStack) this.smeltingList.get(stack);
    }

    /**
     * @author mitchej123
     * @reason Significantly faster
     */
    @Overwrite
    public float func_151398_b /* getSmeltingExperience */(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return 0f;
        float exp = stack.getItem().getSmeltingExperience(stack);
        if (exp == -1) {
            exp = (float) (this.experienceList.getOrDefault(stack, 0f));
        }
        return exp;
    }
}
