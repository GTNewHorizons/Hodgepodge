package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Collection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.hooks.ChunkGenScheduler;

@Mixin(value = ServerConfigurationManager.class, remap = false)
public class MixinServerConfigurationManager {

    @Unique
    private boolean hodgepodge$wasChunkLoadBlocked;

    @Unique
    private boolean hodgepodge$wasLoadChunkOnProvideRequestDisabled;

    @Unique
    private ChunkProviderServer hodgepodge$portalTargetChunkProvider;

    /*
     * Make sure extra hearts aren't lost on dimension change Backported fix from
     * https://github.com/MinecraftForge/MinecraftForge/pull/4830
     */
    @Inject(
            at = @At(
                    target = "Lcpw/mods/fml/common/FMLCommonHandler;instance()Lcpw/mods/fml/common/FMLCommonHandler;",
                    value = "INVOKE"),
            method = "transferPlayerToDimension(Lnet/minecraft/entity/player/EntityPlayerMP;ILnet/minecraft/world/Teleporter;)V")
    private void hodgepodge$sendEntityProperties(EntityPlayerMP player, int dimension, Teleporter teleporter,
            CallbackInfo ci) {
        ServersideAttributeMap attributeMap = (ServersideAttributeMap) player.getAttributeMap();
        Collection<IAttributeInstance> watchedAttribs = attributeMap.getWatchedAttributes();
        if (!watchedAttribs.isEmpty()) {
            player.playerNetServerHandler
                    .sendPacket(new S20PacketEntityProperties(player.getEntityId(), watchedAttribs));
        }
    }

    @Inject(
            method = "transferEntityToWorld(Lnet/minecraft/entity/Entity;ILnet/minecraft/world/WorldServer;Lnet/minecraft/world/WorldServer;Lnet/minecraft/world/Teleporter;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/Teleporter;placeInPortal(Lnet/minecraft/entity/Entity;DDDF)V"),
            require = 0)
    private void hodgepodge$unblockChunkLoadsForPortalPlacement(Entity entityIn, int oldDimension,
            WorldServer fromWorld, WorldServer toWorld, Teleporter teleporter, CallbackInfo ci) {
        hodgepodge$wasChunkLoadBlocked = ChunkGenScheduler.isBlocked();
        if (hodgepodge$wasChunkLoadBlocked) {
            ChunkGenScheduler.enableChunkLoads();
        }

        hodgepodge$portalTargetChunkProvider = toWorld.theChunkProviderServer;
        hodgepodge$wasLoadChunkOnProvideRequestDisabled = !hodgepodge$portalTargetChunkProvider.loadChunkOnProvideRequest;
        if (hodgepodge$wasLoadChunkOnProvideRequestDisabled) {
            hodgepodge$portalTargetChunkProvider.loadChunkOnProvideRequest = true;
        }
    }

    @Inject(
            method = "transferEntityToWorld(Lnet/minecraft/entity/Entity;ILnet/minecraft/world/WorldServer;Lnet/minecraft/world/WorldServer;Lnet/minecraft/world/Teleporter;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/Teleporter;placeInPortal(Lnet/minecraft/entity/Entity;DDDF)V",
                    shift = At.Shift.AFTER),
            require = 0)
    private void hodgepodge$restoreChunkLoadStateAfterPortalPlacement(Entity entityIn, int oldDimension,
            WorldServer fromWorld, WorldServer toWorld, Teleporter teleporter, CallbackInfo ci) {
        if (hodgepodge$wasLoadChunkOnProvideRequestDisabled && hodgepodge$portalTargetChunkProvider != null) {
            hodgepodge$portalTargetChunkProvider.loadChunkOnProvideRequest = false;
        }
        hodgepodge$portalTargetChunkProvider = null;

        if (hodgepodge$wasChunkLoadBlocked) {
            ChunkGenScheduler.disableChunkLoads();
        }
    }
}
