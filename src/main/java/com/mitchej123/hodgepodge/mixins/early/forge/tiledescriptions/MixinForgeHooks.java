package com.mitchej123.hodgepodge.mixins.early.forge.tiledescriptions;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.hax.TileEntityDescriptionBatcher;

@Mixin(ForgeHooks.class)
public class MixinForgeHooks {

    @WrapOperation(
            method = "onBlockBreakEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/tileentity/TileEntity;getDescriptionPacket()Lnet/minecraft/network/Packet;"))
    private static Packet interceptDescriptionPacket(TileEntity instance, Operation<Packet> original,
            @Local(argsOnly = true) EntityPlayerMP player) {
        Packet packet = original.call(instance);

        if (packet instanceof S35PacketUpdateTileEntity updatePacket) {
            if (TileEntityDescriptionBatcher.queueSend(player, instance, updatePacket)) {
                return null;
            }
        }

        return packet;
    }
}
