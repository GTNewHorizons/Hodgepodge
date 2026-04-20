package com.mitchej123.hodgepodge.core.rfb.hooks;

import org.objectweb.asm.ClassReader;

import cpw.mods.fml.common.eventhandler.Event;

public class ForgeEventSubscriptionHook {

    public static boolean shouldTransformClass(Object $this, ClassReader cr) {
        final String superName = cr.getSuperName();
        if (superName == null || superName.startsWith("java/lang/")) return false;
        final Class<?> parent;
        try {
            parent = $this.getClass().getClassLoader().loadClass(superName.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            return false;
        }
        return Event.class.isAssignableFrom(parent);
    }
}
