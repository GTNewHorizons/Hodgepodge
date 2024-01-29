package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiNewChat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat_LongerChat {

    @ModifyConstant(method = "func_146237_a", constant = @Constant(intValue = 100))
    public int hodgepodge$LongerChat(int constant) {
        return TweaksConfig.chatLength;
    }
}
