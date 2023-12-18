package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu_DisableRealmsButton {

    @ModifyVariable(method = "addSingleplayerMultiplayerButtons", at = @At(value = "STORE"), name = "realmsButton")
    private GuiButton hodgepodge$disableRealmsButton(GuiButton realmsButton) {
        realmsButton.enabled = false;
        return realmsButton;
    }
}
