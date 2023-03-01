package com.mitchej123.hodgepodge.mixins.late.xaeroworldmap;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import xaero.map.gui.GuiMap;

@Mixin(GuiMap.class)
public class MixinGuiMap {

    @ModifyVariable(method = "handleMouseInput", at = @At("STORE"), ordinal = 0)
    private int hodgepodge$fixScrolling(int original) {
        return Mouse.getEventDWheel();
    }
}
