package com.mitchej123.hodgepodge.asm.transformers.fml;

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
import org.objectweb.asm.tree.VarInsnNode;

import com.mitchej123.hodgepodge.Common;

@SuppressWarnings("unused")
public class SpeedupProgressBarTransformer implements IClassTransformer {

    private static final Logger LOGGER = LogManager.getLogger("ProgressBarSpeedup");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("cpw.mods.fml.client.FMLClientHandler".equals(transformedName)) {
            Common.logASM(LOGGER, "TRANSFORMING cpw.mods.fml.client.FMLClientHandler");
            final ClassReader cr = new ClassReader(basicClass);
            final ClassNode cn = new ClassNode(ASM5);
            cr.accept(cn, 0);
            for (MethodNode m : cn.methods) {
                if ("stripSpecialChars".equals(m.name) && "(Ljava/lang/String;)Ljava/lang/String;".equals(m.desc)) {
                    Common.logASM(LOGGER, "Speeding up stripSpecialChars");
                    final InsnList i = m.instructions;
                    i.clear();
                    i.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    i.add(
                            new MethodInsnNode(
                                    Opcodes.INVOKESTATIC,
                                    "com/mitchej123/hodgepodge/asm/hooks/fml/FMLClientHandlerHook",
                                    m.name,
                                    m.desc,
                                    false));
                    i.add(new InsnNode(Opcodes.ARETURN));
                }
            }
            final ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);
            return cw.toByteArray();
        }
        return basicClass;
    }
}
