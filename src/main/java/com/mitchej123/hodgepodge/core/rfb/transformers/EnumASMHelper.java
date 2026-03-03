package com.mitchej123.hodgepodge.core.rfb.transformers;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class EnumASMHelper implements Opcodes {

    public static MethodNode findValuesMethod(ClassNode cn) {
        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("values") && mn.desc.equals("()[L" + cn.name + ";")) {
                return mn;
            }
        }
        return null;
    }

    public static String findInternalArrayName(String classname, MethodNode valuesMethod) {
        final ListIterator<AbstractInsnNode> it = valuesMethod.instructions.iterator();
        while (it.hasNext()) {
            final AbstractInsnNode node = it.next();
            if (node.getOpcode() == INVOKEVIRTUAL && node instanceof MethodInsnNode mNode
            // && mNode.owner.equals("[L" + classname + ";") // in kotlin the owner is Object
                    && mNode.name.equals("clone")
                    && mNode.desc.equals("()Ljava/lang/Object;")) {
                final AbstractInsnNode prev = node.getPrevious();
                if (prev instanceof FieldInsnNode fNode && prev.getOpcode() == GETSTATIC
                        && fNode.owner.equals(classname)
                        && fNode.desc.equals("[L" + classname + ";")) {
                    return fNode.name;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

}
