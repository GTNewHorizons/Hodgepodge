package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Set;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.hax.LongChunkCoordIntPairSet;
import com.mitchej123.hodgepodge.mixins.interfaces.MutableChunkCoordIntPair;

@Mixin(AnvilChunkLoader.class)
public class MixinAnvilChunkLoader_FixAllocations {

    @Shadow
    private Set<ChunkCoordIntPair> pendingAnvilChunksCoordinates = new LongChunkCoordIntPairSet();

    private final MutableChunkCoordIntPair reusableCCIP = (MutableChunkCoordIntPair) new ChunkCoordIntPair(0, 0);

    @Redirect(method = "chunkExists", at = @At(value = "NEW", target = "Lnet/minecraft/world/ChunkCoordIntPair;"))
    private ChunkCoordIntPair chunkExistsReusable(int i, int j) {
        return (ChunkCoordIntPair) reusableCCIP.setChunkPos(i, j);
    }
}
