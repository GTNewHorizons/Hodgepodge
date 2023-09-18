package com.mitchej123.hodgepodge.mixin.mixins.early.minecraft;

import net.minecraft.client.gui.GuiNewChat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat_FixColorWrapping {

    @Unique
    private String hodgepodge$s1;

    @ModifyVariable(
            method = "func_146237_a",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/lang/String;)Lnet/minecraft/util/ChatComponentText;",
                    ordinal = 2,
                    shift = At.Shift.BEFORE),
            name = "s1")
    private String hodgepodge$captureS1(String s1) {
        this.hodgepodge$s1 = s1;
        return s1;
    }

    @ModifyVariable(
            method = "func_146237_a",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/lang/String;)Lnet/minecraft/util/ChatComponentText;",
                    ordinal = 2,
                    shift = At.Shift.BEFORE),
            name = "s2")
    private String hodgepodge$fixColorWrapping(String s2) {
        return FontRendererAccessor.callGetFormatFromString(this.hodgepodge$s1) + s2;
    }

}
