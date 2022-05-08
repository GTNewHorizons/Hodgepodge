package com.mitchej123.hodgepodge.asm.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Maps;
import com.mitchej123.hodgepodge.Hodgepodge;
import com.mitchej123.hodgepodge.asm.HodgePodgeASMLoader;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SimpleVerifier;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import cpw.mods.fml.common.FMLCommonHandler;

// Shamelessly Taken from BetterFoliage by octarine-noise

public abstract class AbstractClassTransformer implements IClassTransformer {

    protected final Logger log = LogManager.getLogger(getClass().getSimpleName());

    /** The kind of environment we are in. Assume MCP until proven otherwise */
    protected Namespace environment = Namespace.MCP;

    private final Map<String, Map<MethodRef, AbstractMethodTransformer>> methodTransformers = Maps.newHashMap();

    protected void addMethodTransformer(MethodRef ref, AbstractMethodTransformer methodTransformer) {
        methodTransformers.computeIfAbsent(ref.parent.getName(Namespace.MCP), n -> Maps.newHashMap()).put(ref, methodTransformer);
    }

    // Called once per class visit, for every distinct visit
    // Return true if any changes made to class' nodes.
    protected boolean preStage(ClassNode theClass) {
        return false;
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

        Throwable happened = null;
        ClassReader inputReader = null;
        ClassNode transformedNodes = null;
        ClassWriter resultWriter = null;
        byte[] resultClass = null;
        StringWriter inputDump = new StringWriter();
        String inputVerify = null;
        StringWriter resultDump = new StringWriter();
        String resultVerify = null;

        try {
            // read class data
            inputReader = new ClassReader(basicClass);
            transformedNodes = new ClassNode();
            inputReader.accept(transformedNodes, ClassReader.EXPAND_FRAMES);

            inputVerify = verify(transformedNodes, inputDump);

            boolean hasTransformed = false;

            hasTransformed |= preStage(transformedNodes);

            for (Map.Entry<MethodRef, AbstractMethodTransformer> entry : transformers.entrySet()) {
                if (transformedName.equals(entry.getKey().parent.getName(Namespace.MCP))) {
                    //log.debug(String.format("Found class: %s -> %s", name, transformedName));
                    //log.debug(String.format("Searching for method: %s %s -> %s %s",
                    //        entry.getKey().getName(Namespace.OBF), entry.getKey().getAsmDescriptor(Namespace.OBF),
                    //        entry.getKey().getName(Namespace.MCP), entry.getKey().getAsmDescriptor(Namespace.MCP)));
                    for (MethodNode methodNode : transformedNodes.methods) {
                        //log.debug(String.format("    %s, %s", methodNode.name, methodNode.desc));
                        // try to match against MCP, SRG, and OBF namespaces - mods sometimes have deobfed class names in signatures
                        if (entry.getKey().getName(Namespace.MCP).equals(methodNode.name) && entry.getKey().getAsmDescriptor(Namespace.MCP).equals(methodNode.desc) ||
                            entry.getKey().getName(Namespace.SRG).equals(methodNode.name) && entry.getKey().getAsmDescriptor(Namespace.SRG).equals(methodNode.desc) ||
                            entry.getKey().getName(Namespace.OBF).equals(methodNode.name) && entry.getKey().getAsmDescriptor(Namespace.OBF).equals(methodNode.desc)) {
                            AbstractMethodTransformer transformer = entry.getValue();
                            hasTransformed = true;

                            // transformers are not thread-safe because of laziness reasons
                            synchronized(transformer) {
                                transformer.currentClass = transformedNodes;
                                transformer.currentMethod = methodNode;
                                transformer.environment = environment;
                                transformer.transform();
                            }
                        }
                    }
                }
            }

            // return result
            if (hasTransformed) {
                resultVerify = verify(transformedNodes, resultDump);

                if ((inputVerify != null) && (inputVerify.length() > 0)) {
//                    log.fatal("Found errors in input bytecode\n{}", inputVerify);
                    throw new Exception("Found errors in input bytecode\n" + inputVerify);
                } else if ((resultVerify != null) && (resultVerify.length() > 0)) {
                    throw new Exception("Found errors in transformed bytecode\n" + resultVerify);
                }
        
                resultWriter = new ClassWriter(inputReader, ClassWriter.COMPUTE_FRAMES) {
                    @Override
                    protected String getCommonSuperClass(final String type1, final String type2)
                    {
                        //  the default asm merge uses Class.forName()
//                        String reply = super.getCommonSuperClass(type1, type2);
                        String reply =  "java/lang/Object";
//                        log.info("Common Superclass of {} and {} is {}", type1, type2, reply);
                        return reply;
                    }
                };

                transformedNodes.accept(resultWriter);
                resultClass = resultWriter.toByteArray();
            }

        } catch (Throwable t) {
            happened = t;
        }

        boolean doDump = Hodgepodge.config.internalAsmAlwaysDumpClasses || (happened != null);
        if (doDump) {
            try {
                Files.createDirectories(Paths.get(Hodgepodge.config.internalAsmDumpFoldername));

                Files.write(Paths.get(Hodgepodge.config.internalAsmDumpFoldername, name + "-before.class"), basicClass);
                if (Hodgepodge.config.internalAsmProduceAsmDmpFiles && (inputDump.toString() != null) && (inputDump.toString().length() > 0)) {
                    Files.write(Paths.get(Hodgepodge.config.internalAsmDumpFoldername, name + "-before.asm-dmp"), inputDump.toString().getBytes("UTF-8"));
                }

                if (resultClass != null) {
                    Files.write(Paths.get(Hodgepodge.config.internalAsmDumpFoldername, name + "-after.class"), resultClass);
                    if (Hodgepodge.config.internalAsmProduceAsmDmpFiles && (resultDump.toString() != null) && (resultDump.toString().length() > 0)) {
                        Files.write(Paths.get(Hodgepodge.config.internalAsmDumpFoldername, name + "-before.asm-dmp"), resultDump.toString().getBytes("UTF-8"));
                    }
                }
            } catch (Exception e) {
                log.warn("While dumping classes", e);
            }
        }
            
        if (happened != null) {
            // oops
            log.fatal(String.format("While transforming %s (alias %s)", (transformedName == null) ? "<NULL>":transformedName, (name == null) ? "<NULL>":name), happened);
            FMLCommonHandler.instance().exitJava(-1, true);
            return null;
        }

        return resultClass;
    }

