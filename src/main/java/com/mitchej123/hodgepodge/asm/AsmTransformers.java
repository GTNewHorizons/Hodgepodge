package com.mitchej123.hodgepodge.asm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.gtnhmixins.core.GTNHMixinsCore;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.ASMConfig;
import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.mixins.TargetedMod;

import cpw.mods.fml.relauncher.CoreModManager;
import cpw.mods.fml.relauncher.FMLLaunchHandler;

public enum AsmTransformers {

    // spotless:off
    SPEEDUP_PROGRESS_BAR(
            "Speed up Progress Bar by speeding up stripSpecialCharacters",
            () -> ASMConfig.speedupProgressBar,
            Side.CLIENT,
            "com.mitchej123.hodgepodge.asm.transformers.fml.SpeedupProgressBarTransformer"),
    SPEEDUP_LONG_INT_HASHMAP(
            "Speed up LongHashMap & IntHashMap",
            () -> ASMConfig.speedupLongIntHashMap,
            Side.BOTH,
            null,
            ImmutableList.of(TargetedMod.FASTCRAFT, TargetedMod.BUKKIT),
            "com.mitchej123.hodgepodge.asm.transformers.mc.SpeedupLongIntHashMapTransformer"),
    SPEEDUP_NBT_TAG_COMPOUND_COPY(
            "Speed up NBTTagCompound.copy()",
            () -> ASMConfig.speedupNBTTagCompoundCopy,
            Side.BOTH,
            null,
            ImmutableList.of(TargetedMod.FASTCRAFT, TargetedMod.BUKKIT),
            "com.mitchej123.hodgepodge.asm.transformers.mc.NBTTagCompoundHashMapTransformer"),
    SPEEDUP_PLAYER_MANAGER(
            "Speed up PlayerManager",
            () -> ASMConfig.speedupPlayerManager,
            Side.BOTH,
            null,
            ImmutableList.of(TargetedMod.FASTCRAFT, TargetedMod.BUKKIT),
            "com.mitchej123.hodgepodge.asm.transformers.mc.PlayerManagerTransformer"
    ),
    SPEEDUP_OBJECT_INT_IDENTITY_MAP(
            "Speed up ObjectIntIdentityMap",
            () -> ASMConfig.speedupObjectIntIdentityMap,
            Side.BOTH,
            null,
            ImmutableList.of(TargetedMod.FASTCRAFT, TargetedMod.BUKKIT),
            "com.mitchej123.hodgepodge.asm.transformers.mc.SpeedupObjectIntIdentityMapTransformer"
    ),
    REMOVE_VARARG_SPAM(
            "Remove vararg methods in GenLayer classes",
            () -> ASMConfig.dissectVarargs,
            Side.BOTH,
            "com.mitchej123.hodgepodge.asm.transformers.mc.VarargDissector"
    ),
    FIX_BOGUS_INTEGRATED_SERVER_NPE(
            "Fix bogus FMLProxyPacket NPEs on integrated server crashes",
            () -> FixesConfig.fixBogusIntegratedServerNPEs,
            Side.BOTH,
            "com.mitchej123.hodgepodge.asm.transformers.fml.FMLIndexedMessageToMessageCodecTransformer"
    ),
    THERMOS_SLEDGEHAMMER_FURNACE_FIX(
            "Take a sledgehammer to CraftServer.resetRecipes() to prevent it from breaking our Furnace Fix",
            () -> Common.thermosTainted && SpeedupsConfig.speedupVanillaFurnace,
            Side.BOTH,
            "com.mitchej123.hodgepodge.asm.transformers.thermos.ThermosFurnaceSledgeHammer"),
    OPTIFINE_REMOVE_GLERROR_LOGGING(
            "Removes the logging of GL errors from OptiFine/Shadersmod",
            () -> TweaksConfig.removeOptifineGLErrors,
            Side.CLIENT,
            "com.mitchej123.hodgepodge.asm.transformers.optifine.GLErrorLoggingTransformer");
    // spotless:on

