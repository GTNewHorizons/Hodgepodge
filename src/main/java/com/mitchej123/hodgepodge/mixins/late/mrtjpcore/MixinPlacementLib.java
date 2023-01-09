package com.mitchej123.hodgepodge.mixins.late.mrtjpcore;

import mrtjp.core.world.PlacementLib$;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PlacementLib$.class)
public class MixinPlacementLib {

    /**
     * @reason Allow blocks to stay on unloaded chunks.
     */
    @ModifyConstant(
            method = {"canPlaceWireOnSide", "canPlaceTorchOnBlock"},
            constant = @Constant(intValue = 0, ordinal = 1),
            remap = false)
    private int allowChunkUnloading1(int oldFalse) {
        return 1; /* true */
    }

    @ModifyConstant(method = "canPlaceGateOnSide", constant = @Constant(intValue = 0, ordinal = 2), remap = false)
    private int allowChunkUnloading2(int oldFalse) {
        return 1; /* true */
    }
}
