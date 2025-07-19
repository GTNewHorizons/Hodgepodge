package com.mitchej123.hodgepodge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.mitchej123.hodgepodge.core.HodgepodgeCore;

public class Common {

    public static final Logger log = LogManager.getLogger("Hodgepodge");
    public static final Marker securityMarker = MarkerManager.getMarker("SuspiciousPackets");
    public static XSTR RNG = new XSTR();

    public static void logASM(Logger log, String message) {
        if (HodgepodgeCore.isObf()) {
            log.debug(message);
        } else {
            log.info(message);
        }
    }
}
