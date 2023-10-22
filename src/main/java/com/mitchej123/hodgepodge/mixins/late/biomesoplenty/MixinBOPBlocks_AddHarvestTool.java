package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import biomesoplenty.api.content.BOPCBlocks;
import biomesoplenty.common.core.BOPBlocks;

@Mixin(value = BOPBlocks.class, remap = false)
public class MixinBOPBlocks_AddHarvestTool {

    @Inject(method = "init()V", at = @At("TAIL"))
    private static void hodgepodge$init(CallbackInfo ci) {
        BOPCBlocks.flesh.setHarvestLevel("shovel", 0);
    }
}
