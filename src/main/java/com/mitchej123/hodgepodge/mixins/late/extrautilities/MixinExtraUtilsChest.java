package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.rwtema.extrautils.tileentity.chests.TileFullChest;
import com.rwtema.extrautils.tileentity.chests.TileMiniChest;

@Mixin({ TileFullChest.class, TileMiniChest.class })
public abstract class MixinExtraUtilsChest extends TileEntity {

    @Inject(method = "markDirty", at = @At("TAIL"))
    private void hodgepodge$updateComparatorOnInventoryChange(CallbackInfo ci) {
        World world = this.worldObj;
        if (world != null && !world.isRemote) {
            world.notifyBlocksOfNeighborChange(
                    this.xCoord,
                    this.yCoord,
                    this.zCoord,
                    world.getBlock(this.xCoord, this.yCoord, this.zCoord));
        }
    }
}
