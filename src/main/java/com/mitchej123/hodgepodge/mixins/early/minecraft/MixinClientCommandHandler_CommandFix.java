package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Locale;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(ClientCommandHandler.class)
public class MixinClientCommandHandler_CommandFix {

    @Inject(method = "executeCommand", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$checkSlash(ICommandSender sender, String message, CallbackInfoReturnable<Integer> cir) {
        if (!message.trim().startsWith("/")) {
            cir.setReturnValue(0);
        }
    }

    @ModifyExpressionValue(
            method = "executeCommand",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;split(Ljava/lang/String;)[Ljava/lang/String;"))
    private String[] hodgepodge$caseCommand(String[] original) {
        final String s = original[0];
        if (s != null) {
            original[0] = s.toLowerCase(Locale.ENGLISH);
        }
        return original;
    }

}
