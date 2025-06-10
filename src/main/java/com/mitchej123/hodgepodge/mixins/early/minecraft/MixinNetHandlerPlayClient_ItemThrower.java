package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.mixins.interfaces.S0EPacketSpawnObjectExt;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient_ItemThrower {

    @ModifyExpressionValue(
            method = "handleSpawnObject",
            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/entity/item/EntityItem;init(Lnet/minecraft/world/World;DDD)Lnet/minecraft/entity/item/EntityItem;"))
    private EntityItem hodgepodge$addThrower(EntityItem entityItem, S0EPacketSpawnObject packetIn) {
        if (packetIn instanceof S0EPacketSpawnObjectExt accessor) {
            entityItem.func_145799_b(accessor.hodgepodge$getThrower());
            entityItem.delayBeforeCanPickup = accessor.hodgepodge$getDelay();
        }
        return entityItem;
    }
}
