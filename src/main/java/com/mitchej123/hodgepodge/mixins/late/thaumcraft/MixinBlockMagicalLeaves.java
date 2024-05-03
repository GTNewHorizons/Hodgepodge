package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import thaumcraft.common.blocks.BlockMagicalLeaves;

@Mixin(value = BlockMagicalLeaves.class)
public abstract class MixinBlockMagicalLeaves extends Block {

    private MixinBlockMagicalLeaves(Material material) {
        super(material);
    }

    @Redirect(
            method = "updateTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;II)Z"))
    public boolean Hodgepodge$redirectSetBlockMetadataWithNotify(World world, int x, int y, int z, Block block,
            int metadata, int flags) {
        world.setBlockMetadataWithNotify(x, y, z, metadata, 4);
        return true;
    }
}
