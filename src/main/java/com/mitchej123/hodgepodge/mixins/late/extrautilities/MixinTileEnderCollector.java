package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.rwtema.extrautils.tileentity.endercollector.TileEnderCollector;

import thaumcraft.common.tiles.TileGrate;
import vazkii.botania.common.block.BlockOpenCrate;
import vswe.stevesfactory.blocks.BlockCableIntake;

@Mixin(TileEnderCollector.class)
public class MixinTileEnderCollector {

    @Redirect(
            method = "updateEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getTileEntity(III)Lnet/minecraft/tileentity/TileEntity;"))
    private TileEntity hodgepodge$blockTransferToBlacklist(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        Block blockBelow = world.getBlock(x, y, z);

        if (tileEntity instanceof TileGrate || blockBelow instanceof BlockCableIntake
                || blockBelow instanceof BlockOpenCrate) {
            return null;
        }

        return tileEntity;
    }
}
