package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.BlockPistonBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(value = BlockPistonBase.class, priority = 1001)
public class MixinBlockPistonBase {

    @ModifyExpressionValue(
            method = "updatePistonState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockPistonBase;getPistonOrientation(I)I"))
    private int clampPistonOrientation(int orientation) {
        return Math.floorMod(orientation, 6);
    }
}
