package com.mitchej123.hodgepodge.asm.util;

import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.util.List;

/**
 *
 *  Reference to a method. Contains information to locate the method regardless of environment.
 * @author octarine-noise
 */

// Shamelessly Taken from BetterFoliage by octarine-noise

public class MethodRef extends AbstractResolvable<Method> {

    public ClassRef parent;
    public String mcpName;
    public String srgName;
    public String obfName;
    public ClassRef returnType;
    public ClassRef[] argTypes;

    public MethodRef(
            ClassRef parent,
            String mcpName,
            String srgName,
            String obfName,
            ClassRef returnType,
            ClassRef... argTypes) {
        this.parent = parent;
        this.mcpName = mcpName;
        this.srgName = srgName;
        this.obfName = obfName;
        this.returnType = returnType;
        this.argTypes = argTypes;
    }

    public MethodRef(ClassRef parent, String mcpName, ClassRef returnType, ClassRef... argTypes) {
        this(parent, mcpName, mcpName, mcpName, returnType, argTypes);
    }

    public String getName(Namespace type) {
        if (type == Namespace.OBF) return obfName;
        if (type == Namespace.SRG) return srgName;
        return mcpName;
    }

    public String getAsmDescriptor(Namespace nameType) {
        StringBuilder sb = new StringBuilder("(");
        for (ClassRef arg : argTypes) sb.append(arg.getAsmDescriptor(nameType));
        sb.append(")");
        sb.append(returnType.getAsmDescriptor(nameType));
        return sb.toString();
    }

    public Method resolveInternal() {
        Class<?> parentClass = parent.resolve();
        if (parentClass == null) return null;

        List<Class<?>> argClasses = Lists.newLinkedList();
        for (ClassRef argType : argTypes) {
            if (argType.resolve() == null) return null;
            argClasses.add(argType.resolve());
        }
        Class<?>[] args = argClasses.toArray(new Class<?>[0]);

        Method methodObj;
        try {
            methodObj = parentClass.getDeclaredMethod(srgName, args);
            methodObj.setAccessible(true);
            return methodObj;
        } catch (Exception e) {
        }

        try {
            methodObj = parentClass.getDeclaredMethod(mcpName, args);
            methodObj.setAccessible(true);
            return methodObj;
        } catch (Exception e) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T invokeInstanceMethod(Object instance, Object... args) {
        if (resolve() == null) return null;

        try {
            return (T) resolvedObj.invoke(instance, args);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T invokeStaticMethod(Object... args) {
        return this.<T>invokeInstanceMethod(null, args);
    }
}
