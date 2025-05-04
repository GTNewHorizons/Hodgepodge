package com.mitchej123.hodgepodge.mixins.early.fml;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.mitchej123.hodgepodge.mixins.interfaces.IGuiSlotModList;

import cpw.mods.fml.client.GuiSlotModList;
import cpw.mods.fml.common.ModContainer;

@Mixin(GuiSlotModList.class)
public class MixinGuiSlotModList implements IGuiSlotModList {

    @Shadow(remap = false)
    private ArrayList<ModContainer> mods;

    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 66), remap = false)
    private static int ShiftBottom(int constant) {
        // Shift the bottom of the mod list up by 25 pixels
        return 91;
    }

    @Override
    public ArrayList<ModContainer> hodgepodge$getMods() {
        return mods;
    }
}
