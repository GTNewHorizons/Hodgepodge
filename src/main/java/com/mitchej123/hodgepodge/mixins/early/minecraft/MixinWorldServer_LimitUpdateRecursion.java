package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.FixesConfig;

@Mixin(WorldServer.class)
public class MixinWorldServer_LimitUpdateRecursion {

    public int hodgepodge$currentBlockUpdateRecursiveCalls = 0;

    @Inject(method = "scheduleBlockUpdateWithPriority", at = @At("HEAD"), cancellable = true)
    void hodgepodge$incrementBlockUpdateRecursionCounter(int x, int y, int z, Block block, int tickDelay, int priority,
            CallbackInfo ci) {
        if (hodgepodge$currentBlockUpdateRecursiveCalls >= FixesConfig.limitRecursiveBlockUpdateDepth) {
            final StackOverflowError error = new StackOverflowError(
                    String.format(
                            "Too many recursive block updates (%d) at world %d, block %s (%d, %d, %d) - aborting further block updates",
                            hodgepodge$currentBlockUpdateRecursiveCalls,
                            ((WorldServer) (Object) this).provider.dimensionId,
                            block,
                            x,
                            y,
                            z));
            Common.log.error(error.getMessage(), error);
            ci.cancel();
            return;
        }
        hodgepodge$currentBlockUpdateRecursiveCalls++;
    }

    @Inject(method = "scheduleBlockUpdateWithPriority", at = @At("RETURN"))
    void hodgepodge$decrementBlockUpdateRecursionCounter(int x, int y, int z, Block block, int tickDelay, int priority,
            CallbackInfo ci) {
        hodgepodge$currentBlockUpdateRecursiveCalls = Math.max(0, hodgepodge$currentBlockUpdateRecursiveCalls - 1);
    }
}
