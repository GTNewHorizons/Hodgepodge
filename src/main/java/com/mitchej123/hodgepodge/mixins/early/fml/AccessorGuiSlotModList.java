package com.mitchej123.hodgepodge.mixins.early.fml;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import cpw.mods.fml.client.GuiSlotModList;
import cpw.mods.fml.common.ModContainer;

@Mixin(GuiSlotModList.class)
public interface AccessorGuiSlotModList {

    @Accessor(value = "mods", remap = false)
    ArrayList<ModContainer> hodgepodge$getMods();
}
