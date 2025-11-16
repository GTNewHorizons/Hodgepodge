package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer_FixInstantItemSwitch {

    @ModifyExpressionValue(
            method = "updateEquippedItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
                    ordinal = 0))
    private Item hodgepodge$setItemNull(Item original) {
        // When switching between two items we don't want to immediately switch the item.
        // Setting this value to null will make the condition to fail in our case
        return null;
    }
}
