package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.LoadingConfig;
import com.mitchej123.hodgepodge.util.ColorOverrideType;

@Mixin(RenderBlocks.class)
public class MixinRenderBlocks_Pollution {

    @ModifyExpressionValue(
            method = "renderStandardBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;colorMultiplier(Lnet/minecraft/world/IBlockAccess;III)I"))
    private int hodgepodge$pollutionStandardBlock(int color, Block block, int blockX, int blockY, int blockZ) {
        ColorOverrideType type = LoadingConfig.standardBlocks.matchesID(block);
        if (type == null) return color;
        return type.getColor(color, blockX, blockZ);
    }

    @ModifyExpressionValue(
            method = "renderBlockLiquid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;colorMultiplier(Lnet/minecraft/world/IBlockAccess;III)I"))
    private int hodgepodge$pollutionBlockLiquid(int color, Block block, int blockX, int blockY, int blockZ) {
        ColorOverrideType type = LoadingConfig.liquidBlocks.matchesID(block);
        if (type == null || block.getMaterial() != Material.water) {
            return color;
        }
        return type.getColor(color, blockX, blockZ);
    }

    @ModifyExpressionValue(
            method = "renderBlockDoublePlant",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockDoublePlant;colorMultiplier(Lnet/minecraft/world/IBlockAccess;III)I"))
    private int hodgepodge$pollutionBlockDoublePlant(int color, BlockDoublePlant block, int blockX, int blockY,
            int blockZ) {
        ColorOverrideType type = LoadingConfig.doublePlants.matchesID(block);
        if (type == null) return color;
        return type.getColor(color, blockX, blockZ);
    }

    @ModifyExpressionValue(
            method = "renderCrossedSquares",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;colorMultiplier(Lnet/minecraft/world/IBlockAccess;III)I"))
    private int hodgepodge$pollutionCrossedSquares(int color, Block block, int blockX, int blockY, int blockZ) {
        ColorOverrideType type = LoadingConfig.crossedSquares.matchesID(block);
        if (type == null) return color;
        return type.getColor(color, blockX, blockZ);
    }

    @ModifyExpressionValue(
            method = "renderBlockVine",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;colorMultiplier(Lnet/minecraft/world/IBlockAccess;III)I"))
    private int hodgepodge$pollutionBlockVine(int color, Block block, int blockX, int blockY, int blockZ) {
        ColorOverrideType type = LoadingConfig.blockVine.matchesID(block);
        if (type == null) return color;
        return type.getColor(color, blockX, blockZ);
    }
}
