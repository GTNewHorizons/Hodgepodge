package com.mitchej123.hodgepodge.client;

import java.lang.reflect.Method;
import java.util.Arrays;

import net.minecraftforge.common.MinecraftForge;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.Compat;
import com.mitchej123.hodgepodge.LoadingConfig;
import com.mitchej123.hodgepodge.asm.References;
import com.mitchej123.hodgepodge.client.handlers.ClientKeyListener;
import com.mitchej123.hodgepodge.util.ManagedEnum;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModMetadata;

public class HodgepodgeClient {

    public static Method colorGrass, colorLiquid, colorLeaves, colorFoliage;
    public static final ManagedEnum<AnimationMode> animationsMode = new ManagedEnum<>(AnimationMode.VISIBLE_ONLY);
    public static final ManagedEnum<RenderDebugMode> renderDebugMode = new ManagedEnum<>(RenderDebugMode.REDUCED);

    public static void preInit() {
        if (Compat.isGT5Present()) {
            MinecraftForge.EVENT_BUS.register(PollutionTooltip.INSTANCE);
        }
    }

    public static void postInit() {
        colorGrass = References.gt_PollutionRenderer.getMethod("colorGrass").resolve();
        colorLiquid = References.gt_PollutionRenderer.getMethod("colorLiquid").resolve();
        colorLeaves = References.gt_PollutionRenderer.getMethod("colorLeaves").resolve();
        colorFoliage = References.gt_PollutionRenderer.getMethod("colorFoliage").resolve();

        if (Common.config.renderDebug) {
            renderDebugMode.set(Common.config.renderDebugMode);
        } else {
            renderDebugMode.set(RenderDebugMode.OFF);
        }

        if (Common.config.enableDefaultLanPort) {
            if (Common.config.defaultLanPort < 0 || Common.config.defaultLanPort > 65535) {
                Common.log.error(
                        String.format(
                                "Default LAN port number must be in range of 0-65535, but %s was given. Defaulting to 0.",
                                Common.config.defaultLanPort));
                Common.config.defaultLanPort = 0;
            }
        }

        if (colorGrass != null) {
            LoadingConfig.postInitClient();
            MinecraftForge.EVENT_BUS.register(LoadingConfig.standardBlocks);
            MinecraftForge.EVENT_BUS.register(LoadingConfig.liquidBlocks);
            MinecraftForge.EVENT_BUS.register(LoadingConfig.doublePlants);
            MinecraftForge.EVENT_BUS.register(LoadingConfig.crossedSquares);
            MinecraftForge.EVENT_BUS.register(LoadingConfig.blockVine);
        }

        FMLCommonHandler.instance().bus().register(ClientTicker.INSTANCE);

        if (Common.config.addSystemInfo) {
            MinecraftForge.EVENT_BUS.register(DebugScreenHandler.INSTANCE);
        }

        if (Common.config.speedupAnimations) {
            FMLCommonHandler.instance().bus().register(new ClientKeyListener());
        }

        if (Compat.isIC2CropPluginPresent()) {
            ModMetadata meta = Loader.instance().getIndexedModList().get("Ic2Nei").getMetadata();
            meta.authorList = Arrays.asList("Speiger");
            meta.description = "This IC2 Addon allows you to simulate the CropBreeding.";
            meta.autogenerated = false;
        }
    }

    public enum AnimationMode {
        NONE,
        VISIBLE_ONLY,
        ALL
    }

    public enum RenderDebugMode {
        OFF,
        REDUCED,
        FULL
    }
}
