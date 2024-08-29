package com.mitchej123.hodgepodge.asm.transformers.mc;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class EntityPlayerTransformer implements IClassTransformer {

    private static final Logger LOGGER = LogManager.getLogger("EntityPlayerTransformer");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;

        if (NameContext.Obf.entityPlayer.equals(name)) {
            final ClassReader cr = new ClassReader(basicClass);
            final ClassNode cn = new ClassNode();
            cr.accept(cn, 0);
            transformClassNode(NameContext.Obf, cn);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);

            LOGGER.info("Transformed {}", name);
            return cw.toByteArray();
        }

        if (NameContext.Deobf.entityPlayer.equals(name)) {
            final ClassReader cr = new ClassReader(basicClass);
            final ClassNode cn = new ClassNode();
            cr.accept(cn, 0);
            transformClassNode(NameContext.Deobf, cn);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);

            LOGGER.info("Transformed {}", name);
            return cw.toByteArray();
        }

        return basicClass;
    }

    public void transformClassNode(NameContext names, ClassNode cn) {
        LOGGER.info("Found EntityPlayer (class = {}, name context = {})", names.entityPlayer, names.name());

        for (var method : cn.methods) {
            if (method.name.equals(names.onUpdate)) {
                LOGGER.info("Found EntityPlayer.onUpdate ({})", names.onUpdate);

                var cursor = method.instructions.getFirst();

                while (cursor != null) {
                    if (cursor instanceof FieldInsnNode field && field.getOpcode() == Opcodes.GETFIELD
                            && field.name.equals(names.itemInUse)) {
                        if (cursor.getNext() instanceof JumpInsnNode jump && jump.getOpcode() == Opcodes.IF_ACMPNE) {
                            LOGGER.info("Found injection point");

                            method.instructions.insertBefore(
                                    jump,
                                    new MethodInsnNode(
                                            Opcodes.INVOKESTATIC,
                                            names.itemStack,
                                            names.areItemStacksEqual,
                                            String.format("(L%s;L%s;)Z", names.itemStack, names.itemStack),
                                            false));

                            ((JumpInsnNode) jump).setOpcode(Opcodes.IFEQ);
                            return;
                        }
                    }

                    cursor = cursor.getNext();
                }

                LOGGER.warn("Could not find injection point.");
            }
        }
    }

    private static enum NameContext {

        Deobf("net.minecraft.entity.player.EntityPlayer", "onUpdate", "net/minecraft/item/ItemStack",
                "areItemStacksEqual", "itemInUse"),
        Obf("yz", "h", "add", "b", "f");

        public final String entityPlayer, onUpdate, itemStack, areItemStacksEqual, itemInUse;

        private NameContext(String entityPlayer, String onUpdate, String itemStack, String areItemStacksEqual,
                String itemInUse) {
            this.entityPlayer = entityPlayer;
            this.onUpdate = onUpdate;
            this.itemStack = itemStack;
            this.areItemStacksEqual = areItemStacksEqual;
            this.itemInUse = itemInUse;
        }
    }

}
