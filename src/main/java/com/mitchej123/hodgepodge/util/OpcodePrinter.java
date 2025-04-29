package com.mitchej123.hodgepodge.util;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Converts ASM bytecodes into strings, for display. Not currently used, but preserved for debugging purposes.
 */
@SuppressWarnings("unused")
public class OpcodePrinter {

    // spotless:off
    private static final String[] MAP = {
        "NOP",
        "ACONST_NULL",
        "ICONST_M1",
        "ICONST_0", "ICONST_1", "ICONST_2", "ICONST_3", "ICONST_4", "ICONST_5",
        "LCONST_0", "LCONST_1",
        "FCONST_0", "FCONST_1", "FCONST_2",
        "DCONST_0", "DCONST_1",
        "BIPUSH",
        "SIPUSH",
        "LDC", "LDC_W", "LDC2_W",
        "ILOAD", "LLOAD", "FLOAD", "DLOAD", "ALOAD",
        "ILOAD_0", "ILOAD_1", "ILOAD_2", "ILOAD_3",
        "LLOAD_0", "LLOAD_1", "LLOAD_2", "LLOAD_3",
        "FLOAD_0", "FLOAD_1", "FLOAD_2", "FLOAD_3",
        "DLOAD_0", "DLOAD_1", "DLOAD_2", "DLOAD_3",
        "ALOAD_0", "ALOAD_1", "ALOAD_2", "ALOAD_3",
        "IALOAD", "LALOAD", "FALOAD", "DALOAD", "AALOAD", "BALOAD", "CALOAD", "SALOAD",
        "ISTORE", "LSTORE", "FSTORE", "DSTORE", "ASTORE",
        "ISTORE_0", "ISTORE_1", "ISTORE_2", "ISTORE_3",
        "LSTORE_0", "LSTORE_1", "LSTORE_2", "LSTORE_3",
        "FSTORE_0", "FSTORE_1", "FSTORE_2", "FSTORE_3",
        "DSTORE_0", "DSTORE_1", "DSTORE_2", "DSTORE_3",
        "ASTORE_0", "ASTORE_1", "ASTORE_2", "ASTORE_3",
        "IASTORE", "LASTORE", "FASTORE", "DASTORE", "AASTORE", "BASTORE", "CASTORE", "SASTORE",
        "POP", "POP2",
        "DUP", "DUP_X1", "DUP_X2",
        "DUP2", "DUP2_X1", "DUP2_X2",
        "SWAP", "IADD", "LADD", "FADD", "DADD",
        "ISUB", "LSUB", "FSUB", "DSUB",
        "IMUL", "LMUL", "FMUL", "DMUL",
        "IDIV", "LDIV", "FDIV", "DDIV",
        "IREM", "LREM", "FREM", "DREM",
        "INEG", "LNEG", "FNEG", "DNEG",
        "ISHL", "LSHL", "ISHR", "LSHR",
        "IUSHR", "LUSHR",
        "IAND", "LAND",
        "IOR", "LOR",
        "IXOR", "LXOR",
        "IINC",
        "I2L", "I2F", "I2D",
        "L2I", "L2F", "L2D",
        "F2I", "F2L", "F2D",
        "D2I", "D2L", "D2F",
        "I2B", "I2C", "I2S",
        "LCMP",
        "FCMPL", "FCMPG",
        "DCMPL", "DCMPG",
        "IFEQ", "IFNE", "IFLT", "IFGE", "IFGT", "IFLE",
        "IF_ICMPEQ", "IF_ICMPNE", "IF_ICMPLT", "IF_ICMPGE", "IF_ICMPGT", "IF_ICMPLE", "IF_ACMPEQ", "IF_ACMPNE",
        "GOTO",
        "JSR",
        "RET",
        "TABLESWITCH",
        "LOOKUPSWITCH",
        "IRETURN", "LRETURN", "FRETURN", "DRETURN", "ARETURN", "RETURN",
        "GETSTATIC",
        "PUTSTATIC",
        "GETFIELD",
        "PUTFIELD",
        "INVOKEVIRTUAL",
        "INVOKESPECIAL",
        "INVOKESTATIC",
        "INVOKEINTERFACE",
        "INVOKEDYNAMIC",
        "NEW",
        "NEWARRAY",
        "ANEWARRAY",
        "ARRAYLENGTH",
        "ATHROW",
        "CHECKCAST",
        "INSTANCEOF",
        "MONITORENTER",
        "MONITOREXIT",
        "WIDE",
        "MULTIANEWARRAY",
        "IFNULL",
        "IFNONNULL",
        "GOTO_W",
        "JSR_W",
    };
    //spotless:on

    public static String printOpcode(AbstractInsnNode node) {
        if (node instanceof LabelNode label) return label.toString();

        if (node instanceof LineNumberNode line) return "LINENUMBER " + line.line + " " + printOpcode(line.start);

        if (node instanceof FrameNode frame) {
            return "FRAME " + switch (frame.type) {
                case -1 -> "NEW";
                case 0 -> "FULL";
                case 1 -> "APPEND";
                case 2 -> "CHOP";
                case 3 -> "SAME";
                case 4 -> "SAME1";
                default -> "UNKNOWN";
            };
        }

        if (node.getOpcode() < 0 || node.getOpcode() >= MAP.length) return "UNKNOWN_" + node.getOpcode();
        var base = MAP[node.getOpcode()];

        if (node instanceof VarInsnNode var) {
            return base + " " + var.var;
        } else if (node instanceof MethodInsnNode method) {
            return base + " " + method.owner + "." + method.name + " " + method.desc;
        }
        return base;
    }
}
