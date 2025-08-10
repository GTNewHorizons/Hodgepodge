package com.mitchej123.hodgepodge.rfb;

import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTFIELD;

import java.util.jar.Manifest;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;

/**
 * Reduces the memory usage of Forge's Configuration system significantly. Deduplicates identical strings, empty arrays
 * and switches property maps from TreeMaps to fastutil open hashmaps. Measured in the full GTNH pack to offer a 45
 * percent memory usage reduction for Configuration classes.
 */
public class ForgeConfigurationTransformer implements RfbClassTransformer {

    private static final String EARLY_HOOKS_INTERNAL = "com/mitchej123/hodgepodge/asm/hooks/early/EarlyASMCallHooks";
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
        if (cn.methods == null) {
            return;
        }
        for (final MethodNode mn : cn.methods) {
            if (mn.instructions == null || mn.instructions.size() == 0) {
                continue;
            }
            for (int i = 0; i < mn.instructions.size(); i++) {
                final AbstractInsnNode aInsn = mn.instructions.get(i);
                // Spotless keeps indenting each else-if to the right every time here for some reason
                // spotless:off
                if (aInsn.getOpcode() == INVOKESTATIC && aInsn instanceof MethodInsnNode mInsn) {
                    if (("java/lang/String".equals(mInsn.owner)
                            && "valueOf".equals(mInsn.name)
                            && "(I)Ljava/lang/String;".equals(mInsn.desc)) || ("java/lang/Integer".equals(mInsn.owner)
                            && "toString".equals(mInsn.name)
                            && "(I)Ljava/lang/String;".equals(mInsn.desc))) {
                        mInsn.owner = EARLY_HOOKS_INTERNAL;
                        mInsn.name = "intToCachedString";
                    } else if (("java/lang/String".equals(mInsn.owner)
                            && "valueOf".equals(mInsn.name)
                            && "(D)Ljava/lang/String;".equals(mInsn.desc)) || ("java/lang/Double".equals(mInsn.owner)
                            && "toString".equals(mInsn.name)
                            && "(D)Ljava/lang/String;".equals(mInsn.desc))) {
                        mInsn.owner = EARLY_HOOKS_INTERNAL;
                        mInsn.name = "doubleToCachedString";
                    }
                } else if (aInsn.getOpcode() == PUTFIELD && aInsn instanceof FieldInsnNode fInsn) {
                    if (PROPERTY_INTERNAL.equals(fInsn.owner) && ("value".equals(fInsn.name) || "defaultValue".equals(
                            fInsn.name))) {
                        final MethodInsnNode intern = new MethodInsnNode(INVOKEVIRTUAL,
                                "java/lang/String",
                                "intern",
                                "()Ljava/lang/String;",
                                false);
                        mn.instructions.insertBefore(fInsn, intern);
                        i++;
                    } else if (PROPERTY_INTERNAL.equals(fInsn.owner) && ("values".equals(fInsn.name)
                            || "defaultValues".equals(fInsn.name)
                            || "validValues".equals(fInsn.name))) {
                        final MethodInsnNode intern = new MethodInsnNode(INVOKESTATIC,
                                EARLY_HOOKS_INTERNAL,
                                "internArray",
                                "([Ljava/lang/String;)[Ljava/lang/String;",
                                false);
                        mn.instructions.insertBefore(fInsn, intern);
                        i++;
                    } else if (CATEGORY_INTERNAL.equals(fInsn.owner)
                            && "properties".equals(fInsn.name)
                            && "<init>".equals(mn.name)) {
                        mn.instructions.insertBefore(fInsn, new InsnNode(POP));
                        mn.instructions.insertBefore(fInsn, new TypeInsnNode(NEW, OPEN_MAP_INTERNAL));
                        mn.instructions.insertBefore(fInsn, new InsnNode(DUP));
                        mn.instructions.insertBefore(fInsn,
                                new MethodInsnNode(INVOKESPECIAL, OPEN_MAP_INTERNAL, "<init>", "()V", false));
                        i += 4;
                    }
                } else if ("getOrderedValues".equals(mn.name)
                        && aInsn.getOpcode() == INVOKEINTERFACE
                        && aInsn instanceof MethodInsnNode mInsn) {
                    if ("values".equals(mInsn.name) && "()Ljava/util/Collection;".equals(mInsn.desc)) {
                        mInsn.setOpcode(INVOKESTATIC);
                        mInsn.owner = EARLY_HOOKS_INTERNAL;
                        mInsn.name = "keySortedMapValues";
                        mInsn.desc = "(Ljava/util/Map;)Ljava/util/Collection;";
                        mInsn.itf = false;
                    }
                }
                // spotless:on
            }
        }
    }
}
