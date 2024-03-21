package com.mitchej123.hodgepodge.mixins.late.thermalexpansion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import cofh.thermalexpansion.block.TileInventory;
import cofh.thermalexpansion.block.TileTEBase;
import cofh.thermalexpansion.block.light.TileLightFalse;

@Mixin({ TileInventory.class, TileLightFalse.class })
public abstract class MixinTileInventoryTileLightFalse extends TileTEBase {

    @Unique
    protected boolean hodgepodge$initialUpdate = true;

    @Override
    public void updateEntity() {
        if (this.hodgepodge$initialUpdate) {
            this.cofh_validate();
            this.hodgepodge$initialUpdate = false;
        }
        super.updateEntity();
    }

    @Shadow(remap = false)
    public abstract void cofh_validate();

}
