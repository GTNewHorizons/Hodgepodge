package com.mitchej123.hodgepodge.mixins.ic2;

import ic2.core.block.kineticgenerator.tileentity.TileEntityWaterKineticGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityWaterKineticGenerator.class)
public class MixinIc2WaterKinetic {
    @Redirect(
        method = "checkSpace(IZ)I",
        at = @At(
            value = "INVOKE",
            target="Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"
        )
    )
    protected Block getBlock(World world, int x, int y, int z) {
        if (!world.blockExists(x, y, z))
            return Blocks.air;

        return world.getBlock(x, y, z);
    }
}
