package com.mitchej123.hodgepodge.asm.transformers.netty;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class NettyPatchTransformer implements IClassTransformer, Opcodes {

    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("io.netty.bootstrap.Bootstrap".equals(transformedName)) {
            return patchBootstrap(basicClass);
        }
        return basicClass;
    }

    /**
     * The goal of this transformer is to fix a NPE in {@link io.netty.bootstrap.Bootstrap#checkAddress}, the local
     * variable 'InetAddress address' might be null.
     *
     * @see https://bugs.mojang.com/browse/MC-108343
     */
    private static byte[] patchBootstrap(byte[] basicClass) {
        final Logger logger = LogManager.getLogger("NettyPatch");
        final ClassReader classReader = new ClassReader(basicClass);
        final ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        boolean foundMethod = false;
        boolean patched = false;
        for (MethodNode methodNode : classNode.methods) {
            if (isCheckAddressMethod(methodNode)) {
                foundMethod = true;
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (isGetAddressMethodNode(node)) {
                        final AbstractInsnNode nextNode = node.getNext();
                        if (nextNode instanceof VarInsnNode && nextNode.getOpcode() == ASTORE) {
                            final InsnList list = new InsnList();
                            final LabelNode label = new LabelNode();
                            list.add(new VarInsnNode(ALOAD, ((VarInsnNode) nextNode).var));
                            list.add(new JumpInsnNode(IFNONNULL, label));
                            list.add(new InsnNode(ACONST_NULL));
                            list.add(new InsnNode(ARETURN));
                            list.add(label);
                            methodNode.instructions.insert(nextNode, list);
                            patched = true;
                        }
                    }
                }
            }
        }
        if (patched) {
            final ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            final byte[] patchedBytes = classWriter.toByteArray();
            logger.debug("Patched Netty!");
            return patchedBytes;
        } else if (foundMethod) {
            logger.error("Failed to patch Netty!");
        }
        return basicClass;
    }

    private static boolean isCheckAddressMethod(MethodNode methodNode) {
        return methodNode.name.equals("checkAddress")
                && methodNode.desc.equals("(Ljava/net/SocketAddress;)Lio/netty/channel/ChannelFuture;");
    }

    private static boolean isGetAddressMethodNode(AbstractInsnNode node) {
        return node instanceof MethodInsnNode && ((MethodInsnNode) node).owner.equals("java/net/InetSocketAddress")
                && ((MethodInsnNode) node).name.equals("getAddress")
                && ((MethodInsnNode) node).desc.equals("()Ljava/net/InetAddress;");
    }

}
