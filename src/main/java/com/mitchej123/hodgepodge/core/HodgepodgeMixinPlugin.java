package com.mitchej123.hodgepodge.core;

import com.mitchej123.hodgepodge.Hodgepodge;
import com.mitchej123.hodgepodge.mixins.Mixins;
import com.mitchej123.hodgepodge.mixins.TargetedMod;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.launchwrapper.Launch;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import ru.timeconqueror.spongemixins.MinecraftURLClassPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HodgepodgeMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        final boolean isDevelopmentEnvironment = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

        List<TargetedMod> loadedMods = Arrays.stream(TargetedMod.values())
            .filter(mod -> (
                        mod == TargetedMod.VANILLA 
                            || (isDevelopmentEnvironment && MinecraftURLClassPath.findJarInClassPath(mod.devJarName))
                            || loadModJar(mod)
                    )
            )
            .collect(Collectors.toList());

        for (TargetedMod mod : TargetedMod.values()) {
            if(loadedMods.contains(mod)) {
                Hodgepodge.log.info("Found Mod " + mod.modName + "!");
            }
            else if (ArrayUtils.contains(Hodgepodge.config.requiredMods, mod.modName) && (!isDevelopmentEnvironment || Hodgepodge.config.requiredModsInDev)){
                Hodgepodge.log.error("CRITICAL ERROR: Could not find required jar {}.  If this mod is not required please remove it from the 'requiredMods' section of the config.", mod.modName);
                FMLCommonHandler.instance().exitJava(-1, true);
            } else {
                Hodgepodge.log.info("Could not find " + mod.modName + "! Skipping mixins....");
            }
        }

        List<String> mixins = new ArrayList<>();
        for (Mixins mixin : Mixins.values()) {
            if (mixin.shouldLoad(loadedMods)) {
                mixins.addAll(mixin.mixinClass);
                Hodgepodge.log.debug("Loading hodgepodge mixin: " + mixin.mixinClass);
            }
        }
        
        return mixins;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
    

    private boolean loadModJar(final TargetedMod mod) {
        try {
            File jar = MinecraftURLClassPath.getJarInModPath(mod.jarName);
            if(jar == null) {
                Hodgepodge.log.info("Jar not found for " + mod);
                return false;
            }

            Hodgepodge.log.info("Attempting to add " + jar + " to the URL Class Path");
            if(!jar.exists()) {
                throw new FileNotFoundException(jar.toString());
            }
            MinecraftURLClassPath.addJar(jar);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
