package com.mitchej123.hodgepodge.mixin.mixins.early.minecraft;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.util.PollutionHelper;

/*
 * Merged from ModMixins under the MIT License Copyright bartimaeusnek & GTNewHorizons
 */
@Mixin(TileEntityFurnace.class)
public abstract class MixinTileEntityFurnacePollution extends TileEntity {

    @Inject(
            method = "updateEntity",
            at = @At(
                    value = "FIELD",
                    target = "net/minecraft/tileentity/TileEntityFurnace.furnaceBurnTime:I",
                    opcode = Opcodes.PUTFIELD))
    void hodgepodge$addPollution(CallbackInfo ci) {
        if (!this.worldObj.isRemote && (this.worldObj.getTotalWorldTime() % 20) == 0) PollutionHelper.addPollution(
                this.worldObj.getChunkFromBlockCoords(this.xCoord, this.zCoord),
                Common.config.furnacePollutionAmount);
    }
}
