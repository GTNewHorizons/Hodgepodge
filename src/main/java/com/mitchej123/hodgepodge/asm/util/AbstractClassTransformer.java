package com.mitchej123.hodgepodge.asm.util;

import com.google.common.collect.Maps;
import com.mitchej123.hodgepodge.asm.HodgePodgeASMLoader;
import java.util.Map;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

// Shamelessly Taken from BetterFoliage by octarine-noise

public abstract class AbstractClassTransformer implements IClassTransformer {

    protected final Logger log = LogManager.getLogger(getClass().getSimpleName());

    /** The kind of environment we are in. Assume MCP until proven otherwise */
    protected Namespace environment = Namespace.MCP;

    private final Map<String, Map<MethodRef, AbstractMethodTransformer>> methodTransformers = Maps.newHashMap();
    private final Map<String, Integer> classWriterFlags = Maps.newHashMap();

    protected void addMethodTransformer(
            MethodRef ref, int classWriterFlag, AbstractMethodTransformer methodTransformer) {
        methodTransformers
                .computeIfAbsent(ref.parent.getName(Namespace.MCP), n -> Maps.newHashMap())
                .put(ref, methodTransformer);
        classWriterFlags.merge(ref.parent.getName(Namespace.MCP), classWriterFlag, Integer::max);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        // ???
        if (basicClass == null) return null;

        // test the environment - a name mismatch indicates the presence of obfuscated code
        if (!transformedName.equals(name)) {
            if (HodgePodgeASMLoader.getSortingIndex() >= 1001) environment = Namespace.SRG;
            else environment = Namespace.OBF;
        }

        Map<MethodRef, AbstractMethodTransformer> transformers = methodTransformers.get(transformedName);
        if (transformers == null) return basicClass; // nothing to transform

        // read class data
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        boolean hasTransformed = false;

        for (Map.Entry<MethodRef, AbstractMethodTransformer> entry : transformers.entrySet()) {
            if (transformedName.equals(entry.getKey().parent.getName(Namespace.MCP))) {
                // log.debug(String.format("Found class: %s -> %s", name, transformedName));
                // log.debug(String.format("Searching for method: %s %s -> %s %s",
                //        entry.getKey().getName(Namespace.OBF), entry.getKey().getAsmDescriptor(Namespace.OBF),
                //        entry.getKey().getName(Namespace.MCP), entry.getKey().getAsmDescriptor(Namespace.MCP)));
                for (MethodNode methodNode : classNode.methods) {
                    // log.debug(String.format("    %s, %s", methodNode.name, methodNode.desc));
                    // try to match against MCP, SRG, and OBF namespaces - mods sometimes have deobfed class names in
                    // signatures
                    if (entry.getKey().getName(Namespace.MCP).equals(methodNode.name)
                                    && entry.getKey()
                                            .getAsmDescriptor(Namespace.MCP)
                                            .equals(methodNode.desc)
                            || entry.getKey().getName(Namespace.SRG).equals(methodNode.name)
                                    && entry.getKey()
                                            .getAsmDescriptor(Namespace.SRG)
                                            .equals(methodNode.desc)
                            || entry.getKey().getName(Namespace.OBF).equals(methodNode.name)
                                    && entry.getKey()
                                            .getAsmDescriptor(Namespace.OBF)
                                            .equals(methodNode.desc)) {
                        AbstractMethodTransformer transformer = entry.getValue();
                        hasTransformed = true;

                        // transformers are not thread-safe because of laziness reasons
                        synchronized (transformer) {
                            transformer.currentClass = classNode;
                            transformer.currentMethod = methodNode;
                            transformer.environment = environment;
                            try {
                                transformer.transform();
                            } catch (Exception e) {
                                // oops
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        // return result
        ClassWriter writer =
                new ClassWriter(classWriterFlags.getOrDefault(transformedName, ClassWriter.COMPUTE_FRAMES));
        if (hasTransformed) classNode.accept(writer);
        return !hasTransformed ? basicClass : writer.toByteArray();
    }
}
