package com.mitchej123.hodgepodge.mixins.early.fml;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import cpw.mods.fml.client.GuiSlotModList;

@Mixin(GuiSlotModList.class)
public class MixinGuiSlotModList {

    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 66), remap = false)
    private static int ShiftBottom(int constant) {
        // Shift the bottom of the mod list up by 25 pixels
        return 91;
    }
}
