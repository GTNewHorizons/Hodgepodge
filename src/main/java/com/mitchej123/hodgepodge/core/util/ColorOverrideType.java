package com.mitchej123.hodgepodge.core.util;

import com.mitchej123.hodgepodge.core.HodgePodgeClient;
import java.util.HashMap;

public enum ColorOverrideType {
    GRASS("GRASS"),
    LEAVES("LEAVES"),
    FLOWER("FLOWER"),
    LIQUID("LIQUID");

    private static final HashMap<String, ColorOverrideType> byString = new HashMap<>();

    static {
        for (ColorOverrideType type : ColorOverrideType.values()) {
            byString.put(type.name, type);
        }
    }

    public String name;

    ColorOverrideType(String name) {
        this.name = name;
    }

    public static ColorOverrideType get(String name) {
        return byString.get(name);
    }

    public int getColor(int oColor, int x, int z) {
        try {
            switch (this) {
                case GRASS:
                    return (int) HodgePodgeClient.colorGrass.invoke(null, oColor, x, z);
                case FLOWER:
                    return (int) HodgePodgeClient.colorFoliage.invoke(null, oColor, x, z);
                case LEAVES:
                    return (int) HodgePodgeClient.colorLeaves.invoke(null, oColor, x, z);
                case LIQUID:
                    return (int) HodgePodgeClient.colorLiquid.invoke(null, oColor, x, z);
            }
        } catch (Exception ignore) {
        }
        return oColor;
    }
}
