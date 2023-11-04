package com.mitchej123.hodgepodge.util;

import java.util.HashMap;

import gregtech.common.render.GT_PollutionRenderer;

public enum ColorOverrideType {

    FLOWER,
    GRASS,
    LEAVES,
    LIQUID;

    private static final HashMap<String, ColorOverrideType> fromStringMap = new HashMap<>();

    static {
        for (ColorOverrideType type : ColorOverrideType.values()) {
            fromStringMap.put(type.name(), type);
        }
    }

    public static ColorOverrideType fromName(String name) {
        return fromStringMap.get(name);
    }

    public int getColor(int oColor, int x, int z) {
        switch (this) {
            case FLOWER:
                return GT_PollutionRenderer.colorFoliage(oColor, x, z);
            case GRASS:
                return GT_PollutionRenderer.colorGrass(oColor, x, z);
            case LEAVES:
                return GT_PollutionRenderer.colorLeaves(oColor, x, z);
            case LIQUID:
                return GT_PollutionRenderer.colorLiquid(oColor, x, z);
        }
        throw new RuntimeException();
    }
}
