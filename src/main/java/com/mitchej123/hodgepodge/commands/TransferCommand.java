package com.mitchej123.hodgepodge.commands;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import com.mitchej123.hodgepodge.net.MessageTransfer;
import com.mitchej123.hodgepodge.net.NetworkHandler;

public class TransferCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "transfer";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/transfer <player> <hostname> [port]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        EntityPlayerMP player = getPlayer(sender, args[0]);
        String hostname = args[1];

        int port = 25565;
        if (args.length >= 3) {
            port = parseIntBounded(sender, args[2], 1, 65535);
        }

        NetworkHandler.instance.sendTo(new MessageTransfer(hostname, port), player);
        sender.addChatMessage(
                new ChatComponentText(
                        "Transferring " + player.getCommandSenderName() + " to " + hostname + ":" + port));
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        }
        return null;
    }
}
