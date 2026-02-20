package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat_CleanChatLogs {

    @ModifyArg(
            method = "printChatMessageWithOptionalDeletion",
            at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V"))
    private String stripColorCodes(String msg) {
        return EnumChatFormatting.getTextWithoutFormattingCodes(msg);
    }
}
