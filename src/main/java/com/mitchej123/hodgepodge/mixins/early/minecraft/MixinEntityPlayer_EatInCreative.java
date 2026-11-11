package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer_EatInCreative {

    @Shadow
    public PlayerCapabilities capabilities;

    @ModifyReturnValue(method = "canEat", at = @At("RETURN"))
    private boolean disableCreativeCheck(boolean returnValue) {
        return returnValue || this.capabilities.isCreativeMode;
    }
}
