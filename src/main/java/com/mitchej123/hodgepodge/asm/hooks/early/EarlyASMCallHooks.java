package com.mitchej123.hodgepodge.asm.hooks.early;

import static cpw.mods.fml.common.ModContainerFactory.modTypes;

import java.io.File;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ModCandidate;
import cpw.mods.fml.common.discovery.asm.ASMModParser;
import cpw.mods.fml.common.discovery.asm.ModAnnotation;

@SuppressWarnings("unused")
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

    private static final String INT_MIN_STRING = Integer.toString(Integer.MIN_VALUE);
    private static final String INT_MAX_STRING = Integer.toString(Integer.MAX_VALUE);
    private static final String DOUBLE_MAX_STRING = Double.toString(Double.MAX_VALUE);
    private static final String DOUBLE_NEG_MAX_STRING = Double.toString(-Double.MAX_VALUE);

    public static String intToCachedString(int value) {
        return switch (value) {
            case Integer.MIN_VALUE -> INT_MIN_STRING;
            case Integer.MAX_VALUE -> INT_MAX_STRING;
            case 0 -> "0";
            case 1 -> "1";
            case 2 -> "2";
            case 3 -> "3";
            case 4 -> "4";
            case 5 -> "5";
            case 10 -> "10";
            case 100 -> "100";
            default -> String.valueOf(value).intern();
        };
    }

    public static String doubleToCachedString(double value) {
        if (value == Double.MAX_VALUE) {
            return DOUBLE_MAX_STRING;
        } else if (value == -Double.MAX_VALUE) {
            return DOUBLE_NEG_MAX_STRING;
        }
        return String.valueOf(value).intern();
    }

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static String[] internArray(String[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_STRING_ARRAY;
        }
        for (int i = 0; i < array.length; i++) {
            array[i] = (array[i] == null) ? null : array[i].intern();
        }
        return array;
    }
}
