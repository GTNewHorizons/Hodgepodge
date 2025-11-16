package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import biomesoplenty.api.content.BOPCBlocks;
import com.mitchej123.hodgepodge.common.biomesoplenty.blocks.BOPBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static biomesoplenty.common.core.BOPCrafting.addRecipeToFront;

@Mixin(value = biomesoplenty.common.core.BOPCrafting.class, remap = false)
public class MixinBOPCrafting {
    @Inject(method = "addCraftingRecipes", at = @At("RETURN"))
    private static void addCraftingRecipes(CallbackInfo ci) {
        //Redwood
        addRecipeToFront(new ItemStack(BOPBlocks.redwoodFence, 4), "RSR", "RSR", "RSR", 'R', new ItemStack(BOPCBlocks.planks, 1, 8), 'S', new ItemStack(Items.stick, 1));
        addRecipeToFront(new ItemStack(BOPBlocks.redwoodFenceGate, 4), "RSR", "RSR", "RSR", 'R', new ItemStack(BOPCBlocks.planks, 1, 8), 'S', new ItemStack(Items.stick, 1));

        //Willow
        addRecipeToFront(new ItemStack(BOPBlocks.willowFence, 4), "RSR", "RSR", "RSR", 'R', new ItemStack(BOPCBlocks.planks, 1, 9), 'S', new ItemStack(Items.stick, 1));
        addRecipeToFront(new ItemStack(BOPBlocks.willowFenceGate, 4), "RSR", "RSR", "   ", 'R', new ItemStack(BOPCBlocks.planks, 1, 9), 'S', new ItemStack(Items.stick, 1));

        //Sacred Oak
        addRecipeToFront(new ItemStack(BOPBlocks.sacredoakFence, 4), "RSR", "RSR", "RSR", 'R', new ItemStack(BOPCBlocks.planks, 1, 0), 'S', new ItemStack(Items.stick, 1));
        addRecipeToFront(new ItemStack(BOPBlocks.sacredoakFenceGate, 4), "RSR", "RSR", "   ", 'R', new ItemStack(BOPCBlocks.planks, 1, 0), 'S', new ItemStack(Items.stick, 1));

        //Fir
        addRecipeToFront(new ItemStack(BOPBlocks.firFence, 4), "RSR", "RSR", "RSR", 'R', new ItemStack(BOPCBlocks.planks, 1, 3), 'S', new ItemStack(Items.stick, 1));
        addRecipeToFront(new ItemStack(BOPBlocks.firFenceGate, 4), "RSR", "RSR", "   ", 'R', new ItemStack(BOPCBlocks.planks, 1, 3), 'S', new ItemStack(Items.stick, 1));

        //Cherry
        addRecipeToFront(new ItemStack(BOPBlocks.cherryFence, 4), "RSR", "RSR", "RSR", 'R', new ItemStack(BOPCBlocks.planks, 1, 1), 'S', new ItemStack(Items.stick, 1));
        addRecipeToFront(new ItemStack(BOPBlocks.cherryFenceGate, 4), "RSR", "RSR", "   ", 'R', new ItemStack(BOPCBlocks.planks, 1, 1), 'S', new ItemStack(Items.stick, 1));

        //Dark
        addRecipeToFront(new ItemStack(BOPBlocks.darkFence, 4), "RSR", "RSR", "RSR", 'R', new ItemStack(BOPCBlocks.planks, 1, 2), 'S', new ItemStack(Items.stick, 1));
        addRecipeToFront(new ItemStack(BOPBlocks.darkFenceGate, 4), "RSR", "RSR", "   ", 'R', new ItemStack(BOPCBlocks.planks, 1, 2), 'S', new ItemStack(Items.stick, 1));

        //Magic
        addRecipeToFront(new ItemStack(BOPBlocks.magicFence, 4), "RSR", "RSR", "RSR", 'R', new ItemStack(BOPCBlocks.planks, 1, 5), 'S', new ItemStack(Items.stick, 1));
        addRecipeToFront(new ItemStack(BOPBlocks.magicFenceGate, 4), "RSR", "RSR", "   ", 'R', new ItemStack(BOPCBlocks.planks, 1, 5), 'S', new ItemStack(Items.stick, 1));

        //Palm
        addRecipeToFront(new ItemStack(BOPBlocks.palmFence, 4), "RSR", "RSR", "RSR", 'R', new ItemStack(BOPCBlocks.planks, 1, 7), 'S', new ItemStack(Items.stick, 1));
        addRecipeToFront(new ItemStack(BOPBlocks.palmFenceGate, 4), "RSR", "RSR", "   ", 'R', new ItemStack(BOPCBlocks.planks, 1, 7), 'S', new ItemStack(Items.stick, 1));

        //Mangrove
        addRecipeToFront(new ItemStack(BOPBlocks.mangroveFence, 4), "RSR", "RSR", "RSR", 'R', new ItemStack(BOPCBlocks.planks, 1, 6), 'S', new ItemStack(Items.stick, 1));
        addRecipeToFront(new ItemStack(BOPBlocks.mangroveFenceGate, 4), "RSR", "RSR", "   ", 'R', new ItemStack(BOPCBlocks.planks, 1, 6), 'S', new ItemStack(Items.stick, 1));

        //Ethereal
        addRecipeToFront(new ItemStack(BOPBlocks.etherealFence, 4), "RSR", "RSR", "RSR", 'R', new ItemStack(BOPCBlocks.planks,1,4), 'S', new ItemStack(Items.stick, 1));
        addRecipeToFront(new ItemStack(BOPBlocks.etherealFenceGate, 4), "RSR", "RSR", "   ", 'R', new ItemStack(BOPCBlocks.planks,1,4), 'S', new ItemStack(Items.stick, 1));

        //Pine
        addRecipeToFront(new ItemStack(BOPBlocks.pineFence, 4), "RSR", "RSR", "RSR", 'R', new ItemStack(BOPCBlocks.planks, 1, 11), 'S', new ItemStack(Items.stick, 1));
        addRecipeToFront(new ItemStack(BOPBlocks.pineFenceGate, 4), "RSR", "RSR", "   ", 'R', new ItemStack(BOPCBlocks.planks, 1, 11), 'S', new ItemStack(Items.stick, 1));

        //Hellbark
        addRecipeToFront(new ItemStack(BOPBlocks.hellBarkFence, 4), "RSR", "RSR", "RSR", 'R', new ItemStack(BOPCBlocks.planks, 1, 12), 'S', new ItemStack(Items.stick, 1));
        addRecipeToFront(new ItemStack(BOPBlocks.hellBarkFenceGate, 4), "RSR", "RSR", "   ", 'R', new ItemStack(BOPCBlocks.planks, 1, 12), 'S', new ItemStack(Items.stick, 1));

        //Jacaranda
        addRecipeToFront(new ItemStack(BOPBlocks.jacarandaFence, 4), "RSR", "RSR", "RSR", 'R', new ItemStack(BOPCBlocks.planks, 1, 13), 'S', new ItemStack(Items.stick, 1));
        addRecipeToFront(new ItemStack(BOPBlocks.jacarandaFenceGate, 4), "RSR", "RSR", "   ", 'R', new ItemStack(BOPCBlocks.planks, 1, 13), 'S', new ItemStack(Items.stick, 1));

        //Mahogany
        addRecipeToFront(new ItemStack(BOPBlocks.mahoganyFence, 4), "RSR", "RSR", "RSR", 'R', new ItemStack(BOPCBlocks.planks, 1, 14));
        addRecipeToFront(new ItemStack(BOPBlocks.mahoganyFenceGate, 4), "RSR", "RSR", "   ", 'R', new ItemStack(BOPCBlocks.planks, 1, 14));

    }
}
