package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.Chunk;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public class MixinChunk_FixInvalidEntity {

    @Shadow
    @Final
    public int xPosition;

    @Shadow
    @Final
    public int zPosition;

    @Shadow
    @Final
    private static Logger logger;

    @Inject(
            method = "addEntity",
            at = @At(value = "INVOKE", target = "Ljava/lang/Thread;dumpStack()V"),
            cancellable = true)
    private void hodgepodge$removeInvalidEntity(Entity entity, CallbackInfo ci) {
        if (Double.isNaN(entity.posX) || Double.isNaN(entity.posY)
                || Double.isNaN(entity.posZ)
                || Double.isInfinite(entity.posX)
                || Double.isInfinite(entity.posY)
                || Double.isInfinite(entity.posZ)) {
            logger.info(
                    "[Hodgepodge] Removed invalid Entity {} from chunk ({},{}).",
                    entity,
                    this.xPosition,
                    this.zPosition);
            ci.cancel();
            return;
        }

        double entityChunkX = entity.posX / 16.0D;
        double entityChunkZ = entity.posZ / 16.0D;
        double diffX = Math.abs(entityChunkX - this.xPosition);
        double diffZ = Math.abs(entityChunkZ - this.zPosition);

        // If it's more than 1000 chunks away from its expected position in either direction, it's safe to assume it's
        // invalid
        if (diffX > 1000 || diffZ > 1000) {
            logger.info(
                    "[Hodgepodge] Removed invalid Entity {} from chunk ({},{}) because it was {} chunks away from its expected position.",
                    entity,
                    this.xPosition,
                    this.zPosition,
                    diffX + diffZ);
            ci.cancel();
        }
    }
}
