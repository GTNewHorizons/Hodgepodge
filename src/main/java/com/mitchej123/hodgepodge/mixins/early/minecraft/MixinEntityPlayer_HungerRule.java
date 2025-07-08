package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mitchej123.hodgepodge.mixins.interfaces.GameRuleExt;
import com.mojang.authlib.GameProfile;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer_HungerRule extends EntityLivingBase {

    @WrapWithCondition(
            method = "addExhaustion",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FoodStats;addExhaustion(F)V"))
    private boolean hodgepodge$hungerRule(FoodStats foodStats, float amount) {
        return !((GameRuleExt) this.worldObj.getGameRules()).hodgepodge$isHungerDisabled();
    }

    private MixinEntityPlayer_HungerRule(World world, GameProfile gameProfile) {
        super(world);
    }
}
