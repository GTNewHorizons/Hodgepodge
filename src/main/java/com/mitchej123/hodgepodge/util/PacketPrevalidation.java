package com.mitchej123.hodgepodge.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Utf8;
import com.mitchej123.hodgepodge.Common;

/**
 * Utilities for checking packet data at encoding time to print out the precise cause of bad data, before it reaches the
 * receiver which prints out a useless stacktrace.
 */
public final class PacketPrevalidation {

    // This class just has some static utilities
    private PacketPrevalidation() {}

    public static void validationError(String errorType, String badData) {
        final IllegalArgumentException validationEx = new IllegalArgumentException(
                "Invalid packet data encoded: " + errorType
                        + "; data=\""
                        + StringEscapeUtils.escapeJava(badData)
                        + "\"");
        Common.log.error("Packet validation error", validationEx);
        if (Common.config.validatePacketEncodingBeforeSendingShouldCrash) {
            throw validationEx;
        }
    }

    public static void validateLimitedString(String value, int maxLen) {
        if (value == null) {
            validationError("Null string passed to a ByteBuf encoder", "null");
            return;
        }
        final int utf8Len = Utf8.encodedLength(value);
        if (utf8Len > maxLen) {
            validationError("String longer than " + maxLen, StringUtils.substring(value, 0, maxLen));
        }
    }
}
