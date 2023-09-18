package com.mitchej123.hodgepodge.mixin.mixins.late.mrtjpcore;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import mrtjp.core.world.PlacementLib$;

@Mixin(PlacementLib$.class)
public class MixinPlacementLib {

    /**
     * @reason Allow blocks to stay on unloaded chunks.
     */
    @ModifyConstant(
            method = { "canPlaceWireOnSide", "canPlaceTorchOnBlock" },
            constant = @Constant(intValue = 0, ordinal = 1),
            remap = false)
    private int hodgepodge$allowChunkUnloading1(int oldFalse) {
        return 1; /* true */
    }

    @ModifyConstant(method = "canPlaceGateOnSide", constant = @Constant(intValue = 0, ordinal = 2), remap = false)
    private int hodgepodge$allowChunkUnloading2(int oldFalse) {
        return 1; /* true */
    }
}
