package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {

    @Accessor("itemToRender")
    void setItemToRender(ItemStack itemStack);
}
