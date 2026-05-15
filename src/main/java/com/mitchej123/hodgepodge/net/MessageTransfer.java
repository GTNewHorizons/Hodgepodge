package com.mitchej123.hodgepodge.net;

import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mitchej123.hodgepodge.config.TweaksConfig;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MessageTransfer implements IMessage, IMessageHandler<MessageTransfer, IMessage> {

    private static final Logger LOGGER = LogManager.getLogger("Hodgepodge/Transfer");

    private String host;
    private int port;

    public MessageTransfer() {}

    public MessageTransfer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int hostLen = buf.readUnsignedShort();
        byte[] hostBytes = new byte[hostLen];
        buf.readBytes(hostBytes);
        host = new String(hostBytes, StandardCharsets.UTF_8);
        port = buf.readUnsignedShort();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] hostBytes = host.getBytes(StandardCharsets.UTF_8);
        buf.writeShort(hostBytes.length);
        buf.writeBytes(hostBytes);
        buf.writeShort(port);
    }

    @Override
    public IMessage onMessage(MessageTransfer message, MessageContext ctx) {
        if (!TweaksConfig.enableTransferPacket) {
            LOGGER.info("Ignoring transfer request to {}:{} (disabled in config)", message.host, message.port);
            return null;
        }

        if (message.host == null || message.host.isEmpty() || message.port < 1 || message.port > 65535) {
            LOGGER.warn("Ignoring invalid transfer request: host={}, port={}", message.host, message.port);
            return null;
        }

        if (!isHostAllowed(message.host, message.port)) {
            LOGGER.warn("Ignoring transfer request to {}:{} (not in whitelist)", message.host, message.port);
            return null;
        }

        LOGGER.info("Server requested transfer to {}:{}", message.host, message.port);

        // Delegate to client handler — GUI classes must not be referenced from this class
        // or the server will crash with NoClassDefFoundError for GuiScreen
        TransferClientHandler.execute(message.host, message.port);

        return null;
    }

    private static boolean isHostAllowed(String host, int port) {
        String[] whitelist = TweaksConfig.transferWhitelist;
        if (whitelist == null || whitelist.length == 0) {
            return true;
        }
        String hostPort = host + ":" + port;
        for (String entry : whitelist) {
            if (entry.equalsIgnoreCase(host) || entry.equalsIgnoreCase(hostPort)) {
                return true;
            }
        }
        return false;
    }
}
