package com.mitchej123.hodgepodge.asm.transformers.early;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.POP;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import com.mitchej123.hodgepodge.asm.EarlyConfig;

/**
 * This class transformer will be loaded by CodeChickenCore, if it is present. It runs much earlier than the rest of
 * class transformers and mixins do and can modify more of forge's and fml's classes.
 * <p>
 * Due to peculiarity with CCC, and to prevent loading too many classes from messing up the delicate class loading
 * order, we will try to minimize the class dependencies on this class, meaning we will not use the usual Configuration
 * class to handle configurations
 */
@SuppressWarnings("unused")
public class EarlyClassTransformer implements IClassTransformer {

    private static final String EARLY_HOOKS_INTERNAL = "com/mitchej123/hodgepodge/asm/hooks/early/EarlyASMCallHooks";

    static {
        EarlyConfig.ensureLoaded();
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || name == null) return basicClass;
        if (name.equals("cpw.mods.fml.common.ModContainerFactory")) {
            if (EarlyConfig.noNukeBaseMod) {
                return basicClass;
            }
            return transformModContainerFactory(basicClass);
        }
        return basicClass;
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
            public MethodVisitor visitMethod(int access, String name, String desc, String signature,
                    String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                switch (name) {
                    case "build":
                        // inject callhook
                        mv.visitVarInsn(ALOAD, 1);
                        mv.visitVarInsn(ALOAD, 2);
                        mv.visitVarInsn(ALOAD, 3);
                        mv.visitMethodInsn(
                                INVOKESTATIC,
                                EARLY_HOOKS_INTERNAL,
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
