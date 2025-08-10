package com.mitchej123.hodgepodge.rfb.transformers;

import java.util.jar.Manifest;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;
import com.mitchej123.hodgepodge.asm.EarlyConfig;
import com.mitchej123.hodgepodge.asm.HodgepodgeClassDump;

/**
 * Reduces the memory usage of Forge's Configuration system significantly. Deduplicates identical strings, empty arrays
 * and switches property maps from TreeMaps to fastutil open hashmaps. Measured in the full GTNH pack to offer a 45
 * percent memory usage reduction for Configuration classes.
 */
public class ForgeConfigurationTransformer implements RfbClassTransformer, Opcodes {

    private static final String HOOK_CLASS_INTERNAL = "com/mitchej123/hodgepodge/rfb/hooks/ForgeConfigurationHook";
    private static final String CATEGORY_INTERNAL = "net/minecraftforge/common/config/ConfigCategory";
    private static final String PROPERTY_INTERNAL = "net/minecraftforge/common/config/Property";
    private static final String OPEN_MAP_INTERNAL = "it/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap";

    @Pattern("[a-z0-9-]+")
    @Override
    public @NotNull String id() {
        return "forge-configuration";
    }

    @Override
    public boolean shouldTransformClass(@NotNull ExtensibleClassLoader classLoader,
            @NotNull RfbClassTransformer.Context context, @Nullable Manifest manifest, @NotNull String className,
            @NotNull ClassNodeHandle classNode) {
        return classNode.isPresent() && (className.equals("net.minecraftforge.common.config.Property")
                || className.equals("net.minecraftforge.common.config.ConfigCategory"));
    }

    @Override
    public void transformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull RfbClassTransformer.Context context,
            @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        final ClassNode cn = classNode.getNode();
        if (cn == null) {
            return;
        }
        if (className.equals("net.minecraftforge.common.config.Property")) {
            transformProperty(cn);
        } else if (className.equals("net.minecraftforge.common.config.ConfigCategory")) {
            transformConfigCategory(cn);
        }
        if (EarlyConfig.dumpASMClass) {
            HodgepodgeClassDump.dumpRFBClass(className, classNode, this);
        }
    }

    private static void transformProperty(ClassNode cn) {
        // spotless:off
        for (final MethodNode mn : cn.methods) {
            for (int i = 0; i < mn.instructions.size(); i++) {
                final AbstractInsnNode aInsn = mn.instructions.get(i);
                if (aInsn.getOpcode() == INVOKESTATIC && aInsn instanceof MethodInsnNode mInsn) {
                    if (isString$valueOfI(mInsn) || isInteger$toString(mInsn)) {
                        mInsn.owner = HOOK_CLASS_INTERNAL;
                        mInsn.name = "intToCachedString";
                    } else if (isString$valueOfD(mInsn) || isDouble$toString(mInsn)) {
                        mInsn.owner = HOOK_CLASS_INTERNAL;
                        mInsn.name = "doubleToCachedString";
                    }
                } else if (aInsn.getOpcode() == PUTFIELD && aInsn instanceof FieldInsnNode fInsn) {
                    if (PROPERTY_INTERNAL.equals(fInsn.owner) && ("value".equals(fInsn.name) || "defaultValue".equals(fInsn.name))) {
                        mn.instructions.insertBefore(fInsn,
                                new MethodInsnNode(
                                        INVOKEVIRTUAL,
                                        "java/lang/String",
                                        "intern",
                                        "()Ljava/lang/String;",
                                        false));
                        i++;
                    } else if (PROPERTY_INTERNAL.equals(fInsn.owner) && ("values".equals(fInsn.name) || "defaultValues".equals(fInsn.name) || "validValues".equals(fInsn.name))) {
                        mn.instructions.insertBefore(fInsn,
                                new MethodInsnNode(
                                        INVOKESTATIC,
                                        HOOK_CLASS_INTERNAL,
                                        "internArray",
                                        "([Ljava/lang/String;)[Ljava/lang/String;",
                                        false));
                        i++;
                    }
                }
            }
        }
        // spotless:on
    }

    private static void transformConfigCategory(ClassNode cn) {
        for (final MethodNode mn : cn.methods) {
            if ("<init>".equals(mn.name)) {
                for (AbstractInsnNode aInsn : mn.instructions.toArray()) {
                    if (aInsn.getOpcode() == PUTFIELD && aInsn instanceof FieldInsnNode fInsn) {
                        if (CATEGORY_INTERNAL.equals(fInsn.owner) && "properties".equals(fInsn.name)) {
                            InsnList list = new InsnList();
                            list.add(new InsnNode(POP));
                            list.add(new TypeInsnNode(NEW, OPEN_MAP_INTERNAL));
                            list.add(new InsnNode(DUP));
                            list.add(new MethodInsnNode(INVOKESPECIAL, OPEN_MAP_INTERNAL, "<init>", "()V", false));
                            mn.instructions.insertBefore(fInsn, list);
                        }
                    }
                }
            } else if ("getOrderedValues".equals(mn.name)) {
                for (AbstractInsnNode aInsn : mn.instructions.toArray()) {
                    if (aInsn.getOpcode() == INVOKEINTERFACE && aInsn instanceof MethodInsnNode mInsn) {
                        if ("values".equals(mInsn.name) && "()Ljava/util/Collection;".equals(mInsn.desc)) {
                            mInsn.setOpcode(INVOKESTATIC);
                            mInsn.owner = HOOK_CLASS_INTERNAL;
                            mInsn.name = "keySortedMapValues";
                            mInsn.desc = "(Ljava/util/Map;)Ljava/util/Collection;";
                            mInsn.itf = false;
                        }
                    }
                }
            }
        }
    }

    private static boolean isDouble$toString(MethodInsnNode mInsn) {
        return "java/lang/Double".equals(mInsn.owner) && "toString".equals(mInsn.name)
                && "(D)Ljava/lang/String;".equals(mInsn.desc);
    }

    private static boolean isString$valueOfD(MethodInsnNode mInsn) {
        return "java/lang/String".equals(mInsn.owner) && "valueOf".equals(mInsn.name)
                && "(D)Ljava/lang/String;".equals(mInsn.desc);
    }

    private static boolean isInteger$toString(MethodInsnNode mInsn) {
        return "java/lang/Integer".equals(mInsn.owner) && "toString".equals(mInsn.name)
                && "(I)Ljava/lang/String;".equals(mInsn.desc);
    }

    private static boolean isString$valueOfI(MethodInsnNode mInsn) {
        return "java/lang/String".equals(mInsn.owner) && "valueOf".equals(mInsn.name)
                && "(I)Ljava/lang/String;".equals(mInsn.desc);
    }
}
