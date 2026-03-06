package com.mitchej123.hodgepodge.core.rfb.transformers;

import java.util.ListIterator;
import java.util.jar.Manifest;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;
import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

public class ConfigParsingTimeTransformer implements RfbClassTransformer, Opcodes {

    @Pattern("[a-z0-9-]+")
    @Override
    public @NotNull String id() {
        return "debugconfigparsingtime";
    }

    @Override
    public boolean shouldTransformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
            @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        return "net.minecraftforge.common.config.Configuration".equals(className) && classNode.isPresent();
    }

    @Override
    public void transformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
            @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        final ClassNode cn = classNode.getNode();
        if (cn == null) {
            return;
        }
        transformClassNode(cn);
        HodgepodgeClassDump.dumpRFBClass(className, classNode, this);
    }

    private static final String THIS = "net/minecraftforge/common/config/Configuration";

    private static void transformClassNode(ClassNode cn) {
        // 1. add field :
        // private long hp$time;
        cn.visitField(ACC_PRIVATE, "hp$time", "J", null, null);
        for (MethodNode mn : cn.methods) {
            if ("<init>".equals(mn.name) && "(Ljava/io/File;Ljava/lang/String;)V".equals(mn.desc)) {
                ListIterator<AbstractInsnNode> it = mn.instructions.iterator();
                boolean found = false;
                while (it.hasNext()) {
                    AbstractInsnNode insnNode = it.next();
                    if (!found && insnNode.getOpcode() == INVOKESPECIAL
                            && insnNode instanceof MethodInsnNode mNode
                            && mNode.owner.equals("java/lang/Object")
                            && mNode.name.equals("<init>")
                            && mNode.desc.equals("()V")) {
                        found = true;
                        // 2. in the constructor Configuration(File, String) inject at HEAD :
                        // this.hp$startTimer();
                        final InsnList listStart = new InsnList();
                        listStart.add(new VarInsnNode(ALOAD, 0));
                        listStart.add(new MethodInsnNode(INVOKESPECIAL, THIS, "hp$startTimer", "()V", false));
                        mn.instructions.insert(insnNode, listStart);
                    } else if (insnNode.getOpcode() == RETURN) {
                        // 3. in the constructor Configuration(File, String) inject at RETURN :
                        // this.hp$stopTimer(file);
                        final InsnList listEnd = new InsnList();
                        listEnd.add(new VarInsnNode(ALOAD, 0));
                        listEnd.add(new VarInsnNode(ALOAD, 1));
                        listEnd.add(
                                new MethodInsnNode(INVOKESPECIAL, THIS, "hp$stopTimer", "(Ljava/io/File;)V", false));
                        mn.instructions.insertBefore(insnNode, listEnd);
                    }
                }
            }
        }
        {
            // 4. add the method :
            // private void hp$startTimer() {
            // this.hp$time = System.nanoTime();
            // }
            MethodVisitor mv = cn.visitMethod(ACC_PRIVATE, "hp$startTimer", "()V", null, null);
            mv.visitCode();
            Label label0 = new Label();
            mv.visitLabel(label0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            mv.visitFieldInsn(PUTFIELD, THIS, "hp$time", "J");
            Label label1 = new Label();
            mv.visitLabel(label1);
            mv.visitInsn(RETURN);
            Label label2 = new Label();
            mv.visitLabel(label2);
            mv.visitLocalVariable("this", "L" + THIS + ";", null, label0, label2, 0);
            mv.visitMaxs(3, 1);
            mv.visitEnd();
        }
        {
            // 5. add the method :
            // private void hp$stopTimer(File file) {
            // ConfigParsingTimeHook.onEnd(file, System.nanoTime() - this.hp$time);
            // }
            MethodVisitor mv = cn.visitMethod(ACC_PRIVATE, "hp$stopTimer", "(Ljava/io/File;)V", null, null);
            mv.visitCode();
            Label label0 = new Label();
            mv.visitLabel(label0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, THIS, "hp$time", "J");
            mv.visitInsn(LSUB);
            mv.visitMethodInsn(
                    INVOKESTATIC,
                    "com/mitchej123/hodgepodge/core/rfb/hooks/ConfigParsingTimeHook",
                    "onEnd",
                    "(Ljava/io/File;J)V",
                    false);
            Label label1 = new Label();
            mv.visitLabel(label1);
            mv.visitInsn(RETURN);
            Label label2 = new Label();
            mv.visitLabel(label2);
            mv.visitLocalVariable("this", "L" + THIS + ";", null, label0, label2, 0);
            mv.visitLocalVariable("file", "Ljava/io/File;", null, label0, label2, 1);
            mv.visitMaxs(5, 2);
            mv.visitEnd();
        }
    }
}
