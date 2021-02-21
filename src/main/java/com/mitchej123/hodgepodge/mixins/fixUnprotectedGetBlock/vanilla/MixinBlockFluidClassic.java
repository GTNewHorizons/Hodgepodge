package com.mitchej123.hodgepodge.mixins.fixUnprotectedGetBlock.vanilla;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockFluidClassic.class)
public class MixinBlockFluidClassic {

    @Redirect(
        method = "Lnet/minecraftforge/fluids/BlockFluidClassic;canFlowInto(Lnet/minecraft/world/IBlockAccess;III)Z", 
        at = @At( 
            value = "INVOKE", 
            target="Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"
        ),
        remap = false
    )
    protected Block getBlock(IBlockAccess world, int x, int y, int z) {
        if (!(world instanceof World) || !((World) world).blockExists(x, y, z))
            return Blocks.air;

        return world.getBlock(x, y, z);
    }
}
