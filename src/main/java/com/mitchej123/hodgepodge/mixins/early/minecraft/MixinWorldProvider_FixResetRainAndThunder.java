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
            remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/WorldInfo;func_76080_a(I)V"))
    private int hodgepodge$fixRainTime(int original) {
        return worldObj.rand.nextInt(168000) + 12000;
    }

    @ModifyArg(
            method = "resetRainAndThunder",
            remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/WorldInfo;func_76090_a(I)V"))
    private int hodgepodge$fixThunderTime(int original) {
        return worldObj.rand.nextInt(168000) + 12000;
    }
}
