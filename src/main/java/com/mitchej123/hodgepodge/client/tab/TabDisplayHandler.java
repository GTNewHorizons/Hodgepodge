package com.mitchej123.hodgepodge.client.tab;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mitchej123.hodgepodge.Common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import io.netty.buffer.ByteBuf;

/**
 * Handles the HP|TabUD plugin channel for display name overrides, suffixes, and custom sort order. When a proxy sends
 * HP|TabUD data, the modern tab renderer uses it instead of team-based formatting. Falls back to team-based display for
 * players without overrides.
 */
public class TabDisplayHandler {

    public static final String CHANNEL_NAME = "HP|TabUD";
    public static final TabDisplayHandler INSTANCE = new TabDisplayHandler();

    private final Map<String, String> displayNames = new ConcurrentHashMap<>();
    private final Map<String, String> suffixes = new ConcurrentHashMap<>();
    private volatile Map<String, Integer> sortIndexMap;

    private TabDisplayHandler() {}

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
            String action = obj.get("action").getAsString();

            switch (action) {
                case "update":
                    handleUpdate(obj);
                    break;
                case "remove":
                    handleRemove(obj);
                    break;
                case "sort":
                    handleSort(obj);
                    break;
                default:
                    Common.log.warn("Unknown HP|TabUD action: {}", action);
            }
        } catch (Exception e) {
            Common.log.warn("Failed to parse HP|TabUD packet", e);
        }
    }

    private void handleUpdate(JsonObject obj) {
        JsonObject players = obj.getAsJsonObject("players");
        for (Map.Entry<String, JsonElement> entry : players.entrySet()) {
            String name = entry.getKey();
            JsonObject data = entry.getValue().getAsJsonObject();
            if (data.has("displayName")) {
                displayNames.put(name, data.get("displayName").getAsString());
            }
            if (data.has("suffix")) {
                suffixes.put(name, data.get("suffix").getAsString());
            } else {
                suffixes.remove(name);
            }
        }
    }

    private void handleRemove(JsonObject obj) {
        JsonArray players = obj.getAsJsonArray("players");
        for (JsonElement elem : players) {
            String name = elem.getAsString();
            displayNames.remove(name);
            suffixes.remove(name);
        }
    }

    private void handleSort(JsonObject obj) {
        JsonArray order = obj.getAsJsonArray("order");
        Map<String, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < order.size(); i++) {
            indexMap.put(order.get(i).getAsString(), i);
        }
        sortIndexMap = indexMap;
    }

    public String getDisplayName(String playerName) {
        return displayNames.get(playerName);
    }

    public String getSuffix(String playerName) {
        return suffixes.get(playerName);
    }

    /**
     * Returns the sort index map, or null if no custom sort order has been set. Players not in the map should sort
     * after all listed players.
     */
    public Map<String, Integer> getSortIndexMap() {
        return sortIndexMap;
    }

    public void clear() {
        displayNames.clear();
        suffixes.clear();
        sortIndexMap = null;
    }
}
