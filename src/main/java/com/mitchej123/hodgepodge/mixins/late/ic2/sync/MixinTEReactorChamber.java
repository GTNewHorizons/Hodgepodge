package com.mitchej123.hodgepodge.mixins.late.ic2.sync;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric;

@Mixin(value = TileEntityReactorChamberElectric.class, remap = false)
public abstract class MixinTEReactorChamber extends TileEntity {

    @Redirect(
            method = "updateEntity",
            at = @At(
                    value = "FIELD",
                    target = "Lic2/core/block/reactor/tileentity/TileEntityReactorChamberElectric;ticker:S",
                    ordinal = 0,
                    remap = false),
            remap = true)
    private short hodgepodge$synchronizeReactors(TileEntityReactorChamberElectric instance) {
        // May cause one short cycle, but afterwards will always be synced to world time
        return (short) (instance.getWorldObj().getTotalWorldTime() % 20);
    }
}
