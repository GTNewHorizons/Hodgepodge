package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ic2.core.util.KeyboardClient;

@Mixin(value = KeyboardClient.class, remap = false)
public class MixinKeyboardClient_sendKeyUpdate {

    @Redirect(
            method = "sendKeyUpdate",
            at = @At(
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/client/gui/GuiScreen;allowUserInput:Z",
                    value = "FIELD"))
    private boolean hodgepodge$allowUserInput(GuiScreen gui) {
        return false;
    }
}
