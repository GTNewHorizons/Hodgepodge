package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenResourcePacks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreenResourcePacks.class)
public class MixinGuiScreenResourcePacks_OreOutlineButton extends GuiScreen {

    @Inject(method = "initGui", at = @At("TAIL"))
    private void hodgepodge$addOreOutlineButton(CallbackInfo ci) {
        int buttonX = this.width / 2 - 182;
        int buttonY = this.height - 48;
        this.buttonList.add(new GuiButton(9001, buttonX, buttonY, 20, 20, ""));
    }
}
