package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.config.PollutionConfig;
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
                    opcode = Opcodes.GETFIELD,
                    ordinal = 2))
    void hodgepodge$addPollution(CallbackInfo ci) {
        if (!this.worldObj.isRemote && (this.worldObj.getTotalWorldTime() % 20) == 0) PollutionHelper.addPollution(
                this.worldObj.getChunkFromBlockCoords(this.xCoord, this.zCoord),
                PollutionConfig.furnacePollutionAmount);
    }
}
