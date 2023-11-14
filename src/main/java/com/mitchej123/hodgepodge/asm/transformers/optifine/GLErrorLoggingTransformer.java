package com.mitchej123.hodgepodge.asm.transformers.optifine;

import static org.objectweb.asm.Opcodes.ASM5;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GLErrorLoggingTransformer implements IClassTransformer {

    private static final Logger LOGGER = LogManager.getLogger("GLErrorLogging");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("shadersmod.client.Shaders".equals(transformedName)) {
            LOGGER.info("TRANSFORMING shadersmod.client.Shaders");
            final ClassReader cr = new ClassReader(basicClass);
            final ClassNode cn = new ClassNode(ASM5);
            cr.accept(cn, 0);
            for (MethodNode m : cn.methods) {
                if ("checkGLError".equals(m.name) && m.desc.endsWith(")I")) {
                    LOGGER.info("Removing GL Error Logging: " + m.desc);
                    final InsnList i = m.instructions;
                    i.clear();
                    i.add(
                            new MethodInsnNode(
                                    Opcodes.INVOKESTATIC,
                                    "org/lwjgl/opengl/GL11",
                                    "glGetError",
                                    "()I",
                                    false));
                    i.add(new InsnNode(Opcodes.IRETURN));
                }
            }
            final ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);
            return cw.toByteArray();
        }

        return basicClass;
    }
}
