package com.mitchej123.hodgepodge.core.shared;

import com.gtnewhorizon.gtnhlib.asm.ASMUtil;
import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;

public class HodgepodgeClassDump {

    public static void dumpClass(String className, byte[] originalBytes, byte[] transformedBytes, Object transformer) {
        if (EarlyConfig.dumpASMClass) {
            ASMUtil.saveAsRawClassFile(originalBytes, className + "_PRE", transformer);
            ASMUtil.saveAsRawClassFile(transformedBytes, className + "_POST", transformer);
        }
    }

    public static void dumpRFBClass(String className, ClassNodeHandle classNode, Object transformer) {
        if (EarlyConfig.dumpASMClass) {
            final byte[] originalBytes = classNode.getOriginalBytes();
            final byte[] transformedBytes = classNode.computeBytes();
            ASMUtil.saveAsRawClassFile(originalBytes, className + "_PRE", transformer);
            ASMUtil.saveAsRawClassFile(transformedBytes, className + "_POST", transformer);
        }
    }
}
