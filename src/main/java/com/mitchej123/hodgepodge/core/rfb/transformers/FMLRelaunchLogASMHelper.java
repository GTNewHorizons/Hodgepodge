package com.mitchej123.hodgepodge.core.rfb.transformers;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class FMLRelaunchLogASMHelper implements Opcodes {

    public static final String LOG_DESC = "(Ljava/lang/String;Lorg/apache/logging/log4j/Level;Ljava/lang/String;[Ljava/lang/Object;)V";
    public static final String LOG_THROWABLE_DESC = "(Ljava/lang/String;Lorg/apache/logging/log4j/Level;Ljava/lang/Throwable;Ljava/lang/String;[Ljava/lang/Object;)V";
    public static final String HOOK = "com/mitchej123/hodgepodge/core/rfb/hooks/FMLRelaunchLogHook";
    public static final String HOOK_DESC = "(Ljava/lang/String;Ljava/lang/Class;)Lorg/apache/logging/log4j/Logger;";
    private static final String LOG_MANAGER = "org/apache/logging/log4j/LogManager";
    private static final String GET_LOGGER_DESC = "(Ljava/lang/String;)Lorg/apache/logging/log4j/Logger;";

    public static boolean transform(ClassNode classNode) {
        final List<MethodNode> methods = new ArrayList<>(2);
        final List<MethodInsnNode> sites = new ArrayList<>(2);
        for (MethodNode method : classNode.methods) {
            if (!method.name.equals("log")) continue;
            if (!method.desc.equals(LOG_DESC) && !method.desc.equals(LOG_THROWABLE_DESC)) continue;
            final MethodInsnNode site = singleSite(method);
            if (site == null) return false;
            methods.add(method);
            sites.add(site);
        }
        if (sites.size() != 2) return false;
        final Type owner = Type.getObjectType(classNode.name);
        for (int i = 0; i < sites.size(); i++) {
            final MethodInsnNode site = sites.get(i);
            methods.get(i).instructions.insertBefore(site, new LdcInsnNode(owner));
            site.owner = HOOK;
            site.name = "getLogger";
            site.desc = HOOK_DESC;
            site.itf = false;
        }
        return true;
    }

    private static MethodInsnNode singleSite(MethodNode method) {
        MethodInsnNode found = null;
        for (AbstractInsnNode node = method.instructions.getFirst(); node != null; node = node.getNext()) {
            if (node.getOpcode() == INVOKESTATIC && node instanceof MethodInsnNode call
                    && call.owner.equals(LOG_MANAGER)
                    && call.name.equals("getLogger")
                    && call.desc.equals(GET_LOGGER_DESC)) {
                if (found != null) return null;
                found = call;
            }
        }
        return found;
    }
}