    private final Supplier<Boolean> applyIf;
    private final Side side;
    private final String[] transformerClasses;
    private final List<TargetedMod> targetedMods;
    private final List<TargetedMod> excludedMods;

    AsmTransformers(@SuppressWarnings("unused") String description, Supplier<Boolean> applyIf, Side side,
            String... transformers) {
        this.applyIf = applyIf;
        this.side = side;
        this.transformerClasses = transformers;
        this.targetedMods = new ArrayList<>();
        this.excludedMods = new ArrayList<>();
    }

    AsmTransformers(@SuppressWarnings("unused") String description, Supplier<Boolean> applyIf, Side side,
            List<TargetedMod> targetedMods, List<TargetedMod> excludedMods, String... transformers) {
        this.applyIf = applyIf;
        this.side = side;
        this.transformerClasses = transformers;
        this.targetedMods = targetedMods;
        this.excludedMods = excludedMods;
    }

    private boolean shouldBeLoaded(Set<String> loadedCoreMods) {
        if (loadedCoreMods == null) loadedCoreMods = Collections.emptySet();

        return applyIf.get() && shouldLoadSide()
                && allModsLoaded(targetedMods, loadedCoreMods)
                && noModsLoaded(excludedMods, loadedCoreMods);
    }

    private boolean allModsLoaded(List<TargetedMod> targetedMods, Set<String> loadedCoreMods) {
        // If no mods are targeted, we're good to go
        if (targetedMods == null || targetedMods.isEmpty()) return true;

        for (TargetedMod target : targetedMods) {
            if (target == TargetedMod.VANILLA) continue;

            if (!loadedCoreMods.isEmpty() && target.coreModClass != null
                    && !loadedCoreMods.contains(target.coreModClass))
                return false;
        }

        return true;
    }

    private boolean noModsLoaded(List<TargetedMod> targetedMods, Set<String> loadedCoreMods) {
        if (targetedMods == null || targetedMods.isEmpty()) return true;

        for (TargetedMod target : targetedMods) {
            if (target == TargetedMod.VANILLA) continue;

            if (!loadedCoreMods.isEmpty() && target.coreModClass != null
                    && loadedCoreMods.contains(target.coreModClass))
                return false;
        }

        return true;
    }

    private boolean shouldLoadSide() {
        return side == Side.BOTH || (side == Side.SERVER && FMLLaunchHandler.side().isServer())
                || (side == Side.CLIENT && FMLLaunchHandler.side().isClient());
    }

    @SuppressWarnings("unchecked")
    public static String[] getTransformers() {
        Set<String> coremods;
        // Gross. Also Note - this will only catch coremods loaded by the time this is called.
        try {
            Field loadPlugins = CoreModManager.class.getDeclaredField("loadPlugins");
            loadPlugins.setAccessible(true);
            List<?> loadedCoremods = (List<?>) loadPlugins.get(null);

            Class<?> mixinCore = Class.forName("com.gtnewhorizon.gtnhmixins.core.GTNHMixinsCore");
            Method getLoadedCoremods = mixinCore.getDeclaredMethod("getLoadedCoremods", List.class);
            getLoadedCoremods.setAccessible(true);
            // Might change some class loading behavior, but it's set to load pretty early so it should be fine
            // However I didn't notice GTNHMixinsCore in the loaded core mods... (but did notice the Unimixins-All
            // coremod)
            coremods = (Set<String>) getLoadedCoremods.invoke(new GTNHMixinsCore(), loadedCoremods);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException
                | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        final List<String> transformerList = new ArrayList<>();
        for (AsmTransformers transformer : values()) {
            if (transformer.shouldBeLoaded(coremods)) {
                Common.log.info("Loading transformer {}", (Object[]) transformer.transformerClasses);
                transformerList.addAll(Arrays.asList(transformer.transformerClasses));
            } else {
                Common.log.info("Not loading transformer {}", (Object[]) transformer.transformerClasses);
            }
        }
        return transformerList.toArray(new String[0]);
    }

    private enum Side {
        BOTH,
        CLIENT,
        SERVER
    }

}
