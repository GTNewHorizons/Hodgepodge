package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiDisconnected.class)
public abstract class MixinGuiDisconnected_FixLayout extends GuiScreen {

    @Shadow
    private IChatComponent field_146304_f;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void hodgepodge$fixButtonY(CallbackInfo ci) {
        List<String> lines = this.fontRendererObj
                .listFormattedStringToWidth(this.field_146304_f.getFormattedText(), this.width - 50);
        int textBottom = this.height / 2 - 30 + lines.size() * (this.fontRendererObj.FONT_HEIGHT + 1);
        int minY = this.height / 2 + 10;
        int buttonY = Math.max(minY, textBottom + 5);
        if (!this.buttonList.isEmpty()) {
            ((GuiButton) this.buttonList.get(0)).yPosition = buttonY;
        }
    }
}
