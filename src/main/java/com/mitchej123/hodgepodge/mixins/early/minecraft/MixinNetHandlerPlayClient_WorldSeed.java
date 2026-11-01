package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S07PacketRespawn;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.interfaces.WorldSeedPacketExt;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient_WorldSeed {

    @ModifyArg(
            method = "handleJoinGame",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/WorldSettings;<init>(JLnet/minecraft/world/WorldSettings$GameType;ZZLnet/minecraft/world/WorldType;)V"),
            index = 0)
    private long hodgepodge$replaceJoinSeed(long original, @Local(argsOnly = true) S01PacketJoinGame packetIn) {
        if (packetIn instanceof WorldSeedPacketExt ext && ext.hodgepodge$hasWorldSeed()) {
            return ext.hodgepodge$getWorldSeed();
        }
        return original;
    }

    @ModifyArg(
            method = "handleRespawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/WorldSettings;<init>(JLnet/minecraft/world/WorldSettings$GameType;ZZLnet/minecraft/world/WorldType;)V"),
            index = 0)
    private long hodgepodge$replaceRespawnSeed(long original, @Local(argsOnly = true) S07PacketRespawn packetIn) {
        if (packetIn instanceof WorldSeedPacketExt ext && ext.hodgepodge$hasWorldSeed()) {
            return ext.hodgepodge$getWorldSeed();
        }
        return original;
    }
}
