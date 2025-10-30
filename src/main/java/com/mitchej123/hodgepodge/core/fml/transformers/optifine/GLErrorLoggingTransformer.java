package com.mitchej123.hodgepodge.core.fml.transformers.optifine;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

@SuppressWarnings("unused")
public class GLErrorLoggingTransformer implements IClassTransformer, Opcodes {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if ("shadersmod.client.Shaders".equals(transformedName)) {
            final byte[] transformedBytes = transformBytes(basicClass);
            HodgepodgeClassDump.dumpClass(transformedName, basicClass, transformedBytes, this);
            return transformedBytes;
        }
        return basicClass;
    }

    private static byte[] transformBytes(byte[] basicClass) {
        final Logger logger = LogManager.getLogger("GLErrorLogging");
        logger.debug("TRANSFORMING shadersmod.client.Shaders");
        final ClassReader cr = new ClassReader(basicClass);
        final ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        label: for (MethodNode mn : cn.methods) {
            if (mn.name.equals("checkGLError")) {
                final int maxlocals;
                switch (mn.desc) {
                    case "(Ljava/lang/String;)I":
                        maxlocals = 1;
                        break;
                    case "(Ljava/lang/String;Ljava/lang/String;)I":
                        maxlocals = 2;
                        break;
                    case "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I":
                        maxlocals = 3;
                        break;
                    default:
                        break label;
                }
                logger.debug("Removing GL Error Logging: " + mn.desc);
                mn.visitMaxs(1, maxlocals);
                final InsnList list = mn.instructions;
                list.clear();
                LabelNode l0 = new LabelNode();
                LabelNode l1 = new LabelNode();
                list.add(l0);
                list.add(new MethodInsnNode(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glGetError", "()I", false));
                list.add(new InsnNode(IRETURN));
                list.add(l1);
                final Iterator<LocalVariableNode> it = mn.localVariables.iterator();
                while (it.hasNext()) {
                    final LocalVariableNode lv = it.next();
                    if (lv.desc.equals("Ljava/lang/String;")) {
                        lv.start = l0;
                        lv.end = l1;
                    } else {
                        it.remove();
                    }
                }
            }
        }
        final ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
