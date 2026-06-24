package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin({ EntitySkeleton.class, EntityZombie.class })
public abstract class MixinEntityUndead_SunFireImmunity extends EntityMob {

    private MixinEntityUndead_SunFireImmunity(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    @ModifyExpressionValue(
            method = "onLivingUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isDaytime()Z"))
    private boolean fireImmunityCheck(boolean original) {
        return original && !this.isImmuneToFire;
    }
}
