package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S2FPacketSetSlot;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient_FixHandleSetSlot {

    @Inject(
            locals = LocalCapture.CAPTURE_FAILSOFT,
            method = "handleSetSlot(Lnet/minecraft/network/play/server/S2FPacketSetSlot;)V",
            at = { @At(
                    opcode = Opcodes.GETFIELD,
                    ordinal = 1,
                    target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;inventoryContainer:Lnet/minecraft/inventory/Container;",
                    value = "FIELD"),
                    @At(
                            opcode = Opcodes.GETFIELD,
                            ordinal = 1,
                            target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;openContainer:Lnet/minecraft/inventory/Container;",
                            value = "FIELD") },
            cancellable = true)
    public void hodgepodge$checkPacketItemStackSize(S2FPacketSetSlot packetIn, CallbackInfo ci,
            EntityClientPlayerMP entityclientplayermp) {
        if (packetIn.func_149173_d() >= entityclientplayermp.openContainer.inventorySlots.size()) ci.cancel();
    }
}
