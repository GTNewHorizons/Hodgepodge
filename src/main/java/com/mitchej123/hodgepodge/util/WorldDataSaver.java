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

    public static final WorldDataSaver INSTANCE = new WorldDataSaver();

    static class WrappedNBTTagCompound {

        public final NBTTagCompound tag;
        public final boolean compressed;
        public final boolean backup;

        public WrappedNBTTagCompound(NBTTagCompound tag, boolean compressed, boolean backup) {
            this.tag = tag;
            this.compressed = compressed;
            this.backup = backup;
        }
    }

    public static final Logger LOGGER = LogManager.getLogger("HodgepodgeWorldDataSaver");

    protected WorldDataSaver() {}

    private final Map<File, WrappedNBTTagCompound> pendingData = Collections.synchronizedMap(new LinkedHashMap<>());

    @Override
    public boolean writeNextIO() {
        final File file;
        final WrappedNBTTagCompound wrapped;
        final NBTTagCompound data;
        final boolean compressed;
        final boolean backup;
        synchronized (pendingData) {
            Iterator<Map.Entry<File, WrappedNBTTagCompound>> it = pendingData.entrySet().iterator();
            if (!it.hasNext()) {
                return false;
            }
            Map.Entry<File, WrappedNBTTagCompound> entry = it.next();
            file = entry.getKey();
            wrapped = entry.getValue();
            data = wrapped.tag;
            backup = wrapped.backup;
            compressed = wrapped.compressed;
            it.remove();

        }
        if (backup) {
            final File backupFile = new File(file.getParentFile(), file.getName() + "_old");
            if (backupFile.exists()) {
                backupFile.delete();
            }
            file.renameTo(backupFile);
        }

        try {
            if (compressed) {
                try (FileOutputStream fileoutputstream = new FileOutputStream(file)) {
                    CompressedStreamTools.writeCompressed(data, fileoutputstream);
                }
            } else CompressedStreamTools.write(data, file);

        } catch (Exception e) {
            LOGGER.error("Failed to write data to file {}", file, e);
            e.printStackTrace();
        }
        return true;
    }

    public void saveData(File file, NBTTagCompound parentTag, boolean compressed, boolean backup) {
        WrappedNBTTagCompound wrapped = new WrappedNBTTagCompound(parentTag, compressed, backup);
        if (pendingData.containsKey(file)) {
            pendingData.replace(file, wrapped);
        } else {
            pendingData.put(file, wrapped);
        }
        ThreadedFileIOBase.threadedIOInstance.queueIO(this);
    }
}
