package com.mitchej123.hodgepodge.core;

import com.mitchej123.hodgepodge.core.util.ColorOverrideType;
import com.mitchej123.hodgepodge.loader.References;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Method;

public class HodgePodgeClient {
    public static Method method_GTAPI_GetPollution;

    public static void postInit() {

        method_GTAPI_GetPollution =  References.gt_API.getMethod("getClientPollutionAt").resolve();
        if (method_GTAPI_GetPollution != null) {
            HodgepodgeMixinPlugin.config.postInitClient();
            MinecraftForge.EVENT_BUS.register(LoadingConfig.standardBlocks);
            MinecraftForge.EVENT_BUS.register(LoadingConfig.liquidBlocks);
            MinecraftForge.EVENT_BUS.register(LoadingConfig.doublePlants);
            MinecraftForge.EVENT_BUS.register(LoadingConfig.crossedSquares);
            MinecraftForge.EVENT_BUS.register(LoadingConfig.blockVine);
        }
    }

    private final static int[] uglyGrassColor = {140, 110, 65};

    // ASM hookers
    public static int renderStandardBlock_colorMultiplier(int oColor, Block block, int x, int z) {
        ColorOverrideType type = LoadingConfig.standardBlocks.matchesID(block);
        if (type == null)
            return oColor;
        return type.getColor(oColor, block, x, z);
    }

    public static int renderBlockLiquid_colorMultiplier(int oColor, Block block, int x, int z) {
        ColorOverrideType type = LoadingConfig.liquidBlocks.matchesID(block);
        if (type == null || block.blockMaterial != Material.water) {
            return oColor;
        }
        return type.getColor(oColor, block, x, z);
    }

    public static int renderBlockDoublePlant_colorMultiplier(int oColor, Block block, int x, int z) {
        ColorOverrideType type = LoadingConfig.doublePlants.matchesID(block);
        if (type == null)
            return oColor;
        return type.getColor(oColor, block, x, z);
    }

    public static int renderCrossedSquares_colorMultiplier(int oColor, Block block, int x, int z) {
        ColorOverrideType type = LoadingConfig.crossedSquares.matchesID(block);
        if (type == null)
            return oColor;
        return type.getColor(oColor, block, x, z);
    }

    public static int renderBlockVine_colorMultiplier(int oColor, Block block, int x, int z) {
        ColorOverrideType type = LoadingConfig.blockVine.matchesID(block);
        if (type == null)
            return oColor;
        return type.getColor(oColor, block, x, z);
    }
}
