package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiSlot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replace vanilla scrollbar with a textured one.
 */
@Mixin(GuiSlot.class)
public abstract class MixinGuiSlot_TexturedScrollbar {

    @Inject(method = "", at = @At(""))

    private void hodgepodge$drawTexturedScrollbar(CallbackInfo ci) {

    }
}
