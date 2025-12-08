package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.WorldType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(WorldType.class)
public class MixinWorldType_VoidParticles {

    @ModifyReturnValue(method = "hasVoidParticles", remap = false, at = @At("RETURN"))
    private boolean hasVoidParticles(boolean original, boolean hasNoSky) {
        if (TweaksConfig.disableVoidFog == 0) {
            return original;
        } else if (TweaksConfig.disableVoidFog == 1) {
            return original && (Object) this != WorldType.DEFAULT;
        } else {
            return false;
        }
    }
}
