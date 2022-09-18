package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat_TransparentChat extends Gui {

    @Shadow
    public abstract boolean getChatOpen();

    @Redirect(
            method = "drawChat",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 0))
    public void hodgepodge$transparentChat(int x1, int y1, int x2, int y2, int color) {
        if (getChatOpen()) {
            drawRect(x1, y1, x2, y2, color);
        }
    }
}
