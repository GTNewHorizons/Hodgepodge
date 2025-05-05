package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.projectile.EntityEgg;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityEgg.class)
public abstract class MixinEntityEgg {

    @ModifyArg(
            method = "onImpact",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnParticle(Ljava/lang/String;DDDDDD)V"),
            index = 0)
    private String replaceParticle(String originalParticle) {
        return "eggpoof";
    }
}
