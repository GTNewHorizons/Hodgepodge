package com.mitchej123.hodgepodge.asm.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;

// Shamelessly Taken from BetterFoliage by octarine-noise

public abstract class AbstractMethodTransformer {

    /**
     * Instruction node filter
     *
     * @author octarine-noise
     */
    public static interface IInstructionMatch {
        public boolean matches(AbstractInsnNode node);
    }

    protected ClassNode currentClass;
    protected MethodNode currentMethod;
    protected Namespace environment;

    public abstract void transform();

    protected void insertAfter(IInstructionMatch filter, AbstractInsnNode... added) {
        InsnList listAdd = new InsnList();
        for (AbstractInsnNode inst : added) listAdd.add(inst);
        AbstractInsnNode targetNode = findNext(currentMethod.instructions.getFirst(), filter);
        currentMethod.instructions.insert(targetNode, listAdd);
    }

    protected void insertBefore(IInstructionMatch filter, AbstractInsnNode... added) {
        InsnList listAdd = new InsnList();
        for (AbstractInsnNode inst : added) listAdd.add(inst);
        AbstractInsnNode targetNode = findNext(currentMethod.instructions.getFirst(), filter);
        currentMethod.instructions.insertBefore(targetNode, listAdd);
    }

    protected AbstractInsnNode findNext(AbstractInsnNode start, IInstructionMatch match) {
        AbstractInsnNode current = start;
        while (current != null) {
            if (match.matches(current)) break;
            current = current.getNext();
        }
        return current;
    }

    protected AbstractInsnNode findPrevious(AbstractInsnNode start, IInstructionMatch match) {
        AbstractInsnNode current = start;
        while (current != null) {
            if (match.matches(current)) break;
            current = current.getPrevious();
        }
        return current;
    }

    protected IInstructionMatch matchOpcode(final int opcode) {
        return new IInstructionMatch() {
            public boolean matches(AbstractInsnNode node) {
                return node.getOpcode() == opcode;
            }
        };
    }

    protected IInstructionMatch matchMethodInsn(final int opcode, final String owner, String name, String desc) {
        return new IInstructionMatch() {
            public boolean matches(AbstractInsnNode node) {
                if (node instanceof MethodInsnNode) {
                    MethodInsnNode n = (MethodInsnNode) node;
                    return n.getOpcode() == opcode && n.owner.equals(owner) && n.name.equals(name) && n.desc.equals(desc);
                }
                return false;
            }
        };
    }

    protected IInstructionMatch matchVarInsn(final int opcode, final int var) {
        return new IInstructionMatch() {
            public boolean matches(AbstractInsnNode node) {
                if (node instanceof VarInsnNode) {
                    return (node.getOpcode() == opcode) && ( ((VarInsnNode) node).var == var);
                }
                return false;
            }
        };
    }

    protected FieldInsnNode createGetField(FieldRef field) {
        return new FieldInsnNode(Opcodes.GETFIELD, field.parent.getName(environment).replace(".", "/"), field.getName(environment), field.getAsmDescriptor(environment));
    }

    protected MethodInsnNode createInvokeStatic(MethodRef method) {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, method.parent.getName(environment).replace(".", "/"), method.getName(environment), method.getAsmDescriptor(environment), false);
    }

    protected TypeInsnNode createCheckCast(ClassRef clazz) {
        return new TypeInsnNode(Opcodes.CHECKCAST, clazz.getName(environment).replace(".", "/"));
    }


    private static final Printer printer = new Textifier();
    private static final TraceMethodVisitor mp = new TraceMethodVisitor(printer);
    public static String insnToString(AbstractInsnNode insn){
        insn.accept(mp);
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();
        return sw.toString();
    }
}
