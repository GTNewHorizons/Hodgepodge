package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu_DisableRealmsButton extends GuiScreen {

    @Inject(method = "addSingleplayerMultiplayerButtons", at = @At("RETURN"))
    private void hodgepodge$disableRealmsButton(int p_73969_1_, int p_73969_2_, CallbackInfo ci) {
        for (Object o : this.buttonList) {
            final GuiButton button = (GuiButton) o;
            if (button.id == 14) { // realms button
                button.visible = false;
            } else if (button.id == 6) { // mods button
                button.width = 200;
                button.xPosition = this.width / 2 - 100;
            }
        }
    }
}
