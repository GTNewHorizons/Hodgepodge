package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.util.ColorOverrideType;

@Mixin(RenderBlocks.class)
public class MixinRenderBlocks_PollutionWithOptifine {

    @Dynamic("Target is an optifine method")
    @ModifyExpressionValue(
            method = "renderStandardBlock",
            at = @At(
                    value = "INVOKE",
                    target = "LCustomColorizer;getColorMultiplier(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;III)I",
                    remap = false))
    private int hodgepodge$pollutionStandardBlock(int color, Block block, int blockX, int blockY, int blockZ) {
        ColorOverrideType type = Common.config.standardBlocks.matchesID(block);
        if (type == null) return color;
        return type.getColor(color, blockX, blockZ);
    }

    @Dynamic("Target is an optifine method")
    @ModifyExpressionValue(
            method = "renderBlockLiquid",
            at = @At(
                    value = "INVOKE",
                    target = "LCustomColorizer;getFluidColor(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;III)I",
                    remap = false))
    private int hodgepodge$pollutionBlockLiquid(int color, Block block, int blockX, int blockY, int blockZ) {
        ColorOverrideType type = Common.config.liquidBlocks.matchesID(block);
        if (type == null || block.getMaterial() != Material.water) {
            return color;
        }
        return type.getColor(color, blockX, blockZ);
    }

    @Dynamic("Target is an optifine method")
    @ModifyExpressionValue(
            method = "renderBlockDoublePlant",
            at = @At(
                    value = "INVOKE",
                    target = "LCustomColorizer;getColorMultiplier(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;III)I",
                    remap = false))
    private int hodgepodge$pollutionBlockDoublePlant(int color, BlockDoublePlant block, int blockX, int blockY,
            int blockZ) {
        ColorOverrideType type = Common.config.doublePlants.matchesID(block);
        if (type == null) return color;
        return type.getColor(color, blockX, blockZ);
    }

    @Dynamic("Target is an optifine method")
    @ModifyExpressionValue(
            method = "renderCrossedSquares",
            at = @At(
                    value = "INVOKE",
                    target = "LCustomColorizer;getColorMultiplier(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;III)I",
                    remap = false))
    private int hodgepodge$pollutionCrossedSquares(int color, Block block, int blockX, int blockY, int blockZ) {
        ColorOverrideType type = Common.config.crossedSquares.matchesID(block);
        if (type == null) return color;
        return type.getColor(color, blockX, blockZ);
    }

    @Dynamic("Target is an optifine method")
    @ModifyExpressionValue(
            method = "renderBlockVine",
            at = @At(
                    value = "INVOKE",
                    target = "LCustomColorizer;getColorMultiplier(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;III)I",
                    remap = false))
    private int hodgepodge$pollutionBlockVine(int color, Block block, int blockX, int blockY, int blockZ) {
        ColorOverrideType type = Common.config.blockVine.matchesID(block);
        if (type == null) return color;
        return type.getColor(color, blockX, blockZ);
    }
}
