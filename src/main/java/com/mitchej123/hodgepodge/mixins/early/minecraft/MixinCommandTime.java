package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.command.CommandTime;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandTime.class)
public class MixinCommandTime {

    @Inject(
            at = @At(target = "net/minecraft/command/WrongUsageException", value = "NEW"),
            cancellable = true,
            method = "processCommand")
    private void hodgepodge$handleGet(ICommandSender sender, String[] args, CallbackInfo ci) {
        if (args.length == 0 || !args[0].equals("get")) {
            return;
        }
        World world = sender.getEntityWorld();
        sender.addChatMessage(
                new ChatComponentTranslation("commands.time.get", world.getWorldTime(), world.getWorldTime() % 24000));
        ci.cancel();
    }

    @ModifyArg(
            at = @At(
                    ordinal = 0,
                    target = "Lnet/minecraft/command/CommandTime;getListOfStringsMatchingLastWord([Ljava/lang/String;[Ljava/lang/String;)Ljava/util/List;",
                    value = "INVOKE"),
            index = 1,
            method = "addTabCompletionOptions")
    private String[] hodgepodge$appendGet(String[] possibilities) {
        int length = possibilities.length;
        String[] newPossibilities = new String[length + 1];
        System.arraycopy(possibilities, 0, newPossibilities, 0, length);
        newPossibilities[length] = "get";
        return newPossibilities;
    }
    
    @ModifyConstant(constant = @Constant(stringValue = "commands.time.usage"), method = {"getCommandUsage", "processCommand"})
    private String hodgepodge$newUsage(String original) {
        return "commands.time.usageWithGet";
    }
}
