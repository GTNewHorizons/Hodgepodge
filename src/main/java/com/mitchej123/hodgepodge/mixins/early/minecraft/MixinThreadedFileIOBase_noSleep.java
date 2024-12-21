package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.storage.ThreadedFileIOBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThreadedFileIOBase.class)
public class MixinThreadedFileIOBase_noSleep {

    @Redirect(
            method = "Lnet/minecraft/world/storage/ThreadedFileIOBase;processQueue()V",
            at = @At(value = "INVOKE", target = "Ljava/lang/Thread;sleep(J)V", ordinal = 0))
    private void noSleepForTheWicked(long millis) {
        // Do nothing
    }

}
