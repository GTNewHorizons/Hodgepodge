package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.util.FastMath;

import biomesoplenty.common.entities.EntityPixie;

@Mixin(EntityPixie.class)
public class MixinEntityPixie_FastAtan2 {

    @Redirect(method = "updateEntityActionState", at = @At(value = "INVOKE", target = "Ljava/lang/Math;atan2(DD)D"))
    private double hodgepodge$fastAtan2(double y, double x) {
        return FastMath.atan2(y, x);
    }
}
