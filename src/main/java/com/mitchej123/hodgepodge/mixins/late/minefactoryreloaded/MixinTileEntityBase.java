package com.mitchej123.hodgepodge.mixins.late.minefactoryreloaded;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import powercrystals.minefactoryreloaded.tile.base.TileEntityBase;

@Mixin(TileEntityBase.class)
public abstract class MixinTileEntityBase extends TileEntity {

    @Unique
    protected boolean hodgepodge$initialUpdate = true;

    @Inject(at = @At("HEAD"), method = "updateEntity")
    private void hodgepodge$cofh_validate(CallbackInfo ci) {
        if (this.hodgepodge$initialUpdate) {
            this.cofh_validate();
            this.hodgepodge$initialUpdate = false;
        }
    }

    @Shadow(remap = false)
    public abstract void cofh_validate();
}
