package com.mitchej123.hodgepodge.core.fml.transformers.mc;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.mitchej123.hodgepodge.core.HodgepodgeCore;
import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * Searches for and obliterates var-arg
 */
@SuppressWarnings("unused")
public class VarargDissector implements IClassTransformer, Opcodes {

    // Technically I could just do a 3 <= x <= 8, but this is clearer and not much slower
    private static final IntArrayList CONST_INT_BYTECODES = new IntArrayList(
            new int[] { ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5 });

    private static final String REPLACE_CNAME = "com/mitchej123/hodgepodge/core/fml/hooks/mc/RandomAid";
    private static final String REPLACE_MNAME = "random";
    private static final String[] REPLACE_MDESC = { "(II)I", "(III)I", "(IIII)I", "(IIIII)I", "(IIIIII)I" };

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if (isTargetClass(transformedName)) {
            final byte[] transformedBytes = transformBytes(transformedName, basicClass);
            HodgepodgeClassDump.dumpClass(transformedName, basicClass, transformedBytes, this);
            return transformedBytes;
        }
        return basicClass;
    }

    private static boolean isTargetClass(String s) {
        return "net.minecraft.world.gen.layer.GenLayer".equals(s)
                || "net.minecraft.world.gen.layer.GenLayerZoom".equals(s)
                || "net.minecraft.world.gen.layer.GenLayerFuzzyZoom".equals(s);
    }

    private static byte[] transformBytes(String transformedName, byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        boolean changed = false;
        for (MethodNode method : classNode.methods) {
            changed |= transformMethod(transformedName, method);
        }
        if (changed) {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }
        return basicClass;
    }

    private static boolean transformMethod(String transformedName, MethodNode method) {
        final String className = transformedName.replace('.', '/');
        final String TARGET_MNAME = HodgepodgeCore.isObf() ? "a" : "selectRandom";
        final String TARGET_MDESC = "([I)I";
        boolean changed = false;

        // Loop until we find a call
        int lastCall = -1;
        for (int i = 0; i < method.instructions.size(); i++) {
            final var insn = method.instructions.get(i);
            if (!(insn instanceof MethodInsnNode mInsn) || insn.getOpcode() != Opcodes.INVOKEVIRTUAL) continue;
            if (!mInsn.name.equals(TARGET_MNAME) || !mInsn.desc.equals(TARGET_MDESC)) continue;

            // At this point, we have a `INVOKEVIRTUAL something/selectRandom(int...)int`, good enough for me.
            // Look back through the list to find the first array instantiation - assume that's the one being passed
            int ii;
            int X = -1;
            for (ii = i; ii > lastCall; ii--) {
                final var insn2 = method.instructions.get(ii);
                if (!(insn2 instanceof IntInsnNode intInsn) || intInsn.getOpcode() != Opcodes.NEWARRAY
                        || intInsn.operand != T_INT)
                    continue;

                // We have an array - assume it's the correct one. Now, get the size.
                X = CONST_INT_BYTECODES.indexOf(method.instructions.get(ii - 1).getOpcode());
                break;
            }

            // The array is of variable size, abandon attempt!
            if (X < 0) {
                lastCall = i;
                continue;
            }

            // The array is of constant size, check if the number of arguments matches up
            // We expect:
            // ALOAD 0 (at ii - 2)
            // ICONST_X (at ii - 1)
            // NEWARRAY T_INT (at ii)
            // [ DUP, ICONST_<>, ILOAD <>, IASTORE ] * X
            // INVOKEVIRTUAL (at i)
            // So if the call is what we expect, ii + 4*X + 1 == i
            if (ii + 4 * X + 1 != i) {
                lastCall = i;
                continue;
            }

            // Insn count validated, now record and validate the argument loading
            final var thisInsn = method.instructions.get(ii - 2);
            if (!(thisInsn instanceof VarInsnNode varInsn) || varInsn.getOpcode() != Opcodes.ALOAD
                    || varInsn.var != 0) {
                lastCall = i;
                continue;
            }

            boolean bail = false;
            for (int ni = 0; ni < X; ni++) {
                final int base = ii + 4 * ni;
                final var dupInsn = method.instructions.get(base + 1);
                if (dupInsn.getOpcode() != DUP) {
                    bail = true;
                    break;
                }

                final var idxInsn = method.instructions.get(base + 2);
                if (idxInsn.getOpcode() != ICONST_0 + ni) {
                    bail = true;
                    break;
                }

                final var loadInsn = method.instructions.get(base + 3);
                if (loadInsn.getOpcode() != ILOAD) {
                    bail = true;
                    break;
                }

                final var astoreInsn = method.instructions.get(base + 4);
                if (astoreInsn.getOpcode() != Opcodes.IASTORE) {
                    bail = true;
                    break;
                }
            }

            if (bail) {
                lastCall = i;
                continue;
            }

            // We now have a list of ILOADs to use for arguments. At this point, we know that the input is of the
            // correct form, so no more bailing + we can start modifying the method.
            // The replacement is to call RandomAid.random(GenLayer#nextInt(X), ILOADs...)

            // Remove the NEWARRAY
            method.instructions.remove(method.instructions.get(ii));

            // Insert the GenLayer#nextInt(X). (ii - 2), (ii - 1) is already the ALOAD 0; ICONST that we need
            method.instructions.insert(
                    method.instructions.get(ii - 1),
                    new MethodInsnNode(INVOKEVIRTUAL, className, "nextInt", "(I)I", false));

            // Remove the DUP, ICONST, and IASTORE for each instruction
            // At this stage, (ii) is the nextInt call. Each time this runs through, one extra insn is left behind
            // (the ILOAD)
            for (int ni = 1; ni <= X; ni++) {
                method.instructions.remove(method.instructions.get(ni + ii));
                method.instructions.remove(method.instructions.get(ni + ii));
                method.instructions.remove(method.instructions.get(ni + ii + 1));
            }

            // Now, the insns look like
            // ALOAD 0 (ii - 2)
            // ICONST_X (ii - 1)
            // INVOKEVIRTUAL nextInt (ii)
            // [ ILOAD ] * X
            // INVOKEVIRTUAL selectRandom <- needs to be replaced!

            // Replace the INVOKEVIRTUAL and reset state for the next search
            method.instructions.set(
                    method.instructions.get(ii + X + 1),
                    new MethodInsnNode(INVOKESTATIC, REPLACE_CNAME, REPLACE_MNAME, REPLACE_MDESC[X - 1], false));
            lastCall = ii + X + 1;
            i = lastCall;
            changed = true;
        }

        return changed;
    }
}
