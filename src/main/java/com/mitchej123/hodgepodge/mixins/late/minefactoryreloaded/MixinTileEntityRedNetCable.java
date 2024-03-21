package com.mitchej123.hodgepodge.mixins.late.minefactoryreloaded;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;

@Mixin(TileEntityRedNetCable.class)
public abstract class MixinTileEntityRedNetCable extends MixinTileEntityBase {

    /**
     * {@inheritDoc}
     * 
     * @author glowredman
     * @reason Initial {@link #updateEntity()} call is needed to mark this as dirty. This cannot be done in
     *         {@link #validate()}.
     */
    @Overwrite(remap = false)
    public boolean canUpdate() {
        return this.hodgepodge$initialUpdate;
    }

}
