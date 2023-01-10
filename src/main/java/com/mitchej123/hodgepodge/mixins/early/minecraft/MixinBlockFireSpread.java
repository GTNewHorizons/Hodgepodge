package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Random;
import net.minecraft.block.BlockFire;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFire.class)
public class MixinBlockFireSpread {

    @Inject(
            method =
                    "tryCatchFire(Lnet/minecraft/world/World;IIIILjava/util/Random;ILnet/minecraftforge/common/util/ForgeDirection;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"),
            cancellable = true)
    public void hodgepodge$fixChunkNPE(
            World world, int x, int y, int z, int par5, Random par6, int par7, ForgeDirection face, CallbackInfo ci) {
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000 && y >= 0 && y < 256) {
            if (world.getChunkFromChunkCoords(x >> 4, z >> 4) == null) ci.cancel();
        }
    }
}
