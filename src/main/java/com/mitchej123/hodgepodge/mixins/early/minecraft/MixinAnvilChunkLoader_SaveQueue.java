package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilChunkLoader.class)
public class MixinAnvilChunkLoader_SaveQueue {

    @Shadow
    private Object syncLockObject;

    @Shadow
    private List<?> chunksToRemove;

    @Unique
    private boolean hodgepodge$queued;

    @Redirect(
            method = "addChunkToPending",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/storage/ThreadedFileIOBase;queueIO(Lnet/minecraft/world/storage/IThreadedFileIO;)V"))
    private void hodgepodge$queueDrain(ThreadedFileIOBase io, IThreadedFileIO loader) {
        synchronized (syncLockObject) {
            if (hodgepodge$queued) return;
            hodgepodge$queued = true;
            // A new identity cannot be mistaken for the previous task while the IO thread removes it.
            io.queueIO(() -> {
                if (loader.writeNextIO()) return true;
                synchronized (syncLockObject) {
                    // A producer may have added work after writeNextIO observed an empty list.
                    if (!chunksToRemove.isEmpty()) return true;
                    hodgepodge$queued = false;
                    return false;
                }
            });
        }
    }
}
