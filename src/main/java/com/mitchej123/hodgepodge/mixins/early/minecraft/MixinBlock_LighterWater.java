package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Block.class)
public class MixinBlock_LighterWater {

    @ModifyArg(
            method = "registerBlocks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;setLightOpacity(I)Lnet/minecraft/block/Block;",
                    ordinal = 0))
    private static int hodge$decreaseFlowingWaterOpacity(int in) {
        return 1;
    }

    @ModifyArg(
            method = "registerBlocks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;setLightOpacity(I)Lnet/minecraft/block/Block;",
                    ordinal = 1))
    private static int hodge$decreaseWaterOpacity(int in) {
        return 1;
    }
}
