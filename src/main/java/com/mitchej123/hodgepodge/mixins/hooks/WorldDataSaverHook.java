package com.mitchej123.hodgepodge.mixins.hooks;

import java.io.File;

import net.minecraft.nbt.NBTTagCompound;

import com.mitchej123.hodgepodge.util.WorldDataSaver;

public class WorldDataSaverHook {

    public static void saveWorldDataUncompressed(File file, NBTTagCompound tag) {
        WorldDataSaver.INSTANCE.saveData(file, tag, false, false);
    }

    public static void saveWorldData(File file, NBTTagCompound tag) {
        WorldDataSaver.INSTANCE.saveData(file, tag, true, false);
    }

    public static void saveWorldDataUncompressedBackup(File file, NBTTagCompound tag) {
        WorldDataSaver.INSTANCE.saveData(file, tag, false, true);
    }

    public static void saveWorldDataBackup(File file, NBTTagCompound tag) {
        WorldDataSaver.INSTANCE.saveData(file, tag, true, true);
    }
}
