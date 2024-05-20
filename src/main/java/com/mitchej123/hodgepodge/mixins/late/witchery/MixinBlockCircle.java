package com.mitchej123.hodgepodge.mixins.late.witchery;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.emoniph.witchery.blocks.BlockCircle;

@Mixin(BlockCircle.class)
public class MixinBlockCircle {

    @Redirect(
            method = "activateBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isThundering()Z"))
    private boolean hodgepodge$isThundering(World world) {
        return world.getWorldInfo().isThundering();
    }
}
