package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldProvider.class)
public class MixinWorldProvider_FixResetRainAndThunder {

    @Shadow
    public World worldObj;

    @ModifyArg(
            method = "resetRainAndThunder",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/WorldInfo;setRainTime(I)V"))
    private int fixRainTime(int original) {
        return worldObj.rand.nextInt(168000) + 12000;
    }

    @ModifyArg(
            method = "resetRainAndThunder",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/WorldInfo;setThunderTime(I)V"))
    private int fixThunderTime(int original) {
        return worldObj.rand.nextInt(168000) + 12000;
    }
}
