package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat_FixColorWrapping {

    @ModifyVariable(
            method = "func_146237_a",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/lang/String;)Lnet/minecraft/util/ChatComponentText;",
                    ordinal = 2,
                    shift = At.Shift.BEFORE),
            name = "s2")
    private String hodgepodge$fixColorWrapping(String s2, @Local(name = "s1") String s1) {
        return FontRenderer.getFormatFromString(s1) + s2;
    }

}
