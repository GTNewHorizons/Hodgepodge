package com.mitchej123.hodgepodge.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.asm.AsmTransformers;
import com.mitchej123.hodgepodge.mixins.Mixins;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({ "com.mitchej123.hodgepodge.asm", "optifine" })
@IFMLLoadingPlugin.SortingIndex(1002)
@IFMLLoadingPlugin.DependsOn("cofh.asm.LoadingPlugin")
public class HodgepodgeCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public static final SortingIndex index = HodgepodgeCore.class.getAnnotation(IFMLLoadingPlugin.SortingIndex.class);

    private String[] transformerClasses;

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

    @Override
    public String[] getASMTransformerClass() {
        if (transformerClasses == null) {
            transformerClasses = AsmTransformers.getTransformers();
        }
        return transformerClasses;
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
