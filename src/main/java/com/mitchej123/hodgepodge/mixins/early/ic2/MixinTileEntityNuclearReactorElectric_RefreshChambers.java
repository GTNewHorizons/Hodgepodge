package com.mitchej123.hodgepodge.mixins.early.ic2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;

@Mixin(value = TileEntityNuclearReactorElectric.class, remap = false)
public class MixinTileEntityNuclearReactorElectric_RefreshChambers {

    @Unique
    private boolean hodgepodge$refreshingChambers;

    @WrapMethod(method = "refreshChambers")
    private void hodgepodge$preventRecursiveRefresh(Operation<Void> original) {
        if (hodgepodge$refreshingChambers) {
            return;
        }
        hodgepodge$refreshingChambers = true;
        try {
            original.call();
        } finally {
            hodgepodge$refreshingChambers = false;
        }
    }
}
