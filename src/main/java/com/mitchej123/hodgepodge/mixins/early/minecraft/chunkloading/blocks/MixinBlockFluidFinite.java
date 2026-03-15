package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFluidFinite.class)
public abstract class MixinBlockFluidFinite extends BlockFluidBase {

    public MixinBlockFluidFinite(Fluid fluid, Material material) {
        super(fluid, material);
    }

    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    private void checkExist(World world, int x, int y, int z, Random rand, CallbackInfo ci) {
        final int offset = 1;
        if (!world.checkChunksExist(x - offset, y, z - offset, x + offset, y, z + offset)) {
            world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world) * 5);
            ci.cancel();
        }
    }
}
