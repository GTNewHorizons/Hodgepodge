//version: 1707058017

plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

minecraft {
    extraRunJvmArguments.add("-Dhodgepodge.dumpClass=true")
    extraRunJvmArguments.add("-Dhodgepodge.logModTimes=true")
    //extraRunJvmArguments.add("-Dhodgepodge.logEventTimes=true")
    //extraRunJvmArguments.add("-Dhodgepodge.logConfigTimes=true")
    /*extraRunJvmArguments.addAll(
        "-Dlegacy.debugClassLoading=true",
        "-Dlegacy.debugClassLoadingFiner=true",
        "-Dlegacy.debugClassLoadingSave=true")*/
    // extraRunJvmArguments.addAll("-Drfb.dumpLoadedClasses=true", "-Drfb.dumpLoadedClassesPerTransformer=true")
}

tasks.processResources {
    inputs.property("version", project.version.toString())
    filesMatching("META-INF/rfb-plugin/*") {
        expand("version" to project.version.toString())
    }
}

tasks.applyJST.configure {
    // The path here can be anything, it doesn't need to be in injectedInterfaces
    // The contents of these files must match this:
    // https://github.com/neoforged/JavaSourceTransformer?tab=readme-ov-file#interface-injection
    // Interfaces should only be added to src/injectedInterfaces/java, if they are added to main, mixin, test, etc then MC will not compile
    interfaceInjectionConfigs.setFrom("src/injectedInterfaces/injected_interfaces.json")
}

tasks.jar {
    from(sourceSets.injectedInterfaces.get().output)
}
