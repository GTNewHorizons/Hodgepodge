package com.mitchej123.hodgepodge.mixins.mixinWorld;

import com.mitchej123.hodgepodge.Hodgepodge;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(World.class)
public class fixGetBlock {
    @Redirect(
        method= "getBlock(III)Lnet/minecraft/block/Block;",
        at = @At(value="INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBlock(III)Lnet/minecraft/block/Block;"),
        require = 1
    )
    /*
     *  Reimplementation of a fix inspired by FalsePattern & SirFell
     */
    public Block getBlock(Chunk chunk, int x, int y, int z) {
        if(chunk == null) {
            Hodgepodge.log.info("NULL chunk found at {}, {}, {}, returning Blocks.air", x, y, z);
            return Blocks.air;
        }

        return chunk.getBlock(x, y, z);
    }
}
