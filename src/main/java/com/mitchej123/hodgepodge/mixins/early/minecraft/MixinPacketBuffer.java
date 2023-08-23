package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.PacketBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.mitchej123.hodgepodge.Common;

@Mixin(PacketBuffer.class)
public class MixinPacketBuffer {
    @ModifyArg(
            method = "Lnet/minecraft/network/PacketBuffer;readNBTTagCompoundFromBuffer()Lnet/minecraft/nbt/NBTTagCompound;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTSizeTracker;<init>(J)V"))
    private long hodgepodge$modifyMaxNetworkNBTSizeLimit(long original) {
        return Common.config.maxNetworkNbtSizeLimit;
    }
}
