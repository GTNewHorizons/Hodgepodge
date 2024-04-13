package com.mitchej123.hodgepodge.mixins.late.voxelmap.chunk;

import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.thevoxelbox.voxelmap.b.b;

@Mixin(b.class)
public class MixinCachedRegion {

    @Inject(
            at = @At("HEAD"),
            cancellable = true,
            method = "do(Lnet/minecraft/world/chunk/Chunk;II)V", // void loadChunkData(Chunk, int, int)
            remap = false)
    private void hodgepodge$loadChunkData(Chunk var1, int var2, int var3, CallbackInfo ci) {
        if (var1 == null) ci.cancel();
    }

}
