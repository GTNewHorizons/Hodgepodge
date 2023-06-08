package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S2FPacketSetSlot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient_FixHandleSetSlot {

    @Shadow
    private Minecraft gameController;

    @Inject(
            method = "handleSetSlot(Lnet/minecraft/network/play/server/S2FPacketSetSlot;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/inventory/Container;putStackInSlot(ILnet/minecraft/item/ItemStack;)V"),
            cancellable = true)
    public void hodgepodge$checkPacketItemStackSize(S2FPacketSetSlot packetIn, CallbackInfo ci) {
        EntityClientPlayerMP entityclientplayermp = this.gameController.thePlayer;
        if (packetIn.func_149173_d() >= entityclientplayermp.openContainer.inventorySlots.size()) ci.cancel();
    }
}
