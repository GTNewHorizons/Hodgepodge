package com.mitchej123.hodgepodge.mixins.early.minecraft.tiledescriptions;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.management.PlayerManager.PlayerInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.hax.TileEntityDescriptionBatcher;

@Mixin(PlayerInstance.class)
public class MixinPlayerInstance {

    @Shadow
    @Final
    private List<EntityPlayerMP> playersWatchingChunk;

    @Shadow
    @Final
    private ChunkCoordIntPair chunkLocation;

    @WrapOperation(
            method = "sendTileToAllPlayersWatchingChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/tileentity/TileEntity;getDescriptionPacket()Lnet/minecraft/network/Packet;"))
    public Packet interceptDescriptionPacket(TileEntity instance, Operation<Packet> original) {
        Packet packet = original.call(instance);

        if (packet instanceof S35PacketUpdateTileEntity updatePacket) {
            for (int i = 0; i < this.playersWatchingChunk.size(); ++i) {
                EntityPlayerMP player = this.playersWatchingChunk.get(i);

                if (!player.loadedChunks.contains(this.chunkLocation)) {
                    if (!TileEntityDescriptionBatcher.queueSend(player, instance, updatePacket)) {
                        player.playerNetServerHandler.sendPacket(updatePacket);
                    }
                }
            }

            return null;
        }

        return packet;
    }

}
