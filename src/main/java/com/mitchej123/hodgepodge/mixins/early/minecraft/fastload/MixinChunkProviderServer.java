package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.mitchej123.hodgepodge.config.SpeedupsConfig;

@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer {

    @ModifyConstant(method = "unloadQueuedChunks", constant = @Constant(intValue = 100, ordinal = 0))
    private int hodgepodge$fastChunkDiscard(int original) {
        return SpeedupsConfig.maxUnloadSpeed;
    }
}
