package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import net.minecraft.entity.Entity;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

@Mixin(value = ServerConfigurationManager.class, remap = false)
public class ServerConfigurationManager_FixTeleporter {

    @Inject(
            method = "transferEntityToWorld(Lnet/minecraft/entity/Entity;ILnet/minecraft/world/WorldServer;Lnet/minecraft/world/WorldServer;Lnet/minecraft/world/Teleporter;)V",
            at = @At("HEAD"))
    private void setFinding(Entity entityIn, int originDim, WorldServer origin, WorldServer destination,
            Teleporter teleporter, CallbackInfo ci, @Share("prevFindingSpawnPoint") LocalBooleanRef prev) {
        prev.set(destination.findingSpawnPoint);
        destination.findingSpawnPoint = true;
    }

    @Inject(
            method = "transferEntityToWorld(Lnet/minecraft/entity/Entity;ILnet/minecraft/world/WorldServer;Lnet/minecraft/world/WorldServer;Lnet/minecraft/world/Teleporter;)V",
            at = @At("RETURN"))
    private void tail(Entity entityIn, int originDim, WorldServer origin, WorldServer destination,
            Teleporter teleporter, CallbackInfo ci, @Share("prevFindingSpawnPoint") LocalBooleanRef prev) {
        destination.findingSpawnPoint = prev.get();
    }
}
