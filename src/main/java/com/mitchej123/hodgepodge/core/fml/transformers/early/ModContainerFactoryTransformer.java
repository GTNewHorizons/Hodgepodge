package com.mitchej123.hodgepodge.core.fml.transformers.early;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

/**
 * The targeted class is loaded too early by cofh/asm/LoadingPlugin$CoFHDummyContainer#call(), it's an IFMLCallHook
 * which gets called before our ASM transformers are registered in the usual way. This is why we register this
 * transformer manually when our IFMLLoadingPlugin is instantiated.
 */
@SuppressWarnings("unused")
public class ModContainerFactoryTransformer implements IClassTransformer, Opcodes {

    private static final String HOOK_CLASS_INTERNAL = "com/mitchej123/hodgepodge/core/fml/hooks/early/ModContainerFactoryHook";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if ("cpw.mods.fml.common.ModContainerFactory".equals(name)) {
            final byte[] transformedBytes = transformBytes(basicClass);
            HodgepodgeClassDump.dumpClass(transformedName, basicClass, transformedBytes, this);
            return transformedBytes;
        }
        return basicClass;
    }

    private static byte[] transformBytes(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassWriter cw = new ClassWriter(0);
        ClassVisitor cv = new ClassVisitor(ASM5, cw) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature,
                    String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if (name.equals("build")) {
                    // inject callhook
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitVarInsn(ALOAD, 2);
                    mv.visitVarInsn(ALOAD, 3);
                    mv.visitMethodInsn(
                            INVOKESTATIC,
                            HOOK_CLASS_INTERNAL,
                            "build",
                            "(Lcpw/mods/fml/common/discovery/asm/ASMModParser;Ljava/io/File;Lcpw/mods/fml/common/discovery/ModCandidate;)Lcpw/mods/fml/common/ModContainer;",
                            false);
                    mv.visitInsn(ARETURN);
                    mv.visitMaxs(3, 4);
                    mv.visitEnd();
                    return null;
                }
                return mv;
            }
        };
        cr.accept(cv, 0);
        return cw.toByteArray();
    }
}
