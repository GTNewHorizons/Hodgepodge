package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Chunk.class)
public class MixinChunk_SendWithoutPopulation {

    @Shadow
    public boolean field_150815_m; // field_150815_m = isChunkTicked

    /**
     * @author hodgepodge
     * @reason Allow chunks to be sent before population and lighting complete. Supernova syncs lighting corrections via
     *         its worker thread.
     */
    @Overwrite
    public boolean func_150802_k() {
        return this.field_150815_m;
    }
}
