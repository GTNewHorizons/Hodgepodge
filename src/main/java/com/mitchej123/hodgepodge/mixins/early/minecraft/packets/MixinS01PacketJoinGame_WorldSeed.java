package com.mitchej123.hodgepodge.mixins.early.minecraft.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S01PacketJoinGame;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.mixins.interfaces.WorldSeedPacketExt;

import cpw.mods.fml.common.FMLCommonHandler;

// Lower priority than MixinS01PacketJoinGame_FixDimensionID so this trailing payload is appended/read after it.
@Mixin(value = S01PacketJoinGame.class, priority = 900)
public class MixinS01PacketJoinGame_WorldSeed implements WorldSeedPacketExt {

    @Shadow
    private int field_149202_d;

    @Unique
    private boolean hodgepodge$hasWorldSeed;
    @Unique
    private long hodgepodge$worldSeed;

    @Inject(
            method = "<init>(ILnet/minecraft/world/WorldSettings$GameType;ZILnet/minecraft/world/EnumDifficulty;ILnet/minecraft/world/WorldType;)V",
            at = @At("TAIL"))
    private void hodgepodge$initWorldSeed(int entityId, net.minecraft.world.WorldSettings.GameType gameType,
            boolean hardcore, int dimensionId, net.minecraft.world.EnumDifficulty difficulty, int maxPlayers,
            net.minecraft.world.WorldType worldType, CallbackInfo ci) {
        if (TweaksConfig.syncWorldSeedToClientWorld) {
            hodgepodge$hasWorldSeed = true;
            hodgepodge$worldSeed = FMLCommonHandler.instance().getMinecraftServerInstance()
                    .worldServerForDimension(this.field_149202_d).getSeed();
        }
    }

    @Inject(method = "writePacketData", at = @At("TAIL"))
    private void hodgepodge$writeWorldSeed(PacketBuffer data, CallbackInfo ci) {
        if (TweaksConfig.syncWorldSeedToClientWorld) {
            data.writeLong(hodgepodge$worldSeed);
        }
    }

    @Inject(method = "readPacketData", at = @At("TAIL"))
    private void hodgepodge$readWorldSeed(PacketBuffer data, CallbackInfo ci) {
        if (data.readableBytes() >= 8) {
            hodgepodge$hasWorldSeed = true;
            hodgepodge$worldSeed = data.readLong();
        } else {
            hodgepodge$hasWorldSeed = false;
            hodgepodge$worldSeed = 0L;
        }
    }

    @Override
    public boolean hodgepodge$hasWorldSeed() {
        return hodgepodge$hasWorldSeed;
    }

    @Override
    public long hodgepodge$getWorldSeed() {
        return hodgepodge$worldSeed;
    }
}
