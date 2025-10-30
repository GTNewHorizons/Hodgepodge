package com.mitchej123.hodgepodge.hax;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.MapMaker;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mitchej123.hodgepodge.net.NetworkHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@EventBusSubscriber
public class TileEntityDescriptionBatcher {

    private static final Logger LOGGER = LogManager.getLogger("TileEntityDescriptionBatcher");

    private static final Map<EntityPlayerMP, ByteBuf> pendingPackets = new MapMaker().weakKeys().makeMap();

    private static final ByteBuf backingBuffer = Unpooled.buffer(1024);
    private static final PacketBuffer tempBuffer = new PacketBuffer(backingBuffer);

    private static final int MAX_BYTES_PER_PACKET = 32000; // leave some room for the packet headers

    private static String[] lastBlacklist;
    private static final HashSet<String> classBlacklist = new HashSet<>();

    /// Queues a packet to be sent. Returns false when the packet should be sent normally, either because batching is
    /// disabled or the packet could not be batched.
    public static boolean queueSend(EntityPlayerMP player, TileEntity tile, S35PacketUpdateTileEntity packet) {
        if (!SpeedupsConfig.batchDescriptionPacketsCode) return false;

        if (SpeedupsConfig.batchDescriptionBlacklist != lastBlacklist) {
            lastBlacklist = SpeedupsConfig.batchDescriptionBlacklist;
            classBlacklist.clear();
            classBlacklist.addAll(Arrays.asList(SpeedupsConfig.batchDescriptionBlacklist));
        }

        if (classBlacklist.contains(tile.getClass().getName())) {
            return false;
        }

        try {
            backingBuffer.clear();

            packet.writePacketData(tempBuffer);
        } catch (IOException e) {
            LOGGER.error(
                    "Could not serialize description packet, it will be ignored: {},{},{},{}\n{}",
                    packet.func_148856_c(),
                    packet.func_148855_d(),
                    packet.func_148854_e(),
                    packet.func_148853_f(),
                    packet.func_148857_g(),
                    e);

            return false;
        }

        // This should never happen, but we're also intercepting every description packet so who knows what could
        // go wrong
        if (backingBuffer.writerIndex() > MAX_BYTES_PER_PACKET) {
            return false;
        }

        ByteBuf forPlayer = pendingPackets.computeIfAbsent(player, p -> Unpooled.buffer(Short.MAX_VALUE));

        if (backingBuffer.writerIndex() + forPlayer.writerIndex() > MAX_BYTES_PER_PACKET) {
            send(player, forPlayer);
        }

        forPlayer.writeInt(backingBuffer.writerIndex());
        forPlayer.writeBytes(backingBuffer);

        return true;
    }

    @SubscribeEvent
    public static void flushMessages(TickEvent.ServerTickEvent event) {
        if (event.phase != Phase.END) return;

        pendingPackets.forEach((player, data) -> {
            if (data.readableBytes() > 0) {
                send(player, data);
            }
        });
    }

    private static void send(EntityPlayerMP player, ByteBuf data) {
        ByteBuf temp = Unpooled.buffer(data.readableBytes());
        temp.writeBytes(data);
        data.clear();

        BatchedDescriptionPacket packet = new BatchedDescriptionPacket();
        packet.data = temp;

        NetworkHandler.instance.sendTo(packet, player);
    }

    public static class BatchedDescriptionPacket implements IMessage {

        public ByteBuf data;

        @Override
        public void fromBytes(ByteBuf buf) {
            this.data = Unpooled.buffer(buf.readableBytes());

            data.writeBytes(buf);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeBytes(data);
        }
    }

    public static class BatchedDescriptionHandler implements IMessageHandler<BatchedDescriptionPacket, IMessage> {

        @Override
        public IMessage onMessage(BatchedDescriptionPacket message, MessageContext ctx) {
            ByteBuf temp = Unpooled.buffer(512);
            PacketBuffer buffer = new PacketBuffer(temp);

            while (message.data.isReadable()) {
                temp.clear();

                int byteCount = message.data.readInt();
                temp.writeBytes(message.data, byteCount);

                S35PacketUpdateTileEntity packet = new S35PacketUpdateTileEntity();

                try {
                    packet.readPacketData(buffer);
                } catch (IOException e) {
                    LOGGER.error(
                            "Could not deserialize description packet, it will be ignored. Best effort data (may be incomplete): {},{},{},{}\n{}",
                            packet.func_148856_c(),
                            packet.func_148855_d(),
                            packet.func_148854_e(),
                            packet.func_148853_f(),
                            packet.func_148857_g(),
                            e);

                    continue;
                }

                if (ctx.netHandler instanceof INetHandlerPlayClient client) {
                    client.handleUpdateTileEntity(packet);
                } else {
                    LOGGER.error(
                            "Received BatchedDescriptionPacket while not playing: ignoreing it (ctx: {})",
                            ctx.netHandler);
                }
            }

            return null;
        }
    }
}
