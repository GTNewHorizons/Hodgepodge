package com.mitchej123.hodgepodge.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mitchej123.hodgepodge.Common;

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

    private final Map<File, WrappedNBTTagCompound> pendingData = new LinkedHashMap<>();
    private final Map<File, WrappedNBTTagCompound> failedData = new LinkedHashMap<>();
    private boolean queued;

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
                queued = false;
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
        try {
            writeData(file, data, compressed, backup);
            synchronized (pendingData) {
                failedData.remove(file);
            }
        } catch (Exception e) {
            synchronized (pendingData) {
                if (!pendingData.containsKey(file)) failedData.put(file, wrapped);
            }
            LOGGER.error("Failed to write data to file {}", file, e);
            Common.log.error(e);
        }
        return true;
    }

    public void saveData(File file, NBTTagCompound parentTag, boolean compressed, boolean backup) {
        WrappedNBTTagCompound wrapped = new WrappedNBTTagCompound(parentTag, compressed, backup);
        synchronized (pendingData) {
            failedData.forEach(pendingData::putIfAbsent);
            failedData.clear();
            pendingData.put(file, wrapped);
            if (queued) return;
            queued = true;
            queueIO(new DrainTask());
        }
    }

    protected void queueIO(IThreadedFileIO task) {
        ThreadedFileIOBase.threadedIOInstance.queueIO(task);
    }

    static void writeData(File file, NBTTagCompound data, boolean compressed, boolean backup) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (compressed) {
            CompressedStreamTools.writeCompressed(data, bytes);
        } else {
            try (DataOutputStream output = new DataOutputStream(bytes)) {
                CompressedStreamTools.write(data, output);
            }
        }

        Path target = file.toPath().toAbsolutePath();
        Path parent = target.getParent();
        Files.createDirectories(parent);
        Path temporary = Files.createTempFile(parent, "." + file.getName() + "-", ".tmp");
        Path old = target.resolveSibling(file.getName() + "_old");
        Path oldTemporary = null;
        try {
            try (FileOutputStream output = new FileOutputStream(temporary.toFile())) {
                output.write(bytes.toByteArray());
                output.getFD().sync();
            }
            if (backup && Files.exists(target)) {
                oldTemporary = Files.createTempFile(parent, "." + file.getName() + "-old-", ".tmp");
                Files.copy(
                        target,
                        oldTemporary,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES);
                try (FileChannel channel = FileChannel.open(oldTemporary, StandardOpenOption.WRITE)) {
                    channel.force(true);
                }
                Files.move(oldTemporary, old, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            }
            Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            if (oldTemporary != null) Files.deleteIfExists(oldTemporary);
            Files.deleteIfExists(temporary);
        }
    }

    private final class DrainTask implements IThreadedFileIO {

        @Override
        public boolean writeNextIO() {
            return WorldDataSaver.this.writeNextIO();
        }
    }
}
