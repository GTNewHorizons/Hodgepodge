package com.mitchej123.hodgepodge.asm.transformers.travellersgear;

import static org.objectweb.asm.Opcodes.ASM5;

import com.mitchej123.hodgepodge.Hodgepodge;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

@SuppressWarnings("unused")
public class ClientProxyTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("travellersgear.client.ClientProxy".equals(transformedName)) {
            Hodgepodge.log.info("Patching TravellersGear {}", transformedName);
            final ClassReader cr = new ClassReader(basicClass);
            final ClassWriter cw = new ClassWriter(0);
            final ClassNode cn = new ClassNode(ASM5);
            cr.accept(cn, 0);
            for (MethodNode m : cn.methods) {
                if ("guiPostInit".equals(m.name)) {
                    for (AbstractInsnNode insnNode : m.instructions.toArray()) {
                        if (insnNode instanceof MethodInsnNode
                                && insnNode.getOpcode() == Opcodes.INVOKEINTERFACE
                                && ((MethodInsnNode) insnNode).name.equals("isEmpty")
                                && ((MethodInsnNode) insnNode).desc.equals("()Z")) {
                            InsnList list = new InsnList();
                            list.add(new InsnNode(Opcodes.POP));
                            list.add(new InsnNode(Opcodes.ICONST_1));
                            m.instructions.insert(insnNode, list);
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
