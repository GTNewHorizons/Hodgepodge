//version: 1707058017

plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

tasks.jar {
    manifest {
        // I need a place to call add this IClassTransformer before CoFHCore loads its transformation target
        attributes("CCTransformer" to "com.mitchej123.hodgepodge.asm.transformers.early.EarlyClassTransformer")
    }
}

tasks.processResources {
    inputs.property("version", project.version.toString())
    filesMatching("META-INF/rfb-plugin/*") {
        expand("version" to project.version.toString())
    }
}
