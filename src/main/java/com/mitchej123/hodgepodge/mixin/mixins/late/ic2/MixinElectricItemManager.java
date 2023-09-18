package com.mitchej123.hodgepodge.mixin.mixins.late.ic2;

import net.minecraft.inventory.Container;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ic2.core.item.ElectricItemManager;

@Mixin(ElectricItemManager.class)
public class MixinElectricItemManager {

    @Redirect(
            method = "chargeFromArmor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Container;detectAndSendChanges()V"),
            remap = false)
    public void hodgepodge$cancelDetectAndSendChanges(Container instance) {}
}
