package com.mitchej123.hodgepodge.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldDataSaver implements IThreadedFileIO {

    public static final Logger LOGGER = LogManager.getLogger("HodgepodgeWorldDataSaver");

    public WorldDataSaver() {}

    private final Map<File, NBTTagCompound> pendingData = Collections.synchronizedMap(new LinkedHashMap<>());

    @Override
    public boolean writeNextIO() {
        final File file;
        final NBTTagCompound data;
        synchronized (pendingData) {
            Iterator<Map.Entry<File, NBTTagCompound>> it = pendingData.entrySet().iterator();
            if (!it.hasNext()) {
                return false;
            }
            Map.Entry<File, NBTTagCompound> entry = it.next();
            file = entry.getKey();
            data = entry.getValue();
            it.remove();

        }
        try {
            LOGGER.debug("Writing data to file {}", file);
            FileOutputStream fileoutputstream = new FileOutputStream(file);
            CompressedStreamTools.writeCompressed(data, fileoutputstream);
            fileoutputstream.close();

        } catch (IOException e) {
            LOGGER.error("Failed to write data to file {}", file, e);
            e.printStackTrace();
        }
        return true;
    }

    public void saveData(File file, NBTTagCompound parentTag) {
        if (pendingData.containsKey(file)) {
            pendingData.replace(file, parentTag);
        } else {
            pendingData.put(file, parentTag);
        }
        ThreadedFileIOBase.threadedIOInstance.queueIO(this);
    }
}
