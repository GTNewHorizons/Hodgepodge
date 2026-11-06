package com.mitchej123.hodgepodge.mixins.early.minecraft.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S07PacketRespawn;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.mixins.interfaces.WorldSeedPacketExt;

import cpw.mods.fml.common.FMLCommonHandler;

@Mixin(S07PacketRespawn.class)
public class MixinS07PacketRespawn_WorldSeed implements WorldSeedPacketExt {

    @Shadow
    private int field_149088_a;

    @Unique
    private boolean hodgepodge$hasWorldSeed;
    @Unique
    private long hodgepodge$worldSeed;

    @Inject(
            method = "<init>(ILnet/minecraft/world/EnumDifficulty;Lnet/minecraft/world/WorldType;Lnet/minecraft/world/WorldSettings$GameType;)V",
            at = @At("TAIL"))
    private void hodgepodge$initWorldSeed(int dimensionId, net.minecraft.world.EnumDifficulty difficulty,
            net.minecraft.world.WorldType worldType, net.minecraft.world.WorldSettings.GameType gameType,
            CallbackInfo ci) {
        if (TweaksConfig.syncWorldSeedToClientWorld) {
            hodgepodge$hasWorldSeed = true;
            hodgepodge$worldSeed = FMLCommonHandler.instance().getMinecraftServerInstance()
                    .worldServerForDimension(this.field_149088_a).getSeed();
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
