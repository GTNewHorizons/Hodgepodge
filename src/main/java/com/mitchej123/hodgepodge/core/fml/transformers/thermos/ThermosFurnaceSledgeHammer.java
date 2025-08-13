package com.mitchej123.hodgepodge.core.fml.transformers.thermos;

import static org.objectweb.asm.Opcodes.ASM5;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.mitchej123.hodgepodge.config.ASMConfig;
import com.mitchej123.hodgepodge.core.HodgepodgeCore;
import com.mitchej123.hodgepodge.core.shared.HodgepodgeClassDump;

@SuppressWarnings("unused")
public class ThermosFurnaceSledgeHammer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if (ASMConfig.thermosCraftServerClass.equals(transformedName)) {
            final byte[] transformedBytes = transformBytes(basicClass);
            HodgepodgeClassDump.dumpClass(transformedName, basicClass, transformedBytes, this);
            return transformedBytes;
        }
        return basicClass;
    }

    private static byte[] transformBytes(byte[] basicClass) {
        Logger LOGGER = LogManager.getLogger("ThermosFurnaceSledgeHammer");
        HodgepodgeCore.logASM(LOGGER, "Patching Thermos or derivative to not break our furnace fix");
        final ClassReader cr = new ClassReader(basicClass);
        final ClassNode cn = new ClassNode(ASM5);
        cr.accept(cn, 0);
        for (MethodNode m : cn.methods) {
            if ("resetRecipes".equals(m.name)) {
                HodgepodgeCore.logASM(LOGGER, "Taking a sledgehammer to CraftServer.resetRecipes()");
                // Replace the body with a RETURN opcode
                InsnList insnList = new InsnList();
                insnList.add(new InsnNode(Opcodes.RETURN));
                m.instructions = insnList;
                m.maxStack = 0;
            }
        }
        final ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
