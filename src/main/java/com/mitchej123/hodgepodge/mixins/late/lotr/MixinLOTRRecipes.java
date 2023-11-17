package com.mitchej123.hodgepodge.mixins.late.lotr;

import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import lotr.common.recipe.LOTRRecipes;

@Mixin(value = LOTRRecipes.class, remap = false)
public class MixinLOTRRecipes {

    /**
     * @author Mist475
     * @reason The speedupVanillaFurnace feature replaces the FurnaceRecipes {@link java.util.HashMap} with gtnhlib's
     *         {@link com.gtnewhorizon.gtnhlib.util.map.ItemStackMap}, lotr casts the map to a hashmap causing errors
     */
    @Overwrite(remap = false)
    public static void addSmeltingXPForItem(Item item, float xp) {
        try {
            // Get experienceList
            Map<ItemStack, Float> map = ObfuscationReflectionHelper
                    .getPrivateValue(FurnaceRecipes.class, FurnaceRecipes.smelting(), 2);
            map.put(new ItemStack(item, 1, 32767), xp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
