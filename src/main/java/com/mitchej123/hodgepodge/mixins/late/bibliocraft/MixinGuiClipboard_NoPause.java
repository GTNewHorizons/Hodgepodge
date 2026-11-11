package com.mitchej123.hodgepodge.mixins.late.bibliocraft;

import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.mixin.Mixin;

import jds.bibliocraft.gui.GuiClipboard;

@Mixin(GuiClipboard.class)
public class MixinGuiClipboard_NoPause extends GuiScreen {

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
