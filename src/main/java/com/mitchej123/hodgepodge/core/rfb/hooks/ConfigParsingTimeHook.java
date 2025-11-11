package com.mitchej123.hodgepodge.core.rfb.hooks;

import java.io.File;

import com.mitchej123.hodgepodge.core.shared.FileLogger;

@SuppressWarnings("unused")
public class ConfigParsingTimeHook {

    private static final FileLogger logger;

    static {
        logger = new FileLogger("ConfigParsingTimes.csv");
        logger.log("filename;time");
    }

    public static void onEnd(File file, long time) {
        logger.log(getConfigName(file) + ';' + (time / 1_000_000));
    }

    private static String getConfigName(File file) {
        String name = file.getAbsolutePath().replace(File.separatorChar, '/').replace("/./", "/");
        final String split = "/config/";
        final int index = name.indexOf(split);
        if (index > -1) {
            name = name.substring(index + split.length());
        }
        return name;
    }
}
