package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiIngameMenu.class)
public class MixinGuiIngameMenu_LocalizeModOptionsButton {

    @ModifyConstant(method = "initGui", constant = @Constant(stringValue = "Mod Options..."), require = 0)
    private String hodgepodge$localizeForgeModOptionsButton(String original) {
        return StatCollector.translateToLocal("hodgepodge.menu.mod_options");
    }
}
