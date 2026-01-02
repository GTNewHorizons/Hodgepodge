package com.mitchej123.hodgepodge.common.biomesoplenty.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.mitchej123.hodgepodge.Compat;
import com.mitchej123.hodgepodge.common.biomesoplenty.blocks.BOPBlocks;

import biomesoplenty.api.content.BOPCBlocks;
import cpw.mods.fml.common.registry.GameRegistry;

public class BOPCrafting {

    public static void addRecipes() {
        for (BOPBlocks.WoodTypes woodType : BOPBlocks.WoodTypes.values()) {
            // Fences
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(BOPBlocks.fences.get(woodType), 1),
                            "SRS",
                            "SRS",
                            "SRS",
                            'R',
                            new ItemStack(BOPCBlocks.planks, 1, woodType.ordinal()),
                            'S',
                            "stickWood"));

            // Fence Gates
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(BOPBlocks.fence_gates.get(woodType), 1),
                            "F F",
                            "RSR",
                            "RSR",
                            'F',
                            new ItemStack(Items.flint, 1),
                            'R',
                            new ItemStack(BOPCBlocks.planks, 1, woodType.ordinal()),
                            'S',
                            "stickWood"));
        }

        if (Compat.isGT5Present()) BOPCraftingGT.addGTCraftingRecipes();
    }
}
