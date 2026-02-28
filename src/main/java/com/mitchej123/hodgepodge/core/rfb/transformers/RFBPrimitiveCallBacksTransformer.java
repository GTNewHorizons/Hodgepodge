package com.mitchej123.hodgepodge.core.rfb.transformers;

import java.util.jar.Manifest;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;
import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;
import com.mitchej123.hodgepodge.core.shared.transformers.PrimitiveCallBacksTransformer;

public class RFBPrimitiveCallBacksTransformer implements RfbClassTransformer {

    // TODO add java 8 normal transformer
    private final PrimitiveCallBacksTransformer inner = new PrimitiveCallBacksTransformer();

    @Pattern("[a-z0-9-]+")
    @Override
    public @NotNull String id() {
        return "callbackinfo-transformer";
    }

    @Override
    public @NotNull String @Nullable [] sortAfter() {
        return new String[] { "*", "mixin:mixin" };
    }

    @Override
    public @NotNull String @Nullable [] sortBefore() {
        return new String[] { "lwjgl3ify:redirect" };
    }

    @Override
    public @NotNull String @Nullable [] additionalExclusions() {
        return inner.getTransformerExclusions();
    }

    @Override
    public boolean shouldTransformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
            @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        if (!classNode.isPresent()) {
            return false;
        }
        if (!classNode.isOriginal()) {
            // If a class is already a transformed ClassNode, conservatively continue processing.
            return true;
        }
        return inner.shouldTransform(classNode.getOriginalBytes());
    }

    @Override
    public void transformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
            @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        final boolean changed = inner.transformClassNode(className, classNode.getNode());
        if (changed) {
            classNode.computeMaxs();
            classNode.computeFrames();
            HodgepodgeClassDump.dumpRFBClass(className, classNode, this);
        }
    }
}
