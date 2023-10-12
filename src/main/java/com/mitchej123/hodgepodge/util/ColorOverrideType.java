package com.mitchej123.hodgepodge.util;

import java.util.HashMap;

import com.mitchej123.hodgepodge.client.HodgepodgeClient;

public enum ColorOverrideType {

    GRASS,
    LEAVES,
    FLOWER,
    LIQUID;

    private static final HashMap<String, ColorOverrideType> fromStringMap = new HashMap<>();

    static {
        for (ColorOverrideType type : ColorOverrideType.values()) {
            fromStringMap.put(type.name(), type);
        }
    }

    public static ColorOverrideType fromString(String name) {
        return fromStringMap.get(name);
    }

    public int getColor(int oColor, int x, int z) {
        try {
            switch (this) {
                case GRASS:
                    return (int) HodgepodgeClient.colorGrass.invoke(null, oColor, x, z);
                case FLOWER:
                    return (int) HodgepodgeClient.colorFoliage.invoke(null, oColor, x, z);
                case LEAVES:
                    return (int) HodgepodgeClient.colorLeaves.invoke(null, oColor, x, z);
                case LIQUID:
                    return (int) HodgepodgeClient.colorLiquid.invoke(null, oColor, x, z);
            }
        } catch (Exception ignore) {}
        return oColor;
    }
}
