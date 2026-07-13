package com.mitchej123.hodgepodge.core;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.ITransformers;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.ASMConfig;
import com.mitchej123.hodgepodge.config.DebugConfig;
import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.GeneralConfig;
import com.mitchej123.hodgepodge.config.MemoryConfig;
import com.mitchej123.hodgepodge.config.SoundConfig;
import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.core.fml.AsmTransformers;
import com.mitchej123.hodgepodge.core.shared.EarlyConfig;
import com.mitchej123.hodgepodge.mixins.Mixins;
import com.mitchej123.hodgepodge.util.StringPooler;
import com.mitchej123.hodgepodge.util.VoxelMapCacheMover;

import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({ "com.mitchej123.hodgepodge.core", "optifine" })
public class HodgepodgeCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    private static Boolean isObf = null;
    private String[] transformerClasses;
    private boolean replaceCoFHCoreAT = false;

    public HodgepodgeCore() {
        if (!EarlyConfig.noNukeBaseMod) {
            String transformer = "com.mitchej123.hodgepodge.core.fml.transformers.early.ModContainerFactoryTransformer";
            FMLRelaunchLog.finer("Registering transformer %s", transformer);
            Launch.classLoader.registerTransformer(transformer);
        }
        try {
            ConfigurationManager.registerConfig(ASMConfig.class);
            ConfigurationManager.registerConfig(DebugConfig.class);
            ConfigurationManager.registerConfig(FixesConfig.class);
            ConfigurationManager.registerConfig(GeneralConfig.class);
            ConfigurationManager.registerConfig(MemoryConfig.class);
            ConfigurationManager.registerConfig(SpeedupsConfig.class);
            ConfigurationManager.registerConfig(TweaksConfig.class);
            ConfigurationManager.registerConfig(SoundConfig.class);
            if (TweaksConfig.enableTagCompoundStringPooling || TweaksConfig.enableNBTStringPooling) {
                StringPooler.setupPooler();
            }
            if (FixesConfig.fixBottomFaceUV) {
                Launch.blackboard.put("hodgepodge.FixesConfig.fixBottomFaceUV", Boolean.TRUE);
            }
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
        // spotless:off
        try {
            byte[] classBytes = Launch.classLoader.getClassBytes("org.bukkit.World");
            if (classBytes != null) {
                Common.log.warn("==================================================================================================");
                Common.log.warn("Thermos/Bukkit detected; This is an unsupported configuration -- Things may not function properly.");
                Common.log.warn("Using `{}` for CraftServer Package. If this is not correct, please update your config file!", ASMConfig.thermosCraftServerClass);
                Common.log.warn("==================================================================================================");
            }
        } catch (Exception ignored) {}
        // spotless:on

        try {
            if (ASMConfig.disableCoFHAccessTransformer) {
                final byte[] classBytes = Launch.classLoader.getClassBytes("cofh.CoFHCore");
                if (classBytes != null) {
                    replaceCoFHCoreAT = true;
                }
            }
        } catch (Exception ignored) {}

        if (MemoryConfig.allocs.deduplicateASMDataTableStrings) {
            String transformer = "com.mitchej123.hodgepodge.core.fml.transformers.early.ModCandidateTransformer";
            FMLRelaunchLog.finer("Registering transformer %s", transformer);
            Launch.classLoader.registerTransformer(transformer);
        }

        if (FixesConfig.fixForgeOptionalInterfaceSignature) {
            narrowFmlTransformerExclusion();
        }
    }

    @Override
    public String getMixinConfig() {
        return "mixins.hodgepodge.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return IMixins.getEarlyMixins(Mixins.class, loadedCoreMods);
    }

    @Override
    public String[] getASMTransformerClass() {
        if (transformerClasses == null) {
            transformerClasses = ITransformers.getTransformers(AsmTransformers.class);
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
    public void injectData(Map<String, Object> data) {
        isObf = (Boolean) data.get("runtimeDeobfuscationEnabled");
        VoxelMapCacheMover.changeFileExtensions((File) data.get("mcLocation"));

        final List<String> tweaks = (List<String>) Launch.blackboard.get("TweakClasses");
        if (tweaks != null) {
            tweaks.add("com.mitchej123.hodgepodge.core.fml.tweakers.HodgepodgeLateTweaker");
            if (replaceCoFHCoreAT) {
                tweaks.add("com.mitchej123.hodgepodge.core.fml.tweakers.CoFHCoreATDisablerTweaker");
            }
        }
    }

    @Override
    public String getAccessTransformerClass() {
        try {
            Class.forName("makamys.coretweaks.Config");
            if (TweaksConfig.threadedWorldDataSaving) {
                FMLRelaunchLog
                        .info("[HodgepodgeCoreCompat] Disabled CoreTweaks conflicting mixin: enhanceMapStorageErrors");
                makamys.coretweaks.Config.enhanceMapStorageErrors.disable();
            }
        } catch (ClassNotFoundException ignored) {}

        if (replaceCoFHCoreAT) {
            return "com.mitchej123.hodgepodge.core.CoFHCoreAccessTransformer";
        }

        return null;
    }

    public static boolean isObf() {
        if (isObf == null) {
            throw new IllegalStateException("Obfuscation state has been accessed too early!");
        }
        return isObf;
    }

    public static void logASM(Logger log, String message) {
        if (isObf == null || isObf) {
            log.debug(message);
        } else {
            log.info(message);
        }
    }

    private static final String FML_TRANSFORMER_PKG = "cpw.mods.fml.common.asm.transformers.";

    private static final String[] FML_TRANSFORMER_KEEP_EXCLUDED = { "AccessTransformer", "DeobfuscationTransformer",
            "EventSubscriptionTransformer", "ItemStackTransformer", "MarkerTransformer", "ModAccessTransformer",
            "PatchingTransformer", "SideTransformer", "TerminalTransformer" };

    private static void narrowFmlTransformerExclusion() {
        try {
            final Field f = LaunchClassLoader.class.getDeclaredField("transformerExceptions");
            f.setAccessible(true);
            if (f.get(Launch.classLoader) instanceof Collection<?>col) {
                @SuppressWarnings("unchecked")
                final Collection<String> exceptions = (Collection<String>) col;
                if (exceptions.remove(FML_TRANSFORMER_PKG)) {
                    Arrays.stream(FML_TRANSFORMER_KEEP_EXCLUDED).forEach(s -> exceptions.add(FML_TRANSFORMER_PKG + s));
                    FMLRelaunchLog.fine(
                            "Narrowed %s exclusion to leave ModAPITransformer transformable",
                            FML_TRANSFORMER_PKG);
                } else {
                    FMLRelaunchLog.warning(
                            "%s not present in transformerExceptions - ModAPITransformer Signature patch will not apply",
                            FML_TRANSFORMER_PKG);
                }
            }
        } catch (Throwable t) {
            FMLRelaunchLog.warning(
                    "Failed to narrow FML transformer exclusion - ModAPITransformer Signature patch will not apply: %s",
                    t);
        }
    }
}
