package com.mitchej123.hodgepodge.mixins.late.xaeroworldmap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import xaero.map.gui.GuiMap;

@Mixin(GuiMap.class)
public class MixinGuiMap {

    @ModifyConstant(method = "handleMouseInput", constant = @Constant(intValue = 120, ordinal = 0))
    private int hodgepodge$fixScrolling(int original) {
        return 1;
    }
}
