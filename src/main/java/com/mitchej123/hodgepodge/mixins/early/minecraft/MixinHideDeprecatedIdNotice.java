package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandBase.class)
public class MixinHideDeprecatedIdNotice {

    @Redirect(
            method = "getBlockByText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/command/ICommandSender;addChatMessage(Lnet/minecraft/util/IChatComponent;)V"))
    private static void hodgepodge$doNotAddChatMessageBlock(ICommandSender sender, IChatComponent message) {}

    @Redirect(
            method = "getItemByText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/command/ICommandSender;addChatMessage(Lnet/minecraft/util/IChatComponent;)V"))
    private static void hodgepodge$doNotAddChatMessageItem(ICommandSender sender, IChatComponent message) {}
}
