package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Locale;

import net.minecraft.command.CommandHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(CommandHandler.class)
public class MixinCommandHandler_CommandFix {

    @ModifyExpressionValue(
            method = "executeCommand",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;split(Ljava/lang/String;)[Ljava/lang/String;",
                    remap = false))
    private String[] hodgepodge$caseCommand(String[] original) {
        final String s = original[0];
        if (s != null) {
            original[0] = s.toLowerCase(Locale.ENGLISH);
        }
        return original;
    }

    @ModifyArg(
            method = "registerCommand",
            index = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    remap = false))
    private Object hodgepodge$caseCommand(Object s) {
        if (s instanceof String) {
            s = ((String) s).toLowerCase(Locale.ENGLISH);
        }
        return s;
    }

    @ModifyExpressionValue(
            method = "getPossibleCommands(Lnet/minecraft/command/ICommandSender;Ljava/lang/String;)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;split(Ljava/lang/String;I)[Ljava/lang/String;",
                    remap = false))
    private String[] hodgepodge$caseCommandTabComplete(String[] original) {
        final String s = original[0];
        if (s != null) {
            original[0] = s.toLowerCase(Locale.ENGLISH);
        }
        return original;
    }

}
