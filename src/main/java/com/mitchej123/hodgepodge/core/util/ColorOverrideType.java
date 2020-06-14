package com.mitchej123.hodgepodge.core.util;

import net.minecraft.block.Block;

import java.util.HashMap;

import static com.mitchej123.hodgepodge.core.HodgePodgeClient.method_GTAPI_GetPollution;

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

    ColorOverrideType(String name){
        this.name = name;
    }

    public static ColorOverrideType get(String name) {
        return byString.get(name);
    }

    public int getColor(int oColor, Block block, int x, int z) {
        int pollution = 0;
        try {
            pollution = (int) method_GTAPI_GetPollution.invoke(null, x, z);
        } catch (Exception e) {
            return oColor;
        }

        if (pollution <= 200)
            return oColor;

        int r = (oColor >> 16) & 0xFF;
        int g = (oColor >> 8) & 0xFF;
        int b = oColor & 0xFF;
        float p, pi;

        switch (this) {
            case GRASS:
                p = (pollution - 250) /600F;
                if (p > 1) p = 1;
                else if (p < 0) p = 0;
                pi = 1-p;
                r = ((int) (r * pi + p * 250)) & 0xFF;
                g = ((int) (g * pi + p * 200)) & 0xFF;
                b = ((int) (b * pi + p * 40)) & 0xFF;
                break;
            case FLOWER:
            case LEAVES:
                p = (pollution - 200) /500F;
                if (p > 1) p = 1;
                else if (p < 0) p = 0;
                pi = 1-p;
                r = ((int) (r * pi + p * 160)) & 0xFF;
                g = ((int) (g * pi + p * 80)) & 0xFF;
                b = ((int) (b * pi + p * 15)) & 0xFF;
                break;
            case LIQUID:
                p = (pollution - 200) /500F;
                if (p > 1) p = 1;
                else if (p < 0) p = 0;
                pi = 1-p;
                r = ((int) (r * pi + p * 160)) & 0xFF;
                g = ((int) (g * pi + p * 200)) & 0xFF;
                b = ((int) (b * pi + p * 10)) & 0xFF;
        }
        return (r << 16) | g << 8 | b;
    }
}
