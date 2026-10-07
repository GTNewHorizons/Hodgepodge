package com.mitchej123.hodgepodge.core.rfb.transformers;

import java.util.jar.Manifest;

import org.apache.logging.log4j.LogManager;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;
import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

public class FMLRelaunchLogLoggerCacheTransformer implements RfbClassTransformer {

    private static final String TARGET = "cpw.mods.fml.relauncher.FMLRelaunchLog";

    @Pattern("[a-z0-9-]+")
    @Override
    public @NotNull String id() {
        return "fml-relaunch-log-logger-cache";
    }

    @Override
    public boolean shouldTransformClass(@NotNull ExtensibleClassLoader classLoader,
            @NotNull RfbClassTransformer.Context context, @Nullable Manifest manifest, @NotNull String className,
            @NotNull ClassNodeHandle classNode) {
        return classNode.isPresent() && className.equals(TARGET);
    }

    @Override
    public void transformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull RfbClassTransformer.Context context,
            @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNodeHandle) {
        final ClassNode classNode = classNodeHandle.getNode();
        if (classNode == null || !FMLRelaunchLogASMHelper.transform(classNode)) {
            LogManager.getLogger("HodgepodgeEarly")
                    .warn("[FMLRelaunchLogLoggerCacheTransformer] Unexpected shape, {} left unchanged", className);
            return;
        }
        classNodeHandle.computeMaxs();
        HodgepodgeClassDump.dumpClass(className, classNodeHandle, this);
    }
}
