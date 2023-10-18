package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.village.Village;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(Village.class)
public class MixinVillage {

    @WrapOperation(
            at = @At(target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;", value = "INVOKE"),
            method = "Lnet/minecraft/village/Village;isBlockDoor(III)Z")
    private Block hodgepodge$getBlockWithCheck(World instance, int x, int y, int z, Operation<Block> original) {
        if (instance.blockExists(x, y, z)) {
            return original.call(instance, x, y, z);
        }
        return Blocks.air;
    }

}
