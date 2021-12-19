package com.mitchej123.hodgepodge.mixins.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockFire.class)
public class MixinBlockFireGetBlock {

    @Redirect(
        method = "getChanceToEncourageFire(Lnet/minecraft/world/IBlockAccess;IIIILnet/minecraftforge/common/util/ForgeDirection;)I", 
        at = @At( 
            value = "INVOKE", 
            target="Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"
        )
    )
    protected Block getBlock(IBlockAccess world, int x, int y, int z) {
        if (!(world instanceof World) || !((World) world).blockExists(x, y, z))
            return Blocks.air;

        return world.getBlock(x, y, z);
    }
}
