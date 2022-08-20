package com.mitchej123.hodgepodge.core.commands;

import com.mitchej123.hodgepodge.core.util.AnchorAlarm;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class DebugCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "hp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Usage: hp <subcommand>. Valid subcommands are: toggle, anchor.";
    }

    private void printHelp(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("Usage: hp <toggle|anchor>"));
        sender.addChatMessage(new ChatComponentText("\"toggle anchordebug\" - toggles RC anchor debugging"));
        sender.addChatMessage(new ChatComponentText(
                "\"anchor list [player]\" - list RC anchors placed by the player (empty for current player)"));
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] ss) {
        List<String> l = new ArrayList<>();
        String test = ss.length == 0 ? "" : ss[0].trim();
        if (ss.length == 0
                || ss.length == 1
                        && (test.isEmpty() || Stream.of("toggle", "anchor").anyMatch(s -> s.startsWith(test)))) {
            Stream.of("toggle", "anchor")
                    .filter(s -> test.isEmpty() || s.startsWith(test))
                    .forEach(l::add);
        } else if (test.equals("toggle")) {
            String test1 = ss[1].trim();
            if (test1.isEmpty() || "anchordebug".startsWith(test1)) l.add("anchordebug");
        } else if (test.equals("anchor")) {
            String test1 = ss[1].trim();
            if (test1.isEmpty() || ("list".startsWith(test1) && !"list".equals(test1))) l.add("list");
            else if ("list".equals(test1) && ss.length > 2) {
                l.addAll(getListOfStringsMatchingLastWord(
                        ss, MinecraftServer.getServer().getAllUsernames()));
            }
        }
        return l;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] strings) {
        if (strings.length < 1) {
            printHelp(sender);
            return;
        }
        switch (strings[0]) {
            case "toggle":
                if (strings.length < 2 || !strings[1].equals("anchordebug")) {
                    printHelp(sender);
                    return;
                }
                AnchorAlarm.AnchorDebug = !AnchorAlarm.AnchorDebug;
                sender.addChatMessage(new ChatComponentText("Anchor debugging: " + AnchorAlarm.AnchorDebug));
                break;
            case "anchor":
                if (strings.length < 2 || !strings[1].equals("list")) {
                    printHelp(sender);
                    return;
                }
                String playerName = strings.length > 2 ? strings[2] : sender.getCommandSenderName();
                if (!AnchorAlarm.listSavedAnchors(playerName, sender.getEntityWorld()))
                    sender.addChatMessage(
                            new ChatComponentText("No such player entity in the current world : " + playerName));
                else
                    sender.addChatMessage(
                            new ChatComponentText("Saved anchors dumped to the log for player: " + playerName));
                break;
        }
    }
}
