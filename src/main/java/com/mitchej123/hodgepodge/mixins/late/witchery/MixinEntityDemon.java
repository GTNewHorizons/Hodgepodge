package com.mitchej123.hodgepodge.mixins.late.witchery;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.emoniph.witchery.entity.EntityDemon;

import crazypants.enderio.item.ItemSoulVessel;

@SuppressWarnings("UnusedMixin")
@Mixin(EntityDemon.class)
public class MixinEntityDemon {

    @Redirect(
            method = "interact",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/InventoryPlayer;getCurrentItem()Lnet/minecraft/item/ItemStack;"))
    private ItemStack hodgepodge$fixDemonSoulVialInteraction(InventoryPlayer inventory) {
        ItemStack currentItem = inventory.getCurrentItem();
        if (currentItem != null && currentItem.getItem() instanceof ItemSoulVessel soulVessel
                && inventory.player.isSneaking()
                && !soulVessel.containsSoul(currentItem)) {
            // We take advantage of the fact that Witchery does not open the Demon's
            // trading screen when right-clicking while holding a (unnamed) name tag
            return new ItemStack(Items.name_tag);
        }

        return currentItem;
    }
}
