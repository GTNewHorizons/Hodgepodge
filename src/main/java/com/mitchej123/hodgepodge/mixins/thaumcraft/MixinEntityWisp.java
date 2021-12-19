package com.mitchej123.hodgepodge.mixins.thaumcraft;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.common.entities.monster.EntityWisp;

@Mixin(EntityWisp.class)
public class MixinEntityWisp {
    @Redirect(
        method = "Lthaumcraft/common/entities/monster/EntityWisp;isCourseTraversable(DDDD)Z",
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
    
    @Redirect(
        method = "Lthaumcraft/common/entities/monster/EntityWisp;isCourseTraversable(DDDD)Z",
        at = @At(
            value = "INVOKE",
            target="Lnet/minecraft/world/World;func_147437_c(III)Lnet/minecraft/block/Block;"
        ),
        remap = false
    )
    public boolean isAirBlock(World world, int x, int y, int z) {
//        System.out.println("HMMMMMMMMMMMMMMM WISP isCourseTraversable isAirBlock");
        if (!world.blockExists(x, y, z))
            return true;

        return world.isAirBlock(x, y, z);
    }
}
