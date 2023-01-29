package com.mitchej123.hodgepodge.asm.util;

import java.util.HashMap;

/**
 * Reference to a class. Contains information to locate the class regardless of environment.
 * 
 * @author octarine-noise
 */

// Shamelessly Taken from BetterFoliage by octarine-noise

public class ClassRef extends AbstractResolvable<Class<?>> {

    /** Reference to primitive <b>int</b> type */
    public static final ClassRef INT = primitive("I", int.class);

    /** Reference to primitive <b>float</b> type */
    public static final ClassRef FLOAT = primitive("F", float.class);

    /** Reference to primitive <b>boolean</b> type */
    public static final ClassRef BOOLEAN = primitive("Z", boolean.class);

    /** Reference to primitive <b>void</b> type */
    public static final ClassRef VOID = primitive("V", Void.class);

    public HashMap<String, MethodRef> methods;

    /** True for primitive types */
    public boolean isPrimitive = false;

    /** Class name in MCP namespace */
    public String mcpName;

    /** Class name in OBF namespace */
    public String obfName;

    public ClassRef(String mcpName, String obfName) {
        this.mcpName = mcpName;
        this.obfName = obfName;
    }

    public ClassRef(String mcpName) {
        this(mcpName, mcpName);
    }

    /**
     * Internal factory for primitive types
     * 
     * @param name
     * @param special
     * @return
     */
    protected static ClassRef primitive(String name, Class<?> classObj) {
        ClassRef result = new ClassRef(name);
        result.isPrimitive = true;
        result.resolvedObj = classObj;
        result.isResolved = true;
        return result;
    }

    public void addMethod(String mcpName, String srgName, String obfName, ClassRef returnType, ClassRef... argTypes) {
        if (methods == null) methods = new HashMap<String, MethodRef>() {

            private static final long serialVersionUID = 1L;
        };
        MethodRef method = new MethodRef(this, mcpName, srgName, obfName, returnType, argTypes);
        methods.put(this.mcpName + "_" + mcpName, method);
    }

    public void addMethod(String mcpName, ClassRef returnType, ClassRef... argTypes) {
        addMethod(mcpName, mcpName, mcpName, returnType, argTypes);
    }

    public MethodRef getMethod(String mcpName) {
        return methods.get(this.mcpName + "_" + mcpName);
    }

    /**
     * Get class name in the given namespace
     * 
     * @param ns
     * @return
     */
    public String getName(Namespace ns) {
        return (ns == Namespace.OBF) ? obfName : mcpName;
    }

    /**
     * Get ASM class descriptor in the given namespace
     * 
     * @param ns
     * @return
     */
    public String getAsmDescriptor(Namespace ns) {
        return isPrimitive ? mcpName : "L" + getName(ns).replace(".", "/") + ";";
    }

    protected Class<?> resolveInternal() {
        try {
            return Class.forName(mcpName);
        } catch (ClassNotFoundException e) {}
        try {
            return Class.forName(obfName);
        } catch (ClassNotFoundException e) {}
        return null;
    }
}
