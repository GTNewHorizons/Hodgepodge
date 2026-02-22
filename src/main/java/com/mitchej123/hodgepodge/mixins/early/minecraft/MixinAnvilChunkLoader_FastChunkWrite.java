package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilChunkLoader.class)
public class MixinAnvilChunkLoader_FastChunkWrite {

    @Unique
    private static final int hodgepodge$DEFAULT_BUFFER_SIZE = 8192;
    @Unique
    private static final int hodgepodge$SHRINK_THRESHOLD = 32768;
    @Unique
    private static final int hodgepodge$SHRINK_AFTER_STREAK = 256;

    @Unique
    private ByteArrayOutputStream hodgepodge$nbtBuffer = new ByteArrayOutputStream(hodgepodge$DEFAULT_BUFFER_SIZE);

    @Unique
    private DataOutputStream hodgepodge$nbtDataOutput = new DataOutputStream(hodgepodge$nbtBuffer);

    @Unique
    private int hodgepodge$smallWriteStreak = 0;

    @Redirect(
            method = "writeChunkNBTTags",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/nbt/CompressedStreamTools;write(Lnet/minecraft/nbt/NBTTagCompound;Ljava/io/DataOutput;)V"))
    private void hodgepodge$batchedNBTWrite(NBTTagCompound nbt, DataOutput dataOutput) throws IOException {
        // Write to a reused buffer to avoid multiple allocations and resizes
        hodgepodge$nbtBuffer.reset();
        CompressedStreamTools.func_150663_a(nbt, hodgepodge$nbtDataOutput);
        dataOutput.write(hodgepodge$nbtBuffer.toByteArray());

        // Shrink if we've seen a streak of smaller chunks
        if (hodgepodge$nbtBuffer.size() < hodgepodge$SHRINK_THRESHOLD) {
            if (++hodgepodge$smallWriteStreak >= hodgepodge$SHRINK_AFTER_STREAK) {
                hodgepodge$nbtBuffer = new ByteArrayOutputStream(hodgepodge$DEFAULT_BUFFER_SIZE);
                hodgepodge$nbtDataOutput = new DataOutputStream(hodgepodge$nbtBuffer);
                hodgepodge$smallWriteStreak = 0;
            }
        } else {
            hodgepodge$smallWriteStreak = 0;
        }
    }
}
