package com.mitchej123.hodgepodge.mixins.hooks;

import java.util.Arrays;

import io.netty.buffer.ByteBuf;

/**
 * FMLProxyPacket sends the whole backing array of its payload buffer. Mods that hand it an untrimmed buffer (such as a
 * plain {@code Unpooled.buffer()}) send the buffer's unused capacity as trailing zeros: up to the next power of two, at
 * least 256 bytes per packet.
 */
public final class ProxyPacketPayload {

    private ProxyPacketPayload() {}

    /**
     * The payload's backing array without its unused capacity, the zeros after the written bytes. Everything up to the
     * writer index is kept as before, including bytes before the reader index. The array is returned unchanged if
     * nothing follows the written bytes, if anything after them is not zero, or if nothing was written: a client to
     * server payload of length 0 arrives as null and fails in FML, so empty payloads keep their zeros.
     */
    public static byte[] withoutUnusedCapacity(ByteBuf payload) {
        final byte[] array = payload.array();
        final int end = payload.arrayOffset() + payload.writerIndex();
        if (end <= 0 || end >= array.length) {
            return array;
        }
        for (int i = end; i < array.length; i++) {
            if (array[i] != 0) {
                return array;
            }
        }
        return Arrays.copyOf(array, end);
    }
}
