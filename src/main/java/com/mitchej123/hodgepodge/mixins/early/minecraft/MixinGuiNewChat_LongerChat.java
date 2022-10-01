package com.mitchej123.hodgepodge.mixins.minecraft;

import com.mitchej123.hodgepodge.Common;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat_LongerChat {

    @ModifyConstant(method = "func_146237_a", constant = @Constant(intValue = 100))
    public int hodgepodge$LongerChat(int constant) {
        return Common.config.chatLength;
    }
}
