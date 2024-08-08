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

    @Redirect(
            method = "registerPhiloStoneSmelting",
            at = @At(
                    remap = true,
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/crafting/FurnaceRecipes;getSmeltingList()Ljava/util/Map;"),
            remap = false)
    private static Map<ItemStack, ItemStack> hodgepodge$getFakeRecipeMap(FurnaceRecipes instance) {
        return new HashMap<>(instance.getSmeltingList());
    }
}
