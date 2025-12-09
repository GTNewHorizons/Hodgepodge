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
    private static String setTag_name;
    private static String setTag_desc;

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
        setTag_name = HodgepodgeCore.isObf() ? "a" : "setTag";
        setTag_desc = HodgepodgeCore.isObf() ? "(Ljava/lang/String;Ldy;)V"
                : "(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V";
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
     * public NBTTagCompound(int capacity) {
     *     this.tagMap = new Object2ObjectOpenHashMap<>(capacity);
     * }
     * }
     * </pre>
     */
    private static void addConstructor(ClassNode cn) {
        MethodVisitor mv = cn.visitMethod(ACC_PUBLIC, "<init>", "(I)V", null, null);
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
        mv.visitVarInsn(ILOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, FASTUTIL_HASHMAP, "<init>", "(I)V", false);
        mv.visitFieldInsn(PUTFIELD, nbttagcompound_name, tagMap_name, "Ljava/util/Map;");
        Label label2 = new Label();
        mv.visitLabel(label2);
        mv.visitInsn(RETURN);
        Label label3 = new Label();
        mv.visitLabel(label3);
        mv.visitLocalVariable("this", "L" + nbttagcompound_name + ";", null, label0, label3, 0);
        mv.visitLocalVariable("capacity", "I", null, label0, label3, 1);
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
     *     NBTTagCompound nbt = new NBTTagCompound(this.tagMap.size());
     *     Object2ObjectMap.FastEntrySet<String, NBTBase> entries = ((Object2ObjectOpenHashMap)this.tagMap).object2ObjectEntrySet();
     *     ObjectIterator<Object2ObjectMap.Entry<String, NBTBase>> fastIterator = entries.fastIterator();
     *     while(fastIterator.hasNext()) {
     *         Object2ObjectMap.Entry<String, NBTBase> entry = (Object2ObjectMap.Entry)fastIterator.next();
     *         nbt.setTag((String)entry.getKey(), ((NBTBase)entry.getValue()).copy());
     *     }
     *     return nbt;
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
        mn.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "size", "()I", true);
        mn.visitMethodInsn(INVOKESPECIAL, nbttagcompound_name, "<init>", "(I)V", false);
        mn.visitVarInsn(ASTORE, 1);
        Label label1 = new Label();
        mn.visitLabel(label1);
        mn.visitVarInsn(ALOAD, 0);
        mn.visitFieldInsn(GETFIELD, nbttagcompound_name, tagMap_name, "Ljava/util/Map;");
        mn.visitTypeInsn(CHECKCAST, FASTUTIL_HASHMAP);
        Label label2 = new Label();
        mn.visitLabel(label2);
        mn.visitMethodInsn(
                INVOKEVIRTUAL,
                FASTUTIL_HASHMAP,
                "object2ObjectEntrySet",
                "()Lit/unimi/dsi/fastutil/objects/Object2ObjectMap$FastEntrySet;",
                false);
        mn.visitVarInsn(ASTORE, 2);
        Label label3 = new Label();
        mn.visitLabel(label3);
        mn.visitVarInsn(ALOAD, 2);
        mn.visitMethodInsn(
                INVOKEINTERFACE,
                "it/unimi/dsi/fastutil/objects/Object2ObjectMap$FastEntrySet",
                "fastIterator",
                "()Lit/unimi/dsi/fastutil/objects/ObjectIterator;",
                true);
        mn.visitVarInsn(ASTORE, 3);
        Label label4 = new Label();
        mn.visitLabel(label4);
        mn.visitFrame(
                F_APPEND,
                3,
                new Object[] { nbttagcompound_name, "it/unimi/dsi/fastutil/objects/Object2ObjectMap$FastEntrySet",
                        "it/unimi/dsi/fastutil/objects/ObjectIterator" },
                0,
                null);
        mn.visitVarInsn(ALOAD, 3);
        mn.visitMethodInsn(INVOKEINTERFACE, "it/unimi/dsi/fastutil/objects/ObjectIterator", "hasNext", "()Z", true);
        Label label5 = new Label();
        mn.visitJumpInsn(IFEQ, label5);
        Label label6 = new Label();
        mn.visitLabel(label6);
        mn.visitVarInsn(ALOAD, 3);
        mn.visitMethodInsn(
                INVOKEINTERFACE,
                "it/unimi/dsi/fastutil/objects/ObjectIterator",
                "next",
                "()Ljava/lang/Object;",
                true);
        mn.visitTypeInsn(CHECKCAST, "it/unimi/dsi/fastutil/objects/Object2ObjectMap$Entry");
        mn.visitVarInsn(ASTORE, 4);
        Label label7 = new Label();
        mn.visitLabel(label7);
        mn.visitVarInsn(ALOAD, 1);
        mn.visitVarInsn(ALOAD, 4);
        mn.visitMethodInsn(
                INVOKEINTERFACE,
                "it/unimi/dsi/fastutil/objects/Object2ObjectMap$Entry",
                "getKey",
                "()Ljava/lang/Object;",
                true);
        mn.visitTypeInsn(CHECKCAST, "java/lang/String");
        mn.visitVarInsn(ALOAD, 4);
        mn.visitMethodInsn(
                INVOKEINTERFACE,
                "it/unimi/dsi/fastutil/objects/Object2ObjectMap$Entry",
                "getValue",
                "()Ljava/lang/Object;",
                true);
        mn.visitTypeInsn(CHECKCAST, nbtbase_name);
        mn.visitMethodInsn(INVOKEVIRTUAL, nbtbase_name, copy_name, copy_desc, false);
        mn.visitMethodInsn(INVOKEVIRTUAL, nbttagcompound_name, setTag_name, setTag_desc, false);
        Label label8 = new Label();
        mn.visitLabel(label8);
        mn.visitJumpInsn(GOTO, label4);
        mn.visitLabel(label5);
        mn.visitFrame(F_SAME, 0, null, 0, null);
        mn.visitVarInsn(ALOAD, 1);
        mn.visitInsn(ARETURN);
        Label label9 = new Label();
        mn.visitLabel(label9);
        mn.visitLocalVariable(
                "entry",
                "Lit/unimi/dsi/fastutil/objects/Object2ObjectMap$Entry;",
                "Lit/unimi/dsi/fastutil/objects/Object2ObjectMap$Entry<Ljava/lang/String;L" + nbtbase_name + ";>;",
                label7,
                label8,
                4);
        mn.visitLocalVariable("this", "L" + nbttagcompound_name + ";", null, label0, label9, 0);
        mn.visitLocalVariable("nbt", "L" + nbttagcompound_name + ";", null, label1, label9, 1);
        mn.visitLocalVariable(
                "entries",
                "Lit/unimi/dsi/fastutil/objects/Object2ObjectMap$FastEntrySet;",
                "Lit/unimi/dsi/fastutil/objects/Object2ObjectMap$FastEntrySet<Ljava/lang/String;L" + nbtbase_name
                        + ";>;",
                label3,
                label9,
                2);
        mn.visitLocalVariable(
                "fastIterator",
                "Lit/unimi/dsi/fastutil/objects/ObjectIterator;",
                "Lit/unimi/dsi/fastutil/objects/ObjectIterator<Lit/unimi/dsi/fastutil/objects/Object2ObjectMap$Entry<Ljava/lang/String;L"
                        + nbtbase_name
                        + ";>;>;",
                label4,
                label9,
                3);
        mn.visitMaxs(3, 5);
        mn.visitEnd();
    }
}
