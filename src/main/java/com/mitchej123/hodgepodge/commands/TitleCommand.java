package com.mitchej123.hodgepodge.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import com.mitchej123.hodgepodge.net.TitlePacketHandler;

public final class TitleCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "title";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/title <player> <clear|reset|title|subtitle|times> [args...]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        EntityPlayerMP player = getPlayer(sender, args[0]);
        String action = args[1].toLowerCase();

        switch (action) {
            case "clear":
                TitlePacketHandler.sendClear(player);
                func_152373_a(sender, this, "commands.title.cleared.single", player.getCommandSenderName());
                break;
            case "reset":
                TitlePacketHandler.sendReset(player);
                func_152373_a(sender, this, "commands.title.reset.single", player.getCommandSenderName());
                break;
            case "title":
                if (args.length < 3) throw new WrongUsageException("/title <player> title <text...>");
                TitlePacketHandler.sendTitle(player, textFromArgs(args, 2));
                func_152373_a(sender, this, "commands.title.show.title.single", player.getCommandSenderName());
                break;
            case "subtitle":
                if (args.length < 3) throw new WrongUsageException("/title <player> subtitle <text...>");
                TitlePacketHandler.sendSubtitle(player, textFromArgs(args, 2));
                func_152373_a(sender, this, "commands.title.show.subtitle.single", player.getCommandSenderName());
                break;
            case "times":
                if (args.length < 5) throw new WrongUsageException("/title <player> times <fadeIn> <stay> <fadeOut>");
                TitlePacketHandler.sendTimes(
                        player,
                        parseInt(sender, args[2]),
                        parseInt(sender, args[3]),
                        parseInt(sender, args[4]));
                func_152373_a(sender, this, "commands.title.times.single", player.getCommandSenderName());
                break;
            default:
                throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        }
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "clear", "reset", "title", "subtitle", "times");
        }
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }

    private static IChatComponent textFromArgs(String[] args, int start) {
        String text = String.join(" ", Arrays.copyOfRange(args, start, args.length));
        // Try parsing as JSON component first, fall back to plain text
        try {
            return IChatComponent.Serializer.func_150699_a(text);
        } catch (Exception e) {
            return new ChatComponentText(text);
        }
    }
}
