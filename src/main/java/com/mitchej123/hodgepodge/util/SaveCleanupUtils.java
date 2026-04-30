package com.mitchej123.hodgepodge.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class SaveCleanupUtils {

    private SaveCleanupUtils() {}

    public static UUID getUUIDForCurrentUser() {

        String username = Minecraft.getMinecraft().getSession().getUsername();

        File usernameFile = new File(Minecraft.getMinecraft().mcDataDir, "usernamecache.json");
        if (!usernameFile.exists()) {
            return null;
        }

        try (FileReader reader = new FileReader(usernameFile)) {
            JsonObject usernameObj = new JsonParser().parse(reader).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : usernameObj.entrySet()) {
                JsonElement value = entry.getValue();
                if (value != null && username.equalsIgnoreCase(value.getAsString())) {
                    try {
                        return UUID.fromString(entry.getKey());
                    } catch (IllegalArgumentException ignored) {
                        return null;
                    }
                }

            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    public static UUID getUUIDFromBytes() {
        String username = Minecraft.getMinecraft().getSession().getUsername();
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(UTF_8));
    }

    public static String getVisualProspectingWorldId(String McDataDir, String worldFolder) {
        File vpDat = (Paths.get(McDataDir, "saves", worldFolder, "data", "visualprospecting.dat")).toFile();
        if (!vpDat.exists()) {
            return null;
        }

        try (FileInputStream vpFIS = new FileInputStream(vpDat)) {
            NBTTagCompound vpNBT = CompressedStreamTools.readCompressed(vpFIS);
            if (vpNBT.hasKey("data")) {
                NBTTagCompound data = vpNBT.getCompoundTag("data");
                if (data.hasKey("wId")) {
                    return data.getString("wId");
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
