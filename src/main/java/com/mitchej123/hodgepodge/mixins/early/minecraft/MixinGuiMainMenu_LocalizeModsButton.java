package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.resources.I18n;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu_LocalizeModsButton {

    @ModifyConstant(
            method = "addSingleplayerMultiplayerButtons",
            constant = @Constant(stringValue = "Mods"),
            require = 0)
    private String hodgepodge$localizeForgeModsButton(String original) {
        return I18n.format("fml.menu.mods");
    }
}
