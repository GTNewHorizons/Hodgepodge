package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Queue;

import net.minecraft.network.NetworkManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NetworkManager.class)
public interface NetworkManagerInboundAccessor {

    @Accessor(value = "receivedPacketsQueue")
    Queue<?> hodgepodge$getReceivedPacketsQueue();
}
