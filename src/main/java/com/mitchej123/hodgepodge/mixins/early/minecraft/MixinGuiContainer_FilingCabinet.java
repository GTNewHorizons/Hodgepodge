package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class MixinGuiContainer_FilingCabinet {

    @Inject(method = "func_146977_a", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$skipHiddenCabinetSlot(Slot slot, CallbackInfo ci) {
        if (slot.xDisplayPosition == Integer.MIN_VALUE
                && slot.getClass().getName().equals("com.rwtema.extrautils.gui.SlotFilingCabinet")) {
            ci.cancel();
        }
    }
}
