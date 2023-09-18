package com.mitchej123.hodgepodge.mixin.mixins.early.minecraft.packets;

import net.minecraft.entity.DataWatcher;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.mitchej123.hodgepodge.util.PacketPrevalidation;

@Mixin(DataWatcher.class)
public abstract class MixinDataWatcher {

    /**
     * @author eigenraven
     * @reason Find out which entity data overflows a packet before it gets sent
     */
    @ModifyArg(
            method = "Lnet/minecraft/entity/DataWatcher;writeWatchableObjectToPacketBuffer(Lnet/minecraft/network/PacketBuffer;Lnet/minecraft/entity/DataWatcher$WatchableObject;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketBuffer;writeStringToBuffer(Ljava/lang/String;)V"))
    private static String hodgepodge$validateStringLength(String str) {
        PacketPrevalidation.validateLimitedString(str, 32767);
        return str;
    }
}
