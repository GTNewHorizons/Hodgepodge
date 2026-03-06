package com.mitchej123.hodgepodge.core.rfb.hooks;

import org.objectweb.asm.ClassReader;

import cpw.mods.fml.common.asm.transformers.EventSubscriptionTransformer;
import cpw.mods.fml.common.eventhandler.Event;

public class ForgeEventSubscriptionHook {

    public static boolean shouldTransformClass(ClassReader cr) {
        final String superName = cr.getSuperName();
        if (superName == null || superName.startsWith("java/lang/")) return false;
        final Class<?> parent;
        try {
            parent = EventSubscriptionTransformer.class.getClassLoader().loadClass(superName.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            return false;
        }
        return Event.class.isAssignableFrom(parent);
    }
}
