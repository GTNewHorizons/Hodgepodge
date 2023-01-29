package com.mitchej123.hodgepodge.asm.hooks.early;

import static cpw.mods.fml.common.ModContainerFactory.modTypes;

import java.io.File;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ModCandidate;
import cpw.mods.fml.common.discovery.asm.ASMModParser;
import cpw.mods.fml.common.discovery.asm.ModAnnotation;

public class EarlyASMCallHooks {

    public static ModContainer build(ASMModParser modParser, File modSource, ModCandidate container) {
        String className = modParser.getASMType().getClassName();

        for (ModAnnotation ann : modParser.getAnnotations()) {
            if (modTypes.containsKey(ann.getASMType())) {
                FMLLog.fine("Identified a mod of type %s (%s) - loading", ann.getASMType(), className);
                try {
                    return modTypes.get(ann.getASMType()).newInstance(className, container, ann.getValues());
                } catch (Exception e) {
                    FMLLog.log(Level.ERROR, e, "Unable to construct %s container", ann.getASMType().getClassName());
                    return null;
                }
            }
        }

        return null;
    }
}
