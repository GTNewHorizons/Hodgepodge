package com.mitchej123.hodgepodge.core.rfb.transformers;

import java.util.ListIterator;
import java.util.jar.Manifest;

import net.minecraftforge.common.config.Property;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;
import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

/**
 * Reduces the memory usage of Forge's Configuration system significantly. Deduplicates identical strings, empty arrays
 * and switches property maps from TreeMaps to fastutil open hashmaps. Measured in the full GTNH pack to offer a 45
 * percent memory usage reduction for Configuration classes.
 */
public class ForgeConfigurationTransformer implements RfbClassTransformer, Opcodes {

    private static final String HOOK_CLASS_INTERNAL = "com/mitchej123/hodgepodge/core/rfb/hooks/ForgeConfigurationHook";
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
                || className.equals("net.minecraftforge.common.config.ConfigCategory")
                || className.equals("net.minecraftforge.common.config.Property$Type"));
    }

    @Override
    public void transformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull RfbClassTransformer.Context context,
            @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        final ClassNode cn = classNode.getNode();
        if (cn == null) {
            return;
        }
        switch (className) {
            case "net.minecraftforge.common.config.Property" -> {
                fixStringConcatenationInLoop(cn);
                transformProperty(cn);
                classNode.computeMaxs();
            }
            case "net.minecraftforge.common.config.Property$Type" -> transformPropertyType(cn);
            case "net.minecraftforge.common.config.ConfigCategory" -> transformConfigCategory(cn);
        }
        HodgepodgeClassDump.dumpRFBClass(className, classNode, this);
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
                } else if (aInsn.getOpcode() == PUTFIELD && aInsn instanceof FieldInsnNode fInsn && PROPERTY_INTERNAL.equals(fInsn.owner)) {
                    if ("Ljava/lang/String;".equals(fInsn.desc) && ("value".equals(fInsn.name) || "defaultValue".equals(fInsn.name))) {
                        if (!(aInsn.getPrevious() instanceof LdcInsnNode)) {
                            mn.instructions.insertBefore(fInsn,
                                    new MethodInsnNode(
                                            INVOKEVIRTUAL,
                                            "java/lang/String",
                                            "intern",
                                            "()Ljava/lang/String;",
                                            false));
                        }
                        i++;
                    } else if ("[Ljava/lang/String;".equals(fInsn.desc) && ("values".equals(fInsn.name) || "defaultValues".equals(fInsn.name) || "validValues".equals(fInsn.name))) {
                        mn.instructions.insertBefore(fInsn,
                                new MethodInsnNode(
                                        INVOKESTATIC,
                                        HOOK_CLASS_INTERNAL,
                                        "internArray",
                                        "([Ljava/lang/String;)[Ljava/lang/String;",
                                        false));
                        i++;
                    }
                } else if (aInsn.getOpcode() == ICONST_0) {
                    final AbstractInsnNode nextNode = aInsn.getNext();
                    if (nextNode.getOpcode() == ANEWARRAY && nextNode instanceof TypeInsnNode tInsn && "java/lang/String".equals(tInsn.desc)) {
                        mn.instructions.insert(nextNode,
                                new MethodInsnNode(
                                        INVOKESTATIC,
                                        HOOK_CLASS_INTERNAL,
                                        "emptyStringArray",
                                        "()[Ljava/lang/String;",
                                        false));
                        mn.instructions.remove(aInsn);
                        mn.instructions.remove(nextNode);
                        i--;
                    }
                }
            }
        }
        // spotless:on
    }

    /**
     * In the methods
     * {@link net.minecraftforge.common.config.Property#Property(String, String[], Property.Type, boolean, String[], String)}
     * and {@link net.minecraftforge.common.config.Property#setDefaultValues(String[])} we want to replace string
     * concatenation in a loop with a single StringBuilder.
     *
     * <pre>
     * {@code
     *  // we want to replace this
     *  this.defaultValue = "";
     *  for (String s : values) {
     *      this.defaultValue += ", [" + s + "]";
     *  }
     *  this.defaultValue = this.defaultValue.replaceFirst(", ", "");
     *  // with
     *  this.defaultValue = ForgeConfigurationHook.getDefaultValues(defaultValues);
     * </pre>
     *
     */
    private static void fixStringConcatenationInLoop(ClassNode cn) {
        // we search at the start for : this.defaultValue = "";
        // and at the end for : this.defaultValue = this.defaultValue.replaceFirst(", ", "");
        // ALOAD 0 <- we keep (startNode)
        // LDC "" <- delete everything from here
        // PUTFIELD defaultValue
        // ...
        // ...
        // INVOKEVIRTUAL java/lang/String.replaceFirst <- delete up to here
        // PUTFIELD defaultValue <- we keep and inject our hook right before (endNode)

        for (MethodNode mn : cn.methods) {
            final boolean isConstructor = mn.name.equals("<init>") && mn.desc.equals(
                    "(Ljava/lang/String;[Ljava/lang/String;Lnet/minecraftforge/common/config/Property$Type;Z[Ljava/lang/String;Ljava/lang/String;)V");
            final boolean isSetDefault = mn.name.equals("setDefaultValues")
                    && mn.desc.equals("([Ljava/lang/String;)Lnet/minecraftforge/common/config/Property;");
            if (isConstructor || isSetDefault) {
                AbstractInsnNode startNode = null;
                AbstractInsnNode endNode = null;
                final ListIterator<AbstractInsnNode> it = mn.instructions.iterator();
                while (it.hasNext()) {
                    final AbstractInsnNode node = it.next();
                    if (node instanceof VarInsnNode varNode && node.getOpcode() == ALOAD && varNode.var == 0) {
                        if (node.getNext() instanceof LdcInsnNode ldcNode && "".equals(ldcNode.cst)) {
                            if (isPutDefaultValueFieldNode(ldcNode.getNext())) {
                                startNode = node;
                            }
                        }
                    } else if (startNode != null && isReplaceFirstMethodNode(node)) {
                        if (isPutDefaultValueFieldNode(node.getNext())) {
                            endNode = node.getNext();
                            break;
                        }
                    }
                }
                if (startNode != null && endNode != null) {
                    AbstractInsnNode node = startNode.getNext();
                    while (node != endNode) {
                        AbstractInsnNode next = node.getNext();
                        mn.instructions.remove(node);
                        node = next;
                    }
                    final InsnList list = new InsnList();
                    list.add(new VarInsnNode(ALOAD, isConstructor ? 2 : 1));
                    list.add(
                            new MethodInsnNode(
                                    INVOKESTATIC,
                                    HOOK_CLASS_INTERNAL,
                                    "getDefaultValues",
                                    "([Ljava/lang/String;)Ljava/lang/String;",
                                    false));
                    mn.instructions.insertBefore(endNode, list);
                }
            }
        }
    }

    private static boolean isPutDefaultValueFieldNode(AbstractInsnNode node) {
        return node instanceof FieldInsnNode fNode && fNode.getOpcode() == PUTFIELD
                && fNode.owner.equals(PROPERTY_INTERNAL)
                && fNode.name.equals("defaultValue")
                && fNode.desc.equals("Ljava/lang/String;");
    }

    private static boolean isReplaceFirstMethodNode(AbstractInsnNode node) {
        return node instanceof MethodInsnNode mNode && mNode.getOpcode() == INVOKEVIRTUAL
                && mNode.owner.equals("java/lang/String")
                && mNode.name.equals("replaceFirst")
                && mNode.desc.equals("(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
    }

    private static void transformConfigCategory(ClassNode cn) {
        for (final MethodNode mn : cn.methods) {
            if ("<init>".equals(mn.name)) {
                for (AbstractInsnNode aInsn : mn.instructions.toArray()) {
                    if (aInsn.getOpcode() == NEW && aInsn instanceof TypeInsnNode tInsn
                            && "java/util/TreeMap".equals(tInsn.desc)) {
                        AbstractInsnNode secondNode = aInsn.getNext();
                        if (secondNode.getOpcode() == DUP) {
                            AbstractInsnNode thirdNode = secondNode.getNext();
                            if (thirdNode instanceof MethodInsnNode mInsn && isTreeMapInit(mInsn)) {
                                tInsn.desc = OPEN_MAP_INTERNAL;
                                mInsn.owner = OPEN_MAP_INTERNAL;
                            }
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

    private static boolean isTreeMapInit(MethodInsnNode node) {
        return node.getOpcode() == INVOKESPECIAL && node.owner.equals("java/util/TreeMap")
                && node.name.equals("<init>")
                && node.desc.equals("()V");
    }

    /**
     * Replaces the values() call with direct array access in the method Property$Type.tryParse
     */
    private static void transformPropertyType(ClassNode cn) {

        final MethodNode valuesMethod = EnumASMHelper.findValuesMethod(cn);

        if (valuesMethod == null) {
            return;
        }

        final String arrayName = EnumASMHelper.findInternalArrayName(cn.name, valuesMethod);
        if (arrayName == null) {
            return;
        }

        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("tryParse") && mn.desc.equals("(C)Lnet/minecraftforge/common/config/Property$Type;")) {
                final ListIterator<AbstractInsnNode> it = mn.instructions.iterator();
                while (it.hasNext()) {
                    final AbstractInsnNode node = it.next();
                    if (node instanceof MethodInsnNode mNode && node.getOpcode() == INVOKESTATIC
                            && mNode.owner.equals("net/minecraftforge/common/config/Property$Type")
                            && mNode.name.equals("values")
                            && mNode.desc.equals("()[Lnet/minecraftforge/common/config/Property$Type;")) {
                        it.set(
                                new FieldInsnNode(
                                        GETSTATIC,
                                        "net/minecraftforge/common/config/Property$Type",
                                        arrayName,
                                        "[Lnet/minecraftforge/common/config/Property$Type;"));
                    }
                }
                return;
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
