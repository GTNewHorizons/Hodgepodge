package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.config.PollutionConfig;
import com.mitchej123.hodgepodge.util.PollutionHelper;

import thaumcraft.common.tiles.TileAlchemyFurnace;

/*
 * Merged from ModMixins under the MIT License Copyright bartimaeusnek & GTNewHorizons
 */
@Mixin(TileAlchemyFurnace.class)
public abstract class MixinThaumcraftAlchemyFurnacePollution extends TileEntity {

    @Inject(
            method = "updateEntity",
            at = @At(
                    value = "FIELD",
                    target = "thaumcraft/common/tiles/TileAlchemyFurnace.furnaceBurnTime:I",
                    opcode = Opcodes.PUTFIELD,
                    remap = false))
    public void hodgepodge$addPollution(CallbackInfo ci) {
        if (!this.worldObj.isRemote && (this.worldObj.getTotalWorldTime() % 20) == 0) PollutionHelper.addPollution(
                this.worldObj.getChunkFromBlockCoords(this.xCoord, this.zCoord),
                PollutionConfig.furnacePollutionAmount);
    }
}
