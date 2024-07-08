package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.rwtema.extrautils.tileentity.transfernodes.TileEntityRetrievalNodeLiquid;
import com.rwtema.extrautils.tileentity.transfernodes.nodebuffer.FluidBuffer;
import com.rwtema.extrautils.tileentity.transfernodes.nodebuffer.FluidBufferRetrieval;
import com.rwtema.extrautils.tileentity.transfernodes.pipes.IPipe;

@Mixin(value = FluidBufferRetrieval.class, remap = false)
public class MixinFluidBufferRetrieval extends FluidBuffer {

    // test

    /**
     * @author FourIsTheNumber
     * @reason Change transfer logic to prevent voiding
     */
    @Overwrite
    public boolean transfer(TileEntity tile, ForgeDirection side, IPipe insertingPipe, int x, int y, int z,
            ForgeDirection travelDir) {
        if (tile instanceof IFluidHandler destTank) {
            int drainmax = this.node.getNode().upgradeNo(3) == 0 ? 200 : this.tank.getCapacity();
            int drainable = ((TileEntityRetrievalNodeLiquid) this.node.getNode())
                    .fill(this.node.getNodeDir(), destTank.drain(side, drainmax, false), false);
            return drainable <= 0 || ((TileEntityRetrievalNodeLiquid) this.node.getNode())
                    .fill(this.node.getNodeDir(), destTank.drain(side, drainable, true), true) <= 0;
        }

        return true;
    }
}
