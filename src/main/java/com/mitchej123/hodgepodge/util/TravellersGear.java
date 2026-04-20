package com.mitchej123.hodgepodge.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.FixesConfig;

import cpw.mods.fml.common.FMLCommonHandler;

public class TravellersGear {

    private static File dataFile;
    private static NBTTagCompound nbtSaveData;

    public static void returnTGItems(EntityPlayer player) {
        if (nbtSaveData == null) {
            Common.log.info("[TG Recovery] No player data loaded.");
            return;
        }

        NBTTagCompound data = nbtSaveData.getCompoundTag("data");
        NBTTagList playerList = data.getTagList("playerList", 10);
        UUID playerUUID = player.getUniqueID();

        boolean changed = false;
        for (int i = playerList.tagCount() - 1; i >= 0; i--) {
            NBTTagCompound playerTag = playerList.getCompoundTagAt(i);
            if (playerTag.hasKey("UUIDMost") && playerTag.hasKey("UUIDLeast")) {
                UUID tagUUID = new UUID(playerTag.getLong("UUIDMost"), playerTag.getLong("UUIDLeast"));
                if (playerUUID.equals(tagUUID)) {
                    restoreInventory(player, playerTag);
                    playerList.removeTag(i);
                    changed = true;
                }
            }
        }

        if (changed) {
            if (playerList.tagCount() == 0) {
                deleteFile();
            } else {
                saveDataToFile();
            }
        }
    }

    private static void restoreInventory(EntityPlayer player, NBTTagCompound playerTag) {
        if (!playerTag.hasKey("Inventory", 9)) return;
        NBTTagList inventory = playerTag.getTagList("Inventory", 10);
        for (int j = 0; j < inventory.tagCount(); j++) {
            ItemStack stack = ItemStack.loadItemStackFromNBT(inventory.getCompoundTagAt(j));
            if (stack != null) {
                if (!player.inventory.addItemStackToInventory(stack)) {
                    player.entityDropItem(stack, 0);
                }
            }
        }
        player.inventoryContainer.detectAndSendChanges();
        Common.log.info("[TG Recovery] Restored items to {}.", player.getCommandSenderName());
    }

    public static void initialize() {
        WorldServer world = DimensionManager.getWorld(0);
        dataFile = new File(world.getSaveHandler().getWorldDirectory(), "data/TG-SaveData.dat");

        if (!dataFile.exists() || dataFile.length() == 0) {
            Common.log.info("[TG Recovery] No TG-SaveData.dat file found.");
            return;
        }

        try {
            boolean isCompressed;
            // Detect gzip signature
            try (DataInputStream din = new DataInputStream(new FileInputStream(dataFile))) {
                int b0 = din.readUnsignedByte();
                int b1 = din.readUnsignedByte();
                isCompressed = (b0 == 0x1F && b1 == 0x8B);
            }
            // Should solve problems caused by versions of Hodgepodge between 2.6.96 and 2.7.16. Sorry!
            if (isCompressed) {
                try (FileInputStream in = new FileInputStream(dataFile)) {
                    nbtSaveData = CompressedStreamTools.readCompressed(in);
                    Common.log.info("[TG Recovery] Loaded compressed TG-SaveData.dat");
                }
            } else {
                nbtSaveData = CompressedStreamTools.read(dataFile);
                Common.log.warn("[TG Recovery] Loaded uncompressed TG-SaveData.dat (fallback)");
            }
            NBTTagCompound data = nbtSaveData.getCompoundTag("data");
            NBTTagList playerList = data.getTagList("playerList", 10);
            Common.log.info("[TG Recovery] Loaded TG-SaveData.dat with {} entries.", playerList.tagCount());
        } catch (Exception e) {
            Common.log.error("[TG Recovery] Failed to load TG-SaveData.dat.", e);
        }
    }

    private static void saveDataToFile() {
        try (FileOutputStream out = new FileOutputStream(dataFile)) {
            CompressedStreamTools.writeCompressed(nbtSaveData, out);
            Common.log.info("[TG Recovery] Saved updated TG-SaveData.dat.");
        } catch (IOException e) {
            Common.log.error("[TG Recovery] Failed to save TG-SaveData.dat.", e);
        }
    }

    private static void deleteFile() {
        if (dataFile.delete()) {
            Common.log.info("[TG Recovery] TG-SaveData.dat is empty, file deleted.");
            nbtSaveData = null;
        } else {
            Common.log.warn("[TG Recovery] Failed to delete empty TG-SaveData.dat.");
        }
        if (FMLCommonHandler.instance().getSide().isServer()) {
            FixesConfig.returnTravellersGearItems = false;
            Common.log.info("[TG Recovery] Automatically disabled recovery config on server with no TG-SaveData.dat.");
        }
    }
}
