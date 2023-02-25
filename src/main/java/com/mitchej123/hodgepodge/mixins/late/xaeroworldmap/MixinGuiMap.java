package com.mitchej123.hodgepodge.mixins.late.xaeroworldmap;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import xaero.map.gui.GuiMap;

import com.mitchej123.hodgepodge.mixins.TargetedMod;
import cpw.mods.fml.common.Loader;

@Mixin(GuiMap.class)
public class MixinGuiMap {

    @Unique
    private final boolean hasLwjgl3 = Loader.isModLoaded(TargetedMod.LWJGL3IFY.modId);

    @ModifyVariable(method = "handleMouseInput", at = @At("STORE"), ordinal = 0)
    private int hodgepodge$fixScrolling(int original) {
        return hasLwjgl3 ? Mouse.getEventDWheel() : original;
    }
}
