package com.mitchej123.hodgepodge.mixins.early.minecraft.tiledescriptions;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.hax.TileEntityDescriptionBatcher;

@Mixin(EntityPlayerMP.class)
public class MixinEntityPlayerMP {

    @WrapOperation(
            method = "func_147097_b",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/tileentity/TileEntity;getDescriptionPacket()Lnet/minecraft/network/Packet;"))
    public Packet interceptDescriptionPacket(TileEntity instance, Operation<Packet> original) {
        Packet packet = original.call(instance);

        if (packet instanceof S35PacketUpdateTileEntity updatePacket) {
            if (TileEntityDescriptionBatcher.queueSend((EntityPlayerMP) (Object) this, instance, updatePacket)) {
                return null;
            }
        }

        return packet;
    }

}
