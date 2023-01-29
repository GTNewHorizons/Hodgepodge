package com.mitchej123.hodgepodge.asm.transformers.tconstruct;

import static org.objectweb.asm.Opcodes.ASM5;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import com.mitchej123.hodgepodge.Common;

@SuppressWarnings("unused")
public class TabRegistryTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("tconstruct.client.tabs.TabRegistry".equals(transformedName)) {
            Common.log.info("Patching TConstruct {}", transformedName);
            final ClassReader cr = new ClassReader(basicClass);
            final ClassWriter cw = new ClassWriter(0);
            final ClassNode cn = new ClassNode(ASM5);
            cr.accept(cn, 0);
            for (MethodNode m : cn.methods) {
                if ("guiPostInit".equals(m.name)) {
                    for (AbstractInsnNode insnNode : m.instructions.toArray()) {
                        if (insnNode instanceof MethodInsnNode && insnNode.getOpcode() == Opcodes.INVOKESTATIC
                                && ((MethodInsnNode) insnNode).name.equals("getPotionOffset")
                                && ((MethodInsnNode) insnNode).desc.equals("()I")) {
                            m.instructions.insert(
                                    insnNode,
                                    new MethodInsnNode(
                                            Opcodes.INVOKESTATIC,
                                            "com/mitchej123/hodgepodge/asm/hooks/tconstruct/TabRegistryHook",
                                            "fixPotionOffset",
                                            "()I",
                                            false));
                            m.instructions.remove(insnNode);
                        }
                    }
                }
            }
            cn.accept(cw);
            return cw.toByteArray();
        } else {
            return basicClass;
        }
    }
}
