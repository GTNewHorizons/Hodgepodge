package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Iterator;
import java.util.Set;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.ChunkCoordIntPair;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.hax.LongChunkCoordIntPairSet;

@Mixin(WorldClient.class)
public abstract class MixinWorldClient_FixAllocations {

    @Shadow
    private @Final Set<ChunkCoordIntPair> previousActiveChunkSet = new LongChunkCoordIntPairSet();

    @Redirect(
            method = "func_147456_g",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"))
    private Iterator<ChunkCoordIntPair> fixAllocations(Set<ChunkCoordIntPair> instance) {
        return ((LongChunkCoordIntPairSet) instance).unsafeIterator();
    }

}
