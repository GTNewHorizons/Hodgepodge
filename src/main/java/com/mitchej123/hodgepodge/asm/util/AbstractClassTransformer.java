package com.mitchej123.hodgepodge.asm.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import com.google.common.collect.Maps;
import com.mitchej123.hodgepodge.Hodgepodge;
import com.mitchej123.hodgepodge.asm.HodgePodgeASMLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.launchwrapper.IClassTransformer;

// Shamelessly Taken from BetterFoliage by octarine-noise

public abstract class AbstractClassTransformer implements IClassTransformer {

    protected final Logger log = LogManager.getLogger(getClass().getSimpleName());

    /** The kind of environment we are in. Assume MCP until proven otherwise */
    protected Namespace environment = Namespace.MCP;

    private final Map<String, Map<MethodRef, AbstractMethodTransformer>> methodTransformers = Maps.newHashMap();

    protected void addMethodTransformer(MethodRef ref, AbstractMethodTransformer methodTransformer) {
        methodTransformers.computeIfAbsent(ref.parent.getName(Namespace.MCP), n -> Maps.newHashMap()).put(ref, methodTransformer);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        // ???
        if (basicClass == null) return null;

        // test the environment - a name mismatch indicates the presence of obfuscated code
        if (!transformedName.equals(name)) {
            if (HodgePodgeASMLoader.getSortingIndex() >= 1001) environment = Namespace.SRG;
            else                                               environment = Namespace.OBF;                                            
        }

        Map<MethodRef, AbstractMethodTransformer> transformers = methodTransformers.get(transformedName);
        if (transformers == null) return basicClass; // nothing to transform

        // read class data
        ClassReader classReader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        Textifier text = new Textifier();
        ClassVisitor input = (ClassVisitor) new TraceClassVisitor(classNode, text, null);
        if (Hodgepodge.config.internalAsmCheckBytecode) {
            input = new CheckClassAdapter(input, true);
        }
        classReader.accept(input, 0);
        boolean hasTransformed = false;

        for (Map.Entry<MethodRef, AbstractMethodTransformer> entry : transformers.entrySet()) {
            if (transformedName.equals(entry.getKey().parent.getName(Namespace.MCP))) {
                //log.debug(String.format("Found class: %s -> %s", name, transformedName));
                //log.debug(String.format("Searching for method: %s %s -> %s %s",
                //        entry.getKey().getName(Namespace.OBF), entry.getKey().getAsmDescriptor(Namespace.OBF),
                //        entry.getKey().getName(Namespace.MCP), entry.getKey().getAsmDescriptor(Namespace.MCP)));
                for (MethodNode methodNode : classNode.methods) {
                    //log.debug(String.format("    %s, %s", methodNode.name, methodNode.desc));
                    // try to match against MCP, SRG, and OBF namespaces - mods sometimes have deobfed class names in signatures
                    if (entry.getKey().getName(Namespace.MCP).equals(methodNode.name) && entry.getKey().getAsmDescriptor(Namespace.MCP).equals(methodNode.desc) ||
                        entry.getKey().getName(Namespace.SRG).equals(methodNode.name) && entry.getKey().getAsmDescriptor(Namespace.SRG).equals(methodNode.desc) ||
                        entry.getKey().getName(Namespace.OBF).equals(methodNode.name) && entry.getKey().getAsmDescriptor(Namespace.OBF).equals(methodNode.desc)) {
                        AbstractMethodTransformer transformer = entry.getValue();
                        hasTransformed = true;

                        // transformers are not thread-safe because of laziness reasons
                        synchronized(transformer) {
                            transformer.currentClass = classNode;
                            transformer.currentMethod = methodNode;
                            transformer.environment = environment;
                            try {
                                transformer.transform();
                            } catch (Throwable t) {
                                // oops
                                log.fatal(String.format("While transforming %s (alias %s)", (transformedName == null) ? "<NULL>":transformedName, (name == null) ? "<NULL>":name), t);
                                try {
                                    Files.createDirectories(Paths.get(Hodgepodge.config.internalAsmDumpFoldername));
                                    Files.write(Paths.get(Hodgepodge.config.internalAsmDumpFoldername, name + "-before.class"), basicClass);
                                    Files.write(Paths.get(Hodgepodge.config.internalAsmDumpFoldername, name + "-before.asm"), text.getText().toString().getBytes("UTF-8"));
                                    log.warn("Input class saved to folder {}", Paths.get(Hodgepodge.config.internalAsmDumpFoldername).toAbsolutePath());
                                } catch (Throwable t2) {
                                    log.error("Additionally, while dumping input class and ASM to file", t2);
                                }
                                FMLCommonHandler.instance().exitJava(-1, true);
                            }
                        }
                    }
                }
            }
        }

        // return result
        if (hasTransformed) {
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
            ClassVisitor output = (ClassVisitor) classWriter;

            if (Hodgepodge.config.internalAsmCheckBytecode) {
                output = new CheckClassAdapter(output, true);
            }
            if (Hodgepodge.config.internalAsmAlwaysDumpClasses) {
                try {
                    Files.createDirectories(Paths.get(Hodgepodge.config.internalAsmDumpFoldername));
                    Files.write(Paths.get(Hodgepodge.config.internalAsmDumpFoldername, name + "-before.asm"), text.getText().toString().getBytes("UTF-8"));
                } catch (Exception e) {
                    log.warn("While dumping input class", e);
                }
                try {
                    output = new TraceClassVisitor(output, new Textifier(), new PrintWriter(Paths.get(Hodgepodge.config.internalAsmDumpFoldername, name + "-after.asm").toFile()));
                } catch (Exception e) {
                    log.warn("While preparing dump of output class", e);
                }
            }

            classNode.accept(classWriter);

            if (Hodgepodge.config.internalAsmAlwaysDumpClasses) {
                try {
                    Files.write(Paths.get(Hodgepodge.config.internalAsmDumpFoldername, name + "-after.class"), classWriter.toByteArray());
                } catch (Exception e) {
                    log.warn("While dumping output bytecode", e);
                }
            }

            return classWriter.toByteArray();
        }

        return basicClass;
    }
}

