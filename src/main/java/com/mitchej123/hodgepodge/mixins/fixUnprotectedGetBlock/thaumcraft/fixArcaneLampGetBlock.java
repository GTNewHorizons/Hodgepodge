package com.mitchej123.hodgepodge.mixins.fixUnprotectedGetBlock.thaumcraft;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.common.tiles.TileArcaneLamp;

@Mixin(TileArcaneLamp.class)
public class fixArcaneLampGetBlock {
    @Redirect(
        method = "func_145845_h",
        at = @At(
            value = "INVOKE",
            target="Lnet/minecraft/world/World;func_147439_a(III)Lnet/minecraft/block/Block;"
        ),
        remap = false
    )
    protected Block getBlock(World world, int x, int y, int z) {
        if (!world.blockExists(x, y, z))
            return Blocks.air;

        return world.getBlock(x, y, z);
    }    

}
