package com.mitchej123.hodgepodge.asm;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.gtnhmixins.builders.ITransformers;
import com.gtnewhorizon.gtnhmixins.builders.TransformerBuilder;
import com.mitchej123.hodgepodge.Common;
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
    SPEEDUP_LONG_INT_HASHMAP(
            "Speed up LongHashMap & IntHashMap",
            () -> ASMConfig.speedupLongIntHashMap,
            Side.COMMON,
            ImmutableList.of(TargetedMod.FASTCRAFT, TargetedMod.BUKKIT),
            "com.mitchej123.hodgepodge.asm.transformers.mc.SpeedupLongIntHashMapTransformer"),
    SPEEDUP_NBT_TAG_COMPOUND_COPY(
            "Speed up NBTTagCompound.copy()",
            () -> ASMConfig.speedupNBTTagCompoundCopy,
            Side.COMMON,
            ImmutableList.of(TargetedMod.FASTCRAFT, TargetedMod.BUKKIT),
            "com.mitchej123.hodgepodge.asm.transformers.mc.NBTTagCompoundHashMapTransformer"),
    SPEEDUP_PLAYER_MANAGER(
            "Speed up PlayerManager",
            () -> ASMConfig.speedupPlayerManager,
            Side.COMMON,
            ImmutableList.of(TargetedMod.FASTCRAFT, TargetedMod.BUKKIT),
            "com.mitchej123.hodgepodge.asm.transformers.mc.PlayerManagerTransformer"
    ),
    REMOVE_VARARG_SPAM(
            "Remove vararg methods in GenLayer classes",
            () -> ASMConfig.dissectVarargs,
            Side.COMMON,
            "com.mitchej123.hodgepodge.asm.transformers.mc.VarargDissector"
    ),
    SPEEDUP_ORE_DICTIONARY(
            "Speed up Forge OreDictionary",
            () -> ASMConfig.speedupOreDictionary,
            Side.COMMON,
            ImmutableList.of(TargetedMod.FASTCRAFT, TargetedMod.BUKKIT, TargetedMod.ULTRAMINE),
            "com.mitchej123.hodgepodge.asm.transformers.mc.SpeedupOreDictionaryTransformer"
    ),
    FIX_BOGUS_INTEGRATED_SERVER_NPE(
            "Fix bogus FMLProxyPacket NPEs on integrated server crashes",
            () -> FixesConfig.fixBogusIntegratedServerNPEs,
            Side.COMMON,
            "com.mitchej123.hodgepodge.asm.transformers.fml.FMLIndexedMessageToMessageCodecTransformer"
    ),
    THERMOS_SLEDGEHAMMER_FURNACE_FIX(
            "Take a sledgehammer to CraftServer.resetRecipes() to prevent it from breaking our Furnace Fix",
            () -> Common.thermosTainted && SpeedupsConfig.speedupVanillaFurnace,
            Side.COMMON,
            "com.mitchej123.hodgepodge.asm.transformers.thermos.ThermosFurnaceSledgeHammer"),
    OPTIFINE_REMOVE_GLERROR_LOGGING(
            "Removes the logging of GL errors from OptiFine/Shadersmod",
            () -> TweaksConfig.removeOptifineGLErrors,
            Side.CLIENT,
            "com.mitchej123.hodgepodge.asm.transformers.optifine.GLErrorLoggingTransformer");
    // spotless:on

    private final TransformerBuilder builder;

    AsmTransformers(@SuppressWarnings("unused") String desc, Supplier<Boolean> applyIf, Side side,
            String... transformers) {
        this(desc, applyIf, side, null, transformers);
    }

    AsmTransformers(@SuppressWarnings("unused") String description, Supplier<Boolean> applyIf, Side side,
            List<TargetedMod> excludedMods, String... transformers) {
        this.builder = new TransformerBuilder().addSidedTransformers(side, transformers).setApplyIf(applyIf);
        if (excludedMods != null) {
            excludedMods.forEach(this.builder::addExcludedMod);
        }
    }

    @Nonnull
    @Override
    public TransformerBuilder getBuilder() {
        return builder;
    }

}
