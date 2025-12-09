package com.mitchej123.hodgepodge.core.fml.transformers.mc;

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
import com.mitchej123.hodgepodge.core.HodgepodgeCore;
import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

@SuppressWarnings("unused")
public class SpeedupLongIntHashMapTransformer implements IClassTransformer, Opcodes {

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

    private static boolean transformClassNode(String transformedName, ClassNode cn) {
        boolean changed = false;
        for (MethodNode mn : cn.methods) {
            for (AbstractInsnNode node : mn.instructions.toArray()) {
                if (node.getOpcode() == NEW && node instanceof TypeInsnNode tNode && isTargetDesc(tNode.desc)) {
                    AbstractInsnNode secondNode = node.getNext();
                    if (secondNode.getOpcode() == DUP) {
                        AbstractInsnNode thirdNode = secondNode.getNext();
                        if (thirdNode.getOpcode() == INVOKESPECIAL && thirdNode instanceof MethodInsnNode mNode
                                && mNode.name.equals(INIT)
                                && mNode.desc.equals(EMPTY_DESC)
                                && isTargetDesc(mNode.owner)) {
                            if (tNode.desc.equals(mNode.owner)) {
                                // spotless:off
                                HodgepodgeCore.logASM(LOGGER, "Replaced " + getDeobfName(tNode.desc) + " instantiation in " + transformedName + "." + mn.name);
                                final String replacement = getReplacement(tNode.desc);
                                tNode.desc = replacement;
                                mNode.owner = replacement;
                                changed = true;
                                // spotless:on
                            }
                        }
                    }
                }
            }
        }
        return changed;
    }

    private static boolean isTargetDesc(String desc) {
        if (HodgepodgeCore.isObf()) {
            return desc.equals(INT_HASH_MAP_OBF) || desc.equals(LONG_HASH_MAP_OBF);
        } else {
            return desc.equals(INT_HASH_MAP) || desc.equals(LONG_HASH_MAP);
        }
    }

    private static String getReplacement(String desc) {
        if (desc.equals(INT_HASH_MAP_OBF) || desc.equals(INT_HASH_MAP)) {
            return FAST_UTIL_INT_HASH_MAP;
        }
        if (desc.equals(LONG_HASH_MAP_OBF) || desc.equals(LONG_HASH_MAP)) {
            return FAST_UTIL_LONG_HASH_MAP;
        }
        throw new IllegalArgumentException();
    }

    private static String getDeobfName(String desc) {
        if (desc.equals(INT_HASH_MAP_OBF) || desc.equals(INT_HASH_MAP)) {
            return "IntHashMap";
        }
        if (desc.equals(LONG_HASH_MAP_OBF) || desc.equals(LONG_HASH_MAP)) {
            return "LongHashMap";
        }
        throw new IllegalArgumentException();
    }
}
