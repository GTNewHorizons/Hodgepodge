package com.mitchej123.hodgepodge.mixins.early.ic2;

import com.gtnewhorizon.mixinextras.injector.wrapoperation.Operation;
import com.gtnewhorizon.mixinextras.injector.wrapoperation.WrapOperation;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWaterKineticGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TileEntityWaterKineticGenerator.class)
public class MixinIc2WaterKinetic {

    @WrapOperation(
            at = @At(target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;", value = "INVOKE"),
            method = "checkSpace(IZ)I")
    private Block hodgepodge$getBlockWithCheck(World instance, int x, int y, int z, Operation<Block> original) {
        if (instance.blockExists(x, y, z)) {
            return original.call(instance, x, y, z);
        }
        return Blocks.air;
    }
}
