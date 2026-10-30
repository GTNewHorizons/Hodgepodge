package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

/**
 * When a player enters the End, {@code transferPlayerToDimension} removes them from the old world, then
 * {@code transferEntityToWorld} moves them to the End spawn and calls {@code updateEntityWithOptionalForce} on the
 * <i>old</i> world. If the old world's chunk at those coordinates is loaded, the player is added back to that chunk's
 * entity list and never removed again.
 */
@Mixin(ServerConfigurationManager.class)
public class MixinServerConfigurationManager_FixEndGhostPlayer {

    @WrapWithCondition(
            method = "transferEntityToWorld(Lnet/minecraft/entity/Entity;ILnet/minecraft/world/WorldServer;Lnet/minecraft/world/WorldServer;Lnet/minecraft/world/Teleporter;)V",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    remap = true,
                    target = "Lnet/minecraft/world/WorldServer;updateEntityWithOptionalForce(Lnet/minecraft/entity/Entity;Z)V"))
    private boolean hodgepodge$skipOldWorldPlayerUpdate(WorldServer world, Entity entity, boolean forceUpdate) {
        // the player's dimension is already set to the target here,
        // so a different world is the old world the player was just removed from
        return !(entity instanceof EntityPlayerMP) || world.provider.dimensionId == entity.dimension;
    }
}
