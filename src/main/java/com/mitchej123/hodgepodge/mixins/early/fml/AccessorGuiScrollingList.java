package com.mitchej123.hodgepodge.mixins.early.fml;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import cpw.mods.fml.client.GuiScrollingList;

@Mixin(GuiScrollingList.class)
public interface AccessorGuiScrollingList {

    @Accessor(value = "selectedIndex", remap = false)
    void hodgepodge$setSelectedIndex(int index);
}
