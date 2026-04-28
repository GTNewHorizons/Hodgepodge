package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldProvider.class)
public class MixinWorldProvider_FixResetRainAndThunder {

    @Shadow
    public World worldObj;

    @Inject(method = "resetRainAndThunder", at = @At("HEAD"), cancellable = true, remap = false)
    private void hodgepodge$fixResetRainAndThunder(CallbackInfo ci) {
        worldObj.getWorldInfo().setRainTime(worldObj.rand.nextInt(168000) + 12000);
        worldObj.getWorldInfo().setRaining(false);
        worldObj.getWorldInfo().setThunderTime(worldObj.rand.nextInt(168000) + 12000);
        worldObj.getWorldInfo().setThundering(false);
        ci.cancel();
    }
}
