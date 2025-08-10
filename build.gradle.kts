//version: 1707058017

plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

minecraft {
    extraRunJvmArguments.add("-Dhodgepodge.logModTimes=true")
    extraRunJvmArguments.add("-Dhodgepodge.dumpClass=true")
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
