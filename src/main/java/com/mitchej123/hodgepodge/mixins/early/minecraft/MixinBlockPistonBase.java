package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.BlockPistonBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BlockPistonBase.class)
public class MixinBlockPistonBase {

    @ModifyVariable(
            method = "updatePistonState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockPistonBase;getPistonOrientation(I)I"),
            ordinal = 0,
            argsOnly = true)
    private int clampPistonOrientation(int orientation) {
        return (orientation >= 0 && orientation < 6) ? orientation : Math.floorMod(orientation, 6);
    }
}
