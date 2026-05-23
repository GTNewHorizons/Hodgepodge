package com.mitchej123.hodgepodge.core.fml;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.ITransformers;
import com.gtnewhorizon.gtnhmixins.builders.TransformerBuilder;
import com.mitchej123.hodgepodge.config.ASMConfig;
import com.mitchej123.hodgepodge.mixins.TargetedMod;

/**
 * Transformers that must run at the end of coremods transformer chain.
 * Registered via {@link com.mitchej123.hodgepodge.core.fml.tweakers.HodgepodgeLateTweaker}
 */
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
}
