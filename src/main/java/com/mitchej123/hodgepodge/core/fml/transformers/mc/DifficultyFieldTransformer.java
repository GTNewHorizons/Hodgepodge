package com.mitchej123.hodgepodge.core.fml.transformers.mc;

import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.PUTFIELD;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import com.gtnewhorizon.gtnhlib.asm.ClassConstantPoolParser;
import com.mitchej123.hodgepodge.core.HodgepodgeCore;
import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

@SuppressWarnings("unused")
public class DifficultyFieldTransformer implements IClassTransformer {

    private static final Logger LOGGER = LogManager.getLogger("Hodgepodge|DifficultyASM");

    private static final ClassConstantPoolParser POOL_PARSER_OBF = new ClassConstantPoolParser(
            DifficultyVisitor.FIELD_DESC_OBF);
    private static final ClassConstantPoolParser POOL_PARSER_DEV = new ClassConstantPoolParser(
            DifficultyVisitor.FIELD_DESC_DEV);

    private static int classesTransformed = 0;
    private static int totalGets = 0;
    private static int totalSets = 0;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;

        // In obf env, vanilla MC classes use obf field names/descs.
        // Everything else (mods + dev) uses MCP/SRG names with deobf descs.
        final boolean isVanilla = HodgepodgeCore.isObf() && transformedName.startsWith("net.minecraft.");

        final ClassConstantPoolParser parser = isVanilla ? POOL_PARSER_OBF : POOL_PARSER_DEV;
        if (!parser.find(basicClass)) return basicClass;

        final ClassReader cr = new ClassReader(basicClass);
        final ClassWriter cw = new ClassWriter(0);
        final DifficultyVisitor visitor = new DifficultyVisitor(cw, transformedName, isVanilla);
        cr.accept(visitor, 0);

        if (visitor.gets + visitor.sets == 0) return basicClass;

        classesTransformed++;
        totalGets += visitor.gets;
        totalSets += visitor.sets;

        HodgepodgeCore.logASM(
                LOGGER,
                String.format(
                        "[TRANSFORMED] %s — %dx GET %dx SET | totals: classes=%d gets=%d sets=%d",
                        transformedName,
                        visitor.gets,
                        visitor.sets,
                        classesTransformed,
                        totalGets,
                        totalSets));

        final byte[] result = cw.toByteArray();
        HodgepodgeClassDump.dumpClass(transformedName, basicClass, result, this);
        return result;
    }

    private static final class DifficultyVisitor extends ClassVisitor {

        private static final String FIELD_MCP = "difficultySetting";
        private static final String FIELD_SRG = "field_73013_u";
        private static final String FIELD_OBF = "r";

        private static final String FIELD_DESC_DEV = "Lnet/minecraft/world/EnumDifficulty;";
        private static final String FIELD_DESC_OBF = "Lrd;";

        private static final String WORLD_OWNER_OBF = "ahb";
        private static final String WORLDSERVER_OWNER_OBF = "mt";
        private static final String WORLDCLIENT_OWNER_OBF = "bjf";

        private static final String HOOK_CLASS = "com/mitchej123/hodgepodge/core/fml/hooks/mc/DifficultyHook";
        private static final String WORLD = "net/minecraft/world/World";
        private static final String ENUM_DIFF = "Lnet/minecraft/world/EnumDifficulty;";
        private static final String GET_DESC = "(L" + WORLD + ";)" + ENUM_DIFF;
        private static final String SET_DESC = "(L" + WORLD + ";" + ENUM_DIFF + ")V";

        private final String className;
        private final boolean isVanilla;
        private int gets = 0;
        private int sets = 0;

        DifficultyVisitor(ClassVisitor cv, String className, boolean isVanilla) {
            super(ASM5, cv);
            this.className = className;
            this.isVanilla = isVanilla;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return new DifficultyMethodVisitor(mv, isVanilla, this);
        }
    }

    private static final class DifficultyMethodVisitor extends MethodVisitor {

        private final boolean isVanilla;
        private final DifficultyVisitor parent;

        DifficultyMethodVisitor(MethodVisitor mv, boolean isVanilla, DifficultyVisitor parent) {
            super(ASM5, mv);
            this.isVanilla = isVanilla;
            this.parent = parent;
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            if (opcode == GETFIELD || opcode == PUTFIELD) {

                if (isVanilla) {
                    if (name.equals(DifficultyVisitor.FIELD_OBF) && desc.equals(DifficultyVisitor.FIELD_DESC_OBF)
                            && (owner.equals(DifficultyVisitor.WORLD_OWNER_OBF)
                                    || owner.equals(DifficultyVisitor.WORLDSERVER_OWNER_OBF)
                                    || owner.equals(DifficultyVisitor.WORLDCLIENT_OWNER_OBF))) {
                        emitHook(opcode);
                        return;
                    }
                } else {
                    if ((name.equals(DifficultyVisitor.FIELD_MCP) || name.equals(DifficultyVisitor.FIELD_SRG))
                            && desc.equals(DifficultyVisitor.FIELD_DESC_DEV)) {
                        emitHook(opcode);
                        return;
                    }
                }
            }
            super.visitFieldInsn(opcode, owner, name, desc);
        }

        private void emitHook(int opcode) {
            if (opcode == GETFIELD) {
                parent.gets++;
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        DifficultyVisitor.HOOK_CLASS,
                        "getDifficulty",
                        DifficultyVisitor.GET_DESC,
                        false);
            } else {
                parent.sets++;
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        DifficultyVisitor.HOOK_CLASS,
                        "setDifficulty",
                        DifficultyVisitor.SET_DESC,
                        false);
            }
        }
    }
}
