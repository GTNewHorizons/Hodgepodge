package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;

import net.minecraft.world.chunk.storage.RegionFile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.util.ReusableChunkDeflaterOutputStream;

@Mixin(RegionFile.class)
public class MixinRegionFile_FastChunkCompression {

    @Redirect(
            method = "getChunkDataOutputStream",
            at = @At(value = "NEW", target = "Ljava/util/zip/DeflaterOutputStream;"))
    private DeflaterOutputStream hodgepodge$pooledDeflater(OutputStream out) {
        return ReusableChunkDeflaterOutputStream.create(out);
    }
}
