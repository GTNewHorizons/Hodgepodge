package com.mitchej123.hodgepodge.core;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.mixins.Mixins;
import com.mitchej123.hodgepodge.mixins.TargetedMod;
import cpw.mods.fml.common.FMLCommonHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.launchwrapper.Launch;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import ru.timeconqueror.spongemixins.MinecraftURLClassPath;

public class HodgepodgeMixinPlugin implements IMixinConfigPlugin {

    public static boolean isEnvironmentDeobfuscated;

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        isEnvironmentDeobfuscated = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

        List<TargetedMod> loadedMods = Arrays.stream(TargetedMod.values())
                .filter(mod -> (mod == TargetedMod.VANILLA
                        || (isEnvironmentDeobfuscated && MinecraftURLClassPath.findJarInClassPath(mod.devJarName))
                        || loadModJar(mod)))
                .collect(Collectors.toList());

        for (TargetedMod mod : TargetedMod.values()) {
            if (loadedMods.contains(mod)) {
                Common.log.info("Found Mod " + mod.modName + "!");
            } else if (ArrayUtils.contains(Common.config.requiredMods, mod.modName)
                    && (!isEnvironmentDeobfuscated || Common.config.requiredModsInDev)) {
                Common.log.error(
                        "CRITICAL ERROR: Could not find required jar {}.  If this mod is not required please remove it from the 'requiredMods' section of the config.",
                        mod.modName);
                FMLCommonHandler.instance().exitJava(-1, true);
            } else {
                Common.log.info("Could not find " + mod.modName + "! Skipping mixins....");
            }
        }

        List<String> mixins = new ArrayList<>();
        for (Mixins mixin : Mixins.values()) {
            if (mixin.shouldLoad(loadedMods)) {
                mixins.addAll(mixin.mixinClass);
                Common.log.info("Loading hodgepodge mixin: " + mixin.mixinClass);
            } else {
                Common.log.info("NOT loading mixin: " + mixin.mixinClass);
            }
        }

        return mixins;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    private boolean loadModJar(final TargetedMod mod) {
        try {
            File jar = MinecraftURLClassPath.getJarInModPath(mod.jarName);
            if (jar == null) {
                Common.log.info("Jar not found for " + mod);
                return false;
            }
            // TODO it loads the wrong .jar for certain mods that contains the same same
            //  for instance with TargetedMod = TINKERSCONSTRUCT("TConstruct", "TConstruct", "TinkersConstruct")
            //  it loads IguanaTweaksTConstruct-1.7.10-2.2.2.jar instead
            Common.log.info("Attempting to add " + jar + " to the URL Class Path");
            if (!jar.exists()) {
                throw new FileNotFoundException(jar.toString());
            }
            MinecraftURLClassPath.addJar(jar);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
