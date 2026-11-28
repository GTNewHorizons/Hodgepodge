package com.mitchej123.hodgepodge.core.shared;

import com.gtnewhorizon.gtnhlib.asm.ASMUtil;
import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;

public class HodgepodgeClassDump {

    public static void dumpClass(String className, byte[] originalBytes, byte[] transformedBytes, Object transformer) {
        if (HodgepodgeProperties.DUMP_ASM_CLASS) {
            ASMUtil.saveAsRawClassFile(originalBytes, className + "_PRE", transformer);
            ASMUtil.saveAsRawClassFile(transformedBytes, className + "_POST", transformer);
        }
    }

    public static void dumpClass(String className, ClassNodeHandle classNode, Object transformer) {
        if (HodgepodgeProperties.DUMP_ASM_CLASS) {
            final byte[] originalBytes = classNode.getOriginalBytes();
            final byte[] transformedBytes = classNode.computeBytes();
            ASMUtil.saveAsRawClassFile(originalBytes, className + "_PRE", transformer);
            ASMUtil.saveAsRawClassFile(transformedBytes, className + "_POST", transformer);
        }
    }

    public static void dumpBytecode(String className, ClassNodeHandle classNode, Object transformer) {
        if (HodgepodgeProperties.DUMP_ASM_CLASS) {
            final byte[] originalBytes = classNode.getOriginalBytes();
            final byte[] transformedBytes = classNode.computeBytes();
            ASMUtil.saveAsBytecodeFile(originalBytes, className + "_PRE", transformer);
            ASMUtil.saveAsBytecodeFile(transformedBytes, className + "_POST", transformer);
        }
    }
}
