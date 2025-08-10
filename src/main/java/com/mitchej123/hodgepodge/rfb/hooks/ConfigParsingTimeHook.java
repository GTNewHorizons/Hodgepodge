package com.mitchej123.hodgepodge.rfb.hooks;

import java.io.File;

import com.mitchej123.hodgepodge.util.FileLogger;

@SuppressWarnings("unused")
public class ConfigParsingTimeHook {

    private static final FileLogger logger;

    static {
        logger = new FileLogger("ConfigParsingTimes.csv");
        logger.log("filename;time");
    }

    public static void onEnd(File file, long time) {
        logger.log(file.getName() + ';' + (time / 1_000_000));
    }
}
