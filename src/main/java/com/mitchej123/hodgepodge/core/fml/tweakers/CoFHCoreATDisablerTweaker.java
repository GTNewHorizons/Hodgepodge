package com.mitchej123.hodgepodge.core.fml.tweakers;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import cpw.mods.fml.relauncher.FMLRelaunchLog;

public class CoFHCoreATDisablerTweaker implements ITweaker {

    @Override
    public void acceptOptions(List<String> list, File file, File file1, String s) {}

    @Override
    public void injectIntoClassLoader(LaunchClassLoader launchClassLoader) {
        try {
            final Field xformersField = launchClassLoader.getClass().getDeclaredField("transformers");
            xformersField.setAccessible(true);
            final List<IClassTransformer> xformers = (List<IClassTransformer>) xformersField.get(launchClassLoader);
            for (int idx = xformers.size() - 1; idx >= 0; idx--) {
                final String name = xformers.get(idx).getClass().getName();
                if (name.equals("cofh.asm.CoFHAccessTransformer")) {
                    FMLRelaunchLog.info("[CoFHCoreATDisablerTweaker] Transformer successfully disabled");
                    xformers.remove(idx);
                }
            }
        } catch (Exception e) {
            FMLRelaunchLog.severe("[CoFHCoreATDisablerTweaker] Failed to disable transformer:", e);
        }
    }

    @Override
    public String getLaunchTarget() {
        return null;
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
