package com.mitchej123.hodgepodge.util;

import com.mitchej123.hodgepodge.core.shared.FileLogger;

public class TexturesDebug {

    private static boolean initSpriteLogger = false;
    private static FileLogger logAtlasSprite = null;
    private static boolean initDynamicLogger = false;
    private static FileLogger logDynamic = null;

    public static void logTextureAtlasSprite(String iconName, int width, int height, int frames, int sizeBytes) {
        logTextureAtlasSprite(iconName + "," + width + "," + height + "," + frames + "," + sizeBytes);
    }

    private static void logTextureAtlasSprite(String message) {
        if (!initSpriteLogger) {
            logAtlasSprite = new FileLogger("TexturesDebug.csv");
            initSpriteLogger = true;
            logTextureAtlasSprite("iconName,width,height,frames,sizeBytes");
        }
        if (logAtlasSprite != null) {
            logAtlasSprite.log(message);
        }
    }

    public static void logDynamicTexture(int width, int height) {
        if (!initDynamicLogger) {
            logDynamic = new FileLogger("DynamicTextures.txt");
            initDynamicLogger = true;
        }
        if (logDynamic != null) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            logDynamic.log("Created texture of width " + width + " height " + height);
            for (int i = 0; i < stackTrace.length; i++) {
                StackTraceElement element = stackTrace[i];
                if (i == 4 || i == 5) {
                    logDynamic.log("at " + element);
                    break;
                }
            }
            logDynamic.log(" ");
        }
    }

}
