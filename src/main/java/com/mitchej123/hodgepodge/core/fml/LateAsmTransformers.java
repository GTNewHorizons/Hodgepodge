package com.mitchej123.hodgepodge.core.fml;

import javax.annotation.Nonnull;

import net.minecraft.launchwrapper.Launch;

import com.gtnewhorizon.gtnhmixins.builders.ITransformers;
import com.gtnewhorizon.gtnhmixins.builders.TransformerBuilder;
import com.mitchej123.hodgepodge.config.ASMConfig;
import com.mitchej123.hodgepodge.mixins.TargetedMod;

import cpw.mods.fml.relauncher.FMLRelaunchLog;

// Transformers registered at the END of LaunchClassLoader.transformers, via HodgepodgeLateTweaker.
public enum LateAsmTransformers implements ITransformers {

    // spotless:off
    SPEEDUP_FORGE_ORE_DICTIONARY(new TransformerBuilder()
            .setApplyIf(() -> ASMConfig.speedupOreDictionary)
            .addExcludedMod(TargetedMod.FASTCRAFT)
            .addExcludedMod(TargetedMod.BUKKIT)
            .addExcludedMod(TargetedMod.ULTRAMINE)
            .addCommonTransformers("com.mitchej123.hodgepodge.core.fml.transformers.mc.SpeedupOreDictionaryTransformer"));
    // spotless:on

    private final TransformerBuilder builder;

    LateAsmTransformers(TransformerBuilder builder) {
        this.builder = builder;
    }

    @Nonnull
    @Override
    public TransformerBuilder getBuilder() {
        return builder;
    }

    // invoked via reflection by late tweaker
    @SuppressWarnings("unused")
    public static void registerLateTransformers() {
        for (String transformer : ITransformers.getTransformers(LateAsmTransformers.class)) {
            FMLRelaunchLog.finer("Registering transformer %s", transformer);
            Launch.classLoader.registerTransformer(transformer);
        }
    }
}
