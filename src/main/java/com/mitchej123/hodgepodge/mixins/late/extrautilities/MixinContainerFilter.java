package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.rwtema.extrautils.gui.ContainerFilter;

@Mixin(ContainerFilter.class)
public class MixinContainerFilter {

    @Inject(
            method = "slotClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/inventory/Container;slotClick(IIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;"),
            cancellable = true)
    private void hodgepodge$patchFilterDupe(int slotId, int clickedButton, int mode, EntityPlayer player,
            CallbackInfoReturnable<ItemStack> cir) {
        if (mode != 2) {
            return;
        }
        if (clickedButton == player.inventory.currentItem) {
            cir.setReturnValue(null);
            cir.cancel();
        }
    }
}
