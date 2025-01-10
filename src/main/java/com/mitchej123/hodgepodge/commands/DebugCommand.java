package com.mitchej123.hodgepodge.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.mixins.interfaces.IPauseWhenEmpty;
import com.mitchej123.hodgepodge.util.AnchorAlarm;

public class DebugCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "hp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Usage: hp <subcommand>. Valid subcommands are: toggle, anchor, randomNbt, pauseWhenEmpty.";
    }

    private void printHelp(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("Usage: hp <toggle|anchor|randomNbt|pause_when_empty>"));
        sender.addChatMessage(new ChatComponentText("\"toggle anchordebug\" - toggles RC anchor debugging"));
        sender.addChatMessage(
                new ChatComponentText(
                        "\"anchor list [player]\" - list RC anchors placed by the player (empty for current player)"));
        sender.addChatMessage(
                new ChatComponentText(
                        "\"randomNbt [bytes]\" - adds a random byte array of the given size to the held item"));
        sender.addChatMessage(
                new ChatComponentText(
                        "\"pauseWhenEmpty [seconds]\" - sets the time the server will wait before pausing when no players are online; 0 to disable"));
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] ss) {
        List<String> l = new ArrayList<>();
        String test = ss.length == 0 ? "" : ss[0].trim();
        if (ss.length == 0 || ss.length == 1 && (test.isEmpty()
                || Stream.of("toggle", "anchor", "randomNbt", "pauseWhenEmpty").anyMatch(s -> s.startsWith(test)))) {
            Stream.of("toggle", "anchor", "randomNbt", "pause_when_empty")
                    .filter(s -> test.isEmpty() || s.startsWith(test)).forEach(l::add);
        } else if (test.equals("toggle")) {
            String test1 = ss[1].trim();
            if (test1.isEmpty() || "anchordebug".startsWith(test1)) l.add("anchordebug");
        } else if (test.equals("anchor")) {
            String test1 = ss[1].trim();
            if (test1.isEmpty() || ("list".startsWith(test1) && !"list".equals(test1))) l.add("list");
            else if ("list".equals(test1) && ss.length > 2) {
                l.addAll(getListOfStringsMatchingLastWord(ss, MinecraftServer.getServer().getAllUsernames()));
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
                if (!AnchorAlarm.listSavedAnchors(playerName, sender.getEntityWorld())) sender.addChatMessage(
                        new ChatComponentText("No such player entity in the current world : " + playerName));
                else sender.addChatMessage(
                        new ChatComponentText("Saved anchors dumped to the log for player: " + playerName));
                break;
            case "randomNbt":
                if (strings.length < 2) {
                    printHelp(sender);
                    return;
                }
                final int byteCount = NumberUtils.toInt(strings[1], -1);
                if (byteCount < 1) {
                    printHelp(sender);
                    return;
                }
                final EntityPlayerMP player = getCommandSenderAsPlayer(sender);
                if (player.inventory == null) {
                    return;
                }
                final ItemStack stack = player.inventory.getCurrentItem();
                if (stack == null || stack.getItem() == null) {
                    return;
                }
                if (stack.stackTagCompound == null) {
                    stack.stackTagCompound = new NBTTagCompound();
                }
                final byte[] randomData = RandomUtils.nextBytes(byteCount);
                stack.stackTagCompound.setByteArray("DebugJunk", randomData);
                player.inventory.inventoryChanged = true;
                player.inventoryContainer.detectAndSendChanges();
                break;
            case "pauseWhenEmpty":
                if (!TweaksConfig.pauseWhenEmpty) {
                    sender.addChatMessage(new ChatComponentText("'pauseWhenEmpty' config option is disabled"));
                    return;
                }
                if (strings.length < 2) {
                    printHelp(sender);
                    return;
                }
                final int pauseTimeout = NumberUtils.toInt(strings[1], -1);
                if (pauseTimeout < 0) {
                    printHelp(sender);
                    return;
                }

                MinecraftServer server = MinecraftServer.getServer();
                if (!server.isDedicatedServer()) {
                    sender.addChatMessage(
                            new ChatComponentText("'hp pauseWhenEmpty' only applies to dedicated servers"));
                    return;
                }

                if (server instanceof IPauseWhenEmpty p) {
                    p.setPauseWhenEmptySeconds(pauseTimeout);
                    sender.addChatMessage(
                            new ChatComponentText(
                                    String.format(
                                            "Server property 'pause-when-empty-seconds' set to %d",
                                            pauseTimeout)));
                }

                break;
        }
    }
}
