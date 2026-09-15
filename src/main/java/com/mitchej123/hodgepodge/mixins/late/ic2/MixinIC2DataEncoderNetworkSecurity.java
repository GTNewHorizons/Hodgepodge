package com.mitchej123.hodgepodge.mixins.late.ic2;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Array;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import ic2.core.network.DataEncoder;

@Mixin(DataEncoder.class)
public class MixinIC2DataEncoderNetworkSecurity {

    @Unique
    private static final int hodgepodge$MAX_ARRAY_LENGTH = 32_768;

    @Unique
    private static final int hodgepodge$MAX_DECODE_DEPTH = 64;

    @Unique
    private static final long hodgepodge$MAX_NBT_BYTES = 2L * 1024L * 1024L;

    @Unique
    private static final ThreadLocal<Integer> hodgepodge$decodeDepth = ThreadLocal.withInitial(() -> 0);

    @WrapMethod(method = "decode(Ljava/io/DataInputStream;I)Ljava/lang/Object;", remap = false)
    private static Object hodgepodge$boundDecode(DataInputStream input, int type, Operation<Object> original)
            throws IOException {
        if (type < 0 || type > 33) {
            throw new IOException("Invalid IC2 network data type: " + type);
        }

        int depth = hodgepodge$decodeDepth.get();
        if (depth >= hodgepodge$MAX_DECODE_DEPTH) {
            throw new IOException("IC2 network data is nested too deeply");
        }

        hodgepodge$decodeDepth.set(depth + 1);
        try {
            return original.call(input, type);
        } finally {
            hodgepodge$decodeDepth.set(depth);
        }
    }

    @Redirect(
            method = "decode(Ljava/io/DataInputStream;I)Ljava/lang/Object;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/reflect/Array;newInstance(Ljava/lang/Class;I)Ljava/lang/Object;"),
            remap = false)
    private static Object hodgepodge$boundArray(Class<?> componentType, int length) throws IOException {
        if (length < 0 || length > hodgepodge$MAX_ARRAY_LENGTH) {
            throw new IOException("Invalid IC2 network array length: " + length);
        }
        return Array.newInstance(componentType, length);
    }

    @Redirect(
            method = "decode(Ljava/io/DataInputStream;I)Ljava/lang/Object;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/nbt/CompressedStreamTools;read(Ljava/io/DataInputStream;)Lnet/minecraft/nbt/NBTTagCompound;",
                    remap = true),
            remap = false)
    private static NBTTagCompound hodgepodge$readBoundedNbt(DataInputStream input) throws IOException {
        return CompressedStreamTools.func_152456_a(input, new NBTSizeTracker(hodgepodge$MAX_NBT_BYTES));
    }

    @Redirect(
            method = "decode(Ljava/io/DataInputStream;I)Ljava/lang/Object;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getTileEntity(III)Lnet/minecraft/tileentity/TileEntity;",
                    remap = true),
            remap = false)
    private static TileEntity hodgepodge$getLoadedTileEntity(World world, int x, int y, int z) {
        // getTileEntity may provide or generate the client-selected chunk; blockExists is deliberately no-load.
        return world.blockExists(x, y, z) ? world.getTileEntity(x, y, z) : null;
    }

    /**
     * @author GTNH Team
     * @reason Bound malformed variable-length integers received from untrusted clients.
     */
    @Overwrite(remap = false)
    public static int readVarInt(DataInputStream input) throws IOException {
        int value = 0;
        for (int shift = 0; shift < 32; shift += 7) {
            int part = input.readUnsignedByte();
            if (shift == 28 && (part & 0xF8) != 0) {
                throw new IOException("IC2 network VarInt exceeds the supported positive integer range");
            }
            value |= (part & 0x7F) << shift;
            if ((part & 0x80) == 0) {
                return value;
            }
        }
        throw new IOException("IC2 network VarInt is too long");
    }
}
