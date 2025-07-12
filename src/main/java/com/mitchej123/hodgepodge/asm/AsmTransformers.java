package com.mitchej123.hodgepodge.asm;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.ITransformers;
import com.gtnewhorizon.gtnhmixins.builders.TransformerBuilder;
import com.mitchej123.hodgepodge.config.ASMConfig;
import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.mixins.TargetedMod;

public enum AsmTransformers implements ITransformers {

    // spotless:off
    SPEEDUP_PROGRESS_BAR(
            "Speed up Progress Bar by speeding up stripSpecialCharacters",
            () -> ASMConfig.speedupProgressBar,
            Side.CLIENT,
            "com.mitchej123.hodgepodge.asm.transformers.fml.SpeedupProgressBarTransformer"),
    SPEEDUP_LONG_INT_HASHMAP(new TransformerBuilder()
            .setApplyIf(() -> ASMConfig.speedupLongIntHashMap)
            .addExcludedMod(TargetedMod.FASTCRAFT)
            .addExcludedMod(TargetedMod.BUKKIT)
            .addCommonTransformers("com.mitchej123.hodgepodge.asm.transformers.mc.SpeedupLongIntHashMapTransformer")),
    SPEEDUP_NBT_TAG_COMPOUND_COPY(new TransformerBuilder()
            .setApplyIf(() -> ASMConfig.speedupNBTTagCompoundCopy)
            .addExcludedMod(TargetedMod.FASTCRAFT)
            .addExcludedMod(TargetedMod.BUKKIT)
            .addCommonTransformers("com.mitchej123.hodgepodge.asm.transformers.mc.NBTTagCompoundHashMapTransformer")),
    SPEEDUP_PLAYER_MANAGER(new TransformerBuilder()
            .setApplyIf(() -> ASMConfig.speedupPlayerManager)
            .addExcludedMod(TargetedMod.FASTCRAFT)
            .addExcludedMod(TargetedMod.BUKKIT)
            .addCommonTransformers("com.mitchej123.hodgepodge.asm.transformers.mc.PlayerManagerTransformer")),
    REMOVE_VARARG_SPAM(
            "Remove vararg methods in GenLayer classes",
            () -> ASMConfig.dissectVarargs,
            Side.COMMON,
            "com.mitchej123.hodgepodge.asm.transformers.mc.VarargDissector"),
    SPEEDUP_FORGE_ORE_DICTIONARY(new TransformerBuilder()
            .setApplyIf(() -> ASMConfig.speedupOreDictionary)
            .addExcludedMod(TargetedMod.FASTCRAFT)
            .addExcludedMod(TargetedMod.BUKKIT)
            .addExcludedMod(TargetedMod.ULTRAMINE)
            .addCommonTransformers("com.mitchej123.hodgepodge.asm.transformers.mc.SpeedupOreDictionaryTransformer")),
    FIX_BOGUS_INTEGRATED_SERVER_NPE(
            "Fix bogus FMLProxyPacket NPEs on integrated server crashes",
            () -> FixesConfig.fixBogusIntegratedServerNPEs,
            Side.COMMON,
            "com.mitchej123.hodgepodge.asm.transformers.fml.FMLIndexedMessageToMessageCodecTransformer"),
    THERMOS_SLEDGEHAMMER_FURNACE_FIX(new TransformerBuilder("Take a sledgehammer to CraftServer.resetRecipes() to prevent it from breaking our Furnace Fix")
            .setApplyIf(() -> SpeedupsConfig.speedupVanillaFurnace)
            .addRequiredMod(TargetedMod.BUKKIT)
            .addCommonTransformers("com.mitchej123.hodgepodge.asm.transformers.thermos.ThermosFurnaceSledgeHammer")),
    OPTIFINE_REMOVE_GLERROR_LOGGING(
            "Removes the logging of GL errors from OptiFine/Shadersmod",
            () -> TweaksConfig.removeOptifineGLErrors,
            Side.CLIENT,
            "com.mitchej123.hodgepodge.asm.transformers.optifine.GLErrorLoggingTransformer");
    // spotless:on

    private final TransformerBuilder builder;

    AsmTransformers(TransformerBuilder builder) {
        this.builder = builder;
    }

    AsmTransformers(@SuppressWarnings("unused") String desc, Supplier<Boolean> applyIf, Side side,
            String... transformers) {
        this.builder = new TransformerBuilder().setApplyIf(applyIf).addSidedTransformers(side, transformers);
    }

    @Nonnull
    @Override
    public TransformerBuilder getBuilder() {
        return builder;
    }

}
