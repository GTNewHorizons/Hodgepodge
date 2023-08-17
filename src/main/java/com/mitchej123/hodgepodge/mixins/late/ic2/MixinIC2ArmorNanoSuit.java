package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.inventory.Container;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ic2.core.item.armor.ItemArmorNanoSuit;

@Mixin(ItemArmorNanoSuit.class)
public class MixinIC2ArmorNanoSuit {

    @Redirect(
            method = "onArmorTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Container;detectAndSendChanges()V"))
    public void hodgepodge$cancelDetectAndSendChanges(Container instance) {}
}
