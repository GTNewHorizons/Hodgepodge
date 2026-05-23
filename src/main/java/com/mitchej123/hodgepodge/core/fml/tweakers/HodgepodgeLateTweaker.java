package com.mitchej123.hodgepodge.core.fml.tweakers;

import java.io.File;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

// Must not reference Hodgepodge classes: tweaker may load on a different ClassLoader
// than HodgepodgeCore
public class HodgepodgeLateTweaker implements ITweaker {

    public static final String BLACKBOARD_KEY = "hodgepodge.lateTransformerClasses";

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
        final Object raw = Launch.blackboard.get(BLACKBOARD_KEY);
        if (raw instanceof List<?>) {
            for (Object o : (List<?>) raw) {
                if (o instanceof String) {
                    Launch.classLoader.registerTransformer((String) o);
                }
            }
        }
        return new String[0];
    }
}
