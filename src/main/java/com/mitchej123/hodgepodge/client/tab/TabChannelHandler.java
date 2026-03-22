package com.mitchej123.hodgepodge.client.tab;

import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mitchej123.hodgepodge.Common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import io.netty.buffer.ByteBuf;

/**
 * Holds tab header/footer text from two sources:
 * <ul>
 * <li>MessageConfigSync on login (Hodgepodge server config)</li>
 * <li>Plugin channel HP|TabHF (proxy/external server, overrides config sync)</li>
 * </ul>
 * A proxy like Velocity can send a S3FPacketCustomPayload on channel "HP|TabHF" with a JSON body
 * {"header":"...","footer":"..."} to set the tab header/footer for 1.7.10 clients. Cleared on disconnect.
 */
public class TabChannelHandler {

    public static final String CHANNEL_NAME = "HP|TabHF";
    public static final TabChannelHandler INSTANCE = new TabChannelHandler();

    private String header = "";
    private String footer = "";
    private boolean hasServerData = false;

    private TabChannelHandler() {}

    /**
     * Registers the HP|TabHF plugin channel for proxy/external server support.
     */
    public void registerChannel() {
        FMLEventChannel channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNEL_NAME);
        channel.register(this);
    }

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        try {
            ByteBuf payload = event.packet.payload();
            byte[] bytes = new byte[payload.readableBytes()];
            payload.readBytes(bytes);
            String json = new String(bytes, StandardCharsets.UTF_8);

            JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
            String h = obj.has("header") ? obj.get("header").getAsString() : "";
            String f = obj.has("footer") ? obj.get("footer").getAsString() : "";
            setServerData(h, f);
        } catch (Exception e) {
            Common.log.warn("Failed to parse HP|TabHF packet", e);
        }
    }

    /**
     * Sets the header/footer from server config sync or plugin channel.
     */
    public void setServerData(String header, String footer) {
        this.header = header != null ? header : "";
        this.footer = footer != null ? footer : "";
        this.hasServerData = true;
    }

    /**
     * Clears server-sent header/footer data. Called on disconnect.
     */
    public void clear() {
        header = "";
        footer = "";
        hasServerData = false;
    }

    public String getHeader() {
        return header;
    }

    public String getFooter() {
        return footer;
    }

    public boolean hasServerData() {
        return hasServerData;
    }
}
