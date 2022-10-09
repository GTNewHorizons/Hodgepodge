package com.mitchej123.hodgepodge.mixins.late.ic2;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.util.PollutionHelper;
import ic2.core.block.machine.tileentity.TileEntityIronFurnace;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
 * Merged from ModMixins under the MIT License
 *    Copyright bartimaeusnek & GTNewHorizons
 */
@Mixin(value = TileEntityIronFurnace.class, remap = false)
public abstract class MixinIC2IronFurnacePollution extends TileEntity {
    @Shadow
    public abstract boolean isBurning();

    @Inject(method = "updateEntityServer", at = @At("TAIL"))
    private void updateEntityServer(CallbackInfo ci) {
        if (worldObj.isRemote || !isBurning()) return;
        if ((worldObj.getTotalWorldTime() % 20) == 0) {
            PollutionHelper.addPollution(
                    worldObj.getChunkFromBlockCoords(xCoord, zCoord), Common.config.furnacePollutionAmount);
        }
    }
}
