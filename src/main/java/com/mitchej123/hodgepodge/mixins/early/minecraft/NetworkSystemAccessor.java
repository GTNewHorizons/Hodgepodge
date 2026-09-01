package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.network.NetworkSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NetworkSystem.class)
public interface NetworkSystemAccessor {

    /** Every live connection, including ones still negotiating the FML handshake. */
    @Accessor(value = "networkManagers")
    List<?> hodgepodge$getNetworkManagers();
}
