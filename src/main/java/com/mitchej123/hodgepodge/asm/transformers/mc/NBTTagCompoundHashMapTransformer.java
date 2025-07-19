package com.mitchej123.hodgepodge.asm.transformers.mc;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import com.mitchej123.hodgepodge.Common;

@SuppressWarnings("unused")
public class NBTTagCompoundHashMapTransformer implements IClassTransformer {

    private static final Logger LOGGER = LogManager.getLogger("NBTTagCompoundHashMapTransformer");
    public static final String INIT = "<init>";
    public static final String EMPTY_DESC = "()V";
    public static final String HASHMAP = "java/util/HashMap";
    public static final String FASTUTIL_HASHMAP = "it/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap";
    public static final String NBT_TAG_COMPOUND = "net.minecraft.nbt.NBTTagCompound";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if (!transformedName.equals(NBT_TAG_COMPOUND)) {
            return basicClass;
        }

        final ClassReader cr = new ClassReader(basicClass);
        final ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        final boolean changed = transformClassNode(transformedName, cn);
        if (changed) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            return cw.toByteArray();
        }
        return basicClass;
    }

    /** @return Was the class changed? */
    public boolean transformClassNode(String transformedName, ClassNode cn) {
        if (cn == null) {
            return false;
        }

        boolean changed = false;
        for (MethodNode mn : cn.methods) {
            if (mn.name.equals(INIT) && mn.desc.equals(EMPTY_DESC)) {
                for (AbstractInsnNode node : mn.instructions.toArray()) {
                    if (node.getOpcode() == Opcodes.NEW && node instanceof TypeInsnNode tNode) {
                        if (tNode.desc.equals(HASHMAP)) {
                            Common.logASM(LOGGER, "Found HashMap instantiation in NBTTagCompound.<init>");
                            mn.instructions.insertBefore(tNode, new TypeInsnNode(Opcodes.NEW, FASTUTIL_HASHMAP));
                            mn.instructions.remove(tNode);
                            changed = true;
                        }
                    } else if (node.getOpcode() == Opcodes.INVOKESPECIAL && node instanceof MethodInsnNode mNode) {
                        if (mNode.name.equals(INIT) && mNode.desc.equals(EMPTY_DESC) && mNode.owner.equals(HASHMAP)) {
                            Common.logASM(LOGGER, "Found HashMap constructor call in NBTTagCompound.<init>");
                            mn.instructions.insertBefore(
                                    mNode,
                                    new MethodInsnNode(
                                            Opcodes.INVOKESPECIAL,
                                            FASTUTIL_HASHMAP,
                                            INIT,
                                            EMPTY_DESC,
                                            false));
                            mn.instructions.remove(mNode);
                            changed = true;
                        }
                    }
                }
            }
        }

        return changed;
    }
}
