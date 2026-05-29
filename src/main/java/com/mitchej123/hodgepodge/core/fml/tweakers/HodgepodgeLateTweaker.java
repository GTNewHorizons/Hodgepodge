package com.mitchej123.hodgepodge.core.fml.tweakers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

// Must not reference Hodgepodge classes: tweaker may load on a different ClassLoader
// than HodgepodgeCore
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
        try {
            Class.forName("com.mitchej123.hodgepodge.core.fml.LateAsmTransformers", true, Launch.classLoader)
                    .getDeclaredMethod("registerLateTransformers").invoke(null);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException
                | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return new String[0];
    }
}
