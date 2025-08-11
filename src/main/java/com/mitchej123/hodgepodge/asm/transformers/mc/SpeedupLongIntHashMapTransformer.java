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
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.asm.shared.HodgepodgeClassDump;

@SuppressWarnings("unused")
public class SpeedupLongIntHashMapTransformer implements IClassTransformer {

    private static final String LONG_HASH_MAP = "net/minecraft/util/LongHashMap";
    private static final String LONG_HASH_MAP_OBF = "qd";
    private static final String INT_HASH_MAP = "net/minecraft/util/IntHashMap";
    private static final String INT_HASH_MAP_OBF = "pz";
    private static final String FAST_UTIL_LONG_HASH_MAP = "com/mitchej123/hodgepodge/util/FastUtilLongHashMap";
    private static final String FAST_UTIL_INT_HASH_MAP = "com/mitchej123/hodgepodge/util/FastUtilIntHashMap";

    private static final ClassConstantPoolParser cstPoolParser = new ClassConstantPoolParser(
            INT_HASH_MAP,
            INT_HASH_MAP_OBF,
            LONG_HASH_MAP,
            LONG_HASH_MAP_OBF);

    private static final Logger LOGGER = LogManager.getLogger("SpeedupLongIntHashMapTransformer");
    private static final String INIT = "<init>";
    private static final String EMPTY_DESC = "()V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if (cstPoolParser.find(basicClass, true) && !transformedName.startsWith("com.mitchej123.hodgepodge.util")) {
            final byte[] transformedBytes = transformBytes(transformedName, basicClass);
            HodgepodgeClassDump.dumpClass(transformedName, basicClass, transformedBytes, this);
            return transformedBytes;
        }
        return basicClass;
    }

    private static byte[] transformBytes(String transformedName, byte[] basicClass) {
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
    private static boolean transformClassNode(String transformedName, ClassNode cn) {
        boolean changed = false;
        boolean longHashMapInit = false, intHashMapInit = false;
        for (MethodNode mn : cn.methods) {
            for (AbstractInsnNode node : mn.instructions.toArray()) {
                if (node.getOpcode() == Opcodes.NEW && node instanceof TypeInsnNode tNode) {
                    if (!longHashMapInit
                            && (tNode.desc.equals(LONG_HASH_MAP) || tNode.desc.equals(LONG_HASH_MAP_OBF))) {
                        longHashMapInit = true;
                        Common.logASM(LOGGER, "Found LongHashMap instantiation in " + transformedName + "." + mn.name);
                        mn.instructions.insertBefore(tNode, new TypeInsnNode(Opcodes.NEW, FAST_UTIL_LONG_HASH_MAP));
                        mn.instructions.remove(tNode);
                        changed = true;
                    } else if (!intHashMapInit
                            && (tNode.desc.equals(INT_HASH_MAP) || tNode.desc.equals(INT_HASH_MAP_OBF))) {
                                intHashMapInit = true;
                                Common.logASM(
                                        LOGGER,
                                        "Found IntHashMap instantiation in " + transformedName + "." + mn.name);
                                mn.instructions
                                        .insertBefore(tNode, new TypeInsnNode(Opcodes.NEW, FAST_UTIL_INT_HASH_MAP));
                                mn.instructions.remove(tNode);
                                changed = true;
                            }
                } else if (node.getOpcode() == Opcodes.INVOKESPECIAL && node instanceof MethodInsnNode mNode) {
                    if (mNode.name.equals(INIT) && mNode.desc.equals(EMPTY_DESC)) {
                        if (longHashMapInit
                                && (mNode.owner.equals(LONG_HASH_MAP) || mNode.owner.equals(LONG_HASH_MAP_OBF))) {
                            longHashMapInit = false;
                            Common.logASM(
                                    LOGGER,
                                    "Found LongHashMap constructor call in " + transformedName + "." + mn.name);
                            mn.instructions.insertBefore(
                                    mNode,
                                    new MethodInsnNode(
                                            Opcodes.INVOKESPECIAL,
                                            FAST_UTIL_LONG_HASH_MAP,
                                            INIT,
                                            EMPTY_DESC,
                                            false));
                            mn.instructions.remove(mNode);
                        } else if (intHashMapInit
                                && (mNode.owner.equals(INT_HASH_MAP) || mNode.owner.equals(INT_HASH_MAP_OBF))) {
                                    intHashMapInit = false;
                                    Common.logASM(
                                            LOGGER,
                                            "Found IntHashMap constructor call in " + transformedName + "." + mn.name);
                                    mn.instructions.insertBefore(
                                            mNode,
                                            new MethodInsnNode(
                                                    Opcodes.INVOKESPECIAL,
                                                    FAST_UTIL_INT_HASH_MAP,
                                                    INIT,
                                                    EMPTY_DESC,
                                                    false));
                                    mn.instructions.remove(mNode);
                                }
                    }
                }
            }
        }

        if (longHashMapInit || intHashMapInit) {
            throw new IllegalStateException(
                    "Failed to transform " + transformedName + " due to missing constructor call");
        }

        return changed;
    }
}
