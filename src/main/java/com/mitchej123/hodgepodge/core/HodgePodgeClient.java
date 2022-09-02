package com.mitchej123.hodgepodge.core;

import com.mitchej123.hodgepodge.Hodgepodge;
import com.mitchej123.hodgepodge.asm.References;
import com.mitchej123.hodgepodge.client.ClientTicker;
import com.mitchej123.hodgepodge.client.DebugScreenHandler;
import com.mitchej123.hodgepodge.core.handlers.ClientKeyListener;
import com.mitchej123.hodgepodge.core.util.ColorOverrideType;
import cpw.mods.fml.common.FMLCommonHandler;
import java.lang.reflect.Method;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.MinecraftForge;

public class HodgePodgeClient {
    public static Method colorGrass, colorLiquid, colorLeaves, colorFoliage;
    // 0 - render no animations 1 - render visible animations 2 - render all animations
    public static int animationsMode = 1;
    // 0 - no debug 1 - reduced 2 - full
    public static int renderDebugMode = 0;

    public static void postInit() {
        colorGrass = References.gt_PollutionRenderer.getMethod("colorGrass").resolve();
        colorLiquid = References.gt_PollutionRenderer.getMethod("colorLiquid").resolve();
        colorLeaves = References.gt_PollutionRenderer.getMethod("colorLeaves").resolve();
        colorFoliage = References.gt_PollutionRenderer.getMethod("colorFoliage").resolve();

        renderDebugMode = Hodgepodge.config.renderDebug ? Hodgepodge.config.renderDebugMode : 0;

        if (colorGrass != null) {
            LoadingConfig.postInitClient();
            MinecraftForge.EVENT_BUS.register(LoadingConfig.standardBlocks);
            MinecraftForge.EVENT_BUS.register(LoadingConfig.liquidBlocks);
            MinecraftForge.EVENT_BUS.register(LoadingConfig.doublePlants);
            MinecraftForge.EVENT_BUS.register(LoadingConfig.crossedSquares);
            MinecraftForge.EVENT_BUS.register(LoadingConfig.blockVine);
        }

        FMLCommonHandler.instance().bus().register(ClientTicker.INSTANCE);

        if (Hodgepodge.config.addSystemInfo) {
            MinecraftForge.EVENT_BUS.register(DebugScreenHandler.INSTANCE);
        }

        if (Hodgepodge.config.speedupAnimations) {
            FMLCommonHandler.instance().bus().register(new ClientKeyListener());
        }
    }

    // ASM hookers
    public static int renderStandardBlock_colorMultiplier(int oColor, Block block, int x, int z) {
        ColorOverrideType type = LoadingConfig.standardBlocks.matchesID(block);
        if (type == null) return oColor;
        return type.getColor(oColor, x, z);
    }

    public static int renderBlockLiquid_colorMultiplier(int oColor, Block block, int x, int z) {
        ColorOverrideType type = LoadingConfig.liquidBlocks.matchesID(block);
        if (type == null || block.blockMaterial != Material.water) {
            return oColor;
        }
        return type.getColor(oColor, x, z);
    }

    public static int renderBlockDoublePlant_colorMultiplier(int oColor, Block block, int x, int z) {
        ColorOverrideType type = LoadingConfig.doublePlants.matchesID(block);
        if (type == null) return oColor;
        return type.getColor(oColor, x, z);
    }

    public static int renderCrossedSquares_colorMultiplier(int oColor, Block block, int x, int z) {
        ColorOverrideType type = LoadingConfig.crossedSquares.matchesID(block);
        if (type == null) return oColor;
        return type.getColor(oColor, x, z);
    }

    public static int renderBlockVine_colorMultiplier(int oColor, Block block, int x, int z) {
        ColorOverrideType type = LoadingConfig.blockVine.matchesID(block);
        if (type == null) return oColor;
        return type.getColor(oColor, x, z);
    }
}
