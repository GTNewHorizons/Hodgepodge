package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.mixins.hooks.BukkitSpawnReasonHelper;

@Mixin(World.class)
public class MixinWorld_BukkitSpawnReason {

    @Inject(method = "spawnEntityInWorld", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$routeSpawnReason(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        final Boolean result = BukkitSpawnReasonHelper.routeSpawnReason((World) (Object) this, entity);
        if (result != null) cir.setReturnValue(result);
    }
}
