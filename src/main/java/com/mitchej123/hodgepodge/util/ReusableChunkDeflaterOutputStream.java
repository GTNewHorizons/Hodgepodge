package com.mitchej123.hodgepodge.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import com.mitchej123.hodgepodge.config.SpeedupsConfig;

public class ReusableChunkDeflaterOutputStream extends DeflaterOutputStream {

    private static final ThreadLocal<Deflater> DEFLATER = ThreadLocal.withInitial(Deflater::new);

    private ReusableChunkDeflaterOutputStream(OutputStream out, Deflater def) {
        super(out, def, 8192);
    }

    public static ReusableChunkDeflaterOutputStream create(OutputStream out) {
        final Deflater def = DEFLATER.get();
        def.reset();
        def.setLevel(SpeedupsConfig.chunkCompressionLevel);
        return new ReusableChunkDeflaterOutputStream(out, def);
    }

    @Override
    public void close() throws IOException {
        finish();
        out.close();
    }
}
