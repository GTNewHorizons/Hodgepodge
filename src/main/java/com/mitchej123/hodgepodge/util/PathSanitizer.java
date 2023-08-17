package com.mitchej123.hodgepodge.util;

import java.util.regex.Pattern;

/**
 * Utilities for sanitizing file paths generated with user input
 */
public final class PathSanitizer {

    // This class just has some static utilities
    private PathSanitizer() {}

    // On Linux it's just /, macOS forbids / and :
    // On Windows it's <>:"/\|?*
    private static final Pattern ILLEGAL_FILE_NAME_CHARS = Pattern.compile("[/\\\\<>:\"|?*]");

    /**
     * Replaces all illegal characters (including directory separators) for a file name from input with dashes.
     * 
     * @param input The untrusted file name
     * @return A name that can be trusted to be safe for file reading/creation
     */
    public static String sanitizeFileName(String input) {
        return ILLEGAL_FILE_NAME_CHARS.matcher(input).replaceAll("-").replace('\0', '-');
    }
}
