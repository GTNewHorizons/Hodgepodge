package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlePlayServer_FixWrongBlockPlacementCheck {

    @WrapOperation(
            method = "processPlayerBlockPlacement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;getDistanceSq(DDD)D"))
    private double hodgepodge$fixWrongBlockPlacementCheck(EntityPlayerMP entity, double x, double y, double z,
            Operation<Double> original) {
        y -= entity.getEyeHeight();
        return original.call(entity, x, y, z);
    }
}
