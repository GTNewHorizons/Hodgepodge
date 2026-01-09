package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.common.biomesoplenty.blocks.BOPBlocks;

import biomesoplenty.api.content.BOPCBlocks;
import biomesoplenty.common.core.BOPCrafting;
import cpw.mods.fml.common.registry.GameRegistry;

@Mixin(value = BOPCrafting.class, remap = false)
public class MixinBOPCrafting {

    @Inject(method = "addCraftingRecipes", at = @At("RETURN"))
    private static void addCraftingRecipes(CallbackInfo ci) {
        for (BOPBlocks.WoodTypes woodType : BOPBlocks.WoodTypes.values()) {
            // Fences
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(BOPBlocks.fences.get(woodType), 3),
                            "RSR",
                            "RSR",
                            'R',
                            new ItemStack(BOPCBlocks.planks, 1, woodType.ordinal()),
                            'S',
                            "stickWood"));

            // Fence Gates
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(BOPBlocks.fence_gates.get(woodType), 1),
                            "SRS",
                            "SRS",
                            'R',
                            new ItemStack(BOPCBlocks.planks, 1, woodType.ordinal()),
                            'S',
                            "stickWood"));
        }
    }
}
