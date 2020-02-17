package com.mitchej123.hodgepodge.core;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import sun.misc.URLClassPath;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE + 10000)
@IFMLLoadingPlugin.Name(HodgepodgeLoadingPlugin.PLUGIN_NAME)
public class HodgepodgeLoadingPlugin implements IFMLLoadingPlugin {
    public static final String PLUGIN_NAME = "Hodgepodge Core Plugin";
    private static final Logger log = LogManager.getLogger("Hodgepodge");
    public static LoadingConfig config;

    static {
        log.info("Initializing Hodgepodge");
        config = new LoadingConfig(new File(Launch.minecraftHome, "config/hodgepodge.cfg"));
        fixMixinClasspathOrder();
        initMixin();
    }

    private static void fixMixinClasspathOrder() {
        // Borrowed from VanillaFix -- Move jar up in the classloader's URLs to make sure that the latest version of Mixin is used
        URL url = HodgepodgeLoadingPlugin.class.getProtectionDomain().getCodeSource().getLocation();
        givePriorityInClasspath(url, Launch.classLoader);
        givePriorityInClasspath(url, (URLClassLoader) ClassLoader.getSystemClassLoader());
    }

    private static void givePriorityInClasspath(URL url, URLClassLoader classLoader) {
        try {
            Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
            ucpField.setAccessible(true);

            List<URL> urls = new ArrayList<>(Arrays.asList(classLoader.getURLs()));
            urls.remove(url);
            urls.add(0, url);
            URLClassPath ucp = new URLClassPath(urls.toArray(new URL[0]));

            ucpField.set(classLoader, ucp);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static void initMixin() {
        MixinBootstrap.init();

        if (config.fixNorthWestBias) initPlugin("Fix Northwest Bias", "mixins.hodgepodge.fixnorthwestbias.json");

    }

    public static void initPlugin(String name, String plugin) {
        log.info("Loading hodgepodge plugin '" + name + "' from '" + plugin + "'");
        Mixins.addConfiguration(plugin);
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
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
    public void injectData(Map<String, Object> map) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
