package com.mitchej123.hodgepodge.util;

import java.util.HashMap;

import com.mitchej123.hodgepodge.client.HodgepodgeClient;

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
