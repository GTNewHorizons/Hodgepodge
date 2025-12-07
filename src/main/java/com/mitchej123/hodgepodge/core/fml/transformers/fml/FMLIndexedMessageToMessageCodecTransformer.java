package com.mitchej123.hodgepodge.core.fml.transformers.fml;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.mitchej123.hodgepodge.core.HodgepodgeCore;
import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

// This can't be a mixin because of mixinDebug causing it to generate a
// java.lang.reflect.MalformedParameterizedTypeException
@SuppressWarnings("unused")
public class FMLIndexedMessageToMessageCodecTransformer implements IClassTransformer, Opcodes {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if ("cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec".equals(transformedName)) {
            final byte[] transformedBytes = transformBytes(basicClass);
            HodgepodgeClassDump.dumpClass(transformedName, basicClass, transformedBytes, this);
            return transformedBytes;
        }
        return basicClass;
    }

    private static byte[] transformBytes(byte[] basicClass) {
        final Logger LOGGER = LogManager.getLogger("FMLIndexedMessageToMessageCodecTransformer");
        HodgepodgeCore.logASM(LOGGER, "TRANSFORMING cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec");
        final ClassReader cr = new ClassReader(basicClass);
        final ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        if (cn.methods != null) {
            for (MethodNode m : cn.methods) {
                if ("encode".equals(m.name)) {
                    HodgepodgeCore.logASM(LOGGER, "Adding dispatcher safety");
                    int ctxIdx = 1;
                    int proxyIdx = 7;
                    if (m.localVariables != null) {
                        for (LocalVariableNode local : m.localVariables) {
                            if ("ctx".equals(local.name)) {
                                ctxIdx = local.index;
                            } else if ("proxy".equals(local.name)) {
                                proxyIdx = local.index;
                            }
                        }
                    }
                    for (AbstractInsnNode insnNode : m.instructions.toArray()) {
                        if (insnNode.getOpcode() == RETURN) {
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, ctxIdx));
                            list.add(new VarInsnNode(ALOAD, proxyIdx));
                            list.add(
                                    new MethodInsnNode(
                                            INVOKESTATIC,
                                            "com/mitchej123/hodgepodge/core/fml/hooks/fml/FMLIndexedMessageToMessageCodecHook",
                                            "addMissingDispatcher",
                                            "(Lio/netty/channel/ChannelHandlerContext;Lcpw/mods/fml/common/network/internal/FMLProxyPacket;)V",
                                            false));
                            m.instructions.insertBefore(insnNode, list);
                        }
                    }
                }
            }
        }
        final ClassWriter cw = new ClassWriter(cr, 0);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
