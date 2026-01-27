package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import static biomesoplenty.common.core.BOPBlocks.registerBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.common.biomesoplenty.blocks.BOPBlocks;
import com.mitchej123.hodgepodge.common.biomesoplenty.blocks.BlockBOPFence;
import com.mitchej123.hodgepodge.common.biomesoplenty.blocks.BlockBOPFenceGate;

@Mixin(value = biomesoplenty.common.core.BOPBlocks.class, remap = false)
public class MixinBOPBlocks {

    @Inject(method = "registerBlocks", at = @At("RETURN"))
    private static void registerBlocks(CallbackInfo ci) {
        for (BOPBlocks.WoodTypes woodType : BOPBlocks.WoodTypes.values()) {
            BOPBlocks.fences.put(
                    woodType,
                    registerBlock(new BlockBOPFence(woodType.name()).setBlockName(woodType.name() + "Fence")));

            BOPBlocks.fence_gates.put(
                    woodType,
                    registerBlock(new BlockBOPFenceGate(woodType.name()).setBlockName(woodType.name() + "FenceGate")));
        }
    }
}
