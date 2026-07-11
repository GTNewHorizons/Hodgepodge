package com.mitchej123.hodgepodge.mixins.early.fml;

import cpw.mods.fml.client.GuiScrollingList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.client.gui.ScrollbarRenderer;

/**
 * Replace vanilla scrollbar with a textured one.(used by GuiModList)
 */
@Mixin(GuiScrollingList.class)
public abstract class MixinGuiScrollingList_TexturedScrollbar {

    @Inject(method = "", at = @At(""))

    private void hodgepodge$drawTexturedScrollbar(CallbackInfo ci) {

    }
}
