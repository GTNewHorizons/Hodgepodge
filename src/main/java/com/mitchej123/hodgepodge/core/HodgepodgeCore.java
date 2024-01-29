package com.mitchej123.hodgepodge.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.mitchej123.hodgepodge.asm.AsmTransformers;
import com.mitchej123.hodgepodge.config.ASMConfig;
import com.mitchej123.hodgepodge.config.DebugConfig;
import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.OverallConfig;
import com.mitchej123.hodgepodge.config.PollutionConfig;
import com.mitchej123.hodgepodge.config.PollutionRecolorConfig;
import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.mixins.Mixins;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({ "com.mitchej123.hodgepodge.asm", "optifine" })
@IFMLLoadingPlugin.DependsOn("cofh.asm.LoadingPlugin")
public class HodgepodgeCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    static {
        try {
            ConfigurationManager.registerConfig(ASMConfig.class);
            ConfigurationManager.registerConfig(DebugConfig.class);
            ConfigurationManager.registerConfig(FixesConfig.class);
            ConfigurationManager.registerConfig(OverallConfig.class);
            ConfigurationManager.registerConfig(PollutionConfig.class);
            ConfigurationManager.registerConfig(PollutionRecolorConfig.class);
            ConfigurationManager.registerConfig(SpeedupsConfig.class);
            ConfigurationManager.registerConfig(TweaksConfig.class);
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
    }

    private String[] transformerClasses;

    @Override
    public String getMixinConfig() {
        return "mixins.hodgepodge.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return Mixins.getEarlyMixins(loadedCoreMods);
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
