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
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        for (int i = 0; i < args.size(); i++) {
            final String arg = args.get(i);
            if (!arg.startsWith("--quickPlay")) continue;

            final int eq = arg.indexOf('=');
            final String key = eq < 0 ? arg : arg.substring(0, eq);
            String value = eq < 0 ? null : arg.substring(eq + 1);
            if (value == null && i + 1 < args.size() && !args.get(i + 1).startsWith("-")) {
                value = args.get(++i);
            }
            if (value == null || value.trim().isEmpty()) continue;

            if ("--quickPlaySingleplayer".equals(key)) {
                Launch.blackboard.put("hodgepodge.quickPlay.singleplayer", value);
            } else if ("--quickPlayMultiplayer".equals(key)) {
                Launch.blackboard.put("hodgepodge.quickPlay.multiplayer", value);
            }
        }
    }

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
