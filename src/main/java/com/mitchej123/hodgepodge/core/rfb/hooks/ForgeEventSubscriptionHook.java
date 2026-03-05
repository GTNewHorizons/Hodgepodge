package com.mitchej123.hodgepodge.core.rfb.hooks;

import java.io.IOException;

import net.minecraft.launchwrapper.Launch;

import org.objectweb.asm.ClassReader;

public class ForgeEventSubscriptionHook {

    private static final String JAVA_OBJECT = "java/lang/Object";
    private static final String FML_EVENT = "cpw/mods/fml/common/eventhandler/Event";

    public static boolean shouldTransformClass(byte[] classBytes) {
        while (classBytes != null) {
            String superName = new ClassReader(classBytes).getSuperName();

            if (superName == null || superName.equals(JAVA_OBJECT)) {
                return false;
            }

            if (superName.equals(FML_EVENT)) {
                return true;
            }

            try {
                classBytes = Launch.classLoader.getClassBytes(superName.replace('/', '.'));
            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }
}
