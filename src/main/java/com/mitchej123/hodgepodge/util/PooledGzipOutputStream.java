package com.mitchej123.hodgepodge.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

/**
 * GZIP output stream that pools Deflater instances to avoid native cleanup overhead.
 */
public class PooledGzipOutputStream extends DeflaterOutputStream {

    private static final ThreadLocal<Deflater> DEFLATER_POOL = ThreadLocal
            .withInitial(() -> new Deflater(Deflater.DEFAULT_COMPRESSION, true));

    private static final byte[] GZIP_HEADER = { 0x1f, (byte) 0x8b, // Magic number
            0x08, // Compression method (deflate)
            0x00, // Flags
            0x00, 0x00, 0x00, 0x00, // Modification time
            0x00, // Extra flags
            0x00 // OS (unknown)
    };

    private final CRC32 crc = new CRC32();
    private int size;

    private PooledGzipOutputStream(ByteArrayOutputStream out, Deflater deflater) throws IOException {
        super(out, deflater, 512, true);
        out.write(GZIP_HEADER);
    }

    public static byte[] compressNBT(NBTTagCompound nbt) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
        Deflater deflater = DEFLATER_POOL.get();
        deflater.reset();

        try (PooledGzipOutputStream gzos = new PooledGzipOutputStream(baos, deflater);
                DataOutputStream dos = new DataOutputStream(gzos)) {
            CompressedStreamTools.func_150663_a(nbt, dos);
        }

        return baos.toByteArray();
    }

    @Override
    public void write(int b) throws IOException {
        super.write(b);
        crc.update(b);
        size++;
    }

    @Override
    public void write(byte[] buf, int off, int len) throws IOException {
        super.write(buf, off, len);
        crc.update(buf, off, len);
        size += len;
    }

    @Override
    public void close() throws IOException {
        finish();
        writeTrailer();
        out.close();
        // Don't call def.end() - reset for reuse
        def.reset();
    }

    private void writeTrailer() throws IOException {
        int crcVal = (int) crc.getValue();
        out.write(crcVal & 0xff);
        out.write((crcVal >> 8) & 0xff);
        out.write((crcVal >> 16) & 0xff);
        out.write((crcVal >> 24) & 0xff);

        out.write(size & 0xff);
        out.write((size >> 8) & 0xff);
        out.write((size >> 16) & 0xff);
        out.write((size >> 24) & 0xff);
    }
}
