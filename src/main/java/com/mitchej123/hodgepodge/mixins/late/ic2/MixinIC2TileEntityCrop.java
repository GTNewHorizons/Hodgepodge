package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ic2.core.crop.TileEntityCrop;

@Mixin(TileEntityCrop.class)
public class MixinIC2TileEntityCrop extends TileEntity {

    @Inject(method = "calcTrampling", at = @At("HEAD"), cancellable = true, remap = false)
    public void hodgepodge$fixIC2CropTrampling(CallbackInfo ci) {
        Block below = worldObj.getBlock(this.xCoord, this.yCoord - 1, this.zCoord);
        // If the crop is trampled (usually by sprinting over it), the block below always replaced with dirt by ic2.
        // This only makes sense if the block was actually dirt-farmland, as otherwise special farmland may
        // get replaced with dirt, including those that are supposed to be un-trample-able.
        // Special handling for other farmland types that should be trample-able may be added here, if there are any.
        if (below != Blocks.farmland) ci.cancel();
    }
}
