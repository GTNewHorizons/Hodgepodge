package com.mitchej123.hodgepodge.core.fml.transformers.mc;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
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

    private static String copy_name;
    private static String copy_desc;
    private static String nbtbase_name;
    private static String nbttagcompound_name;
    private static String tagMap_name;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if ("net.minecraft.nbt.NBTTagCompound".equals(transformedName)) {
            initNames();
            final byte[] transformedBytes = transformBytes(basicClass);
            HodgepodgeClassDump.dumpClass(transformedName, basicClass, transformedBytes, this);
            return transformedBytes;
        }
        return basicClass;
    }

    private static void initNames() {
        copy_name = HodgepodgeCore.isObf() ? "b" : "copy";
        copy_desc = HodgepodgeCore.isObf() ? "()Ldy;" : "()Lnet/minecraft/nbt/NBTBase;";
        nbtbase_name = HodgepodgeCore.isObf() ? "dy" : "net/minecraft/nbt/NBTBase";
        nbttagcompound_name = HodgepodgeCore.isObf() ? "dh" : "net/minecraft/nbt/NBTTagCompound";
        tagMap_name = HodgepodgeCore.isObf() ? "c" : "tagMap";
    }

    private static byte[] transformBytes(byte[] basicClass) {
        final ClassReader cr = new ClassReader(basicClass);
        final ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        transformClassNode(cn);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private static void transformClassNode(ClassNode cn) {
        addConstructor(cn);
        for (MethodNode mn : cn.methods) {
            if (mn.name.equals(INIT) && mn.desc.equals(EMPTY_DESC)) {
                transformConstructor(mn);
            } else if (mn.name.equals(copy_name) && mn.desc.equals(copy_desc)) {
                overwriteCopy(mn);
            }
        }
    }

    /**
     * Adds the constructor :
     *
     * <pre>
     * {@code
     * private NBTTagCompound(Map map) {
     *     this.tagMap = new Object2ObjectOpenHashMap(map);
     * }
     * }
     * </pre>
     */
    private static void addConstructor(ClassNode cn) {
        MethodVisitor mv = cn.visitMethod(ACC_PRIVATE, "<init>", "(Ljava/util/Map;)V", null, null);
        mv.visitCode();
        Label label0 = new Label();
        mv.visitLabel(label0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, nbtbase_name, "<init>", "()V", false);
        Label label1 = new Label();
        mv.visitLabel(label1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(NEW, FASTUTIL_HASHMAP);
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, FASTUTIL_HASHMAP, "<init>", "(Ljava/util/Map;)V", false);
        mv.visitFieldInsn(PUTFIELD, nbttagcompound_name, tagMap_name, "Ljava/util/Map;");
        Label label2 = new Label();
        mv.visitLabel(label2);
        mv.visitInsn(RETURN);
        Label label3 = new Label();
        mv.visitLabel(label3);
        mv.visitLocalVariable("this", "L" + nbttagcompound_name + ";", null, label0, label3, 0);
        mv.visitLocalVariable("map", "Ljava/util/Map;", null, label0, label3, 1);
        mv.visitMaxs(4, 2);
        mv.visitEnd();
    }

    /**
     * Replaces in the constructor : this.tagMap = new HashMap(); with : this.tagMap = new Object2ObjectOpenHashMap(4);
     */
    private static void transformConstructor(MethodNode mn) {
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
                    }
                }
            }
        }
    }

    /**
     * Overwrites the copy method with :
     *
     * <pre>
     * {@code
     * public NBTBase copy() {
     *     return new NBTTagCompound(this.tagMap);
     * }
     * }
     * </pre>
     */
    private static void overwriteCopy(MethodNode mn) {
        mn.instructions.clear();
        mn.exceptions.clear();
        mn.tryCatchBlocks.clear();
        mn.localVariables.clear();
        mn.visitCode();
        Label label0 = new Label();
        mn.visitLabel(label0);
        mn.visitTypeInsn(NEW, nbttagcompound_name);
        mn.visitInsn(DUP);
        mn.visitVarInsn(ALOAD, 0);
        mn.visitFieldInsn(GETFIELD, nbttagcompound_name, tagMap_name, "Ljava/util/Map;");
        mn.visitMethodInsn(INVOKESPECIAL, nbttagcompound_name, "<init>", "(Ljava/util/Map;)V", false);
        mn.visitInsn(ARETURN);
        Label label1 = new Label();
        mn.visitLabel(label1);
        mn.visitLocalVariable("this", "L" + nbttagcompound_name + ";", null, label0, label1, 0);
        mn.visitMaxs(3, 1);
        mn.visitEnd();
    }
}
