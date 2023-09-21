package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiChat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.mitchej123.hodgepodge.Common;

@Mixin(GuiChat.class)
public class MixinGuiChat_LongerMessages {

    @ModifyConstant(method = "initGui", constant = @Constant(intValue = 100))
    public int hodgepodge$LongerMessages(int constant) {
        return Common.config.messageLength;
    }
}
