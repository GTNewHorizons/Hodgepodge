package com.mitchej123.hodgepodge.core.fml.transformers.fml;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.gtnewhorizon.gtnhlib.asm.SafeClassWriter;
import com.mitchej123.hodgepodge.core.HodgepodgeCore;
import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

// Patches ModAPITransformer.stripInterface to also rewrite classNode.signature after removing the interface
public class ModAPITransformerSignaturePatch implements IClassTransformer, Opcodes {

    private static final String TARGET = "cpw.mods.fml.common.asm.transformers.ModAPITransformer";
    private static final String STRIP_INTERFACE_NAME = "stripInterface";
    private static final String STRIP_INTERFACE_DESC = "(Lorg/objectweb/asm/tree/ClassNode;Ljava/lang/String;Z)V";
    private static final String LIST_OWNER = "java/util/List";
    private static final String LIST_REMOVE_NAME = "remove";
    private static final String LIST_REMOVE_DESC = "(Ljava/lang/Object;)Z";
    private static final String HOOK_OWNER = "com/mitchej123/hodgepodge/core/fml/hooks/fml/ModAPITransformerHook";
    private static final String HOOK_NAME = "stripInterfaceFromSignature";
    private static final String HOOK_DESC = "(Lorg/objectweb/asm/tree/ClassNode;Ljava/lang/String;)V";

    private static final Logger LOGGER = LogManager.getLogger("ModAPITransformerSignaturePatch");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if (!TARGET.equals(name)) return basicClass;
        final byte[] transformedBytes = transformBytes(basicClass);
        HodgepodgeClassDump.dumpClass(transformedName, basicClass, transformedBytes, this);
        return transformedBytes;
    }

    private static byte[] transformBytes(byte[] basicClass) {
        final ClassReader cr = new ClassReader(basicClass);
        final ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        boolean patched = false;
        for (MethodNode m : cn.methods) {
            if (STRIP_INTERFACE_NAME.equals(m.name) && STRIP_INTERFACE_DESC.equals(m.desc)) {
                patched = injectSignatureHook(m);
                break;
            }
        }

        if (!patched) {
            LOGGER.warn(
                    "Could not locate {}#{}{} call site - Signature attribute rewrite not installed",
                    TARGET,
                    STRIP_INTERFACE_NAME,
                    STRIP_INTERFACE_DESC);
            return basicClass;
        }

        HodgepodgeCore.logASM(LOGGER, "TRANSFORMING " + TARGET);
        final ClassWriter cw = new SafeClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }

    // Patches ModAPITransformer.stripInterface(ClassNode, String, boolean):
    //
    // String ifaceName = interfaceName.replace('.', '/');
    // boolean found = classNode.interfaces.remove(ifaceName);
    //
    // if (found) ModAPITransformerHook.stripInterfaceFromSignature(classNode, interfaceName); // <-- injected
    //
    // if (found && logDebugInfo) ...
    //
    private static boolean injectSignatureHook(MethodNode m) {
        for (AbstractInsnNode insn : m.instructions.toArray()) {
            if (insn.getOpcode() == INVOKEINTERFACE) {
                MethodInsnNode mn = (MethodInsnNode) insn;
                if (LIST_OWNER.equals(mn.owner) && LIST_REMOVE_NAME.equals(mn.name)
                        && LIST_REMOVE_DESC.equals(mn.desc)) {
                    if (insn.getNext().getOpcode() == ISTORE && insn.getPrevious().getOpcode() == ALOAD) {
                        final int boolIndex = ((VarInsnNode) insn.getNext()).var;
                        final LabelNode skip = new LabelNode();
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ILOAD, boolIndex));
                        list.add(new JumpInsnNode(IFEQ, skip));
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK_OWNER, HOOK_NAME, HOOK_DESC, false));
                        list.add(skip);
                        m.instructions.insert(insn.getNext(), list);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
