package com.mitchej123.hodgepodge.commands;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.DebugConfig;
import com.mitchej123.hodgepodge.core.HodgepodgeCore;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

public final class PacketStatsCommand extends CommandBase {

    private static final int DEFAULT_TICKS = 200;
    private static final int MAX_TICKS = 12000;
    private static final int TOP_COUNT = 10;

    private static volatile boolean trackingIncoming;
    private static final LongAdder incomingTotal = new LongAdder();
    private static final ConcurrentHashMap<String, LongAdder> incomingByType = new ConcurrentHashMap<>();

    private static final Comparator<Object2LongMap.Entry<String>> PACKET_ORDER = Collections
            .reverseOrder(Comparator.comparingLong(Object2LongMap.Entry::getLongValue));

    private boolean measuring;
    private int ticksRemaining;
    private int measuredTicks;

    private static Field RECEIVED_PACKETS_QUEUE_FIELD;

    static {
        try {
            String fieldName = HodgepodgeCore.isObf() ? "field_150748_i" : "receivedPacketsQueue";
            RECEIVED_PACKETS_QUEUE_FIELD = NetworkManager.class.getDeclaredField(fieldName);
            RECEIVED_PACKETS_QUEUE_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Common.log.error("Failed to get NetworkManager receivedPacketsQueue field", e);
        }
    }

    @Override
    public String getCommandName() {
        return "packetstats";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/packetstats <queue|incoming [ticks]>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) throw new WrongUsageException(getCommandUsage(sender));

        int ticks = switch (args[0]) {
            case "queue" -> {
                if (args.length != 1) throw new WrongUsageException(getCommandUsage(sender));
                yield 0;
            }
            case "incoming" -> {
                if (!DebugConfig.trackIncomingPackets) {
                    throw new CommandException(
                            "[PacketStats] Tracking incoming packets requires "
                                    + "\"trackIncomingPackets\" to be enabled in the config.");
                }
                yield switch (args.length) {
                    case 1 -> DEFAULT_TICKS;
                    case 2 -> parseIntBounded(sender, args[1], 1, MAX_TICKS);
                    default -> throw new WrongUsageException(getCommandUsage(sender));
                };
            }
            default -> throw new WrongUsageException(getCommandUsage(sender));
        };

        Queue<Packet> queue = getReceiveQueue();
        if (queue == null) throw new CommandException("[PacketStats] No active client connection");

        // rerunning the command just restarts the measurement
        if (!measuring) {
            FMLCommonHandler.instance().bus().register(this);
            measuring = true;
        }

        measuredTicks = ticks;
        ticksRemaining = ticks;

        if (measuredTicks != 0) {
            trackingIncoming = false;
            incomingTotal.reset();
            incomingByType.clear();
            trackingIncoming = true;

            printChatMessage("[PacketStats] Analyzing incoming packets for " + ticks + " ticks.");
        } else {
            printChatMessage("[PacketStats] Analyzing queued packets");
        }
    }

    public static void recordIncoming(Packet packet) {
        if (!trackingIncoming) return;

        incomingTotal.increment();
        incomingByType.computeIfAbsent(packetName(packet), ignored -> new LongAdder()).increment();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !measuring) return;
        if (--ticksRemaining > 0) return;

        measuring = false;
        trackingIncoming = false;

        FMLCommonHandler.instance().bus().unregister(this);

        Queue<Packet> queue = getReceiveQueue();
        if (queue == null) {
            printChatMessage(EnumChatFormatting.RED + "[PacketStats] Received packets queue closed.");
            return;
        }

        if (measuredTicks != 0) {
            printChatMessage(EnumChatFormatting.GOLD + "[PacketStats] Incoming packets - " + measuredTicks + " ticks");
        } else {
            printChatMessage(EnumChatFormatting.GOLD + "[PacketStats] Queued packets");
        }

        printChatMessage("Queue size: " + queue.size());

        long entryCount;
        List<Object2LongMap.Entry<String>> topEntries;
        if (measuredTicks != 0) {
            long total = incomingTotal.sum();
            double avg = total / (double) measuredTicks;

            printChatMessage("Incoming: " + format(avg) + "/t (" + total + " total)");

            entryCount = total;
            topEntries = incomingByType.entrySet().stream()
                    .map(e -> Object2LongMap.entry(e.getKey(), e.getValue().sum())).sorted(PACKET_ORDER)
                    .limit(TOP_COUNT).collect(Collectors.toList());
        } else {
            Object2LongMap<String> countsSnapshot = queue.stream().collect(
                    Collectors.toMap(
                            PacketStatsCommand::packetName,
                            _packet -> 1L,
                            Long::sum,
                            Object2LongOpenHashMap::new));

            entryCount = countsSnapshot.object2LongEntrySet().stream().mapToLong(Object2LongMap.Entry::getLongValue)
                    .sum();
            topEntries = countsSnapshot.object2LongEntrySet().stream().sorted(PACKET_ORDER).limit(TOP_COUNT)
                    .collect(Collectors.toList());
        }

        printChatMessage(EnumChatFormatting.YELLOW + "Top packets:");

        StringBuilder builder = new StringBuilder();
        for (Object2LongMap.Entry<String> entry : topEntries) {
            builder.setLength(0);

            long count = entry.getLongValue();
            double percentage = entryCount == 0 ? 0.0 : count * 100.0 / entryCount;

            String perTick = measuredTicks != 0 ? "(" + format(count / (double) measuredTicks) + "/t)" : "";

            builder.append(EnumChatFormatting.GRAY);
            builder.append(String.format(Locale.ROOT, "%8d %10s %6.1f%%  ", count, perTick, percentage));
            builder.append(EnumChatFormatting.WHITE).append(entry.getKey());

            printChatMessage(builder.toString());
        }

        if (entryCount == 0) {
            printChatMessage(EnumChatFormatting.GRAY + "  none");
        }
    }

    private static String packetName(Packet packet) {
        if (packet instanceof FMLProxyPacket fmlProxyPacket) {
            String channel = fmlProxyPacket.channel();
            return "FMLProxyPacket[" + (channel == null ? "???" : channel) + "]";
        }

        return packet.getClass().getSimpleName();
    }

    @SuppressWarnings("unchecked")
    private static Queue<Packet> getReceiveQueue() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getNetHandler() == null) return null;
        if (RECEIVED_PACKETS_QUEUE_FIELD == null) return null;

        try {
            NetworkManager manager = mc.getNetHandler().getNetworkManager();
            return (Queue<Packet>) RECEIVED_PACKETS_QUEUE_FIELD.get(manager);
        } catch (ReflectiveOperationException e) {
            Common.log.error("Failed to access NetworkManager received packet queue", e);
            return null;
        }
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private static void printChatMessage(String message) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.ingameGUI == null) return;
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
    }
}
