package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

@Mixin(CraftingManager.class)
public class MixinCraftingManager {

    @Unique
    private IRecipe hp$lastMatch = null;

    @Inject(
            method = "findMatchingRecipe",
            at = @At(value = "CONSTANT", args = "intValue=0", shift = At.Shift.BEFORE),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/Item;II)V"),
                    to = @At(value = "INVOKE", target = "Ljava/util/List;size()I", remap = false)),
            cancellable = true)
    private void testCachedRecipe(InventoryCrafting inventory, World world, CallbackInfoReturnable<ItemStack> cir) {
        if (hp$lastMatch != null) {
            if (hp$lastMatch.matches(inventory, world)) {
                cir.setReturnValue(hp$lastMatch.getCraftingResult(inventory));
            }
        }
    }

    @Inject(
            method = "findMatchingRecipe",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/crafting/IRecipe;getCraftingResult(Lnet/minecraft/inventory/InventoryCrafting;)Lnet/minecraft/item/ItemStack;",
                    shift = At.Shift.BEFORE))
    private void updateCachedRecipe(InventoryCrafting inventory, World world, CallbackInfoReturnable<ItemStack> cir,
            @Local IRecipe irecipe) {
        hp$lastMatch = irecipe;
    }
}
