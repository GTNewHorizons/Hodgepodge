package hodgepodge;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import org.junit.jupiter.api.DynamicTest;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;

final class MixinScenarios {

    private static boolean bootstrapped;

    private MixinScenarios() {}

    static Stream<DynamicTest> run(Class<?> owner) throws Exception {
        bootstrap(owner.getClassLoader());
        Class<?> scenarios = Launch.classLoader.loadClass(owner.getName() + "$Scenarios");
        return Arrays.stream(scenarios.getDeclaredMethods()).filter(method -> method.getName().startsWith("test"))
                .map(method -> DynamicTest.dynamicTest(method.getName(), () -> {
                    Object fixture = scenarios.getConstructor().newInstance();
                    try {
                        scenarios.getMethod("setUp").invoke(fixture);
                        method.invoke(fixture);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    } finally {
                        scenarios.getMethod("tearDown").invoke(fixture);
                    }
                }));
    }

    private static synchronized void bootstrap(ClassLoader testLoader) throws Exception {
        if (bootstrapped) return;
        // Minecraft must be loaded through LaunchWrapper before JUnit/Mockito can load any of its classes.
        Set<URL> classpath = new LinkedHashSet<>();
        for (ClassLoader loader = testLoader; loader != null; loader = loader.getParent()) {
            if (loader instanceof URLClassLoader) {
                classpath.addAll(Arrays.asList(((URLClassLoader) loader).getURLs()));
            }
        }
        for (String entry : System.getProperty("java.class.path").split(File.pathSeparator)) {
            classpath.add(new File(entry).toURI().toURL());
        }
        Launch.classLoader = new LaunchClassLoader(classpath.toArray(new URL[0]));
        Launch.blackboard = new HashMap<>();
        Launch.blackboard.put("TweakClasses", new ArrayList<>());
        Launch.blackboard.put("Tweaks", new ArrayList<>());
        for (String prefix : new String[] { "org.spongepowered.", "org.objectweb.asm.", "com.llamalad7.", "org.junit.",
                "org.mockito.", "net.bytebuddy.", "org.objenesis." }) {
            Launch.classLoader.addClassLoaderExclusion(prefix);
        }
        // Do not launch unrelated FML coremods or discover their mixin configs from dependency manifests.
        Class.forName("org.spongepowered.asm.launch.platform.MixinContainer");
        List<String> agents = GlobalProperties.get(GlobalProperties.Keys.AGENTS);
        agents.clear();
        Launch.classLoader.registerTransformer("cpw.mods.fml.common.asm.transformers.EventSubscriptionTransformer");
        MixinBootstrap.init();
        MixinExtrasBootstrap.init();
        Mixins.addConfiguration("mixins.hodgepodge.login-test.json");
        Mixins.addConfiguration("mixins.hodgepodge.world-save-test.json");
        Mixins.addConfiguration("mixins.hodgepodge.chunkgen-test.json");
        Method phase = MixinEnvironment.class.getDeclaredMethod("gotoPhase", MixinEnvironment.Phase.class);
        phase.setAccessible(true);
        phase.invoke(null, MixinEnvironment.Phase.DEFAULT);
        MixinEnvironment.getCurrentEnvironment().setSide(MixinEnvironment.Side.SERVER);
        bootstrapped = true;
    }
}
