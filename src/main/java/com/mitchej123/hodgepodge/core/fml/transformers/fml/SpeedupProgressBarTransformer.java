package com.mitchej123.hodgepodge.core.fml.transformers.fml;

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
import org.objectweb.asm.tree.VarInsnNode;

import com.mitchej123.hodgepodge.core.HodgepodgeCore;
import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

@SuppressWarnings("unused")
public class SpeedupProgressBarTransformer implements IClassTransformer, Opcodes {

    private static final Logger LOGGER = LogManager.getLogger("ProgressBarSpeedup");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if ("cpw.mods.fml.client.FMLClientHandler".equals(transformedName)) {
            final byte[] transformedBytes = transformBytes(basicClass);
            HodgepodgeClassDump.dumpClass(transformedName, basicClass, transformedBytes, this);
            return transformedBytes;
        }
        return basicClass;
    }

    private static byte[] transformBytes(byte[] basicClass) {
        HodgepodgeCore.logASM(LOGGER, "TRANSFORMING cpw.mods.fml.client.FMLClientHandler");
        final ClassReader cr = new ClassReader(basicClass);
        final ClassNode cn = new ClassNode(ASM5);
        cr.accept(cn, 0);
        for (MethodNode m : cn.methods) {
            if ("stripSpecialChars".equals(m.name) && "(Ljava/lang/String;)Ljava/lang/String;".equals(m.desc)) {
                HodgepodgeCore.logASM(LOGGER, "Speeding up stripSpecialChars");
                final InsnList i = m.instructions;
                i.clear();
                i.add(new VarInsnNode(ALOAD, 1));
                i.add(
                        new MethodInsnNode(
                                INVOKESTATIC,
                                "com/mitchej123/hodgepodge/core/fml/hooks/fml/FMLClientHandlerHook",
                                m.name,
                                m.desc,
                                false));
                i.add(new InsnNode(ARETURN));
            }
        }
        final ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
