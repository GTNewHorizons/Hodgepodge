package com.mitchej123.hodgepodge.common.biomesoplenty.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.mitchej123.hodgepodge.common.biomesoplenty.blocks.BOPBlocks;

import biomesoplenty.api.content.BOPCBlocks;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeBuilder;

public class BOPCraftingGT {

    public static void addGTCraftingRecipes() {
        for (BOPBlocks.WoodTypes woodType : BOPBlocks.WoodTypes.values()) {

            // Fences
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(BOPBlocks.fences.get(woodType), 2),
                            "CDC",
                            "SRS",
                            "SRS",
                            'C',
                            "screwIron",
                            'D',
                            "craftingToolScrewdriver",
                            'R',
                            new ItemStack(BOPCBlocks.planks, 1, woodType.ordinal()),
                            'S',
                            "stickWood"));

            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(BOPBlocks.fences.get(woodType), 4),
                            "CDC",
                            "SRS",
                            "SRS",
                            'C',
                            "screwSteel",
                            'D',
                            "craftingToolScrewdriver",
                            'R',
                            new ItemStack(BOPCBlocks.planks, 1, woodType.ordinal()),
                            'S',
                            "stickWood"));

            // Fence Gates
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(BOPBlocks.fence_gates.get(woodType), 2),
                            "CDC",
                            "RSR",
                            "RSR",
                            'C',
                            "screwIron",
                            'D',
                            "craftingToolScrewdriver",
                            'R',
                            new ItemStack(BOPCBlocks.planks, 1, woodType.ordinal()),
                            'S',
                            "stickWood"));

            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(BOPBlocks.fence_gates.get(woodType), 4),
                            "CDC",
                            "RSR",
                            "RSR",
                            'C',
                            "screwSteel",
                            'D',
                            "craftingToolScrewdriver",
                            'R',
                            new ItemStack(BOPCBlocks.planks, 1, woodType.ordinal()),
                            'S',
                            "stickWood"));

            GTRecipeBuilder.builder().duration(15 * GTRecipeBuilder.SECONDS).eut(8)
                    .itemInputs(new ItemStack(Items.stick, 2), new ItemStack(BOPCBlocks.planks, 2, woodType.ordinal()))
                    .itemOutputs(new ItemStack(BOPBlocks.fence_gates.get(woodType), 1))
                    .addTo(RecipeMaps.assemblerRecipes);
        }
    }
}
