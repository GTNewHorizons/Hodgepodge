package com.mitchej123.hodgepodge.asm;

import com.mitchej123.hodgepodge.Hodgepodge;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({"com.mitchej123.hodgepodge.asm", "optifine"})
@IFMLLoadingPlugin.SortingIndex(1002)
@IFMLLoadingPlugin.DependsOn("cofh.asm.LoadingPlugin")
public class HodgePodgeASMLoader implements IFMLLoadingPlugin {
    private static final Logger log = LogManager.getLogger("Hodgepodge");
    public static final SortingIndex index = HodgePodgeASMLoader.class.getAnnotation(IFMLLoadingPlugin.SortingIndex.class);
    
    public static int getSortingIndex() {
        return index != null ? index.value() : 0;
    }
    
    public enum AsmTransformers {
        POLLUTION_TRANSFORMER(
            "Pollution Transformer",
            () -> Hodgepodge.config.pollutionAsm,
            Collections.singletonList("com.mitchej123.hodgepodge.asm.PollutionClassTransformer")
        ),
        CoFHWorldTransformer(
            "World Transformer - Remove CoFH tile entity cache",
            () -> Hodgepodge.config.cofhWorldTransformer,
            Collections.singletonList("com.mitchej123.hodgepodge.asm.WorldTransformer")
        ),
        SpeedupProgressBar(
            "Speed up Progress Bar by speeding up stripSpecialCharacters",
            () -> Hodgepodge.config.speedupProgressBar,
            Collections.singletonList("com.mitchej123.hodgepodge.asm.SpeedupProgressBarTransformer")
        ),
        FIX_POTION_EFFECT_RENDERING("Move vanilla potion effect status rendering before everything else",
            () -> Hodgepodge.config.fixPotionEffectRender,
            Collections.singletonList("com.mitchej123.hodgepodge.asm.InventoryEffectRendererTransformer")),
        THERMOS_SLEDGEHAMMER_FURNACE_FIX("Take a sledgehammer to CraftServer.resetRecipes() to prevent it from breaking our Furnace Fix", 
            ()-> Hodgepodge.thermosTainted && Hodgepodge.config.speedupVanillaFurnace,
            Collections.singletonList("com.mitchej123.hodgepodge.asm.ThermosFurnaceSledgeHammer")
        ),
        BIBLIOCRAFT_RECIPE_SLEDGEHAMMER("Remove recipes from Bibliocraft BlockLoader and Itemloader : addRecipies()", () -> Hodgepodge.config.biblocraftRecipes,
            Collections.singletonList("com.mitchej123.hodgepodge.asm.BibliocraftTransformer"))
        ;
        
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
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
