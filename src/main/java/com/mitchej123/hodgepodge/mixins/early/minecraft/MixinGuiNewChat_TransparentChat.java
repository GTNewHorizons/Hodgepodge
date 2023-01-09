package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.gtnewhorizon.mixinextras.injector.WrapWithCondition;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat_TransparentChat {

    @Shadow
    public abstract boolean getChatOpen();

    @WrapWithCondition(
            at = @At(ordinal = 0, target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", value = "INVOKE"),
            method = "drawChat(I)V")
    private boolean getChatOpen(int left, int top, int right, int bottom, int color) {
        return this.getChatOpen();
    }
}
