package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.rwtema.extrautils.gui.ContainerFilingCabinet;

import invtweaks.api.container.ContainerSection;

@Mixin(value = ContainerFilingCabinet.class)
public abstract class MixinContainerFilingCabinet extends Container {

    @Shadow(remap = false)
    public abstract Map<ContainerSection, List<Slot>> getSlots();

    @Unique
    private ItemStack hodgepodge$buffer;

    @Inject(
            method = "slotClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/inventory/Container;slotClick(IIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;",
                    ordinal = 1))
    private void hodgepodge$patchFilingCabinetDupe(int slotId, int clickedButton, int mode, EntityPlayer player,
            CallbackInfoReturnable<ItemStack> cir) {
        if (mode != 2) {
            return;
        }
        ItemStack stack = this.getInventory().get(slotId);
        if (stack != null && stack.getMaxStackSize() < stack.stackSize) {
            int newSize = stack.stackSize - stack.getMaxStackSize();
            hodgepodge$buffer = stack.copy();
            hodgepodge$buffer.stackSize = newSize;
            stack.stackSize = stack.getMaxStackSize();
        }
    }

    @Inject(method = "slotClick", at = @At(value = "TAIL"))
    private void hodgepodge$restoreItemStackSize(int slotId, int clickedButton, int mode, EntityPlayer player,
            CallbackInfoReturnable<ItemStack> cir) {
        if (mode != 2) {
            return;
        }

        if (hodgepodge$buffer != null && slotId >= 0 && slotId < this.getSlots().size()) {
            Slot slot = this.inventorySlots.get(slotId);
            if (slot.getStack() == null) {
                slot.putStack(hodgepodge$buffer.copy());
                hodgepodge$buffer = null;
            }
        }
    }
}
