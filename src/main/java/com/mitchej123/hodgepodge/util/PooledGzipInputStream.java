package com.mitchej123.hodgepodge.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

/**
 * GZIPInputStream equivalent that pools Inflater instances via ThreadLocal. Subclasses InflaterInputStream to reuse
 * JDK's inflate logic while avoiding the native Inflater.end() overhead on every close().
 */
public class PooledGzipInputStream extends InflaterInputStream {

    private static final ThreadLocal<Inflater> INFLATER_POOL = ThreadLocal.withInitial(() -> new Inflater(true));

    private PooledGzipInputStream(InputStream in, Inflater inf) throws IOException {
        super(in, inf, 512);
        readHeader();
    }

    public static PooledGzipInputStream create(byte[] data) throws IOException {
        Inflater inflater = INFLATER_POOL.get();
        inflater.reset();
        return new PooledGzipInputStream(new ByteArrayInputStream(data), inflater);
    }

    public static NBTTagCompound readNBT(byte[] data, NBTSizeTracker sizeTracker) throws IOException {
        try (PooledGzipInputStream gzis = create(data); DataInputStream dis = new DataInputStream(gzis)) {
            return CompressedStreamTools.func_152456_a(dis, sizeTracker);
        }
    }

    @Override
    public void close() throws IOException {
        // Reset for reuse instead of end() - the pooled Inflater stays alive
        inf.reset();
        in.close();
    }

    // GZIP header parsing - simplified from java.util.zip.GZIPInputStream
    private void readHeader() throws IOException {
        // Magic number (2 bytes)
        if (readUShort() != 0x8b1f) {
            throw new ZipException("Not in GZIP format");
        }
        // Compression method (1 byte) - must be 8 (deflate)
        if (readUByte() != 8) {
            throw new ZipException("Unsupported compression method");
        }
        // Flags (1 byte)
        int flg = readUByte();
        // Skip MTIME, XFL, OS (6 bytes)
        skipBytes(6);
        // Optional extra field
        if ((flg & 0x04) != 0) {
            skipBytes(readUShort());
        }
        // Optional file name (null-terminated)
        if ((flg & 0x08) != 0) {
            while (readUByte() != 0) { /* skip */ }
        }
        // Optional comment (null-terminated)
        if ((flg & 0x10) != 0) {
            while (readUByte() != 0) { /* skip */ }
        }
        // Optional header CRC (2 bytes)
        if ((flg & 0x02) != 0) {
            skipBytes(2);
        }
    }

    private int readUByte() throws IOException {
        int b = in.read();
        if (b == -1) throw new EOFException();
        return b;
    }

    private int readUShort() throws IOException {
        int b1 = readUByte();
        int b2 = readUByte();
        return (b2 << 8) | b1;
    }

    private void skipBytes(int n) throws IOException {
        while (n-- > 0) {
            if (in.read() == -1) throw new EOFException();
        }
    }
}
