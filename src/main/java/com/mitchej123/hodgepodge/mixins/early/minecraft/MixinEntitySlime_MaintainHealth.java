package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.monster.EntitySlime;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(EntitySlime.class)
public abstract class MixinEntitySlime_MaintainHealth {

    @WrapOperation(
            method = "readEntityFromNBT",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/monster/EntitySlime;setSlimeSize(I)V"))
    private void maintainHealth(EntitySlime instance, int size, Operation<Void> original) {
        float health = instance.getHealth();
        original.call(instance, size);
        instance.setHealth(health);
    }
}
