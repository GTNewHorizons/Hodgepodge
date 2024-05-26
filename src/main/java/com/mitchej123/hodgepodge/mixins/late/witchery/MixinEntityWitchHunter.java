package com.mitchej123.hodgepodge.mixins.late.witchery;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.emoniph.witchery.entity.EntityWitchHunter;

@Mixin(EntityWitchHunter.class)
public class MixinEntityWitchHunter {

    @Redirect(
            method = "isValidLightLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isThundering()Z"))
    private boolean hodgepodge$isThundering(World world) {
        return world.getWorldInfo().isThundering();
    }

}
