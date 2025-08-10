//version: 1707058017

plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

tasks.jar {
    manifest {
        // I need a place to call add this IClassTransformer before CoFHCore loads its transformation target
        attributes("CCTransformer" to "com.mitchej123.hodgepodge.asm.transformers.early.ModContainerFactoryTransformer")
    }
}
minecraft {
    extraRunJvmArguments.add("-Dhodgepodge.logModTimes=true")
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
