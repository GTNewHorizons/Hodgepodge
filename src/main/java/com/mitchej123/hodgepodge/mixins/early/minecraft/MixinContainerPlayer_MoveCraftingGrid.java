package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerPlayer.class)
public abstract class MixinContainerPlayer_MoveCraftingGrid extends Container {

    // Slot 0 is the crafting output, slots 1 to 4 are the 2x2 crafting grid.
    @Inject(method = "<init>", at = @At("RETURN"))
    private void hodgepodge$moveCraftingSlots(InventoryPlayer inventoryPlayer, boolean isLocalWorld,
            EntityPlayer player, CallbackInfo ci) {
        for (int slotIndex = 0; slotIndex <= 4; slotIndex++) {
            Slot slot = (Slot) inventorySlots.get(slotIndex);
            slot.xDisplayPosition += 2;
            slot.yDisplayPosition -= 4;
        }
    }
}
