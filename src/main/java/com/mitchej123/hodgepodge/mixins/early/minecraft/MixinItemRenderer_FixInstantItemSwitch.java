package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer_FixInstantItemSwitch {

    /**
     * @author danyadev
     * @reason When a player switches an item in their hand, there's an animation that hides the previous item and
     *         shows the new item. The problem is that when the items have the same id and meta but different NBT,
     *         this animation breaks: at the beginning of the hiding animation the item is already new. For example,
     *         this happens when you switch from a normal sword to an enchanted sword
     * @see <a href="https://github.com/GTNewHorizons/Hodgepodge/pull/666">PR with a demo</a>
     */
    @ModifyExpressionValue(
            method = "updateEquippedItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
                    ordinal = 0))
    private Item hodgepodge$setItemNull(Item original) {
        // The problem is that the method checks if the items are the same-ish, but the check
        // ignores any differences in NBT.
        // In order to fix it we're just making this check to never pass, so we're always seeing
        // the animation. We're setting newItemStack.getItem() to null (just inside the condition,
        // not really) when usually it's never null
        return null;
    }
}
