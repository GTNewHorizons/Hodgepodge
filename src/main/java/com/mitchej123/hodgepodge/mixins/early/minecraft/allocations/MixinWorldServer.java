package com.mitchej123.hodgepodge.mixins.early.minecraft.allocations;

import java.util.Iterator;
import java.util.Set;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.mixins.interfaces.MutableChunkCoordIntPair;
import com.mitchej123.hodgepodge.util.ChunkPosUtil;

import it.unimi.dsi.fastutil.longs.LongIterator;

@Mixin(WorldServer.class)
public class MixinWorldServer {

    @Redirect(
            method = "func_147456_g",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"))
    private Iterator redirectIterator(Set set) {
        return ((MixinWorld) (Object) this).activeChunkLongSet.iterator();
    }

    private static MutableChunkCoordIntPair reusableChunkCoordIntPair = (MutableChunkCoordIntPair) new ChunkCoordIntPair(
            0,
            0);

    @Redirect(
            method = "func_147456_g",
            at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"),
            remap = false)
    private Object onGetPersistentChunksNext(Iterator instance) {
        // Return the activeChunkLongSet iterator
        final long chunkPos = ((LongIterator) instance).nextLong();
        reusableChunkCoordIntPair.setChunkXPos(ChunkPosUtil.getPackedX(chunkPos));
        reusableChunkCoordIntPair.setChunkZPos(ChunkPosUtil.getPackedZ(chunkPos));
        return reusableChunkCoordIntPair;
    }
}
