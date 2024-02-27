package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Block.class)
public class MixinBlock_LighterWater {

    @ModifyArg(
            method = "registerBlocks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;setLightOpacity(I)Lnet/minecraft/block/Block;"),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/block/BlockDynamicLiquid;<init>(Lnet/minecraft/block/material/Material;)V",
                            ordinal = 0),
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/block/BlockDynamicLiquid;<init>(Lnet/minecraft/block/material/Material;)V",
                            ordinal = 1)))
    private static int angelica$decraseWaterOpacity(int in) {
        return 1;
    }
}
