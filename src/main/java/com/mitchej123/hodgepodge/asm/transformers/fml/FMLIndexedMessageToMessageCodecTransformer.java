package com.mitchej123.hodgepodge.asm.transformers.fml;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import com.mitchej123.hodgepodge.Common;

// This can't be a mixin because of mixinDebug causing it to generate a
// java.lang.reflect.MalformedParameterizedTypeException
public class FMLIndexedMessageToMessageCodecTransformer implements IClassTransformer {

    private static final Logger LOGGER = LogManager.getLogger("FMLIndexedMessageToMessageCodecTransformer");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec".equals(transformedName)) {
            Common.logASM(LOGGER, "TRANSFORMING cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec");
            final ClassReader cr = new ClassReader(basicClass);
            final ClassNode cn = new ClassNode();
            cr.accept(cn, 0);
            if (cn.methods != null) {
                for (MethodNode m : cn.methods) {
                    if ("encode".equals(m.name)) {
                        Common.logASM(LOGGER, "Adding dispatcher safety");
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
                        final InsnList i = m.instructions;
                        final AbstractInsnNode tail = i.getLast();
                        if (tail.getOpcode() == Opcodes.RETURN) {
                            i.remove(tail);
                        }
                        final InstructionAdapter ia = new InstructionAdapter(m);
                        ia.load(ctxIdx, Type.getType("Lio/netty/channel/ChannelHandlerContext;"));
                        ia.load(proxyIdx, Type.getType("Lcpw/mods/fml/common/network/internal/FMLProxyPacket;"));
                        ia.invokestatic(
                                "com/mitchej123/hodgepodge/asm/hooks/fml/FMLIndexedMessageToMessageCodecHook",
                                "addMissingDispatcher",
                                "(Lio/netty/channel/ChannelHandlerContext;Lcpw/mods/fml/common/network/internal/FMLProxyPacket;)V",
                                false);
                        ia.areturn(Type.VOID_TYPE);
                    }
                }
            }
            final ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);
            return cw.toByteArray();
        }
        return basicClass;
    }
}
