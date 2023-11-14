package com.mitchej123.hodgepodge.asm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.mitchej123.hodgepodge.Common;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

public enum AsmTransformers {

    // spotless:off
    SPEEDUP_PROGRESS_BAR(
            "Speed up Progress Bar by speeding up stripSpecialCharacters",
            () -> Common.config.speedupProgressBar,
            Side.CLIENT,
            "com.mitchej123.hodgepodge.asm.transformers.fml.SpeedupProgressBarTransformer"),
    THERMOS_SLEDGEHAMMER_FURNACE_FIX(
            "Take a sledgehammer to CraftServer.resetRecipes() to prevent it from breaking our Furnace Fix",
            () -> Common.thermosTainted && Common.config.speedupVanillaFurnace,
            Side.BOTH,
            "com.mitchej123.hodgepodge.asm.transformers.thermos.ThermosFurnaceSledgeHammer"),
    OPTIFINE_REMOVE_GLERROR_LOGGING(
            "Removes the logging of GL errors from OptiFine/Shadersmod",
            () -> Common.config.removeOptifineGLErrors,
            Side.CLIENT,
            "com.mitchej123.hodgepodge.asm.transformers.optifine.GLErrorLoggingTransformer");
    // spotless:on

    private final Supplier<Boolean> applyIf;
    private final Side side;
    private final String[] transformerClasses;

    AsmTransformers(@SuppressWarnings("unused") String description, Supplier<Boolean> applyIf, Side side,
            String... transformers) {
        this.applyIf = applyIf;
        this.side = side;
        this.transformerClasses = transformers;
    }

    private boolean shouldBeLoaded() {
        return applyIf.get() && shouldLoadSide();
    }

    private boolean shouldLoadSide() {
        return side == Side.BOTH || (side == Side.SERVER && FMLLaunchHandler.side().isServer())
                || (side == Side.CLIENT && FMLLaunchHandler.side().isClient());
    }

    public static String[] getTransformers() {
        final List<String> list = new ArrayList<>();
        for (AsmTransformers transformer : values()) {
            if (transformer.shouldBeLoaded()) {
                Common.log.info("Loading transformer {}", (Object[]) transformer.transformerClasses);
                list.addAll(Arrays.asList(transformer.transformerClasses));
            } else {
                Common.log.info("Not loading transformer {}", (Object[]) transformer.transformerClasses);
            }
        }
        return list.toArray(new String[0]);
    }

    private enum Side {
        BOTH,
        CLIENT,
        SERVER
    }

}
