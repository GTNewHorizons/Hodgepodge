package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.rwtema.extrautils.gui.GuiFilingCabinet;

@Mixin(value = GuiFilingCabinet.class, remap = false)
public class MixinGuiFilingCabinet {

    @ModifyConstant(method = "sortItems", constant = @Constant(intValue = Integer.MIN_VALUE), require = 2, allow = 2)
    private int hodgepodge$safeHiddenSlotPosition(int original) {
        // We need a low number to represent an offscreen position, but not so low that
        // rendering offsets cause integer overflow and bugs with extra rendering passes,
        // such as enchantment glint.
        return -100_000;
    }
}
