package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.util.ChunkPosUtil;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

@Mixin(World.class)
public abstract class MixinWorld_FixAllocations {

    public LongSet activeChunkLongSet = new LongOpenHashSet();

    @Shadow
    public List<EntityPlayer> playerEntities;

    @Shadow
    protected abstract int func_152379_p();

    @Inject(
            method = "setActivePlayerChunksAndCheckLight",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;clear()V"))
    private void clearActiveChunks(CallbackInfo ci) {
        // Clear our set instead of the original set
        activeChunkLongSet.clear();
    }

    @Redirect(
            method = "setActivePlayerChunksAndCheckLight",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;addAll(Ljava/util/Collection;)Z", ordinal = 0))
    private boolean addAllToOurSet(Set<ChunkCoordIntPair> instance, Collection<ChunkCoordIntPair> c) {
        // Don't add anything to the original set, but to our set instead
        for (ChunkCoordIntPair coord : c) {
            activeChunkLongSet.add(ChunkPosUtil.toLong(coord.chunkXPos, coord.chunkZPos));
        }
        return false;
    }

    @Redirect(
            method = "setActivePlayerChunksAndCheckLight",
            at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0))
    private int cancelExistingForLoop(List instance) {
        // Don't add anything to the original set so cancel the original loop
        return 0;
    }

    @Inject(
            method = "setActivePlayerChunksAndCheckLight",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/profiler/Profiler;endSection()V",
                    shift = At.Shift.BEFORE))
    private void replacedForLoopLessAllocations(CallbackInfo ci) {
        // And instead add it to our long set here
        final int size = this.playerEntities.size();
        for (int i = 0; i < size; ++i) {
            final EntityPlayer entityPlayer = this.playerEntities.get(i);
            final int j = MathHelper.floor_double(entityPlayer.posX / 16.0D);
            final int k = MathHelper.floor_double(entityPlayer.posZ / 16.0D);
            final int l = this.func_152379_p();

            for (int i1 = -l; i1 <= l; ++i1) {
                for (int j1 = -l; j1 <= l; ++j1) {
                    this.activeChunkLongSet.add(ChunkPosUtil.toLong(i1 + j, j1 + k));
                }
            }
        }
    }

}
