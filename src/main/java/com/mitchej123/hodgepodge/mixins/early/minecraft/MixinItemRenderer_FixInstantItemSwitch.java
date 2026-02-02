package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer_FixInstantItemSwitch {

    @Shadow
    private ItemStack itemToRender;

    /**
     * @author danyadev
     * @reason When a player switches an item in their hand, there's an animation that hides the previous item and shows
     *         the new item. The problem is that when the items have the same id and meta but different NBT, the
     *         animation breaks: at the beginning of the hiding animation the item is already new. For example, it
     *         happens when you switch from a normal sword to an enchanted sword
     * @see <a href="https://github.com/GTNewHorizons/Hodgepodge/pull/666">PR with a demo</a>
     */
    @Redirect(
            method = "updateEquippedItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
                    ordinal = 0))
    private Item hodgepodge$compareNBT(ItemStack newItemToRender) {
        // This is the only part of the condition we can change to control the results, because ItemStack.getItem()
        // under normal circumstances is not null, so returning null here should prevent the condition from passing.

        // Copy the original checks here because it's cheaper to run them twice than to compare NBTs for nothing
        if (itemToRender.getItem() != newItemToRender.getItem()) {
            return null;
        }
        if (itemToRender.getItemDamage() != newItemToRender.getItemDamage()) {
            return null;
        }

        NBTTagCompound oldTag = itemToRender.getTagCompound();
        NBTTagCompound newTag = newItemToRender.getTagCompound();

        if (oldTag == null || newTag == null) {
            if (oldTag == newTag) {
                return newItemToRender.getItem();
            }
            return null;
        }

        if (oldTag.equals(newTag)) {
            return newItemToRender.getItem();
        }
        return null;
    }
}
