package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.Compat;
import com.rwtema.extrautils.tileentity.endercollector.TileEnderCollector;

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
        if (Compat.isThaumcraftPresent()) {
            if (tileEntity != null && tileEntity.getClass().getName().equals("thaumcraft.common.tiles.TileGrate")) {
                return null;
            }
        }

        if (Compat.isSFMPresent()) {
            if (blockBelow != null
                    && blockBelow.getClass().getName().equals("vswe.stevesfactory.blocks.BlockCableIntake")) {
                return null;
            }
        }

        return tileEntity;
    }
}
