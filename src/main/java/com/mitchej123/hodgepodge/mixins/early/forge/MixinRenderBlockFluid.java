package com.mitchej123.hodgepodge.mixins.early.forge;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.RenderBlockFluid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.hooks.FluidHooks;

@Mixin(value = RenderBlockFluid.class, remap = false)
public class MixinRenderBlockFluid {

    @Redirect(
            method = "renderWorldBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fluids/BlockFluidBase;getFlowDirection(Lnet/minecraft/world/IBlockAccess;III)D"))
    double hodgepodge$directFlowDirectionCheck(IBlockAccess world, int x, int y, int z, @Local BlockFluidBase block) {
        return FluidHooks.getFlowDirection(world, x, y, z, block);
    }
}
