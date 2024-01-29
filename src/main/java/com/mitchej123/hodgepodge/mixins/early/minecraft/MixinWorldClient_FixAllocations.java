package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Set;

import net.minecraft.client.multiplayer.WorldClient;

import org.spongepowered.asm.mixin.Mixin;

import com.mitchej123.hodgepodge.hax.LongChunkCoordIntPairSet;

@Mixin(WorldClient.class)
public abstract class MixinWorldClient_FixAllocations {

    private final Set previousActiveChunkSet = new LongChunkCoordIntPairSet();

}
