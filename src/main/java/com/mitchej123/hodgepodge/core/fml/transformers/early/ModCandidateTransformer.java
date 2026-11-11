package com.mitchej123.hodgepodge.core.fml.transformers.early;

import java.util.ListIterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.gtnewhorizon.gtnhlib.asm.SafeClassWriter;
import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

/**
 * The targeted class is loaded too early by cofh/asm/LoadingPlugin$CoFHDummyContainer#call(), it's an IFMLCallHook
 * which gets called before our ASM transformers are registered in the usual way. This is why we register this
 * transformer manually when our IFMLLoadingPlugin is instantiated.
 */
public final class ModCandidateTransformer implements IClassTransformer, Opcodes {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if ("cpw.mods.fml.common.discovery.ModCandidate".equals(transformedName)) {
            final byte[] transformedBytes = transformBytes(basicClass);
            HodgepodgeClassDump.dumpClass(transformedName, basicClass, transformedBytes, this);
            return transformedBytes;
        }
        return basicClass;
    }

    private static byte[] transformBytes(byte[] basicClass) {
        final ClassReader cr = new ClassReader(basicClass);
        final ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        // add field
        cn.visitField(
                ACC_PRIVATE | ACC_FINAL,
                "hp$packageSet",
                "Ljava/util/Set;",
                "Ljava/util/Set<Ljava/lang/String;>;",
                null);

        for (MethodNode method : cn.methods) {
            if (method.name.equals("<init>") && method.desc
                    .equals("(Ljava/io/File;Ljava/io/File;Lcpw/mods/fml/common/discovery/ContainerType;ZZ)V")) {
                transformConstructor(method);
            } else if (method.name.equals("addClassEntry") && method.desc.equals("(Ljava/lang/String;)V")) {
                transformAddClassEntry(method);
            } else if (method.name.equals("getContainedPackages") && method.desc.equals("()Ljava/util/List;")) {
                transformGetContainedPackages(method);
            }
        }

        final ClassWriter cw = new SafeClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }

    /**
     * Injects at the end of the constructor : this.hp$packageSet = new HashSet<>();
     */
    private static void transformConstructor(MethodNode method) {
        final ListIterator<AbstractInsnNode> it = method.instructions.iterator();
        while (it.hasNext()) {
            final AbstractInsnNode node = it.next();
            if (node.getOpcode() == RETURN) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new TypeInsnNode(NEW, "java/util/HashSet"));
                list.add(new InsnNode(DUP));
                list.add(new MethodInsnNode(INVOKESPECIAL, "java/util/HashSet", "<init>", "()V", false));
                list.add(
                        new FieldInsnNode(
                                PUTFIELD,
                                "cpw/mods/fml/common/discovery/ModCandidate",
                                "hp$packageSet",
                                "Ljava/util/Set;"));
                method.instructions.insertBefore(node, list);
            }
        }
    }

    /**
     * Replaces the call : this.packages.add(pkg); with : this.hp$packageSet.add(pkg);
     */
    private static void transformAddClassEntry(MethodNode method) {
        final ListIterator<AbstractInsnNode> it = method.instructions.iterator();
        while (it.hasNext()) {
            final AbstractInsnNode node = it.next();
            if (node.getOpcode() == GETFIELD && node instanceof FieldInsnNode fn
                    && fn.owner.equals("cpw/mods/fml/common/discovery/ModCandidate")
                    && fn.name.equals("packages")
                    && fn.desc.equals("Ljava/util/List;")) {
                final AbstractInsnNode secondNode = node.getNext();
                if (secondNode instanceof VarInsnNode) {
                    final AbstractInsnNode thirdNode = secondNode.getNext();
                    if (thirdNode.getOpcode() == INVOKEINTERFACE && thirdNode instanceof MethodInsnNode mn
                            && mn.owner.equals("java/util/List")
                            && mn.name.equals("add")
                            && mn.desc.equals("(Ljava/lang/Object;)Z")) {
                        fn.name = "hp$packageSet";
                        fn.desc = "Ljava/util/Set;";
                        mn.owner = "java/util/Set";
                    }
                }
            }
        }
    }

    /**
     * Replaces the call : return packages; with : return new ArrayList<>(this.hp$packageSet);
     */
    private static void transformGetContainedPackages(MethodNode method) {
        final ListIterator<AbstractInsnNode> it = method.instructions.iterator();
        while (it.hasNext()) {
            final AbstractInsnNode node = it.next();
            if (node.getOpcode() == ALOAD && node instanceof VarInsnNode vn && vn.var == 0) {
                final AbstractInsnNode nextNode = node.getNext();
                if (nextNode.getOpcode() == GETFIELD && nextNode instanceof FieldInsnNode fn
                        && fn.owner.equals("cpw/mods/fml/common/discovery/ModCandidate")
                        && fn.name.equals("packages")
                        && fn.desc.equals("Ljava/util/List;")) {
                    fn.name = "hp$packageSet";
                    fn.desc = "Ljava/util/Set;";
                    method.instructions.insertBefore(node, new TypeInsnNode(NEW, "java/util/ArrayList"));
                    method.instructions.insertBefore(node, new InsnNode(DUP));
                    method.instructions.insert(
                            nextNode,
                            new MethodInsnNode(
                                    INVOKESPECIAL,
                                    "java/util/ArrayList",
                                    "<init>",
                                    "(Ljava/util/Collection;)V",
                                    false));
                }
            }
        }
    }
}
