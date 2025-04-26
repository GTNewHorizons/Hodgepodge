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

import com.gtnewhorizon.gtnhlib.asm.ClassConstantPoolParser;

@SuppressWarnings("unused")
public class SpeedupObjectIntIdentityMapTransformer implements IClassTransformer {

    public static final String OBJECT_INT_IDENTITY_MAP = "net/minecraft/util/ObjectIntIdentityMap";
    public static final String OBJECT_INT_IDENTITY_MAP_OBF = "ct";
    public static final String PROPER_OBJECT_INT_IDENTITY_MAP = "com/mitchej123/hodgepodge/util/ProperObjectIntIdentityMap";

    private static final ClassConstantPoolParser cstPoolParser = new ClassConstantPoolParser(
            OBJECT_INT_IDENTITY_MAP,
            OBJECT_INT_IDENTITY_MAP_OBF);

    private static final Logger LOGGER = LogManager.getLogger("SpeedupObjectIntIdentityMapTransformer");
    public static final String INIT = "<init>";
    public static final String EMPTY_DESC = "()V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if (!cstPoolParser.find(basicClass, true)) {
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
        if (transformedName.startsWith("com.mitchej123.hodgepodge.util")) {
            return false;
        }

        boolean changed = false;

        boolean objectIntIdentityMapInit = false;
        for (MethodNode mn : cn.methods) {
            for (AbstractInsnNode node : mn.instructions.toArray()) {
                if (node.getOpcode() == Opcodes.NEW && node instanceof TypeInsnNode tNode) {
                    if (!objectIntIdentityMapInit && (tNode.desc.equals(OBJECT_INT_IDENTITY_MAP)
                            || tNode.desc.equals(OBJECT_INT_IDENTITY_MAP_OBF))) {
                        objectIntIdentityMapInit = true;
                        LOGGER.info("Found ObjectIntIdentityMap instantiation in " + transformedName + "." + mn.name);
                        mn.instructions
                                .insertBefore(tNode, new TypeInsnNode(Opcodes.NEW, PROPER_OBJECT_INT_IDENTITY_MAP));
                        mn.instructions.remove(tNode);
                        changed = true;
                    }
                } else if (node.getOpcode() == Opcodes.INVOKESPECIAL && node instanceof MethodInsnNode mNode) {
                    if (mNode.name.equals(INIT) && mNode.desc.equals(EMPTY_DESC)) {
                        if (objectIntIdentityMapInit && (mNode.owner.equals(OBJECT_INT_IDENTITY_MAP)
                                || mNode.owner.equals(OBJECT_INT_IDENTITY_MAP_OBF))) {
                            objectIntIdentityMapInit = false;
                            LOGGER.info(
                                    "Found ObjectIntIdentityMap constructor call in " + transformedName
                                            + "."
                                            + mn.name);
                            mn.instructions.insertBefore(
                                    mNode,
                                    new MethodInsnNode(
                                            Opcodes.INVOKESPECIAL,
                                            PROPER_OBJECT_INT_IDENTITY_MAP,
                                            INIT,
                                            EMPTY_DESC,
                                            false));
                            mn.instructions.remove(mNode);
                        }
                    }
                }
            }
        }

        if (objectIntIdentityMapInit) {
            throw new IllegalStateException(
                    "Failed to transform " + transformedName + " due to missing constructor call");
        }

        return changed;
    }
}
