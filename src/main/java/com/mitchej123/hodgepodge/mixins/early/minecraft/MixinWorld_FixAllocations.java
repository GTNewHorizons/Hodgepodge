package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.hax.LongChunkCoordIntPairSet;
import com.mitchej123.hodgepodge.util.ChunkPosUtil;

@Mixin(World.class)
public abstract class MixinWorld_FixAllocations {

    protected Set activeChunkSet = new LongChunkCoordIntPairSet();

    @Shadow
    public List<EntityPlayer> playerEntities;

    @Shadow
    protected abstract int func_152379_p();

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
                    ((LongChunkCoordIntPairSet) this.activeChunkSet).addLong(ChunkPosUtil.toLong(i1 + j, j1 + k));
                }
            }
        }
    }

}
