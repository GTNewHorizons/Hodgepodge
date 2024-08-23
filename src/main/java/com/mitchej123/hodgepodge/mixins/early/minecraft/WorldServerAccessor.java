package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldServer.class)
public interface WorldServerAccessor {

    @Invoker("wakeAllPlayers")
    void hodgepodge$wakeAllPlayers();
}
