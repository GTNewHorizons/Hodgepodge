package com.mitchej123.hodgepodge.asm;

import static org.objectweb.asm.Opcodes.*;

import java.io.*;
import java.util.Properties;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.*;

/**
 * This class transformer will be loaded by CodeChickenCore, if it is present.
 * It runs much earlier than the rest of class transformers and mixins do and can modify more of forge's and fml's classes.
 * <p>
 * Due to peculiarity with CCC, and to prevent loading too many classes from messing up the delicate class loading order,
 * we will try to minimize the class dependencies on this class, meaning we will not use the usual Configuration class to
 * handle configurations
 */
public class EarlyClassTransformer implements IClassTransformer {
    private static final boolean noNukeBaseMod;
    private static final Logger LOGGER = LogManager.getLogger("HodgePodgeEarly");

    static {
        Properties config = new Properties();
        File configLocation = new File(Launch.minecraftHome, "config/hodgepodgeEarly.properties");
        try (Reader r = new BufferedReader(new FileReader(configLocation))) {
            config.load(r);
        } catch (IOException e) {
            LOGGER.error("Error reading configuration file. Will use defaults", e);
        }
        noNukeBaseMod = Boolean.parseBoolean(config.getProperty("noNukeBaseMod"));
        config.setProperty("noNukeBaseMod", String.valueOf(noNukeBaseMod));
        try (Writer r = new BufferedWriter(new FileWriter(configLocation))) {
            config.store(r, "Configuration file for early hodgepodge class transformers");
        } catch (IOException e) {
            LOGGER.error("Error reading configuration file. Will use defaults", e);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || name == null) return basicClass;
        switch (name) {
            case "cpw.mods.fml.common.ModContainerFactory":
                if (noNukeBaseMod) return basicClass;
                return transformModContainerFactory(basicClass);
            default:
                return basicClass;
        }
    }

    private static byte[] transformModContainerFactory(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassWriter cw = new ClassWriter(0);
        ClassVisitor cv = new ClassVisitor(ASM5, cw) {
            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                // we no longer need to check for basemod, so this regex is also useless now. remove it.
                if ("modClass".equals(name)) return null;
                return super.visitField(access, name, desc, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(
                    int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                switch (name) {
                    case "build":
                        // inject callhook
                        mv.visitVarInsn(ALOAD, 1);
                        mv.visitVarInsn(ALOAD, 2);
                        mv.visitVarInsn(ALOAD, 3);
                        mv.visitMethodInsn(
                                INVOKESTATIC,
                                "com/mitchej123/hodgepodge/asm/EarlyASMCallHooks",
                                "build",
                                "(Lcpw/mods/fml/common/discovery/asm/ASMModParser;Ljava/io/File;Lcpw/mods/fml/common/discovery/ModCandidate;)Lcpw/mods/fml/common/ModContainer;",
                                false);
                        mv.visitInsn(ARETURN);
                        mv.visitMaxs(3, 4);
                        mv.visitEnd();
                        return null;
                    case "<clinit>":
                        mv = new MethodVisitor(ASM5, mv) {
                            @Override
                            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                                if (name.equals("modClass")) {
                                    // remove access to modClass
                                    super.visitInsn(POP);
                                } else {
                                    super.visitFieldInsn(opcode, owner, name, desc);
                                }
                            }
                        };
                }
                return mv;
            }
        };
        cr.accept(cv, 0);
        return cw.toByteArray();
    }
}
