package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.NetworkManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NetworkManager.class)
public interface NetworkManagerAccessor {

    @Accessor(value = "isClientSide")
    boolean hodgepodge$isClientSide();
}
