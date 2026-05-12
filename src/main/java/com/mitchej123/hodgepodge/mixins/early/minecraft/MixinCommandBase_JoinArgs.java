package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.PlayerSelector;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandBase.class)
public class MixinCommandBase_JoinArgs {

    @Inject(
            method = "func_147176_a(Lnet/minecraft/command/ICommandSender;[Ljava/lang/String;IZ)Lnet/minecraft/util/IChatComponent;",
            at = @At("HEAD"),
            cancellable = true)
    private static void hodgepodge$joinNonSelectorArgs(ICommandSender sender, String[] args, int index,
            boolean checkSelectors, CallbackInfoReturnable<IChatComponent> cir) {
        ChatComponentText root = new ChatComponentText("");
        StringBuilder textRun = new StringBuilder();
        boolean firstArg = true;

        for (int j = index; j < args.length; j++) {
            IChatComponent selectorResult = checkSelectors ? PlayerSelector.func_150869_b(sender, args[j]) : null;

            if (selectorResult != null) {
                if (textRun.length() > 0) {
                    root.appendSibling(new ChatComponentText(textRun.toString()));
                    textRun.setLength(0);
                }
                if (!firstArg) root.appendText(" ");
                root.appendSibling(selectorResult);
            } else if (checkSelectors && PlayerSelector.hasArguments(args[j])) {
                throw new PlayerNotFoundException();
            } else {
                if (!firstArg) textRun.append(' ');
                textRun.append(args[j]);
            }
            firstArg = false;
        }

        if (textRun.length() > 0) {
            root.appendSibling(new ChatComponentText(textRun.toString()));
        }

        cir.setReturnValue(root);
    }
}
