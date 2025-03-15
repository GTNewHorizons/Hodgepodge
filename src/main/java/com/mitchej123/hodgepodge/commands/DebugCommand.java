package com.mitchej123.hodgepodge.commands;

import static com.mitchej123.hodgepodge.common.CPSPregen.getPregenerator;

import com.mitchej123.hodgepodge.util.AnchorAlarm;
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

public class DebugCommand extends CommandBase {
    private static final String[] subcommands = {
        "toggle",
        "anchor",
        "randomNbt",
        "pregen"
    };

    private static final String USAGE;
    static {
        StringBuilder ret = new StringBuilder("Usage: hp <");
        for (int i = 0; i < subcommands.length; i++) {
            ret.append(subcommands[i]);
            if (i < subcommands.length - 1) ret.append("|");
            else ret.append(">");
        }
        USAGE = ret.toString();
    }

    @Override
    public String getCommandName() {
        return "hp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return USAGE;
    }

    private void printHelp(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(USAGE));
        sender.addChatMessage(new ChatComponentText("\"toggle anchordebug\" - toggles RC anchor debugging"));
        sender.addChatMessage(
                new ChatComponentText(
                        "\"anchor list [player]\" - list RC anchors placed by the player (empty for current player)"));
        sender.addChatMessage(
                new ChatComponentText(
                        "\"randomNbt [bytes]\" - adds a random byte array of the given size to the held item"));
        sender.addChatMessage(
                new ChatComponentText(
                        "\"pregen [cx1] [cz1] [cx2] [cz2]\" - pregenerates the chunks within a rectangle, bound by chunk coords (cx1, cz1), (cx2, cz2)"));
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] ss) {
        List<String> l = new ArrayList<>();
        String test = ss.length == 0 ? "" : ss[0].trim();
        if (ss.length == 0 || ss.length == 1
                && (test.isEmpty() || Stream.of(subcommands).anyMatch(s -> s.startsWith(test)))) {
            Stream.of(subcommands).filter(s -> test.isEmpty() || s.startsWith(test))
                    .forEach(l::add);
        } else if (test.equals("toggle")) {
            String test1 = ss[1].trim();
            if ("anchordebug".startsWith(test1)) l.add("anchordebug");
        } else if (test.equals("anchor")) {
            String test1 = ss[1].trim();
            if (("list".startsWith(test1) && !"list".equals(test1))) l.add("list");
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
            case "toggle" -> {
                if (strings.length < 2 || !strings[1].equals("anchordebug")) {
                    printHelp(sender);
                    return;
                }
                AnchorAlarm.AnchorDebug = !AnchorAlarm.AnchorDebug;
                sender.addChatMessage(new ChatComponentText("Anchor debugging: " + AnchorAlarm.AnchorDebug));
            }
            case "anchor" -> {
                if (strings.length < 2 || !strings[1].equals("list")) {
                    printHelp(sender);
                    return;
                }
                String playerName = strings.length > 2 ? strings[2] : sender.getCommandSenderName();
                if (!AnchorAlarm.listSavedAnchors(playerName, sender.getEntityWorld())) sender.addChatMessage(
                        new ChatComponentText("No such player entity in the current world : " + playerName));
                else sender.addChatMessage(
                        new ChatComponentText("Saved anchors dumped to the log for player: " + playerName));
            }
            case "randomNbt" -> {
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
            }
            case "pregen" -> {
                if (strings.length < 5) {
                    printHelp(sender);
                }

                var bounds = new int[4];
                for (int i = 1; i < 5; i++) {
                    try {
                        bounds[i - 1] = Integer.parseInt(strings[i]);
                    } catch (NumberFormatException e) {
                        printHelp(sender);
                    }
                }

                final var player = getCommandSenderAsPlayer(sender);
                final int dimID = player.dimension;
                sender.addChatMessage(new ChatComponentText("Started to pregenerate chunks..."));
                var num = getPregenerator(dimID).generateChunksBlocking(bounds[0], bounds[1], bounds[2], bounds[3]);
                sender.addChatMessage(new ChatComponentText("Successfully pregenerated " + num + " chunks!"));
            }
        }
    }
}
