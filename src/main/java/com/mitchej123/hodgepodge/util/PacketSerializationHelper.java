package com.mitchej123.hodgepodge.util;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/** Utilities for extending the packet serialization format */
public class PacketSerializationHelper {

    /**
     * Behaves like {@link cpw.mods.fml.common.network.ByteBufUtils#readVarShort(ByteBuf)} for values smaller than 21
     * bits, or reads a full integer if larger.
     */
    public static int readExtendedVarShortOrInt(ByteBuf buf) {
        int low = buf.readUnsignedShort();
        int high = 0;
        if ((low & 0x8000) != 0) {
            low = low & 0x7FFF;
            high = buf.readUnsignedByte();
        }
        final int shortValue = ((high & 0xFF) << 15) | low;
        if (shortValue != 0x7F_FFFF) {
            return shortValue;
        } else {
            return ByteBufUtils.readVarInt(buf, 5);
        }
    }

    /**
     * Behaves like {@link cpw.mods.fml.common.network.ByteBufUtils#writeVarShort(ByteBuf, int)} for values smaller than
     * 21 bits, or reads a full integer if larger.
     */
    public static void writeExtendedVarShortOrInt(ByteBuf buf, int toWrite) {
        if (toWrite != (toWrite & 0x7F_FFFF) || toWrite == 0x7F_FFFF) {
            // too large to fit in the original representation
            buf.writeShort(0xFF_FFFF);
            buf.writeByte(0xFF);
            ByteBufUtils.writeVarInt(buf, toWrite, 5);
            return;
        }
        int low = toWrite & 0x7FFF;
        int high = (toWrite & 0x7F8000) >> 15;
        if (high != 0) {
            low = low | 0x8000;
        }
        buf.writeShort(low);
        if (high != 0) {
            buf.writeByte(high);
        }
    }

    private static void validateExtendedShort(ByteBuf scratch, int value) {
        scratch.clear();
        writeExtendedVarShortOrInt(scratch, value);
        final int readback = readExtendedVarShortOrInt(scratch);
        if (value != readback) {
            System.err.printf("0x%08x != 0x%08x%n", value, readback);
            throw new RuntimeException("mismatch");
        }
        if (scratch.isReadable()) {
            System.err.printf("0x%08x overflow%n", value);
            throw new RuntimeException("overflow");
        }
        if (value >= 0 && value < 0x7F_FFFF) {
            scratch.resetReaderIndex();
            final int fmlReadback = ByteBufUtils.readVarShort(scratch);
            if (fmlReadback != value) {
                System.err.printf("FML: 0x%08x != 0x%08x%n", value, fmlReadback);
                throw new RuntimeException("FML mismatch");
            }
        }
    }

    // A simple test program to validate the implementation for all possible encodings
    // This is quite slow to run through, so not automatically tested.
    public static void main(String[] args) {
        final ByteBuf scratchBuffer = Unpooled.buffer(16);
        for (int value = 0; value < Integer.MAX_VALUE; value++) {
            if (value % 0x1000_0000 == 0) {
                System.out.printf("Checking 0x%08x%n", value);
            }
            validateExtendedShort(scratchBuffer, value);
        }
        validateExtendedShort(scratchBuffer, Integer.MAX_VALUE); // Can't iterate to MAX_VALUE without causing an
                                                                 // integer overflow
        System.out.println("All positive integers checked.");
    }
}
