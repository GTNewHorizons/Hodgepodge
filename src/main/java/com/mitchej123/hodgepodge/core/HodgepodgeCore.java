package com.mitchej123.hodgepodge.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.mixins.Mixins;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({ "com.mitchej123.hodgepodge.asm", "optifine" })
@IFMLLoadingPlugin.SortingIndex(1002)
@IFMLLoadingPlugin.DependsOn("cofh.asm.LoadingPlugin")
public class HodgepodgeCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    private static final Logger log = LogManager.getLogger("Hodgepodge");
    public static final SortingIndex index = HodgepodgeCore.class.getAnnotation(IFMLLoadingPlugin.SortingIndex.class);

    public static int getSortingIndex() {
        return index != null ? index.value() : 0;
    }

    @Override
    public String getMixinConfig() {
        return "mixins.hodgepodge.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        final List<String> mixins = new ArrayList<>();
        final List<String> notLoading = new ArrayList<>();
        for (Mixins mixin : Mixins.values()) {
            if (mixin.phase == Mixins.Phase.EARLY) {
                if (mixin.shouldLoad(loadedCoreMods, Collections.emptySet())) {
                    mixins.addAll(mixin.mixinClasses);
                } else {
                    notLoading.addAll(mixin.mixinClasses);
                }
            }
        }
        Common.log.info("Not loading the following EARLY mixins: {}", notLoading.toString());
        return mixins;
    }

    public enum AsmTransformers {

        POLLUTION_TRANSFORMER("Pollution Transformer", () -> Common.config.pollutionAsm,
                Collections.singletonList(
                        "com.mitchej123.hodgepodge.asm.transformers.pollution.PollutionClassTransformer")),
        CoFHWorldTransformer("World Transformer - Remove CoFH tile entity cache",
                () -> Common.config.cofhWorldTransformer,
                Collections.singletonList("com.mitchej123.hodgepodge.asm.transformers.cofh.WorldTransformer")),
        SpeedupProgressBar("Speed up Progress Bar by speeding up stripSpecialCharacters",
                () -> Common.config.speedupProgressBar,
                Collections
                        .singletonList("com.mitchej123.hodgepodge.asm.transformers.fml.SpeedupProgressBarTransformer")),
        THERMOS_SLEDGEHAMMER_FURNACE_FIX(
                "Take a sledgehammer to CraftServer.resetRecipes() to prevent it from breaking our Furnace Fix",
                () -> Common.thermosTainted && Common.config.speedupVanillaFurnace,
                Collections.singletonList(
                        "com.mitchej123.hodgepodge.asm.transformers.thermos.ThermosFurnaceSledgeHammer")),
        BIBLIOCRAFT_RECIPE_SLEDGEHAMMER("Remove recipes from Bibliocraft BlockLoader and Itemloader : addRecipies()",
                () -> Common.config.biblocraftRecipes, Collections.singletonList(
                        "com.mitchej123.hodgepodge.asm.transformers.bibliocraft.BibliocraftTransformer"));

        private final String name;
        private final Supplier<Boolean> applyIf;
        private final List<String> asmTransformers;

        AsmTransformers(String name, Supplier<Boolean> applyIf, List<String> asmTransformers) {
            this.name = name;
            this.applyIf = applyIf;
            this.asmTransformers = asmTransformers;
        }

        public boolean shouldBeLoaded() {
            return applyIf.get();
        }
    }

    @Override
    public String[] getASMTransformerClass() {

        return Arrays.stream(AsmTransformers.values()).map(asmTransformer -> {
            if (asmTransformer.shouldBeLoaded()) {
                log.info("Loading hodgepodge transformers {}", asmTransformer.name);
                return asmTransformer.asmTransformers;
            } else {
                return null;
            }
        }).filter(Objects::nonNull).flatMap(List::stream).toArray(String[]::new);
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
