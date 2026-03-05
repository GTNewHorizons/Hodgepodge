package com.mitchej123.hodgepodge.core.rfb.transformers;

import java.util.jar.Manifest;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;
import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

import cpw.mods.fml.relauncher.FMLRelaunchLog;

/**
 * Reduces Forge's EventSubscriptionTransformer transforming time in the full pack from 31% to 6%
 * <p>
 * It prevents the transformer from creating a ClassNode and recursively loading all classes in a superclass chain most
 * of the time
 */
public class ForgeEventSubscriptionTransformer implements RfbClassTransformer {

    @Pattern("[a-z0-9-]+")
    @Override
    public @NotNull String id() {
        return "forge-event-subscription-transformer";
    }

    @Override
    public boolean shouldTransformClass(@NotNull ExtensibleClassLoader classLoader,
            @NotNull RfbClassTransformer.Context context, @Nullable Manifest manifest, @NotNull String className,
            @NotNull ClassNodeHandle classNode) {
        return classNode.isPresent()
                && className.equals("cpw.mods.fml.common.asm.transformers.EventSubscriptionTransformer");
    }

    @Override
    public void transformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull RfbClassTransformer.Context context,
            @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNodeHandle) {
        final ClassNode classNode = classNodeHandle.getNode();
        if (classNode == null) {
            return;
        }
        boolean changed = false;
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("transform") && method.desc.equals("(Ljava/lang/String;Ljava/lang/String;[B)[B")) {
                changed |= transformTransformMethod(method);
            }
            if (method.name.equals("buildEvents") && method.desc.equals("(Lorg/objectweb/asm/tree/ClassNode;)Z")) {
                changed |= transformBuildEventsMethod(method);
            }
        }

        if (changed) {
            classNodeHandle.computeFrames();
            HodgepodgeClassDump.dumpRFBClass(className, classNodeHandle, this);
        } else {
            FMLRelaunchLog.severe("[ForgeEventSubscriptionTransformer] Failed to transform {}", classNode.name);
        }
    }

    private boolean transformTransformMethod(MethodNode method) {
        // spotless:off
        // public byte[] transform(String name, String transformedName, byte[] bytes) {
        //     if (...) return bytes;
        //     <we want to inject instructions here>
        //     ClassReader cr = new ClassReader(bytes); // <-- this is the anchor
        //     ...
        // }
        // spotless:on
        AbstractInsnNode anchor = null;

        for (AbstractInsnNode insn = method.instructions.getFirst(); insn != null; insn = insn.getNext()) {
            if (insn.getOpcode() == Opcodes.NEW && insn instanceof TypeInsnNode typeNode
                    && typeNode.desc.equals("org/objectweb/asm/ClassReader")) {
                anchor = insn;
                break;
            }
        }

        if (anchor == null) {
            return false;
        }

        // We want to insert this code:
        // spotless:off
        // if (!ForgeEventSubscriptionHook.shouldTransformClass(bytes)) {
        //     return bytes;
        // }
        // spotless:on
        InsnList instructions = new InsnList();
        LabelNode continueLabel = new LabelNode();

        // Load local variable 'bytes' (#3) onto the operand stack.
        // Local variables so far (counting from #0): this, name, transformedName, bytes
        instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
        // Call ForgeEventSubscriptionHook.shouldTransformClass(bytes) using the loaded 'bytes' variable
        instructions.add(
                new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "com/mitchej123/hodgepodge/core/rfb/hooks/ForgeEventSubscriptionHook",
                        "shouldTransformClass",
                        "([B)Z",
                        false));
        // Take returned boolean, if it's != 0 jump to continueLabel, otherwise fall through to the next instruction
        instructions.add(new JumpInsnNode(Opcodes.IFNE, continueLabel));
        // Load local variable 'bytes' (#3)
        instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
        // return bytes;
        instructions.add(new InsnNode(Opcodes.ARETURN));
        // the label right after the condition, we jump here if we should transform class
        instructions.add(continueLabel);

        method.instructions.insertBefore(anchor, instructions);
        return true;
    }

    private boolean transformBuildEventsMethod(MethodNode method) {
        // spotless:off
        // private boolean buildEvents(ClassNode classNode) throws Exception {
        //     Class<?> parent = this.getClass().getClassLoader().loadClass(classNode.superName.replace('/', '.'));
        //     if (!Event.class.isAssignableFrom(parent)) return false;
        //     // we want to delete all the code above
        //     Type tList = Type.getType("Lcpw/mods/fml/common/eventhandler/ListenerList;");
        //     ...
        // }
        // spotless:on
        AbstractInsnNode firstInstruction = null;
        AbstractInsnNode ldcInstruction = null;
        AbstractInsnNode node = method.instructions.getFirst();

        while (node != null) {
            // 1. Find any first real instruction
            if (firstInstruction == null && node.getOpcode() != -1) {
                firstInstruction = node;
            }

            // 2. Find the LDC instruction with the desired constant
            if (node instanceof LdcInsnNode ldcNode
                    && ldcNode.cst.equals("Lcpw/mods/fml/common/eventhandler/ListenerList;")) {
                ldcInstruction = ldcNode;
                break;
            }

            node = node.getNext();
        }

        if (firstInstruction == null || ldcInstruction == null) {
            return false;
        }

        // 3. Delete every instruction from firstInstruction to ldcInstruction
        node = firstInstruction;
        while (node != ldcInstruction) {
            AbstractInsnNode next = node.getNext();
            method.instructions.remove(node);
            node = next;
        }

        return true;
    }
}
