package com.mitchej123.hodgepodge.core.rfb.transformers;

import java.util.jar.Manifest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassHeaderMetadata;
import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;
import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

public class EnumValuesTransformer implements RfbClassTransformer, Opcodes {

    private static final boolean LOG_SPAM = false;
    private static final Logger logger = LogManager.getLogger("EnumValuesTransformer");

    @Override
    public @NotNull String id() {
        return "enum-values-transformer";
    }

    @Override
    public @NotNull String @Nullable [] additionalExclusions() {
        return new String[] { "com.gtnewhorizon.gtnhlib.asm" };
    }

    @Override
    public boolean shouldTransformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
            @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        if (!classNode.isPresent()) {
            return false;
        }
        ClassHeaderMetadata metadata = classNode.getOriginalMetadata();
        if (metadata == null) {
            return false;
        }
        return "java/lang/Enum".equals(metadata.binarySuperName);
    }

    @Override
    public void transformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
            @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        final ClassNode cn = classNode.getNode();
        assert cn != null;
        final boolean changed = transformClassNode(cn);
        if (changed) {
            classNode.computeMaxs();
            classNode.computeFrames();
            HodgepodgeClassDump.dumpBytecode(className, classNode, this);
        }
    }

    private static boolean transformClassNode(ClassNode cn) {
        final MethodNode valuesMethod = EnumASMHelper.findValuesMethod(cn);

        if (valuesMethod == null) {
            logger.error("Enum {} doesn't have values() method.", cn.name);
            return false;
        }

        final String arrayName = EnumASMHelper.findInternalArrayName(cn.name, valuesMethod);
        if (arrayName == null) {
            logger.error("Couldn't find internal VALUES array in Enum {}.", cn.name);
            return false;
        }

        if (!injectSizeInit(cn, arrayName)) {
            logger.error("Couldn't inject in static constructor of Enum {}.", cn.name);
            return false;
        }

        cn.fields.add(new FieldNode(ACC_PRIVATE | ACC_STATIC | ACC_FINAL, "hodgepodge$debug$size", "I", null, 0));
        cn.fields.add(new FieldNode(ACC_PRIVATE | ACC_STATIC, "hodgepodge$debug$counter", "I", null, 0));
        cn.fields.add(new FieldNode(ACC_PRIVATE | ACC_STATIC, "hodgepodge$debug$counter$total", "I", null, 0));
        injectInstrumentation(valuesMethod, cn.name);

        if (LOG_SPAM) {
            logger.info("Injected instrumentation in {}.", cn.name);
        }

        return true;
    }

    private static boolean injectSizeInit(ClassNode cn, String arrayName) {
        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("<clinit>") && mn.desc.equals("()V")) {
                AbstractInsnNode node = mn.instructions.getLast();
                AbstractInsnNode lastReturn = null;
                while (node != null) {
                    if (node.getOpcode() == RETURN) {
                        lastReturn = node;
                        break;
                    }
                    node = node.getPrevious();
                }
                if (lastReturn != null) {
                    final InsnList list = new InsnList();
                    list.add(new FieldInsnNode(GETSTATIC, cn.name, arrayName, "[L" + cn.name + ";"));
                    list.add(new InsnNode(ARRAYLENGTH));
                    list.add(new FieldInsnNode(PUTSTATIC, cn.name, "hodgepodge$debug$size", "I"));
                    mn.instructions.insertBefore(lastReturn, list);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Injects the following at head of the values() method
     *
     * <pre>
     * {@code
     * public static THIS<>[] values() {
     *     if (EnumValuesHook.shouldLog(counter)) {
     *         EnumValuesHook.logMethod(THIS.class, total);
     *         counter -= EnumValuesHook.THRESHOLD;
     *     }
     *     counter += size;
     *     total += size;
     * ...
     * ...
     * }
     * </pre>
     */
    private static void injectInstrumentation(MethodNode valuesMethod, String classname) {
        final String classdesc = "L" + classname + ";";
        final InsnList list = new InsnList();
        LabelNode label0 = new LabelNode();
        list.add(label0);
        list.add(new FieldInsnNode(GETSTATIC, classname, "hodgepodge$debug$counter", "I"));
        list.add(
                new MethodInsnNode(
                        INVOKESTATIC,
                        "com/mitchej123/hodgepodge/core/rfb/hooks/EnumValuesHook",
                        "shouldLog",
                        "(I)Z",
                        false));
        LabelNode label1 = new LabelNode();
        list.add(new JumpInsnNode(IFEQ, label1));
        LabelNode label2 = new LabelNode();
        list.add(label2);
        list.add(new LdcInsnNode(Type.getType(classdesc)));
        list.add(new FieldInsnNode(GETSTATIC, classname, "hodgepodge$debug$counter$total", "I"));
        list.add(
                new MethodInsnNode(
                        INVOKESTATIC,
                        "com/mitchej123/hodgepodge/core/rfb/hooks/EnumValuesHook",
                        "logMethod",
                        "(Ljava/lang/Class;I)V",
                        false));
        LabelNode label3 = new LabelNode();
        list.add(label3);
        list.add(new FieldInsnNode(GETSTATIC, classname, "hodgepodge$debug$counter", "I"));
        list.add(
                new FieldInsnNode(
                        GETSTATIC,
                        "com/mitchej123/hodgepodge/core/rfb/hooks/EnumValuesHook",
                        "THRESHOLD",
                        "I"));
        list.add(new InsnNode(ISUB));
        list.add(new FieldInsnNode(PUTSTATIC, classname, "hodgepodge$debug$counter", "I"));
        list.add(label1);
        // list.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
        list.add(new FieldInsnNode(GETSTATIC, classname, "hodgepodge$debug$counter", "I"));
        list.add(new FieldInsnNode(GETSTATIC, classname, "hodgepodge$debug$size", "I"));
        list.add(new InsnNode(IADD));
        list.add(new FieldInsnNode(PUTSTATIC, classname, "hodgepodge$debug$counter", "I"));
        LabelNode label4 = new LabelNode();
        list.add(label4);
        list.add(new FieldInsnNode(GETSTATIC, classname, "hodgepodge$debug$counter$total", "I"));
        list.add(new FieldInsnNode(GETSTATIC, classname, "hodgepodge$debug$size", "I"));
        list.add(new InsnNode(IADD));
        list.add(new FieldInsnNode(PUTSTATIC, classname, "hodgepodge$debug$counter$total", "I"));
        valuesMethod.instructions.insert(list);
    }
}
