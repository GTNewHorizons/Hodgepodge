package com.mitchej123.hodgepodge.mixins.early.ic2;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric;

@Mixin(TileEntityReactorChamberElectric.class)
public class MixinTileEntityReactorChamberElectricNoDupe {

    @Redirect(
            method = "getReactor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;onNeighborBlockChange(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;)V"))
    public void hodgepodge$fixGetReactor(Block blk, World w, int x, int y, int z, Block blk2) {
        w.notifyBlockChange(x, y, z, blk);
    }
}