    // Slighty adapted from org.objectweb.asm.util.CheckClassAdapter
    private String verify(ClassNode classNode, StringWriter dump) {
        ClassNode cn = classNode;
        StringWriter swError = new StringWriter();
        PrintWriter pwError = new PrintWriter(swError);
        PrintWriter pw = new PrintWriter(dump);

        Type syperType = cn.superName == null ? null : Type.getObjectType(cn.superName);
        List<MethodNode> methods = cn.methods;

        List<Type> interfaces = new ArrayList<Type>();
        for (Iterator<String> i = cn.interfaces.iterator(); i.hasNext();) {
            interfaces.add(Type.getObjectType(i.next()));
        }

        for (int i = 0; i < methods.size(); ++i) {
            MethodNode method = methods.get(i);
            SimpleVerifier verifier = new SimpleVerifier(
                    Type.getObjectType(cn.name), syperType, interfaces,
                    (cn.access & Opcodes.ACC_INTERFACE) != 0);
            Analyzer<BasicValue> a = new Analyzer<BasicValue>(verifier);
            try {
                a.analyze(cn.name, method);
            } catch (Exception e) {
                try {
                    pwError.printf("Method %s%s: " , method.name, method.desc);
                    pwError.println(e.getMessage());
                    printAnalyzerResult(method, a, pwError);
                } catch (Exception e2) {
                    throw new RuntimeException("Internal verifier error", e2);
                }
            }
            printAnalyzerResult(method, a, pw);
        }
        pw.flush();
        return swError.toString();
    }

    // Straight from org.objectweb.asm.util.CheckClassAdapter
    static void printAnalyzerResult(MethodNode method, Analyzer<BasicValue> a, final PrintWriter pw) {
        Frame<BasicValue>[] frames = a.getFrames();
        Textifier t = new Textifier();
        TraceMethodVisitor mv = new TraceMethodVisitor(t);

        pw.println(method.name + method.desc);
        for (int j = 0; j < method.instructions.size(); ++j) {
            method.instructions.get(j).accept(mv);

            StringBuilder sb = new StringBuilder();
            Frame<BasicValue> f = frames[j];
            if (f == null) {
                sb.append('?');
            } else {
                for (int k = 0; k < f.getLocals(); ++k) {
                    sb.append(getShortName(f.getLocal(k).toString()))
                            .append(' ');
                }
                sb.append(" : ");
                for (int k = 0; k < f.getStackSize(); ++k) {
                    sb.append(getShortName(f.getStack(k).toString()))
                            .append(' ');
                }
            }
            while (sb.length() < method.maxStack + method.maxLocals + 1) {
                sb.append(' ');
            }
            pw.print(Integer.toString(j + 100000).substring(1));
            pw.print(" " + sb + " : " + t.text.get(t.text.size() - 1));
        }
        for (int j = 0; j < method.tryCatchBlocks.size(); ++j) {
            method.tryCatchBlocks.get(j).accept(mv);
            pw.print(" " + t.text.get(t.text.size() - 1));
        }
        pw.println();
    }

    // Straight from org.objectweb.asm.util.CheckClassAdapter
    private static String getShortName(final String name) {
        int n = name.lastIndexOf('/');
        int k = name.length();
        if (name.charAt(k - 1) == ';') {
            k--;
        }
        return n == -1 ? name : name.substring(n + 1, k);
    }

}

