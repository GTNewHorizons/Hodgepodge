package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ic2.api.Direction;
import ic2.core.ContainerBase;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric;

@Mixin(value = TileEntityNuclearReactorElectric.class, remap = false)
public class MixinTEReactorCacheReactorSize extends TileEntity {

    @Unique
    private short hodgepodge$reactorSize = -1;

    @Inject(
            method = "updateEntityServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lic2/core/block/reactor/tileentity/TileEntityNuclearReactorElectric;getReactorSize()S",
                    shift = At.Shift.BEFORE,
                    ordinal = 0))
    private void hodgepodge$resetReactorSize(CallbackInfo ci) {
        hodgepodge$reactorSize = -1;
    }

    @Inject(method = "getGuiContainer", at = @At("HEAD"))
    private void hodgepodge$resetReactorSizeOnGui(
            CallbackInfoReturnable<ContainerBase<TileEntityNuclearReactorElectric>> cir) {
        // Reset reactor size when opening the GUI, so that it is recalculated immediately
        hodgepodge$reactorSize = -1;
    }

    /**
     * @author kuba6000
     * @reason cache reactor size to reduce lags
     */
    @Overwrite
    public short getReactorSize() {
        if (this.worldObj == null) {
            return 9;
        }
        if (hodgepodge$reactorSize != -1 && !this.worldObj.isRemote) {
            return hodgepodge$reactorSize;
        }
        short cols = 3;
        for (Direction direction : Direction.directions) {
            TileEntity target = direction.applyToTileEntity(this);
            if (target instanceof TileEntityReactorChamberElectric) {
                ++cols;
            }
        }
        hodgepodge$reactorSize = cols;
        return cols;
    }

}
