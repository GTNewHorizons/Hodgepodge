package com.mitchej123.hodgepodge.mixins.early.fml;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import cpw.mods.fml.client.GuiScrollingList;

@Mixin(GuiScrollingList.class)
public interface AccessorGuiScrollingList {

    @Accessor(value = "selectedIndex", remap = false)
    void hodgepodge$setSelectedIndex(int index);

    @Accessor(value = "bottom", remap = false)
    int hodgepodge$getBottom();

    @Accessor(value = "right", remap = false)
    int hodgepodge$getRight();

    @Accessor(value = "scrollDistance", remap = false)
    float hodgepodge$getScrollDistance();

    @Accessor(value = "scrollDistance", remap = false)
    void hodgepodge$setScrollDistance(float distance);
}
