package com.mitchej123.hodgepodge.mixins.early.ic2;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TileEntityNuclearReactorElectric.class)
public abstract class MixinTileEntityNuclearReactorElectric extends TileEntityInventory {
    @Shadow(remap = false)
    public abstract boolean isFluidCooled();

    /**
     * @author glee8e
     */
    public int getSizeInventory() {
        if (isFluidCooled()) {
            int sum = 0;
            for (InvSlot invSlot : this.invSlots) {
                sum += invSlot.size();
            }
            return sum;
        } else {
            return this.invSlots.get(0).size();
        }
    }
}
