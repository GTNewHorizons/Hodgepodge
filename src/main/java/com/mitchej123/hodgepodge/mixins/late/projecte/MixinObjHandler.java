package com.mitchej123.hodgepodge.mixins.late.projecte;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import moze_intel.projecte.gameObjs.ObjHandler;

@Mixin(ObjHandler.class)
public class MixinObjHandler {

    @SuppressWarnings("unchecked")
    @Redirect(
            method = "registerPhiloStoneSmelting",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/crafting/FurnaceRecipes;func_77599_b()Ljava/util/Map;"),
            remap = false)
    private static Map<ItemStack, ItemStack> hodgepodge$getFakeRecipeMap(FurnaceRecipes instance) {
        return new HashMap<>(instance.getSmeltingList());
    }
}
