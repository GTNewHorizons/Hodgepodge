package com.mitchej123.hodgepodge.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ASMUtil {

    /**
     * Gets a variable node from the local vars.
     */
    public static @Nullable LocalVariableNode getVariableNode(@Nullable List<LocalVariableNode> vars, int index) {
        if (vars == null) {
            return null;
        }

        for (var var : vars) {
            if (var.index == index) {
                return var;
            }
        }

        return null;
    }

    /**
     * Acts like rust's dbg!() macro. Prints as much information about an instruction as it can - mainly for debugging
     * and introspection purposes. Does not print anything useful for insns involving labels beyond their opcode.
     * 
     * @param insn The instruction.
     * @param vars The local variables in the method. Used for var insns.
     */
    public static void debug(AbstractInsnNode insn, @Nullable List<LocalVariableNode> vars) {
        if (insn instanceof MethodInsnNode method) {
            System.out.format(
                    "MethodInsnNode{opcode=%s, owner=%s, name=%s, desc=%s, itf=%b}\n",
                    getOpcodeName(method.getOpcode()),
                    method.owner,
                    method.name,
                    method.desc,
                    method.itf);
        } else if (insn instanceof FieldInsnNode field) {
            System.out.format(
                    "FieldInsnNode{opcode=%s, owner=%s, name=%s, desc=%s}\n",
                    getOpcodeName(field.getOpcode()),
                    field.owner,
                    field.name,
                    field.desc);
        } else if (insn instanceof TypeInsnNode type) {
            System.out.format("TypeInsnNode{opcode=%s, desc=%s}\n", getOpcodeName(type.getOpcode()), type.desc);
        } else if (insn instanceof LdcInsnNode ldc) {
            System.out.format("LdcInsnNode{opcode=%s, cst=%s}\n", getOpcodeName(ldc.getOpcode()), ldc.cst);
        } else if (insn instanceof VarInsnNode var) {
            if (vars == null) {
                System.out.format("VarInsnNode{opcode=%s, var index=%s}\n", getOpcodeName(var.getOpcode()), var.var);
            } else {
                LocalVariableNode vnode = getVariableNode(vars, var.var);
                Objects.requireNonNull(vnode);
                System.out.format(
                        "VarInsnNode{opcode=%s, var index=%s, name=%s, desc=%s, signature=%s}\n",
                        getOpcodeName(var.getOpcode()),
                        var.var,
                        vnode.name,
                        vnode.desc,
                        vnode.signature);
            }
        } else if (insn instanceof InsnNode insnNode) {
            System.out.format("InsnNode{opcode=%s}\n", getOpcodeName(insnNode.getOpcode()));
        } else if (insn instanceof IntInsnNode intInsn) {
            System.out.format(
                    "IntInsnNode{opcode=%s, operand=%s}\n",
                    getOpcodeName(intInsn.getOpcode()),
                    intInsn.operand);
        } else if (insn instanceof IincInsnNode iinc) {
            if (vars == null) {
                System.out.format(
                        "IincInsnNode{opcode=%s, var index=%d, incr=%d}\n",
                        getOpcodeName(iinc.getOpcode()),
                        iinc.var,
                        iinc.incr);
            } else {
                LocalVariableNode vnode = getVariableNode(vars, iinc.var);
                Objects.requireNonNull(vnode);
                System.out.format(
                        "IincInsnNode{opcode=%s, var index=%s, name=%s, desc=%s, signature=%s, incr=%d}\n",
                        getOpcodeName(iinc.getOpcode()),
                        iinc.var,
                        vnode.name,
                        vnode.desc,
                        vnode.signature,
                        iinc.incr);
            }
        } else if (insn instanceof InvokeDynamicInsnNode invDyn) {
            System.out.format(
                    "InvokeDynamicInsnNode{opcode=%s, name=%s, desc=%s, bsm=%s, bsmArgs=%s}\n",
                    getOpcodeName(invDyn.getOpcode()),
                    invDyn.name,
                    invDyn.desc,
                    invDyn.bsm,
                    Arrays.toString(invDyn.bsmArgs));
        } else if (insn instanceof LineNumberNode line) {
            System.out.format("LineNumberNode{line=%d, start=%s}\n", line.line, line.start);
        } else if (insn instanceof MultiANewArrayInsnNode multiNewArray) {
            System.out.format(
                    "MultiANewArrayInsnNode{opcode=%s, desc=%s, dims=%d}\n",
                    getOpcodeName(multiNewArray.getOpcode()),
                    multiNewArray.desc,
                    multiNewArray.dims);
        } else {
            System.out.format("%s{opcode=%s, ...}\n", getInsnName(insn.getType()), getOpcodeName(insn.getOpcode()));
        }
    }

    /**
     * Gets a human-readable name for an insn node's type int.
     * 
     * @param insnType The value of insn.getType().
     * @return The human-readable name.
     */
    public static String getInsnName(int insnType) {
        return switch (insnType) {
            case 0 -> "INSN";
            case 1 -> "INT_INSN";
            case 2 -> "VAR_INSN";
            case 3 -> "TYPE_INSN";
            case 4 -> "FIELD_INSN";
            case 5 -> "METHOD_INSN";
            case 6 -> "INVOKE_DYNAMIC_INSN";
            case 7 -> "JUMP_INSN";
            case 8 -> "LABEL";
            case 9 -> "LDC_INSN";
            case 10 -> "IINC_INSN";
            case 11 -> "TABLESWITCH_INSN";
            case 12 -> "LOOKUPSWITCH_INSN";
            case 13 -> "MULTIANEWARRAY_INSN";
            case 14 -> "FRAME";
            case 15 -> "LINE";
            default -> "AbstractInsnNode";
        };
    }

    /**
     * Gets the human-readable name for an opcode.
     * 
     * @param opcode The opcode.
     * @return The name.
     */
    public static String getOpcodeName(int opcode) {
        return switch (opcode) {
            case 0 -> "NOP";
            case 1 -> "ACONST_NULL";
            case 2 -> "ICONST_M1";
            case 3 -> "ICONST_0";
            case 4 -> "ICONST_1";
            case 5 -> "ICONST_2";
            case 6 -> "ICONST_3";
            case 7 -> "ICONST_4";
            case 8 -> "ICONST_5";
            case 9 -> "LCONST_0";
            case 10 -> "LCONST_1";
            case 11 -> "FCONST_0";
            case 12 -> "FCONST_1";
            case 13 -> "FCONST_2";
            case 14 -> "DCONST_0";
            case 15 -> "DCONST_1";
            case 16 -> "BIPUSH";
            case 17 -> "SIPUSH";
            case 18 -> "LDC";
            case 21 -> "ILOAD";
            case 22 -> "LLOAD";
            case 23 -> "FLOAD";
            case 24 -> "DLOAD";
            case 25 -> "ALOAD";
            case 46 -> "IALOAD";
            case 47 -> "LALOAD";
            case 48 -> "FALOAD";
            case 49 -> "DALOAD";
            case 50 -> "AALOAD";
            case 51 -> "BALOAD";
            case 52 -> "CALOAD";
            case 53 -> "SALOAD";
            case 54 -> "ISTORE";
            case 55 -> "LSTORE";
            case 56 -> "FSTORE";
            case 57 -> "DSTORE";
            case 58 -> "ASTORE";
            case 79 -> "IASTORE";
            case 80 -> "LASTORE";
            case 81 -> "FASTORE";
            case 82 -> "DASTORE";
            case 83 -> "AASTORE";
            case 84 -> "BASTORE";
            case 85 -> "CASTORE";
            case 86 -> "SASTORE";
            case 87 -> "POP";
            case 88 -> "POP2";
            case 89 -> "DUP";
            case 90 -> "DUP_X1";
            case 91 -> "DUP_X2";
            case 92 -> "DUP2";
            case 93 -> "DUP2_X1";
            case 94 -> "DUP2_X2";
            case 95 -> "SWAP";
            case 96 -> "IADD";
            case 97 -> "LADD";
            case 98 -> "FADD";
            case 99 -> "DADD";
            case 100 -> "ISUB";
            case 101 -> "LSUB";
            case 102 -> "FSUB";
            case 103 -> "DSUB";
            case 104 -> "IMUL";
            case 105 -> "LMUL";
            case 106 -> "FMUL";
            case 107 -> "DMUL";
            case 108 -> "IDIV";
            case 109 -> "LDIV";
            case 110 -> "FDIV";
            case 111 -> "DDIV";
            case 112 -> "IREM";
            case 113 -> "LREM";
            case 114 -> "FREM";
            case 115 -> "DREM";
            case 116 -> "INEG";
            case 117 -> "LNEG";
            case 118 -> "FNEG";
            case 119 -> "DNEG";
            case 120 -> "ISHL";
            case 121 -> "LSHL";
            case 122 -> "ISHR";
            case 123 -> "LSHR";
            case 124 -> "IUSHR";
            case 125 -> "LUSHR";
            case 126 -> "IAND";
            case 127 -> "LAND";
            case 128 -> "IOR";
            case 129 -> "LOR";
            case 130 -> "IXOR";
            case 131 -> "LXOR";
            case 132 -> "IINC";
            case 133 -> "I2L";
            case 134 -> "I2F";
            case 135 -> "I2D";
            case 136 -> "L2I";
            case 137 -> "L2F";
            case 138 -> "L2D";
            case 139 -> "F2I";
            case 140 -> "F2L";
            case 141 -> "F2D";
            case 142 -> "D2I";
            case 143 -> "D2L";
            case 144 -> "D2F";
            case 145 -> "I2B";
            case 146 -> "I2C";
            case 147 -> "I2S";
            case 148 -> "LCMP";
            case 149 -> "FCMPL";
            case 150 -> "FCMPG";
            case 151 -> "DCMPL";
            case 152 -> "DCMPG";
            case 153 -> "IFEQ";
            case 154 -> "IFNE";
            case 155 -> "IFLT";
            case 156 -> "IFGE";
            case 157 -> "IFGT";
            case 158 -> "IFLE";
            case 159 -> "IF_ICMPEQ";
            case 160 -> "IF_ICMPNE";
            case 161 -> "IF_ICMPLT";
            case 162 -> "IF_ICMPGE";
            case 163 -> "IF_ICMPGT";
            case 164 -> "IF_ICMPLE";
            case 165 -> "IF_ACMPEQ";
            case 166 -> "IF_ACMPNE";
            case 167 -> "GOTO";
            case 168 -> "JSR";
            case 169 -> "RET";
            case 170 -> "TABLESWITCH";
            case 171 -> "LOOKUPSWITCH";
            case 172 -> "IRETURN";
            case 173 -> "LRETURN";
            case 174 -> "FRETURN";
            case 175 -> "DRETURN";
            case 176 -> "ARETURN";
            case 177 -> "RETURN";
            case 178 -> "GETSTATIC";
            case 179 -> "PUTSTATIC";
            case 180 -> "GETFIELD";
            case 181 -> "PUTFIELD";
            case 182 -> "INVOKEVIRTUAL";
            case 183 -> "INVOKESPECIAL";
            case 184 -> "INVOKESTATIC";
            case 185 -> "INVOKEINTERFACE";
            case 186 -> "INVOKEDYNAMIC";
            case 187 -> "NEW";
            case 188 -> "NEWARRAY";
            case 189 -> "ANEWARRAY";
            case 190 -> "ARRAYLENGTH";
            case 191 -> "ATHROW";
            case 192 -> "CHECKCAST";
            case 193 -> "INSTANCEOF";
            case 194 -> "MONITORENTER";
            case 195 -> "MONITOREXIT";
            case 197 -> "MULTIANEWARRAY";
            case 198 -> "IFNULL";
            case 199 -> "IFNONNULL";
            default -> Integer.toString(opcode);
        };
    }
}
