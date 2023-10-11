package com.mitchej123.hodgepodge.asm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.mitchej123.hodgepodge.Common;

public enum AsmTransformers {

    // spotless:off
    POLLUTION_TRANSFORMER(
            "Pollution Transformer",
            () -> Common.config.pollutionAsm,
            "com.mitchej123.hodgepodge.asm.transformers.pollution.PollutionClassTransformer"),
    SPEEDUP_PROGRESS_BAR(
            "Speed up Progress Bar by speeding up stripSpecialCharacters",
            () -> Common.config.speedupProgressBar,
            "com.mitchej123.hodgepodge.asm.transformers.fml.SpeedupProgressBarTransformer"),
    THERMOS_SLEDGEHAMMER_FURNACE_FIX(
            "Take a sledgehammer to CraftServer.resetRecipes() to prevent it from breaking our Furnace Fix",
            () -> Common.thermosTainted && Common.config.speedupVanillaFurnace,
            "com.mitchej123.hodgepodge.asm.transformers.thermos.ThermosFurnaceSledgeHammer");
    // spotless:on

    private final String name;
    private final Supplier<Boolean> applyIf;
    private final String[] asmTransformers;

    AsmTransformers(String name, Supplier<Boolean> applyIf, String... transformers) {
        this.name = name;
        this.applyIf = applyIf;
        this.asmTransformers = transformers;
    }

    private boolean shouldBeLoaded() {
        return applyIf.get();
    }

    public static String[] getTransformers() {
        final List<String> list = new ArrayList<>();
        for (AsmTransformers transformer : values()) {
            if (transformer.shouldBeLoaded()) {
                Common.log.info("Loading hodgepodge transformers {}", transformer.name);
                list.addAll(Arrays.asList(transformer.asmTransformers));
            }
        }
        return list.toArray(new String[0]);
    }

}
