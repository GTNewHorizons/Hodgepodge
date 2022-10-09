package com.mitchej123.hodgepodge.mixins.late.railcraft;

import com.mitchej123.hodgepodge.Common;
import gregtech.common.GT_Pollution;
import java.util.List;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.blocks.machine.TileMultiBlockOven;
import mods.railcraft.common.blocks.machine.alpha.TileBlastFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
 * Merged from ModMixins under the MIT License
 *    Copyright bartimaeusnek & GTNewHorizons
 */
@Mixin(TileMultiBlockOven.class)
public abstract class MixinRailcraftCokeOvenPollution extends TileMultiBlock {
    @Shadow(remap = false)
    boolean cooking;

    public MixinRailcraftCokeOvenPollution(List<? extends MultiBlockPattern> patterns) {
        super(patterns);
    }

    @Inject(method = "updateEntity", at = @At("HEAD"))
    private void addPollution(CallbackInfo ci) {
        if (this.worldObj.isRemote || !this.cooking || !this.isMaster) return;
        if ((this.worldObj.getTotalWorldTime() % 20) == 0) {
            final int pollution = (((TileMultiBlock) this) instanceof TileBlastFurnace)
                    ? Common.config.advancedCokeOvenPollutionAmount
                    : Common.config.cokeOvenPollutionAmount;
            GT_Pollution.addPollution(this.worldObj.getChunkFromBlockCoords(this.xCoord, this.zCoord), pollution);
        }
    }
}
