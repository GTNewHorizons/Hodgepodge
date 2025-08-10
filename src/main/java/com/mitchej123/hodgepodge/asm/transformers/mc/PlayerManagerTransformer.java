package com.mitchej123.hodgepodge.asm.transformers.mc;

import java.util.ListIterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import com.mitchej123.hodgepodge.asm.HodgepodgeClassDump;

@SuppressWarnings("unused")
public class PlayerManagerTransformer implements IClassTransformer {

    private static final String TARGET_CLASS = "net.minecraft.server.management.PlayerManager";
    private static final String METHOD_NAME = "filterChunkLoadQueue";
    private static final String[] METHOD_NAMES_OBF = { "func_72691_b", "b" };

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if (TARGET_CLASS.equals(transformedName)) {
            final byte[] transformedBytes = transformBytes(basicClass);
            HodgepodgeClassDump.dumpClass(transformedName, basicClass, transformedBytes, this);
            return transformedBytes;
        }
        return basicClass;
    }

    private static byte[] transformBytes(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        boolean changed = false;
        for (MethodNode method : classNode.methods) {
            if (isTargetMethod(method)) {
                changed |= transformMethod(method);
            }
        }
        if (changed) {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }
        return basicClass;
    }

    private static boolean isTargetMethod(MethodNode method) {
        if (METHOD_NAME.equals(method.name)) {
            return true;
        }
        for (String obfName : METHOD_NAMES_OBF) {
            if (obfName.equals(method.name)) {
                return true;
            }
        }
        return false;
    }

    private static boolean transformMethod(MethodNode method) {
        boolean changed = false;

        ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode insn = iterator.next();

            if (insn.getOpcode() == Opcodes.INVOKESPECIAL && insn instanceof MethodInsnNode methodInsn) {
                if (methodInsn.owner.equals("java/util/ArrayList") && methodInsn.name.equals("<init>")) {
                    methodInsn.owner = "it/unimi/dsi/fastutil/objects/ObjectOpenHashSet";
                    changed = true;
                }
            } else if (insn.getOpcode() == Opcodes.NEW && insn instanceof TypeInsnNode typeInsn) {
                if (typeInsn.desc.equals("java/util/ArrayList")) {
                    typeInsn.desc = "it/unimi/dsi/fastutil/objects/ObjectOpenHashSet";
                    changed = true;
                }
            } else if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL && insn instanceof MethodInsnNode methodInsn) {
                if (methodInsn.owner.equals("java/util/ArrayList") && methodInsn.name.equals("contains")) {
                    methodInsn.owner = "java/util/Set";
                    methodInsn.name = "contains";
                    methodInsn.desc = "(Ljava/lang/Object;)Z"; // Ensure correct descriptor
                    methodInsn.setOpcode(Opcodes.INVOKEINTERFACE); // Change to INVOKEINTERFACE
                    methodInsn.itf = true; // Mark as interface method
                    changed = true;
                }
            }
        }

        // Update local variable declarations
        if (method.localVariables != null) {
            for (LocalVariableNode localVar : method.localVariables) {
                if ("arraylist".equals(localVar.name) && "Ljava/util/ArrayList;".equals(localVar.desc)) {
                    localVar.name = "loadedChunks";
                    localVar.desc = "Ljava/util/Set;";
                    changed = true;
                }
            }
        }

        return changed;
    }
}
