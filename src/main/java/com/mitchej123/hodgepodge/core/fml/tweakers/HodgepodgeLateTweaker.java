package com.mitchej123.hodgepodge.core.fml.tweakers;

import java.io.File;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.gtnewhorizon.gtnhmixins.builders.ITransformers;
import com.mitchej123.hodgepodge.core.fml.LateAsmTransformers;

import cpw.mods.fml.relauncher.FMLRelaunchLog;

/**
 * Registers Hodgepodge transformers that must run last in the LaunchClassLoader chain.
 */
public class HodgepodgeLateTweaker implements ITweaker {

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {}

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {}

    @Override
    public String getLaunchTarget() {
        return null;
    }

    @Override
    public String[] getLaunchArguments() {
        for (String transformer : ITransformers.getTransformers(LateAsmTransformers.class)) {
            FMLRelaunchLog.finer("Registering late transformer %s", transformer);
            Launch.classLoader.registerTransformer(transformer);
        }
        return new String[0];
    }
}
