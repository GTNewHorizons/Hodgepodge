package com.mitchej123.hodgepodge.core.fml.transformers.mc;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import com.mitchej123.hodgepodge.core.HodgepodgeCore;
import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

@SuppressWarnings("unused")
public class NBTTagCompoundHashMapTransformer implements IClassTransformer, Opcodes {

    private static final Logger LOGGER = LogManager.getLogger("NBTTagCompoundHashMapTransformer");
    private static final String INIT = "<init>";
    private static final String EMPTY_DESC = "()V";
    private static final String HASHMAP = "java/util/HashMap";
    private static final String FASTUTIL_HASHMAP = "it/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if ("net.minecraft.nbt.NBTTagCompound".equals(transformedName)) {
            final byte[] transformedBytes = transformBytes(basicClass);
            HodgepodgeClassDump.dumpClass(transformedName, basicClass, transformedBytes, this);
            return transformedBytes;
        }
        return basicClass;
    }

    private static byte[] transformBytes(byte[] basicClass) {
        final ClassReader cr = new ClassReader(basicClass);
        final ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        final boolean changed = transformClassNode(cn);
        if (changed) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            return cw.toByteArray();
        }
        return basicClass;
    }

    private static boolean transformClassNode(ClassNode cn) {
        boolean changed = false;
        for (MethodNode mn : cn.methods) {
            if (mn.name.equals(INIT) && mn.desc.equals(EMPTY_DESC)) {
                changed = transformConstructor(mn);
            }
        }
        return changed;
    }

    /**
     * Replaces in the constructor : this.tagMap = new HashMap(); with : this.tagMap = new Object2ObjectOpenHashMap(4);
     */
    private static boolean transformConstructor(MethodNode mn) {
        boolean changed = false;
        for (AbstractInsnNode node : mn.instructions.toArray()) {
            if (node.getOpcode() == NEW && node instanceof TypeInsnNode tNode && tNode.desc.equals(HASHMAP)) {
                AbstractInsnNode secondNode = node.getNext();
                if (secondNode.getOpcode() == DUP) {
                    AbstractInsnNode thirdNode = secondNode.getNext();
                    if (thirdNode.getOpcode() == INVOKESPECIAL && thirdNode instanceof MethodInsnNode mNode
                            && mNode.name.equals(INIT)
                            && mNode.desc.equals(EMPTY_DESC)
                            && mNode.owner.equals(HASHMAP)) {
                        HodgepodgeCore.logASM(
                                LOGGER,
                                "Replaced HashMap instantiation in NBTTagCompound.<init> with " + FASTUTIL_HASHMAP);
                        tNode.desc = FASTUTIL_HASHMAP;
                        mn.instructions.insertBefore(mNode, new InsnNode(ICONST_4));
                        mNode.owner = FASTUTIL_HASHMAP;
                        mNode.desc = "(I)V";
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }
}
