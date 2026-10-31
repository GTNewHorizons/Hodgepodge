package com.mitchej123.hodgepodge.mixins.early.minecraft.outlineshapes;

import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockPane;

import org.spongepowered.asm.mixin.Mixin;

import com.gtnewhorizon.gtnhlib.api.IBlockWithOutlineShape;

@Mixin({ BlockBrewingStand.class, BlockCauldron.class, BlockPane.class })
public abstract class MixinCollisionShapedBlocks_OutlineShape implements IBlockWithOutlineShape {
}
