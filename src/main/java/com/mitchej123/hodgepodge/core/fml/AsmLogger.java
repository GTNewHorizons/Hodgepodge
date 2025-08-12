package com.mitchej123.hodgepodge.core.fml;

import org.apache.logging.log4j.Logger;

import com.mitchej123.hodgepodge.core.HodgepodgeCore;

public class AsmLogger {

    public static void log(Logger log, String message) {
        if (HodgepodgeCore.isObf()) {
            log.debug(message);
        } else {
            log.info(message);
        }
    }
}
