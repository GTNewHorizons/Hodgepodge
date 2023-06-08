package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Container.class)
public class MixinContainer {

    @Shadow
    public List inventorySlots;

    @Inject(method = "putStacksInSlots([Lnet/minecraft/item/ItemStack;)V", at = @At(value = "HEAD"), cancellable = true)
    public void hodgepodge$checkStacksSize(ItemStack[] p_75131_1_, CallbackInfo ci) {
        if (this.inventorySlots.size() < p_75131_1_.length) ci.cancel();
    }
}
